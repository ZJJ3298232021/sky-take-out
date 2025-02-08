package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.sky.constant.MessageConstant;
import com.sky.constant.ThirdPartyConstant;
import com.sky.constant.TradeStatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.result.SocketResult;
import com.sky.server.SocketServer;
import com.sky.service.OrderService;
import com.sky.utils.AlipayUtil;
import com.sky.utils.EmptyUtil;
import com.sky.utils.HttpClientUtil;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    @Value("${sky.amap.api-key}")
    private String amap_key;

    @Value("${sky.shop.address}")
    private String shop_location;

    private final Gson gson;

    private final OrderMapper orderMapper;

    private final OrderDetailMapper orderDetailMapper;

    private final AddressBookMapper addressBookMapper;

    private final ShoppingCartMapper shoppingCartMapper;

    private final AlipayUtil alipayUtil;

    private final UserMapper userMapper;

    public final SocketServer server;

    /**
     * 提交订单
     *
     * @param dto .
     * @return .
     */
    @Transactional
    @Override
    public OrderSubmitVO submitOrder(OrdersSubmitDTO dto) {
        //首先判断地址簿是否为空（实际上前端有过校验,此处可复用）
        AddressBook address = addressBookMapper.getById(dto.getAddressBookId());
        if (Objects.isNull(address)) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        checkRange(address.getProvinceName() +
                address.getCityName() +
                address.getDistrictName() +
                address.getDetail());

        //判断购物车是否为空，不为空，则清空购物车
        ShoppingCart shoppingCart = ShoppingCart
                .builder()
                .userId(BaseContext.getCurrentId())
                .build();
        List<ShoppingCart> cartItems = shoppingCartMapper.getCartItem(shoppingCart);
        if (!EmptyUtil.listEmpty(cartItems)) {
            shoppingCartMapper.deleteByUserId(BaseContext.getCurrentId());
        } else {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //向订单表插入一条数据
        Orders orders = Orders.builder()
                .status(Orders.PENDING_PAYMENT)
                .number(String.valueOf(System.currentTimeMillis()))
                .userId(BaseContext.getCurrentId())
                .orderTime(LocalDateTime.now())
                .payStatus(Orders.UN_PAID)
                .phone(address.getPhone())
                .address(address.getDetail())
                .userName("TODO")
                .consignee(address.getConsignee())
                .build();

        BeanUtils.copyProperties(dto, orders);

        orderMapper.insert(orders);
        //向订单明细表插入n条数据

        List<OrderDetail> orderDetails =
                cartItems.stream().map(item -> {
                    OrderDetail orderDetail = new OrderDetail();
                    BeanUtils.copyProperties(item, orderDetail);
                    orderDetail.setOrderId(orders.getId());
                    return orderDetail;
                }).toList();

        orderDetailMapper.insertBatch(orderDetails);
        //封装订单VO数据
        return OrderSubmitVO
                .builder()
                .orderTime(orders.getOrderTime())
                .id(orders.getId())
                .orderAmount(orders.getAmount())
                .orderNumber(orders.getNumber())
                .build();
    }

    /**
     * 支付订单
     *
     * @param paymentDTO .
     * @return .
     */

    @Override
    public String pay(OrdersPaymentDTO paymentDTO) {
        //获取用户唯一Openid，防止多用户二维码覆盖
        String openid = userMapper.getById(BaseContext.getCurrentId()).getOpenid();
        List<Orders> orders = orderMapper.getOrders(Orders
                .builder()
                .id(paymentDTO.getOrderId())
                .build()
        );
        Orders order = orders.get(0);
        //生成二维码
        String codeInBase64 = alipayUtil.getQrCodeInBase64(
                order.getNumber(),
                openid,
                order.getAmount()
        );
        //微信支付的话，这里判断是否订单已支付，但是支付宝沙箱做不到，所以暂时不判断


        return codeInBase64;
    }

    /**
     * 更新订单状态
     *
     * @param tradeStatus .
     * @param outTradeNo  .
     * @return 返回订单支付状态
     */

    @Override
    public Integer tradeStatus(String tradeStatus, String outTradeNo) {
        Orders order = Orders.builder()
                .checkoutTime(LocalDateTime.now())
                .payMethod(2)
                .number(outTradeNo)
                .build();

        switch (tradeStatus) {
            case TradeStatusConstant.SUCCESS, TradeStatusConstant.FINISHED: {
                order.setStatus(Orders.TO_BE_CONFIRMED);
                order.setPayStatus(Orders.PAID);

                //websocket通知
                Orders temp = Orders.builder().number(outTradeNo).build();
                List<Orders> orders = orderMapper.getOrders(temp);
                if (!EmptyUtil.listEmpty(orders)) {
                    Orders tempOrder = orders.get(0);
                    server.sendToAll(
                            gson.toJson(SocketResult
                                    .builder()
                                    .type(1)
                                    .orderId(tempOrder.getId())
                                    .content("订单号:"+outTradeNo)
                            )
                    );
                } else throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
                break;
            }
            case TradeStatusConstant.PAYING, TradeStatusConstant.CLOSED: {
                order.setStatus(Orders.PENDING_PAYMENT);
                order.setPayStatus(Orders.UN_PAID);
                break;
            }
        }
        orderMapper.update(order);
        return order.getPayStatus();
    }

    /**
     * 获取订单支付状态
     *
     * @param orderId .
     * @return .
     */
    @Override
    public Integer getPayStatus(Long orderId) {
        List<Orders> orders = orderMapper.getOrders(Orders.builder().id(orderId).build());

        if (!EmptyUtil.listEmpty(orders)) {
            Orders order = orders.get(0);
            String status = alipayUtil.getOrderStatus(order.getNumber());
            return tradeStatus(status, order.getNumber());
        }

        return null;
    }

    /**
     * 历史订单
     *
     * @param dto .
     * @return .
     */
    @Override
    public PageResult<OrderVO> historyOrders(OrdersPageQueryDTO dto) {
        //PageHelper分页
        PageHelper.startPage(dto.getPage(), dto.getPageSize());

        // 查询历史订单
        dto.setUserId(BaseContext.getCurrentId());
        Page<Orders> pages = orderMapper.pageQuery(dto);

        //获取历史订单详情
        List<OrderVO> orderVOS = pages.getResult().stream().map(order -> {
            Long id = order.getId();
            List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);

            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(order, orderVO);
            orderVO.setOrderDetailList(orderDetails);
            return orderVO;
        }).toList();

        return new PageResult<>(pages.getTotal(), orderVOS);
    }

    /**
     * 订单详情
     *
     * @param id 订单ID
     * @return .
     */
    @Override
    public OrderVO orderDetail(Long id) {
        Orders order = Orders
                .builder()
                .id(id)
                .build();

        List<Orders> orders = orderMapper.getOrders(order);

        if (!EmptyUtil.listEmpty(orders)) {
            order = orders.get(0);
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(order, orderVO);
            orderVO.setOrderDetailList(orderDetailMapper.getByOrderId(order.getId()));
            return orderVO;
        }
        log.error("订单不存在");
        return null;
    }

    /**
     * 取消订单
     *
     * @param dto .
     */
    @Override
    public void cancelOrder(OrdersCancelDTO dto) {
        Orders order = Orders.builder().id(dto.getId()).build();
        List<Orders> orders = orderMapper.getOrders(order);

        //判断订单是否存在
        if (!EmptyUtil.listEmpty(orders)) {
            order = orders.get(0);
            Orders temp = Orders.builder().id(dto.getId()).build();

            //由于用户端与管理员端复用此方法，故注释此判断
//            if (order.getStatus() > 2) {
//                throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
//            }

            if (Objects.equals(order.getPayStatus(), Orders.PAID)) {
                alipayUtil.refund(order.getNumber(), order.getAmount());
                temp.setPayStatus(Orders.REFUND);
            }

            temp.setStatus(Orders.CANCELLED);
            temp.setCancelReason(dto.getCancelReason());
            temp.setCancelTime(LocalDateTime.now());
            orderMapper.update(temp);
        } else {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
    }

    /**
     * 再来一单
     *
     * @param id .
     */
    @Override
    public void oneMoreOrder(Long id) {
        //首先根据订单ID查询出订单下的所有商品
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);

        List<ShoppingCart> list = orderDetails.stream().map(orderDetail -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail, shoppingCart, "id");
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).toList();

        //将这些商品添加到购物车
        shoppingCartMapper.insertBatch(list);

    }

    /**
     * 条件搜索订单
     *
     * @param dto .
     * @return .
     */
    @Override
    public PageResult<Orders> conditionSearch(OrdersPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(), dto.getPageSize());

        Page<Orders> orders = orderMapper.pageQuery(dto);
        return new PageResult<>(orders.getTotal(), orders.getResult());
    }

    /**
     * 订单统计
     *
     * @return .
     */
    @Override
    public OrderStatisticsVO statistics() {
        OrderStatisticsVO statisticsVO = new OrderStatisticsVO();

        Integer toBeConfirmed = orderMapper.statusCount(Orders.TO_BE_CONFIRMED);
        Integer confirmed = orderMapper.statusCount(Orders.CONFIRMED);
        Integer deliveryInProgress = orderMapper.statusCount(Orders.DELIVERY_IN_PROGRESS);

        statisticsVO.setToBeConfirmed(toBeConfirmed);
        statisticsVO.setConfirmed(confirmed);
        statisticsVO.setDeliveryInProgress(deliveryInProgress);

        return statisticsVO;
    }

    /**
     * 接单
     *
     * @param id .
     */
    @Override
    public void confirmOrder(Long id) {
        Orders order = Orders
                .builder()
                .status(Orders.CONFIRMED)
                .id(id)
                .build();
        orderMapper.update(order);
    }

    /**
     * 拒单
     *
     * @param dto .
     */
    @Override
    public void rejectOrder(OrdersRejectionDTO dto) {
        Orders order = Orders
                .builder()
                .id(dto.getId())
                .build();
        List<Orders> orders = orderMapper.getOrders(order);
        // 查询订单,为空抛出异常
        if (!EmptyUtil.listEmpty(orders)) {
            order = orders.get(0);
            //如果订单状态为待接单，才能拒单，支付状态为已支付，则退款
            if (Objects.equals(order.getStatus(), Orders.TO_BE_CONFIRMED)) {
                Integer payStatus = order.getPayStatus();
                if (Objects.equals(payStatus, Orders.PAID)) {
                    // 调用支付宝退款接口
                    alipayUtil.refund(order.getNumber(), order.getAmount());
                    log.info("拒单，金额:{}", order.getAmount());
                }
                // 更新订单状态为已取消，并记录拒单原因，时间
                Orders temp = Orders
                        .builder()
                        .id(dto.getId())
                        .status(Orders.CANCELLED)
                        .rejectionReason(dto.getRejectionReason())
                        .cancelTime(LocalDateTime.now())
                        .payStatus(Orders.REFUND)
                        .build();

                orderMapper.update(temp);
            } else throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        } else
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
    }

    /**
     * 派送订单
     *
     * @param id .
     */
    @Override
    public void deliverOrder(Long id) {
        Orders order = Orders
                .builder()
                .status(Orders.DELIVERY_IN_PROGRESS)
                .id(id)
                .build();
        orderMapper.update(order);
    }

    /**
     * 完成订单
     *
     * @param id .
     */
    @Override
    public void completeOrder(Long id) {
        Orders order = Orders
                .builder()
                .deliveryTime(LocalDateTime.now())
                .status(Orders.COMPLETED)
                .id(id)
                .build();

        orderMapper.update(order);
    }

    /**
     * 催单
     * @param id .
     */
    @Override
    public void reminder(Long id) {
        List<Orders> orders = orderMapper.getOrders(Orders.builder().id(id).build());
        if (!EmptyUtil.listEmpty(orders)) {
            Orders order = orders.get(0);
            if (Objects.equals(order.getStatus(), Orders.TO_BE_CONFIRMED)) {
                server.sendToAll(
                        gson.toJson(
                                SocketResult
                                        .builder()
                                        .orderId(id)
                                        .type(2)
                                        .content("订单号:"+order.getNumber())
                        )
                );
            } else throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        } else throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
    }

    /**
     * 检查用户地址是否超出配送范围
     *
     * @param userLocation 用户地址
     */
    private void checkRange(String userLocation) {
        String userGeo = getGeo(userLocation);
        String shopGeo = getGeo(shop_location);
        calculateDistance(userGeo, shopGeo);
    }

    /**
     * 根据地址获取经纬度
     *
     * @param address 地址
     * @return 经纬度  示例：116.301392,40.050897
     */
    private String getGeo(String address) {
        Map<String, String> params = new HashMap<>();
        //参数封装
        params.put("key", amap_key);
        params.put("address", address);

        //请求位置
        String locationJson = HttpClientUtil.doGet(ThirdPartyConstant.GEOCODER_URL, params);
        JsonElement locationJsonElement = gson.fromJson(locationJson, JsonElement.class);

        //解析失败，抛出异常
        if (locationJsonElement.getAsJsonObject().get("status").getAsString().equals("0")) {
            throw new OrderBusinessException(MessageConstant.USER_ADDRESS_ANALYSIS_FAILED);
        }

        //返回经纬度  示例：116.301392,40.050897
        return locationJsonElement
                .getAsJsonObject()
                .get("geocodes")
                .getAsJsonArray()
                .get(0)
                .getAsJsonObject()
                .get("location")
                .getAsString();
    }

    /**
     * 距离计算
     *
     * @param origin      原地址
     * @param destination 目的地址
     */
    private void calculateDistance(String origin, String destination) {
        // 参数封装
        Map<String, String> params = new HashMap<>();
        params.put("key", amap_key);
        params.put("origins", origin);
        params.put("destination", destination);

        // 请求距离
        String distanceJson = HttpClientUtil.doGet(ThirdPartyConstant.DISTANCE_URL, params);
        JsonElement distanceJsonElement = gson.fromJson(distanceJson, JsonElement.class);

        //解析失败，抛出异常
        if (distanceJsonElement.getAsJsonObject().get("status").getAsString().equals("0")) {
            throw new OrderBusinessException(MessageConstant.DISTANCE_CALCULATION_FAILED);
        }

        //判断距离是否超过5公里，超过则抛出异常
        if (distanceJsonElement
                .getAsJsonObject()
                .get("results")
                .getAsJsonArray()
                .get(0)
                .getAsJsonObject()
                .get("distance")
                .getAsInt()
                > 5000) {
            throw new OrderBusinessException(MessageConstant.DISTANCE_OUT_OF_RANGE);
        }
    }
}

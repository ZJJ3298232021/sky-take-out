package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.TradeConstant;
import com.sky.constant.TradeStatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.AlipayUtil;
import com.sky.utils.EmptyUtil;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;

    private final OrderDetailMapper orderDetailMapper;

    private final AddressBookMapper addressBookMapper;

    private final ShoppingCartMapper shoppingCartMapper;

    private final AlipayUtil alipayUtil;

    private final UserMapper userMapper;

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

    @Override
    public void cancelOrder(Long id) {
        Orders order = Orders.builder().id(id).build();
        List<Orders> orders = orderMapper.getOrders(order);
        if (!EmptyUtil.listEmpty(orders)) {
            order = orders.get(0);
            Orders temp = Orders.builder().number(order.getNumber()).build();
            if (order.getStatus() > 2) {
                throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
            }

            if(order.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
                alipayUtil.refund(order.getNumber(), order.getAmount());
                temp.setPayStatus(Orders.REFUND);
            }

            temp.setStatus(Orders.CANCELLED);
            temp.setCancelReason(TradeConstant.CANCELED);
            temp.setCancelTime(LocalDateTime.now());
            orderMapper.update(temp);
        } else {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
    }
}

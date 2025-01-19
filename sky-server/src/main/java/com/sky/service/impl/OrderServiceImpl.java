package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.OrderService;
import com.sky.utils.EmptyUtil;
import com.sky.vo.OrderSubmitVO;
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

}

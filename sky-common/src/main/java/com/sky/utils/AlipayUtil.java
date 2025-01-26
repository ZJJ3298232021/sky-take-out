package com.sky.utils;

import cn.hutool.extra.qrcode.QrCodeUtil;
import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.payment.common.models.AlipayTradeQueryResponse;
import com.alipay.easysdk.payment.common.models.AlipayTradeRefundResponse;
import com.alipay.easysdk.payment.facetoface.models.AlipayTradePrecreateResponse;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sky.constant.MessageConstant;
import com.sky.exception.OrderBusinessException;
import com.sky.properties.AlipayProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.Base64;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlipayUtil {

    private final AlipayProperties aliPayProperties;


    /**
     * 获取支付宝二维码Base64
     * @param orderNumber 订单号
     * @param openid 微信用户唯一标识
     * @param amount 订单金额
     * @return 二维码Base64
     */
    public String getQrCodeInBase64(String orderNumber, String openid, BigDecimal amount) {
        log.info("正在获取支付宝二维码Base64");
        log.info("alipayPublicKey:{}", aliPayProperties.getAlipayPublicKey());
        log.info("appId:{}", aliPayProperties.getAppId());
        log.info("merchantPrivateKey:{}", aliPayProperties.getMerchantPrivateKey());
        log.info("notifyUrl:{}", aliPayProperties.getNotifyUrl());
        log.info("gatewayHost:{}", aliPayProperties.getGatewayHost());
        Factory.setOptions(aliPayProperties.toAlipayConfig());
        String path = "./qrCode" + openid + ".png";
        //log方法参数
        log.info("orderNumber:{}", orderNumber);
        log.info("amount:{}", amount);
        log.info("openid:{}", openid);
        try {
            AlipayTradePrecreateResponse response = Factory.Payment.FaceToFace().preCreate(
                    "苍穹外卖点菜",
                    orderNumber,
                    String.valueOf(amount)
            );
            String body = response.getHttpBody();
            JsonElement jsonElement = JsonParser.parseString(body);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String qrCodeUrl = String.valueOf(jsonObject
                    .getAsJsonObject("alipay_trade_precreate_response")
                    .get("qr_code"));
            log.info("qrcode_url:{}", qrCodeUrl);
            QrCodeUtil.generate(
                    qrCodeUrl.replace("\"", ""),
                    300,
                    300,
                    new File(path));
            log.info("二维码文件生成成功");
            File file = new File(path);
            String base64Pic = Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
            //删除图片
            boolean deleted = file.delete();
            if (deleted) {
                log.info("删除二维码文件成功");
            } else {
                log.info("删除二维码文件失败");
            }
            log.info(body);
            log.info("二维码Base64:{}", base64Pic);
            return base64Pic;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询支付宝订单状态
     *
     * @param orderNumber 商户订单号
     * @return 订单状态：
     * WAIT_BUYER_PAY（交易创建，等待买家付款）
     * TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）
     * TRADE_SUCCESS（交易支付成功）
     * TRADE_FINISHED（交易结束，不可退款）
     */
    public String getOrderStatus(String orderNumber) {
        log.info("开始查询支付宝订单状态：{}", orderNumber);
        Factory.setOptions(aliPayProperties.toAlipayConfig());
        try {
            // 调用支付宝API查询订单状态
            AlipayTradeQueryResponse response =
                    Factory.Payment.Common().query(orderNumber);

            log.info("支付宝订单查询结果：{}", response.getHttpBody());

            if (response.code.equals("10000")) {
                // 接口调用成功，返回订单状态
                return response.tradeStatus;
            } else {
                log.error("查询支付宝订单状态失败，错误码：{}，错误信息：{}",
                        response.code, response.msg);
                throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
            }

        } catch (Exception e) {
            log.error("查询支付宝订单状态异常", e);
            throw new RuntimeException("查询支付宝订单状态异常", e);
        }
    }

    /**
     * 申请退款
     * @param orderNumber 商户订单号
     * @param refundAmount 退款金额
     * @return 是否退款成功
     */
    public boolean refund(String orderNumber, BigDecimal refundAmount) {
        log.info("开始申请支付宝退款，订单号：{}，退款金额：{}",
                orderNumber, refundAmount);
        try {
            // 调用支付宝API申请退款
            AlipayTradeRefundResponse response =
                    Factory.Payment.Common().refund(
                            orderNumber,
                            refundAmount.toString()
                    );

            log.info("支付宝退款申请结果：{}", response.getHttpBody());

            if (response.code.equals("10000")) {
                // 退款成功
                log.info("退款成功，退款金额：{}", response.refundFee);
                return true;
            } else {
                log.error("退款失败，错误码：{}，错误信息：{}", response.code, response.msg);
                return false;
            }

        } catch (Exception e) {
            log.error("申请退款异常", e);
            throw new RuntimeException("申请退款异常", e);
        }
    }
}
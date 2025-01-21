package com.sky.utils;

import cn.hutool.extra.qrcode.QrCodeUtil;
import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.payment.facetoface.models.AlipayTradePrecreateResponse;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
        log.info("orderNumber:{}",orderNumber);
        log.info("amount:{}",amount);
        log.info("openid:{}",openid);
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
}
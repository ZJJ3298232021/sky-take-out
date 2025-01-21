package com.sky.properties;

import com.alipay.easysdk.kernel.Config;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sky.alipay")
@Data
public class AlipayProperties {
    private String protocol;
    private String gatewayHost;
    private String signType;
    private String appId;
    private String merchantPrivateKey;
    private String alipayPublicKey;
    private String notifyUrl;

    public Config toAlipayConfig() {
        Config alipayConfig = new Config();
        alipayConfig.protocol = protocol;
        alipayConfig.gatewayHost = gatewayHost;
        alipayConfig.signType = signType;
        alipayConfig.appId = appId;
        alipayConfig.merchantPrivateKey = merchantPrivateKey;
        alipayConfig.alipayPublicKey = alipayPublicKey;
        alipayConfig.notifyUrl = notifyUrl;
        return alipayConfig;
    }
}

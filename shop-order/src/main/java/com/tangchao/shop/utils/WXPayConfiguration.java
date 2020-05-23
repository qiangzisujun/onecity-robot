package com.tangchao.shop.utils;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created: with idea
 *
 * @AUTHOR: MR HO
 * @USER: Administrator
 * @DATE: 2019/8/3
 * @TIME: 10:48
 * @Description:
 */
@Configuration
@PropertySource("classpath:wxPayConfig.properties")
public class WXPayConfiguration {

    @Bean
    @ConfigurationProperties("wx") //指明前缀
    public PayConfig payConfig() {
        return new PayConfig();
    }

    @Bean
    public WXPay wxPay() {
        return new WXPay(payConfig(), WXPayConstants.SignType.MD5);
    }
}

package com.tangchao.web.test;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.tangchao.shop.util.MD5util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

/**
 * @Class PayTest
 * @Description TODO
 * @Author Aquan
 * @Date 2020/1/11 17:48
 * @Version 1.0
 **/
@Slf4j
public class PayTest {

    // TODO: 2020/1/13 shopId:9dc95920359d281916b10c1fef669d56  Key:27DF42C6A08A2B50760A5ED13B731468
    @Test
    public void Test() {
        String key = "27DF42C6A08A2B50760A5ED13B731468";
        // String url = "http://192.168.0.218:8199/index/getBannerList";
        String payUrl = "http://pay.congmingpay.com/pay/buypay.do";
        String shopId = "a2431747072c6bcfe457024259bc273d";//9dc95920359d581916b10c1fef669d56
        String money = "1";
        String orderId = RandomUtil.randomString(18);
        String redirectUrl = "https://baidu.com";

        String code = "money=" + money + "&orderId=" + orderId + "&shopId=" + shopId + "&key=" + key;
        log.warn("code:" + code);

        String sign = DigestUtils.md5Hex(code).toUpperCase();
        log.warn("sign:" + sign);

        String sign2 = MD5util.toMD5(code);
        log.warn("sign2:" + sign2);

        String url = payUrl + "?shopId=" + shopId + "&money=" + money + "&orderId=" + orderId + "&sign=" + sign + "&redirectUrl=" + redirectUrl;
        log.warn("URL:" + url);

        // String body = HttpRequest.get(url)
        //                          .timeout(20000)
        //                          .execute()
        //                          .body();
        // log.warn(body);

        String result1= HttpUtil.get(url);
        log.warn(result1);
    }

}

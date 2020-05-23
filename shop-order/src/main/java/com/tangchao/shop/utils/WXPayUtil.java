package com.tangchao.shop.utils;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import com.tangchao.shop.util.MD5util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * @Class WXPayUtil
 * @Description TODO
 * @Author Aquan
 * @Date 2020/1/14 17:40
 * @Version 1.0
 **/
@Slf4j
public class WXPayUtil {

    private final static String key = "27DF42C6A08A2B50760A5ED13B731468";
    private final static String payUrl = "http://pay.congmingpay.com/pay/buypay.do";
    private final static String shopId = "a2431747072c6bcfe457024259bc273d";

    public static String pay(String money, String orderId, String redirectUrl, String returnUrl) {

        String code = "money=" + money + "&orderId=" + orderId + "&shopId=" + shopId + "&key=" + key;
        log.warn("code:" + code);

        String sign = DigestUtils.md5Hex(code).toUpperCase();
        log.warn("sign:" + sign);

        String url = payUrl + "?shopId=" + shopId + "&money=" + money + "&orderId=" + orderId + "&sign=" + sign + "&redirectUrl=" + redirectUrl + "&redirectNumber=1&returnUrl=" + returnUrl;
        log.warn("URL:" + url);

        // String body = HttpRequest.get(url)
        //         .timeout(20000)
        //         .execute()
        //         .body();
        // log.warn(body);
        return url;
    }

    public static Boolean checkSign( String sign, String money, String orderId) {
        String str = "money=" + money + "&orderId=" + orderId + "&result_code=SUCCESS&shopId=" + shopId;
        String checkSign = DigestUtils.md5Hex(str).toUpperCase();
        log.warn("sign:" + checkSign);
        if(sign.equals(checkSign)) {
            return true;
        }
        return false;
    }


}

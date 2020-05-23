package com.tangchao.shop.utils;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.tangchao.common.utils.IPAddressUtil;
import com.tangchao.shop.pojo.ShopOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created: with idea
 *
 * @AUTHOR: MR HO
 * @USER: Administrator
 * @DATE: 2019/8/3
 * @TIME: 10:55
 * @Description:
 */
@Slf4j
@Component
public class PayHelper {

    @Autowired
    private WXPay wxPay;

    @Autowired
    private PayConfig config;

    public Map<String, String> unifiedOrder(HttpServletRequest request, ShopOrder order, String description) {

        Map<String, String> result = new HashMap<>();

        BigDecimal totalFee = new BigDecimal(order.getTotalPay());
        BigDecimal price = totalFee.multiply(new BigDecimal(100));
        int total_fee = price.intValue();
        try {
            HashMap<String, String> data = new HashMap<>();
            //描述
            data.put("body", description);
            //订单号
            data.put("out_trade_no", order.getOrderNo().toString());
            //货币（默认就是人民币）
            data.put("fee_type", "CNY");
            //总金额
            data.put("total_fee", price.toString());
            //回调地址
            String domain=request.getServerName();
            String notifyUrl="https://"+domain+"/api/order/payNotify";

            data.put("notify_url", notifyUrl);
            //交易类型为H5
            data.put("trade_type", "MWEB");
            //随机字符串
            String nonceStr = UUID.randomUUID().toString().replace("-", ""); //
            data.put("nonce_str", nonceStr);
            String ip = IPAddressUtil.getClientIpAddress(request);
            if (ip.equals("0:0:0:0:0:0:0:1")) {
                ip = "121.32.145.168";
            }
            //调用微信支付的终端ip
            data.put("spbill_create_ip", ip);

            String scene_info = "{'h5_info': {'type':'Wap','wap_url': 'www.tckj365.com','wap_name': '斑马'}}";
            data.put("scene_info", scene_info);
            //利用wxPay工具，完成下单
            result = wxPay.unifiedOrder(data);


            //通信失败
            if (WXPayConstants.FAIL.equals(result.get("return_code"))) {
                log.error("【微信下单】与微信通信失败，失败信息：{}", result.get("return_msg"));
                throw new Exception("支付失败");
            }

            //下单失败
            if (WXPayConstants.FAIL.equals(result.get("result_code"))) {
                log.error("【微信下单】创建预交易订单失败，错误码：{}，错误信息：{}",
                        result.get("err_code"), result.get("err_code_des"));
                throw new Exception("支付失败");
            }
            String timeStamp = Long.toString(new Date().getTime());
            RequestHandler reqHandler = new RequestHandler(request, null);
            SortedMap<Object, Object> params = new TreeMap<Object, Object>();
            params.put("appId", config.getAppID());
            params.put("timeStamp", timeStamp);
            params.put("nonceStr", nonceStr);
            params.put("package", "prepay_id=" + result.get("prepay_id"));
            params.put("signType", "MD5");
            String paySign = reqHandler.createSign("UTF-8", params, config.getKey());
            result.put("paySign", paySign);
            result.put("orderId", order.getOrderId().toString());
            result.put("timeStamp", timeStamp);
            result.put("nonceStr", nonceStr);
        } catch (Exception e) {
            log.error("【微信下单】创建预交易订单异常", e);
            return null;
        }
        return result;
    }
}

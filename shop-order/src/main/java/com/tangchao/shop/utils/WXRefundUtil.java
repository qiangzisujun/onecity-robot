package com.tangchao.shop.utils;

import com.tangchao.common.utils.HttpUtil;
import com.tangchao.common.utils.JsonUtils;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashMap;
import java.util.Map;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2020/3/20 9:45
 */
public class WXRefundUtil {

    private final static String key = "27DF42C6A08A2B50760A5ED13B731468";
    private final static String payUrl = "http://pay.congmingpay.com/pay/refund.do";
    private final static String shopId = "a2431747072c6bcfe457024259bc273d";

    public static Map<String,Object> wxRefund(Map<String,Object> params){
        Map<String,Object> map=new HashMap<>();
        map.put("shopId",shopId);
        map.putAll(params);

        StringBuffer str=new StringBuffer();
        map.keySet().stream()
                .filter(k->map.get(k)!=null&&!map.get(k).equals(""))
                .forEach(k->str.append(String.format("%s=%s&",k,map.get(k))));
        String sign = DigestUtils.md5Hex(str.toString()+"key="+key).toUpperCase();
        map.put("sign",sign);

        Map<String,Object> resultMap=new HashMap<>();
        String result= HttpUtil.http(payUrl,map);
        resultMap= JsonUtils.parseMap(result,String.class,Object.class);
        return resultMap;
    }
}

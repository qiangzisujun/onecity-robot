package com.tangchao.shop.utils;

import cn.hutool.json.JSONObject;
import com.tangchao.common.utils.HttpUtil;
import com.tangchao.common.utils.SignUtil;
import com.tangchao.shop.pojo.CPNPApi;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.util.*;


/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/12/16 20:10
 */
public class PayByCPNPUtil {

    private static final Logger logger = LoggerFactory.getLogger(PayUtil.class);

    public static String UID = "172000320";

    public static String NOTIFY_URL = "http://api.rn193.cn/user/userPaymentNotifyPay_CPNP";

    public static String RETURN_URL = "http://api.rn193.cn/user/paySuccessView";

    public static String BASE_URL = " http://35.229.224.168:6060/api/createtrade";

    public static String TOKEN = "13800e94a0144ef3a70aeacb451f932b";

    public static Map<String,Object> createOrder(Double totalFee,String tradeNo){
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("uid", UID);
        paramMap.put("orderNo", tradeNo);
        paramMap.put("body", "缘购充值");
        paramMap.put("price", totalFee);
        paramMap.put("backUrl", RETURN_URL);
        paramMap.put("postUrl", NOTIFY_URL);
        paramMap.put("type",  "unionpay");


        SortedMap<Object, Object> params = new TreeMap<Object, Object>();
        params.put("uid", UID);
        params.put("orderNo", tradeNo);
        params.put("body", "缘购充值");
        params.put("price", totalFee.toString());
        params.put("backUrl", RETURN_URL);
        params.put("postUrl", NOTIFY_URL);
        params.put("type", "unionpay");

        paramMap.put("sign", SignUtil.createSign("UTF-8",params,TOKEN));

        Map<String,Object> resultMap=new HashMap<>();
        String result= HttpUtil.http(BASE_URL,paramMap);
        JSONObject jsonObject = new JSONObject(result);
        String resultCode = jsonObject.getStr("resultCode");
        if (resultCode.equals("success")){
            String url = jsonObject.getStr("payUrl");
            resultMap.put("payUrl",url);
            logger.info(url);
            resultMap.put("resultCode","success");
        }else{
            resultMap.put("message","支付失败!");
            resultMap.put("resultCode","fail");
        }
        return resultMap;
    }

    public static boolean checkPaySign(SortedMap<Object, Object> map) throws Exception {
         Map<String,String> params = new HashMap<>();
        if (!StringUtils.isBlank(map.get("resultCode").toString())){
            params.put("resultCode", map.get("resultCode").toString());
            logger.info("支付回来的状态："+map.get("resultCode"));
        }

        if (!StringUtils.isBlank(map.get("message").toString())){
            params.put("message", map.get("message").toString());
            logger.info("支付回来的消息："+map.get("message").toString());
        }

        if (!StringUtils.isBlank(map.get("orderNo").toString())){
            params.put("orderNo", map.get("orderNo").toString());
            logger.info("支付回来的客户订单号："+map.get("orderNo"));
        }

        if (!StringUtils.isBlank(map.get("sysOrderNo").toString())){
            params.put("sysOrderNo", map.get("sysOrderNo").toString());
            logger.info("支付回来的系统订单号："+map.get("sysOrderNo"));
        }

        if (!StringUtils.isBlank(map.get("threeOrderNo").toString())){
            params.put("threeOrderNo", map.get("threeOrderNo").toString());
            logger.info("支付回来的支付订单号："+map.get("threeOrderNo"));
        }

        if (!StringUtils.isBlank(map.get("price").toString())){
            params.put("price", map.get("price").toString());
            logger.info("支付回来的订单金额："+map.get("price"));
        }

        if (!StringUtils.isBlank(map.get("payPrice").toString())){
            params.put("payPrice", map.get("payPrice").toString());
            logger.info("支付回来的金额："+map.get("payPrice"));
        }

        String signMap=null;
        if (!StringUtils.isBlank(map.get("sign").toString())){
            logger.info("支付回来的sign："+map.get("sign"));
            signMap=map.get("sign").toString();
        }
        String sign=getSign(params);
        logger.info("本地的sign："+sign);
        return sign.equals(signMap);
    }

    public static String getSign(Map<String, String> params) throws Exception {
        Set<String> keySet = params.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            if (params.get(k).trim().length() > 0) // 参数值为空，则不参与签名
                sb.append(k).append("=").append(params.get(k).trim()).append("&");
        }
        sb.append("key=").append(TOKEN);
        return md5((sb.toString())).toUpperCase();
    }


    public static String md5(String encryptStr) throws Exception {
        // md5
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(encryptStr.getBytes("UTF-8"));
        byte[] digest = md.digest();
        StringBuffer md5 = new StringBuffer();
        for (int i = 0; i < digest.length; i++) {
            md5.append(Character.forDigit((digest[i] & 0xF0) >> 4, 16));
            md5.append(Character.forDigit((digest[i] & 0xF), 16));
        }
        encryptStr = md5.toString();
        return encryptStr;
    }
}

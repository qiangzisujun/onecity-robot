package com.tangchao.shop.utils;

import com.tangchao.common.utils.HttpUtil;
import com.tangchao.common.utils.JsonUtils;
import com.tangchao.common.utils.SignUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static com.tangchao.common.utils.MD5Util.createMD5;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2020/3/6 10:30
 */
public class PayHelperByBOB {

    private static final Logger logger = LoggerFactory.getLogger(PayHelper.class);

    public static String mchid = "1584324971";

    public static String RETURN_URL = "http://hao.banmatongxiao.com/pages/order/payafter?number=";

    public static String URL_PREFIX = "https://paybob.cn/api/cashier";

    public static String KEY = "wv06wj1NDwBquw1P";

    public static List<String> MCHIDLIST = new ArrayList<>(Arrays.asList("1584324971","1584880271"));

    public static Map<String,String> KEYMAP=new HashMap<String, String>(){
        {
            put("1584324971","wv06wj1NDwBquw1P");
            put("1584880271","vl5GPjVPPlcCz5XD");
        }
    };

    public static Map<String,String> payOrder(Double totalFee,String tradeNo,String notifyURL,Long totalIntegral) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Random random = new Random();
        int index= random.nextInt(MCHIDLIST.size());
        String mchid=MCHIDLIST.get(index);
        String key=KEYMAP.get(mchid);
        Map<String, String> params = new HashMap<>();
        params.put("mchid", mchid);
        params.put("total_fee", SignUtil.convertAmount(totalFee.toString()));
        params.put("out_trade_no",tradeNo);
        params.put("callback_url", RETURN_URL+totalIntegral);
        params.put("notify_url", notifyURL);
        params.put("auto", "1");
        params.put("hide", "1");
        params.put("body", "薇蜜购");

        StringBuilder queryString = new StringBuilder();
        // 获取key集合
        params.keySet().stream()
                // 过滤空值
                .filter(k -> params.get(k) != null && !params.get(k).trim().equals(""))
                // 排序
                .sorted()
                // 循环key拼接查询字符串
                .forEach(k -> queryString.append(String.format("%s=%s&", k, params.get(k))));
        // MD5签名
        String sign = createMD5(queryString.toString() + "key=" + key);

        /**
         * 拼接支付URL
         */
        String url = URL_PREFIX + "?" + queryString.toString() + "sign=" + sign;
        Map<String,String> paramMap=new HashMap<>();
        paramMap.put("payUrl",url);
        paramMap.put("resultCode","success");
        paramMap.put("key",key);
        paramMap.putAll(params);
        return paramMap;
    }

    public static boolean checkPaySign(SortedMap<Object, Object> map) throws Exception {
        SortedMap<Object, Object> params = new TreeMap<>();

        if (!StringUtils.isBlank(map.get("return_code").toString())){
            params.put("return_code", map.get("return_code").toString());
            logger.info("支付回来的状态："+map.get("return_code"));
        }

        if (!StringUtils.isBlank(map.get("total_fee").toString())){
            params.put("total_fee", map.get("total_fee").toString());
            logger.info("支付回来的金额："+map.get("total_fee").toString());
        }

        if (!StringUtils.isBlank(map.get("out_trade_no").toString())){
            params.put("out_trade_no", map.get("out_trade_no").toString());
            logger.info("用户端自主生成的订单号："+map.get("out_trade_no"));
        }

        if (!StringUtils.isBlank(map.get("payjs_order_id").toString())){
            params.put("payjs_order_id", map.get("payjs_order_id").toString());
            logger.info("支付回来的平台订单号："+map.get("payjs_order_id"));
        }

        if (!StringUtils.isBlank(map.get("transaction_id").toString())){
            params.put("transaction_id", map.get("transaction_id").toString());
            logger.info("微信用户手机显示订单号："+map.get("transaction_id"));
        }
        if (!StringUtils.isBlank(map.get("time_end").toString())){
            params.put("time_end", map.get("time_end").toString());
            logger.info("支付成功时间："+map.get("time_end"));
        }
        if (!StringUtils.isBlank(map.get("openid").toString())){
            params.put("openid", map.get("openid").toString());
            logger.info("微信openid："+map.get("openid"));
        }
        if (!StringUtils.isBlank(map.get("mchid").toString())){
            params.put("mchid", map.get("mchid").toString());
            logger.info("商户号："+map.get("mchid"));
        }

        String signMap=null;
        if (!StringUtils.isBlank(map.get("sign").toString())){
            logger.info("支付回来的sign："+map.get("sign"));
            signMap=map.get("sign").toString();
        }

        String key=null;
        if (!StringUtils.isBlank(map.get("key").toString())){
            logger.info("key："+map.get("key"));
            key=map.get("key").toString();
        }


        String sign= SignUtil.createSign("UTF-8",params,key);
        logger.info("本地的sign："+sign);
        return sign.equals(signMap);
    }

    public static Map<String,Object> wxRefund(Map<String,Object> params){
        String key=params.get("key").toString();
        params.remove("key");
        StringBuffer str=new StringBuffer();
        params.keySet().stream()
                .filter(k->params.get(k)!=null&&!params.get(k).equals(""))
                .sorted()
                .forEach(k->str.append(String.format("%s=%s&",k,params.get(k))));

        String sign = DigestUtils.md5Hex(str.toString()+"key="+key).toUpperCase();
        params.put("sign",sign);

        String payUrl="https://paybob.cn/api/refund";

        Map<String,Object> resultMap=new HashMap<>();
        String result= HttpUtil.http(payUrl,params);
        resultMap= JsonUtils.parseMap(result,String.class,Object.class);
        return resultMap;
    }

   /* public static void main(String[] args) {
        for (int i=0;i<20;i++){

            Random random = new Random();
            int index= random.nextInt(MCHIDLIST.size());
            String mchid=MCHIDLIST.get(index);

            String key=KEYMAP.get(mchid);
            System.out.println(key);
        }
    }*/
}

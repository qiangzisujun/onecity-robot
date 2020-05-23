package com.tangchao.shop.utils;

import com.tangchao.common.utils.HttpUtil;
import com.tangchao.common.utils.MD5Util;
import com.tangchao.shop.pojo.GLpayApi;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/12/9 15:03
 */
public class PayHelper {

    private static final Logger logger = LoggerFactory.getLogger(PayHelper.class);

    public static String UID = "7832";

    public static String NOTIFY_URL = "http://api.rn193.cn/user/userPaymentNotifyPay";

    public static String RETURN_URL = "http://www.rn193.cn/#/pages/yygpage/success";

    public static String BASE_URL = "http://pay.wsdy.com.cn";

    public static String TOKEN = "yLp44i3ZE4y45eDW4wLt5et43sPWW5wE";



    public static Map<String,Object> payOrder(Map<String,Object> map) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("uid", UID);
        paramMap.put("notify_url", NOTIFY_URL);
        paramMap.put("return_url", RETURN_URL);
        paramMap.putAll(map);
        paramMap.put("goodsname", "缘购充值!");
        paramMap.put("key", getKey(paramMap));

        String result= HttpUtil.http(BASE_URL,paramMap);
        Map<String,Object> resultMap=new HashMap<>();
        System.out.println(result);
        Document doc = Jsoup.parse(result);
        String rows =doc.getElementById("money").text();
        if (!StringUtils.isEmpty(rows)){
            resultMap.put("money",rows);
        }
        List<String> srcList = new ArrayList<String>(); //用来存储获取到的图片地址
        Pattern p = Pattern.compile("<(img|IMG)(.*?)(>|></img>|/>)");//匹配字符串中的img标签
        Matcher matcher = p.matcher(result);
        boolean hasPic = matcher.find();
        if(hasPic == true)//判断是否含有图片
        {
            while(hasPic) //如果含有图片，那么持续进行查找，直到匹配不到
            {
                String group = matcher.group(2);//获取第二个分组的内容，也就是 (.*?)匹配到的
                Pattern srcText = Pattern.compile("(src|SRC)=(\"|\')(.*?)(\"|\')");//匹配图片的地址
                Matcher matcher2 = srcText.matcher(group);
                if( matcher2.find() )
                {
                    srcList.add( matcher2.group(3) );//把获取到的图片地址添加到列表中
                }
                hasPic = matcher.find();//判断是否还有img标签
            }

        }

        if (srcList.size()>=2){
            resultMap.put("payUrl",srcList.get(1));
        }
        return resultMap;
    }

    public static String getKey(Map<String, Object> remoteMap) {
        String key = "";
        if (null != remoteMap.get("goodsname")) {
            key += remoteMap.get("goodsname");
        }
        if (null != remoteMap.get("istype")) {
            key += remoteMap.get("istype");
        }
        if (null != remoteMap.get("notify_url")) {
            key += remoteMap.get("notify_url");
        }
        if (null != remoteMap.get("orderid")) {
            key += remoteMap.get("orderid");
        }
        if (null != remoteMap.get("orderuid")) {
            key += remoteMap.get("orderuid");
        }
        if (null != remoteMap.get("price")) {
            key += remoteMap.get("price");
        }
        if (null != remoteMap.get("return_url")) {
            key += remoteMap.get("return_url");
        }
        key += TOKEN;
        if (null != remoteMap.get("uid")) {
            key += remoteMap.get("uid");
        }
        return MD5Util.encryption(key);
    }

    public static boolean checkPayKey(GLpayApi payAPI) {
        String key = "";
        if (!StringUtils.isBlank(payAPI.getOrderid())) {
            logger.info("支付回来的订单号：" + payAPI.getOrderid());
            key += payAPI.getOrderid();
        }
        if (!StringUtils.isBlank(payAPI.getOrderuid())) {
            logger.info("支付回来的支付记录的ID：" + payAPI.getOrderuid());
            key += payAPI.getOrderuid();
        }
        if (!StringUtils.isBlank(payAPI.getPlatform_trade_no())) {
            logger.info("支付回来的平台订单号：" + payAPI.getPlatform_trade_no());
            key += payAPI.getPlatform_trade_no();
        }
        if (!StringUtils.isBlank(payAPI.getPrice())) {
            logger.info("支付回来的价格：" + payAPI.getPrice());
            key += payAPI.getPrice();
        }
        if (!StringUtils.isBlank(payAPI.getRealprice())) {
            logger.info("支付回来的真实价格：" + payAPI.getRealprice());
            key += payAPI.getRealprice();
        }
        logger.info("支付回来的Key：" + payAPI.getKey());
        key += TOKEN;
        logger.info("我们自己拼接的Key：" + MD5Util.encryption(key));
        return payAPI.getKey().equals(MD5Util.encryption(key));
    }

    public static String getOrderIdByUUId() {
        int machineId = 1;// 最大支持1-9个集群机器部署
        int hashCodeV = UUID.randomUUID().toString().hashCode();
        if (hashCodeV < 0) {// 有可能是负数
            hashCodeV = -hashCodeV;
        }
        // 0 代表前面补充0;d 代表参数为正数型
        return machineId + String.format("%01d", hashCodeV);
    }

    public static String createKEY(String charSet, SortedMap<Object, Object> parameters, String parentKey) {
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            sb.append(v+"+");
        }
        sb.append(parentKey);
        logger.info(sb.toString());
        String sign = MD5Util.MD5Encode(sb.toString(), charSet).toLowerCase();
        return sign;
    }
}

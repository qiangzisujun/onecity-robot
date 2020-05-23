package com.tangchao.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/12/16 20:13
 */
public class SignUtil {

    public static String createSign(String charSet, SortedMap<Object, Object> parameters, String parentKey) {
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (null != v && !"".equals(v)
                    && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + parentKey);//xxxxxx换成你的API密钥
        System.out.println("sb.toString():" + sb.toString());
        String sign = MD5Util.MD5Encode(sb.toString(), charSet).toUpperCase();
        return sign;
    }
    public static String convertAmount(String trxamt) {
        BigDecimal fee = new BigDecimal(trxamt);
        //  四舍五入，精确2位小数
        fee = fee.setScale(2, BigDecimal.ROUND_HALF_UP);
        //  转换单位为：分
        fee = fee.multiply(new BigDecimal(100));
        //  取整，过滤小数部分
        fee = fee.setScale(0, RoundingMode.DOWN);
        String amount = String.valueOf(fee);
        return amount;
    }
}

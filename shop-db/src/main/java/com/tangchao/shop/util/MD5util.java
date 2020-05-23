package com.tangchao.shop.util;

import java.security.MessageDigest;

/**
 * @Class MD5util
 * @Description TODO
 * @Author Aquan
 * @Date 2020/1/13 10:52
 * @Version 1.0
 **/
public class MD5util {


    public static String toMD5(String plainText) {
        StringBuffer buf = new StringBuffer("");
        try {
            // 生成实现指定摘要算法的 MessageDigest 对象。
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 使用指定的字节数组更新摘要。
            md.update(plainText.getBytes());
            // 通过执行诸如填充之类的最终操作完成哈希计算。
            byte b[] = md.digest();
            // 生成具体的md5密码到buf数组(32位小写)
            int i;

            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0){
                    i += 256;
                }
                if (i < 16){
                    buf.append("0");
                }else{
                    buf.append(Integer.toHexString(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buf.toString();
    }

    public static byte[] toMD5byte(byte[] plainText) {
        StringBuffer buf = new StringBuffer("");
        try {
            // 生成实现指定摘要算法的 MessageDigest 对象。
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 使用指定的字节数组更新摘要。
            md.update(plainText);
            // 通过执行诸如填充之类的最终操作完成哈希计算。
            byte b[] = md.digest();
            // 生成具体的md5密码到buf数组
            int i;

            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buf.toString().getBytes();
    }

    /**
     * 解决php与javaMD5加密不同 获取MD5加密后的字符串
     *
     * @param str
     *            明文
     * @return 加密后的字符串
     * @throws Exception
     */
    public static String getMD5(String str) throws Exception {
        /** 创建MD5加密对象 */
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        /** 进行加密 */
        md5.update(str.getBytes("GBK"));
        /** 获取加密后的字节数组 */
        byte[] md5Bytes = md5.digest();
        String res = "";
        for (int i = 0; i < md5Bytes.length; i++) {
            int temp = md5Bytes[i] & 0xFF;
            if (temp <= 0XF) { // 转化成十六进制不够两位，前面加零
                res += "0";
            }
            res += Integer.toHexString(temp);
        }
        return res;
    }

}

package com.tangchao.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * title: RandomUtil
 * package: com.chichao.eyyg.common.util
 * description: 随机数工具类
 * author: 王飞腾
 * date: 2018/6/22
 */
public final class RandomUtil {

    //  日志工具
    private static final Logger LOGGER = LoggerFactory.getLogger(RandomUtil.class);

    //  随机类
    private static final Random RANDOM = new Random();

    private static final String NUMBER = "0123456789";


    /**
     * 随机生成一个整数
     *
     * @param length 长度
     * @return 随机数
     */
    public static Integer generateInteger(int length) {
        double min = Math.pow(10, length - 1);
        double max = Math.pow(10, length);
        return (int) (RANDOM.nextInt((int) (max - min)) + min);
    }

    /**
     * 生成一个由纳秒值拼上一个随机数的字符
     *
     * @param length 随机数长度
     * @return 正整数
     */
    public static String generateLongByDateTime(int length) {
        //  获取当前毫秒值
        //long dateTime = System.nanoTime();
        long dateTime = System.currentTimeMillis();
        //  生成随机数
        String randonNum = generateInteger(length).toString();
        return dateTime + randonNum;
    }


    private static final String SNALL_ABC = "qazwsxedcvfrtgbnhyujmkiolp";

    /**
     * 生成一个全小写的字符
     *
     * @param length 长度
     * @return 字符
     */
    public static String generateSmallAbc(int length) {
        return generateString(SNALL_ABC, length);
    }

    private static final String BIG_ABC = "QAZXSWEDCVFRTGBNHYUJMKIOLP";

    /**
     * 生成一个全大写的字符
     *
     * @param length 长度
     * @return 字符
     */
    public static String generateBigAbc(int length) {
        return generateString(BIG_ABC, length);
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     * 从模板中随机挑选几个字符进行组合
     *
     * @param template 模板
     * @param length   长度
     * @return 随机字符
     */
    private static String generateString(String template, int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomNum = RANDOM.nextInt(template.length());
            stringBuilder.append(template.substring(randomNum, randomNum + 1));
        }
        return stringBuilder.toString();
    }

    /**
     * 根据 number 生成唯一字符串
     *
     * @param length 结果的最小长度
     * @param number 数字
     * @return 唯一字符串
     */
    public static String generateUniqueCode(int length, long number) {
        if (length <= 0) {
            return "";
        }
        String result = Long.toString(number, 36);
        //  长度不足自动补0
        if (result.length() < length) {
            StringBuilder sb = new StringBuilder();
            for (int i = result.length(); i < length; i++) {
                sb.append("0");
            }
            result = sb.append(result).toString();
        }
        return result;
    }

    /**
     * 生成随机数字和字母
     *
     * @param length 长度
     * @return result
     */
    public static String getStringRandom(int length) {
        String val = "";
        Random random = new Random();
        //参数length，表示生成几位随机数  
        for (int i = 0; i < length; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //输出字母还是数字  
            if ("char".equalsIgnoreCase(charOrNum)) {
                //输出是大写字母还是小写字母  
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char) (random.nextInt(26) + temp);
            } else if ("num".equalsIgnoreCase(charOrNum)) {
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }


}

package com.tangchao.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Encoder;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.persistence.Convert;
import javax.swing.ImageIcon;

/**
 * @Class WaterMarkUtil
 * @Description TODO 图片合成工具类
 * @Author Aquan
 * @Date 2019/11/28 11:26
 * @Version 1.0
 **/
public class WaterMarkUtil {

    private final static Logger logger = LoggerFactory.getLogger(WaterMarkUtil.class);

    /**
     * 给图片添加水印
     *
     * @param iconPath
     *            水印图片路径
     * @param srcImgPath
     *            源图片路径
     * @param targerPath
     *            目标图片路径
     */
    public static void markImageByIcon(String iconPath, String srcImgPath, String targerPath) {
        markImageByIcon(iconPath, srcImgPath, targerPath, null);
    }

    /**
     * 给图片添加水印、可设置水印图片旋转角度
     *
     * @param iconPath
     *            水印图片路径
     * @param srcImgPath
     *            源图片路径
     * @param targerPath
     *            目标图片路径
     * @param degree
     *            水印图片旋转角度
     */
    public static void markImageByIcon(String iconPath, String srcImgPath, String targerPath, Integer degree) {
        OutputStream os = null;
        try {
            Image srcImg = ImageIO.read(new File(srcImgPath));

            BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null), srcImg.getHeight(null),
                    BufferedImage.TYPE_INT_RGB);

            // 得到画笔对象
            // Graphics g= buffImg.getGraphics();
            Graphics2D g = buffImg.createGraphics();

            // 设置对线段的锯齿状边缘处理
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            g.drawImage(srcImg.getScaledInstance(srcImg.getWidth(null), srcImg.getHeight(null), Image.SCALE_SMOOTH), 0,
                    0, null);

            if (null != degree) {
                // 设置水印旋转
                g.rotate(Math.toRadians(degree), (double) buffImg.getWidth() / 2, (double) buffImg.getHeight() / 2);
            }

            // 水印图象的路径 水印一般为gif或者png的，这样可设置透明度
            ImageIcon imgIcon = new ImageIcon(iconPath);

            // 得到Image对象。
            Image img = imgIcon.getImage();

            float alpha = 1f; // 透明度
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));

            // 表示水印图片的位置
            g.drawImage(img, 270, 734, null);

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

            g.dispose();

            os = new FileOutputStream(targerPath);

            // 生成图片
            ImageIO.write(buffImg, "JPG", os);

            // System.out.println("图片完成添加Icon印章。。。。。。");
            logger.warn("图片生成完成 >>> " + targerPath);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 背景图和二维码合成
     * @param QRCode 二维码
     * @param srcImgPath //背景图片链接
     * @param abscissa 二维码在背景图中的横坐标
     * @param ordinate 二维码在背景图中的纵坐标
     */
    public static String markImageByIcon(BufferedImage QRCode,String srcImgPath,Integer abscissa,Integer ordinate) {
        OutputStream os = null;
        try {

            //new一个URL对象
            URL url = new URL(srcImgPath);
            //打开链接
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            //设置请求方式为"GET"
            conn.setRequestMethod("GET");
            //超时响应时间为5秒
            conn.setConnectTimeout(5 * 1000);
            //通过输入流获取图片数据
            InputStream inStream = conn.getInputStream();
            Image srcImg = ImageIO.read(inStream);//读取背景图

            BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null), srcImg.getHeight(null),
                    BufferedImage.TYPE_INT_RGB);

            // 得到画笔对象
            // Graphics g= buffImg.getGraphics();
            Graphics2D g = buffImg.createGraphics();

            // 设置对线段的锯齿状边缘处理
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            g.drawImage(srcImg.getScaledInstance(srcImg.getWidth(null), srcImg.getHeight(null), Image.SCALE_SMOOTH), 0,
                    0, null);

            // 水印图象的路径 水印一般为gif或者png的，这样可设置透明度
            ImageIcon imgIcon = new ImageIcon(QRCode);//二维码


            // 得到Image对象。
            Image img = imgIcon.getImage();
            float alpha = 1f; // 透明度
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
            // 表示水印图片的位置
            g.drawImage(img, abscissa, ordinate, null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
            g.dispose();
            //转化
            ByteArrayOutputStream baos = new ByteArrayOutputStream();//io流
            ImageIO.write(buffImg, "png", baos);//写入流中
            byte[] bytes = baos.toByteArray();//转换成字节
            BASE64Encoder encoder = new BASE64Encoder();
            //转换成base64串
            String png_base = encoder.encodeBuffer(bytes).trim();
            //删除 \r\n
            String str = png_base.replaceAll("\n", "").replaceAll("\r", "");
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void main(String[] args) throws Exception {

        /**
         * 给图片添加水印、可设置水印图片旋转角度
         *
         * @param iconPath
         *            水印图片路径
         * @param srcImgPath
         *            源图片路径
         * @param targerPath
         *            目标图片路径
         * @param degree
         *            水印图片旋转角度
         */
        //markImageByIcon("F:\\qrcode3.png","F:\\logo.jpg","F:\\test.jpg",null);
        BufferedImage image = QRCodeUtil.createImage("http://www.baidu.com", null, true);
        String str=markImageByIcon(image,"http://test.banmatongxiao.com/storagegroup/M00/00/26/rBKVLF3nboGAdNO1AAD9fvtjuhQ192.jpg",270,734);
        //String fileContent = Convert.ToBase64String(File.ReadAllBytes(filePath));
    }
}

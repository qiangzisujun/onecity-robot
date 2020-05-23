package com.tangchao.user.test;

import com.alibaba.druid.util.Base64;
import com.github.binarywang.utils.qrcode.MatrixToImageWriter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.tangchao.common.utils.HttpUtil;
import com.tangchao.common.utils.PasswordUtil;
import com.tangchao.common.utils.QRCodeUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Test {

    public static String getImage2(String codeUrl, String url, String destPath, String fileName) {
        BufferedImage image = null;
        String png_base64 = null;
        String file = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "_app" + ".jpg";
        try {
            //生成带有logo的二维码图片 其实这儿就可以存储
            //本文演示全过程
            image = encode("666", null, false);
            //转化
            ByteArrayOutputStream baos = new ByteArrayOutputStream();//io流
            ImageIO.write(image, "png", baos);//写入流中
            byte[] bytes = baos.toByteArray();//转换成字节
            BASE64Encoder encoder = new BASE64Encoder();
            //转换成base64串

            String png_base = encoder.encodeBuffer(bytes).trim();
            //删除 \r\n

            png_base64 = png_base.replaceAll("\n", "").replaceAll("\r", "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //将base64转文件  目标文件得位置   64得码  定义得文件名
        base64ToFile(destPath, png_base64, fileName);
        return fileName;
    }

    //转化并存储文件
    public static void base64ToFile(String destPath, String base64, String fileName) {
        File file = null;
        //创建文件目录
        String filePath = destPath;
        //创建文件目录
        File dir = new File(filePath);
//如果不存在则创建目录
        if (!dir.exists() && !dir.isDirectory()) {
            dir.mkdirs();
        }
        BufferedOutputStream bos = null;
        java.io.FileOutputStream fos = null;
        try {
            //创建的64得解码 注  用什么编码 就用什么解码 这里之前用的是BASE64Encoder 编码就用相应的Decoder
            BASE64Decoder decode = new BASE64Decoder();
            byte[] bytes = decode.decodeBuffer(base64);
            file = new File(filePath + "/" + fileName);
//开始写入文件
            fos = new java.io.FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //编码生成图片
    public static BufferedImage encode(String content, String imgPath, boolean needCompress) throws Exception {
        System.out.println(imgPath);
        BufferedImage image = QRCodeUtil.createImage(content, imgPath, needCompress);
        return image;

    }

    public static void main(String[] args) throws IOException {
      /* *//* //getImage2("666",null,"E:\\tmp","888.png");
        String str = "20";
        Integer num = Integer.parseInt(str);
        System.out.println(num);*//*
        String str= PasswordUtil.contrastPassword("admin","cz81513215.").toString();
        System.out.println(str);*/

        //网页版京东
       /* String result= HttpUtil.Get("https://item.jd.com/32255248024.html");
        Document doc = Jsoup.parse(result);
        Elements elements=doc.getElementById("spec-list").select("li");
        for (Element ele:elements) {
            String bookID=ele.select("img").attr("src");
            System.out.println(bookID);
        }

        Elements goodsName=doc.select("div[class=itemInfo-wrap]");
        System.out.println(goodsName.select("div[class=sku-name]").text());


        Elements price=doc.select("div[class=summary-price]");
        System.out.println(price);

        Elements img=doc.getElementById("J-detail-content").select("p");
        for (Element ele:img) {
            String bookID=ele.select("img").attr("src");
            System.out.println(bookID);
        }*/



        //

        /*String result= HttpUtil.Get("https://item.m.jd.com/product/35014695830.html");
        Document doc = Jsoup.parse(result);
        Elements elements=doc.select("ul[class=pic_list]").select("li");
        for (Element ele:elements) {
            String bookID=ele.select("img").attr("src");
            System.out.println(bookID);
        }

        String goodsName=doc.getElementById("itemName").html();
        System.out.println(goodsName);


        String price=doc.getElementById("priceSale").select("em").text();
        System.out.println(price);

        Elements img=doc.getElementById("commDesc").select("p");
        for (Element ele:img) {
            String bookID=ele.select("img").attr("src");
            System.out.println(bookID);
        }*/

        Runtime rt = Runtime.getRuntime();
        //window下的位置,linux下需改动
        Process p = rt.exec("D:\\phantomjs\\bin\\phantomjs.exe D:\\tmp\\code\\code.js " + "https://item.jd.com/32255248024.html");
        InputStream is = p.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuffer sbf = new StringBuffer();
        String tmp = "";
        while ((tmp = br.readLine()) != null) {
            sbf.append(tmp);
        }
        Document doc = Jsoup.parse(sbf.toString());
        String goodsName=doc.select("div[class='sku-name']").html();
        System.out.println(goodsName);

        Elements img=doc.getElementById("J-detail-content").select("p");
        for (Element ele:img) {
            String bookID=ele.select("img").attr("src");
            System.out.println(bookID);
        }

    }
}
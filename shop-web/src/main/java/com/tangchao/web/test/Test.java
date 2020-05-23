package com.tangchao.web.test;


import cn.hutool.core.util.ObjectUtil;
import com.tangchao.shop.pojo.Goods;

import java.io.*;
import java.util.Calendar;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class Test {
    /*public static void main(String[] args) {
        //System.out.println(1000%200);
        *//*List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put(i + "", i);
            list.add(map);
        }
        String str = JsonUtils.serialize(list);
        System.out.println(str);*//*
        String str="http://www.rn193.cn/goods/list";

      *//*  String domain = str.substring(0,str.indexOf(contextPath)+contextPath.length()+1).
                replaceAll("http://", "").replaceAll(contextPath, "").replaceAll("/", "");
*//*
        Integer openTimeMillis = 20 * 2 * 1000;
        Date awardTime = new Date(System.currentTimeMillis() - openTimeMillis.longValue());
        System.out.println(awardTime+"==="+System.currentTimeMillis());
    }*/

    public static String NO_DEFINE = "no_define";//未定义的字段
    public static String DEFAULT_DATE_PATTERN="yyyy年MM月dd日";//默认日期格式
    public static int DEFAULT_COLOUMN_WIDTH = 17;
    /**
     * 导出Excel 2007 OOXML (.xlsx)格式
     * @param title 标题行
     * @param headMap 属性-列头
     * @param jsonArray 数据集
     * @param datePattern 日期格式，传null值则默认 年月日
     * @param colWidth 列宽 默认 至少17个字节
     * @param out 输出流
     */
    // public static void exportExcelX(String title, Map<String, String> headMap, JSONArray jsonArray, String datePattern, int colWidth, OutputStream out) {
    //     if(datePattern==null) datePattern = DEFAULT_DATE_PATTERN;
    //     // 声明一个工作薄
    //     SXSSFWorkbook workbook = new SXSSFWorkbook(1000);//缓存
    //     workbook.setCompressTempFiles(true);
    //     //表头样式
    //     CellStyle titleStyle = workbook.createCellStyle();
    //     titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
    //     Font titleFont = workbook.createFont();
    //     titleFont.setFontHeightInPoints((short) 20);
    //     titleFont.setBoldweight((short) 700);
    //     titleStyle.setFont(titleFont);
    //     // 列头样式
    //     CellStyle headerStyle = workbook.createCellStyle();
    //     headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    //     headerStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
    //     headerStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
    //     headerStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
    //     headerStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
    //     headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
    //     Font headerFont = workbook.createFont();
    //     headerFont.setFontHeightInPoints((short) 12);
    //     headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
    //     headerStyle.setFont(headerFont);
    //     // 单元格样式
    //     CellStyle cellStyle = workbook.createCellStyle();
    //     cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    //     cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
    //     cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
    //     cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
    //     cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
    //     cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
    //     cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
    //     Font cellFont = workbook.createFont();
    //     cellFont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
    //     cellStyle.setFont(cellFont);
    //     // 生成一个(带标题)表格
    //     SXSSFSheet sheet = workbook.createSheet();
    //     //设置列宽
    //     int minBytes = colWidth<DEFAULT_COLOUMN_WIDTH?DEFAULT_COLOUMN_WIDTH:colWidth;//至少字节数
    //     int[] arrColWidth = new int[headMap.size()];
    //     // 产生表格标题行,以及设置列宽
    //     String[] properties = new String[headMap.size()];
    //     String[] headers = new String[headMap.size()];
    //     int ii = 0;
    //     for (Iterator<String> iter = headMap.keySet().iterator(); iter
    //             .hasNext();) {
    //         String fieldName = iter.next();
    //
    //         properties[ii] = fieldName;
    //         headers[ii] = headMap.get(fieldName);
    //
    //         int bytes = fieldName.getBytes().length;
    //         arrColWidth[ii] =  bytes < minBytes ? minBytes : bytes;
    //         sheet.setColumnWidth(ii,arrColWidth[ii]*256);
    //         ii++;
    //     }
    //     // 遍历集合数据，产生数据行
    //     int rowIndex = 0;
    //     for (Object obj : jsonArray) {
    //         if(rowIndex == 65535 || rowIndex == 0){
    //             if ( rowIndex != 0 ) sheet = workbook.createSheet();//如果数据超过了，则在第二页显示
    //
    //             SXSSFRow titleRow = sheet.createRow(0);//表头 rowIndex=0
    //             titleRow.createCell(0).setCellValue(title);
    //             titleRow.getCell(0).setCellStyle(titleStyle);
    //             sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headMap.size() - 1));
    //
    //             SXSSFRow headerRow = sheet.createRow(1); //列头 rowIndex =1
    //             for(int i=0;i<headers.length;i++)
    //             {
    //                 headerRow.createCell(i).setCellValue(headers[i]);
    //                 headerRow.getCell(i).setCellStyle(headerStyle);
    //
    //             }
    //             rowIndex = 2;//数据内容从 rowIndex=2开始
    //         }
    //         JSONObject jo = (JSONObject) JSONObject.toJSON(obj);
    //         SXSSFRow dataRow = sheet.createRow(rowIndex);
    //         for (int i = 0; i < properties.length; i++)
    //         {
    //             SXSSFCell newCell = dataRow.createCell(i);
    //
    //             Object o =  jo.get(properties[i]);
    //             String cellValue = "";
    //             if(o==null) cellValue = "";
    //             else if(o instanceof Date) cellValue = new SimpleDateFormat(datePattern).format(o);
    //             /*else if(o instanceof Float || o instanceof Double) {
    //             	double d = (double) o;
    //             	if(d%1==0)  cellValue=o.toString();
    //             	else cellValue= new BigDecimal(o.toString()).setScale(2,BigDecimal.ROUND_HALF_UP).toString();
    //             }*/
    //             else cellValue = o.toString();
    //             newCell.setCellValue(cellValue);
    //             newCell.setCellStyle(cellStyle);
    //         }
    //         rowIndex++;
    //     }
    //     // 自动调整宽度
    //     /*for (int i = 0; i < headers.length; i++) {
    //         sheet.autoSizeColumn(i);
    //     }*/
    //     try {
    //         workbook.write(out);
    //         workbook.close();
    //         workbook.dispose();
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    //     Integer openTimeMillis = 20 * 2 * 1000;
    //     Date awardTime = new Date(System.currentTimeMillis() - openTimeMillis.longValue());
    //     System.out.println(awardTime+"==="+System.currentTimeMillis());
    // }
    /**
     *  压缩单个excel文件的输出流 到zip输出流,注意zipOutputStream未关闭，需要交由调用者关闭之
     * @param zipOutputStream zip文件的输出流
     * @param excelOutputStream excel文件的输出流
     * @param excelFilename 文件名可以带目录，例如 TestDir/test1.xlsx
     */
    public static void compressFileToZipStream(ZipOutputStream zipOutputStream,
                                               ByteArrayOutputStream excelOutputStream, String excelFilename) {
        byte[] buf = new byte[1024];
        try {
            // Compress the files
            byte[] content = excelOutputStream.toByteArray();
            ByteArrayInputStream is = new ByteArrayInputStream(content);
            BufferedInputStream bis = new BufferedInputStream(is);
            // Add ZIP entry to output stream.
            zipOutputStream.putNextEntry(new ZipEntry(excelFilename));
            // Transfer bytes from the file to the ZIP file
            int len;
            while ((len = bis.read(buf)) > 0) {
                zipOutputStream.write(buf, 0, len);
            }
            // Complete the entry
            //excelOutputStream.close();//关闭excel输出流
            zipOutputStream.closeEntry();
            bis.close();
            is.close();
            // Complete the ZIP file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException {

//        Calendar cal = Calendar.getInstance();
//
//        int hour=cal.get(Calendar.HOUR_OF_DAY);
//        if (hour>6&&hour<18){
//            System.out.println("白天");
//        }else{
//            System.out.println("晚上");
//        }
//        System.out.println(hour);




      /*  int count = 100;
        JSONArray ja = new JSONArray();
        for(int i=0;i<count;i++){
            Student s = new Student();
            s.setName("POI"+i);
            s.setAge(i);
            s.setBirthday(new Date());
            s.setHeight(i);
            s.setWeight(i);
            s.setSex(i/2==0?false:true);
            ja.add(s);
        }
        Map<String,String> headMap = new LinkedHashMap<String,String>();
        headMap.put("name","姓名");
        headMap.put("age","年龄");
        headMap.put("birthday","生日");
        headMap.put("height","身高");
        headMap.put("weight","体重");
        headMap.put("sex","性别");
        //导出zip
        OutputStream outXlsx = new FileOutputStream("E://test.zip");
        ZipOutputStream zipOutputStream = new ZipOutputStream(outXlsx);
        for(int i=1;i<6;i++) {
            String dir = i%2==0?"dirA":"dirB";
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Test.exportExcelX("111",headMap,ja,null,0,baos);
            Test.compressFileToZipStream(zipOutputStream, baos, dir+"/test"+i+".xlsx");
            baos.close();
        }*/

        //导出zip
       /* OutputStream outXlsx = new FileOutputStream("E://test2.zip");
        ZipOutputStream zipOutputStream = new ZipOutputStream(outXlsx);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();


        List<Map<String,Object>> list=new ArrayList<>();
        Map<String,Object> test=new HashMap<>();
        test.put("order_no","123456");
        test.put("goods_name","123456");
        test.put("goods_price","123456");
        test.put("open_prize_time","123456");
        test.put("open_prize_result","123456");
        test.put("customer_name","123456");
        test.put("customer_phone","123456");
        test.put("user_name","123456");
        test.put("user_mobile","123456");
        test.put("user_address","123456");
        list.add(test);
        String[] keys = { "order_no", "goods_name", "goods_price", "open_prize_time", "open_prize_result", "customer_name","customer_phone","user_name","user_mobile","user_address"};
        Integer[] widths = { 20 };
        String[] headers = { "订单编号", "商品名称","商品价格","开奖时间", "中奖号码", "中奖会员","会员电话","收件人","收件人手机","收件人地址"};
        ExportExcelUtil.exportExcelByZIP(null, headers, keys, widths, list, null,baos);
        Test.compressFileToZipStream(zipOutputStream, baos, "test1.xlsx");

        baos.close();
        zipOutputStream.flush();
        zipOutputStream.close();
        outXlsx.close();

        System.out.println("导出zip完成");*/

    }
}

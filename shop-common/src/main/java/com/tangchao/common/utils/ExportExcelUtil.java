package com.tangchao.common.utils;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/11/5 11:13
 */

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 导出Excel
 *
 * @author 张淮鑫
 *
 */
public class ExportExcelUtil {

    /**
     *
     * @param fileName
     *            文件名
     * @param headers
     *            表格头部标题
     * @param keys
     *            数据中的key
     * @param widths
     *            表格宽度，值和数组中的值都可为null，例{null,100,null}
     * @param dataList
     *            数据集合
     * @param response
     *            响应
     * @param pattern
     *            如果有时间数据，设定输出格式。默认为"yyyy-MM-dd HH:mm:ss"
     * @throws UnsupportedEncodingException
     */
    @SuppressWarnings("resource")
    public static void exportExcel(String fileName, String[] headers, String[] keys, Integer[] widths,
                                   List<Map<String, Object>> dataList, HttpServletResponse response, String pattern)
            throws UnsupportedEncodingException {
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xls");
        // 声明一个工作薄
        XSSFWorkbook workbook = new XSSFWorkbook();
        // 生成一个表格
        XSSFSheet sheet = workbook.createSheet(fileName);
        // 设置表格默认列宽度为20个字节
        if(widths != null) {
            for (int i = 0; i < headers.length; i++) {
                if(i >= widths.length ) {
                    sheet.setColumnWidth(i, 20 * 256);
                }else {
                    if(widths[i] == null) {
                        sheet.setColumnWidth(i, 20 * 256);
                    }else {
                        sheet.setColumnWidth(i, widths[i] * 256);
                    }
                }
            }
        }else {
            for (int i = 0; i < headers.length; i++) {
                sheet.setColumnWidth(i, 20 * 256);
            }
        }
        // 生成一个样式
        XSSFCellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setFillForegroundColor(new XSSFColor(java.awt.Color.gray));
        style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        style.setBorderRight(XSSFCellStyle.BORDER_THIN);
        style.setBorderTop(XSSFCellStyle.BORDER_THIN);
        style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        // 生成一个字体
        XSSFFont font = workbook.createFont();
        font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
        font.setFontName("微软雅黑");
        font.setColor(new XSSFColor(java.awt.Color.BLACK));
        font.setFontHeightInPoints((short) 11);
        // 把字体应用到当前的样式
        style.setFont(font);
        // 生成并设置另一个样式
        XSSFCellStyle style2 = workbook.createCellStyle();
        style2.setFillForegroundColor(new XSSFColor(java.awt.Color.WHITE));
        style2.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style2.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        style2.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        style2.setBorderRight(XSSFCellStyle.BORDER_THIN);
        style2.setBorderTop(XSSFCellStyle.BORDER_THIN);
        style2.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        style2.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        style2.setWrapText(true);
        // 生成另一个字体
        XSSFFont font2 = workbook.createFont();
        font2.setBoldweight(XSSFFont.BOLDWEIGHT_NORMAL);
        // 把字体应用到当前的样式
        style2.setFont(font2);

        // 产生表格标题行
        XSSFRow row = sheet.createRow(0);
        XSSFCell cellHeader;
        for (int i = 0; i < headers.length; i++) {
            cellHeader = row.createCell(i);
            cellHeader.setCellStyle(style);
            cellHeader.setCellValue(new XSSFRichTextString(headers[i]));
        }

        // 遍历集合数据，产生数据行
        // Iterator<T> it = dataset.iterator();
        int index = 0;
        XSSFRichTextString richString;
        Pattern p = Pattern.compile("^//d+(//.//d+)?$");
        Matcher matcher;
        XSSFCell cell;
        Object value;
        String textValue;
        if (StringUtils.isBlank(pattern)) {
            pattern = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        for (Map<String, Object> map : dataList) {
            index++;
            row = sheet.createRow(index);
            for (int i = 0; i < keys.length; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(style2);
                value = map.get(keys[i]);
                textValue = null;
                if (value instanceof Integer) {
                    cell.setCellValue((Integer) value);
                } else if (value instanceof Float) {
                    textValue = String.valueOf((Float) value);
                    cell.setCellValue(textValue);
                } else if (value instanceof Double) {
                    textValue = String.valueOf((Double) value);
                    cell.setCellValue(textValue);
                } else if (value instanceof Long) {
                    cell.setCellValue((Long) value);
                }
                if (value instanceof Boolean) {
                    textValue = "是";
                    if (!(Boolean) value) {
                        textValue = "否";
                    }
                } else if (value instanceof Date) {
                    textValue = sdf.format((Date) value);
                } else {
                    // 其它数据类型都当作字符串简单处理
                    if (value != null) {
                        textValue = value.toString();
                    }
                }
                if (textValue != null) {
                    matcher = p.matcher(textValue);
                    if (matcher.matches()) {
                        // 是数字当作double处理
                        cell.setCellValue(Double.parseDouble(textValue));
                    } else {
                        richString = new XSSFRichTextString(textValue);
                        cell.setCellValue(richString);
                    }
                }
            }

        }

        try {
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void exportExcelByZIP(String fileName, String[] headers, String[] keys, Integer[] widths,
                                        List<Map<String, Object>> dataList, String pattern, OutputStream out) {
        // 声明一个工作薄
        XSSFWorkbook workbook = new XSSFWorkbook();
        // 生成一个表格
        XSSFSheet sheet = workbook.createSheet();
        // 设置表格默认列宽度为20个字节
        if(widths != null) {
            for (int i = 0; i < headers.length; i++) {
                if(i >= widths.length ) {
                    sheet.setColumnWidth(i, 20 * 256);
                }else {
                    if(widths[i] == null) {
                        sheet.setColumnWidth(i, 20 * 256);
                    }else {
                        sheet.setColumnWidth(i, widths[i] * 256);
                    }
                }
            }
        }else {
            for (int i = 0; i < headers.length; i++) {
                sheet.setColumnWidth(i, 20 * 256);
            }
        }
        // 生成一个样式
        XSSFCellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setFillForegroundColor(new XSSFColor(java.awt.Color.gray));
        style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        style.setBorderRight(XSSFCellStyle.BORDER_THIN);
        style.setBorderTop(XSSFCellStyle.BORDER_THIN);
        style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        // 生成一个字体
        XSSFFont font = workbook.createFont();
        font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
        font.setFontName("微软雅黑");
        font.setColor(new XSSFColor(java.awt.Color.BLACK));
        font.setFontHeightInPoints((short) 11);
        // 把字体应用到当前的样式
        style.setFont(font);
        // 生成并设置另一个样式
        XSSFCellStyle style2 = workbook.createCellStyle();
        style2.setFillForegroundColor(new XSSFColor(java.awt.Color.WHITE));
        style2.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style2.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        style2.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        style2.setBorderRight(XSSFCellStyle.BORDER_THIN);
        style2.setBorderTop(XSSFCellStyle.BORDER_THIN);
        style2.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        style2.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        style2.setWrapText(true);
        // 生成另一个字体
        XSSFFont font2 = workbook.createFont();
        font2.setBoldweight(XSSFFont.BOLDWEIGHT_NORMAL);
        // 把字体应用到当前的样式
        style2.setFont(font2);

        // 产生表格标题行
        XSSFRow row = sheet.createRow(0);
        XSSFCell cellHeader;
        for (int i = 0; i < headers.length; i++) {
            cellHeader = row.createCell(i);
            cellHeader.setCellStyle(style);
            cellHeader.setCellValue(new XSSFRichTextString(headers[i]));
        }

        // 遍历集合数据，产生数据行
        // Iterator<T> it = dataset.iterator();
        int index = 0;
        XSSFRichTextString richString;
        Pattern p = Pattern.compile("^//d+(//.//d+)?$");
        Matcher matcher;
        XSSFCell cell;
        Object value;
        String textValue;
        if (StringUtils.isBlank(pattern)) {
            pattern = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        for (Map<String, Object> map : dataList) {
            index++;
            row = sheet.createRow(index);
            for (int i = 0; i < keys.length; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(style2);
                value = map.get(keys[i]);
                textValue = null;
                if (value instanceof Integer) {
                    cell.setCellValue((Integer) value);
                } else if (value instanceof Float) {
                    textValue = String.valueOf((Float) value);
                    cell.setCellValue(textValue);
                } else if (value instanceof Double) {
                    textValue = String.valueOf((Double) value);
                    cell.setCellValue(textValue);
                } else if (value instanceof Long) {
                    cell.setCellValue((Long) value);
                }
                if (value instanceof Boolean) {
                    textValue = "是";
                    if (!(Boolean) value) {
                        textValue = "否";
                    }
                } else if (value instanceof Date) {
                    textValue = sdf.format((Date) value);
                } else {
                    // 其它数据类型都当作字符串简单处理
                    if (value != null) {
                        textValue = value.toString();
                    }
                }
                if (textValue != null) {
                    matcher = p.matcher(textValue);
                    if (matcher.matches()) {
                        // 是数字当作double处理
                        cell.setCellValue(Double.parseDouble(textValue));
                    } else {
                        richString = new XSSFRichTextString(textValue);
                        cell.setCellValue(richString);
                    }
                }
            }

        }

        try {
            workbook.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
}

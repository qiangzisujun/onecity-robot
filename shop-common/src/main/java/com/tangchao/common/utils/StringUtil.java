package com.tangchao.common.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.*;

/**
 * title: StringUtil
 * package: com.chichao.eyyg.common.util
 * description: 字符串工具类
 * author: 王飞腾
 * date: 2018/6/26
 */
public final class StringUtil {

    public static final String COMMA = ",";

    /**
     * 切割字符串
     *
     * @param text   被切割的字符串
     * @param symbol 分隔符
     * @return List<String>
     */
    public static List<String> splitString(String text, String symbol) {
        String[] array = text.split(symbol);
        return Arrays.asList(array);
    }

    /**
     * 集合转数组
     *
     * @param list  集合
     * @param clazz 数组类型
     * @return T[]
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] listToArray(List<T> list, Class<T> clazz) {
        T[] array = (T[]) Array.newInstance(clazz, list.size());
        return list.toArray(array);
    }

    /**
     * 判断文本是否有值
     *
     * @param text 文本内容
     * @return boolean
     */
    public static boolean hasText(String text) {
        return !(null != text && text.trim().equals(""));
    }


    public static List removeAll(List src, List oth) {
        LinkedList result = new LinkedList(src);//大集合用linkedlist
        HashSet othHash = new HashSet(oth);//小集合用hashset
        Iterator iter = result.iterator();//采用Iterator迭代器进行数据的操作
        while (iter.hasNext()) {
            if (othHash.contains(iter.next())) {
                iter.remove();
            }
        }
        return result;
    }

    /**
     * 生成幸运号码
     *
     * @param luckyNumberList 幸运号码列表
     * @return String
     */
    public static String createLuckyNumber(List<Integer> luckyNumberList) {
        //  生成随机数
        int randomNum = 0;
        if (luckyNumberList == null || luckyNumberList.size() == 0) {
            randomNum = 0;
        } else {
            randomNum = new Random().nextInt(luckyNumberList.size());
        }
        Integer result = luckyNumberList.get(randomNum);
        luckyNumberList.remove(result);
        return result.toString();
    }

    public static Map xml2map(String xmlStr, boolean needRootKey) throws DocumentException {
        Document doc = DocumentHelper.parseText(xmlStr);
        Element root = doc.getRootElement();
        Map<String, Object> map = (Map<String, Object>) xml2map(root);
        if(root.elements().size()==0 && root.attributes().size()==0){
            return map;
        }
        if(needRootKey){
            //在返回的map里加根节点键（如果需要）
            Map<String, Object> rootMap = new HashMap<String, Object>();
            rootMap.put(root.getName(), map);
            return rootMap;
        }
        return map;
    }

    public static Map xml2map(Element e) {
        Map map = new LinkedHashMap();
        List list = e.elements();
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Element iter = (Element) list.get(i);
                List mapList = new ArrayList();

                if (iter.elements().size() > 0) {
                    Map m = xml2map(iter);
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!(obj instanceof List)) {
                            mapList = new ArrayList();
                            mapList.add(obj);
                            mapList.add(m);
                        }
                        if (obj instanceof List) {
                            mapList = (List) obj;
                            mapList.add(m);
                        }
                        map.put(iter.getName(), mapList);
                    } else
                        map.put(iter.getName(), m);
                } else {
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!(obj instanceof List)) {
                            mapList = new ArrayList();
                            mapList.add(obj);
                            mapList.add(iter.getText());
                        }
                        if (obj instanceof List) {
                            mapList = (List) obj;
                            mapList.add(iter.getText());
                        }
                        map.put(iter.getName(), mapList);
                    } else
                        map.put(iter.getName(), iter.getText());
                }
            }
        } else
            map.put(e.getName(), e.getText());
        return map;
    }

    public static String getValue(Cell hssfCell) {
        if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
            // 返回布尔类型的值
            return String.valueOf(hssfCell.getBooleanCellValue());
        } else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
            // 返回数值类型的值
            Object inputValue = null;// 单元格值
            Long longVal = Math.round(hssfCell.getNumericCellValue());
            Double doubleVal = hssfCell.getNumericCellValue();
            if(Double.parseDouble(longVal + ".0") == doubleVal){   //判断是否含有小数位.0
                inputValue = longVal;
            }
            else{
                inputValue = doubleVal;
            }
            DecimalFormat df = new DecimalFormat("#.####");    //格式化为四位小数，按自己需求选择；
            return String.valueOf(df.format(inputValue));      //返回String类型
        } else {
            // 返回字符串类型的值
            return String.valueOf(hssfCell.getStringCellValue());
        }
    }
}

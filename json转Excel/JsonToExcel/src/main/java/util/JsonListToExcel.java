package util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: 蚝烙迈落蚝
 * @date: 2021/11/26
 * @description: jsonListToExcel json数组转Excel
 */
public class JsonListToExcel {

    /**
     * 存放json数组的文件
     */
    public static final String JSON_LIST_FILE_PATH = "jsonList.txt";
    /**
     * excel文件路径
     */
    public static final String EXCEL_FILE_PATH = "resultExcel.xls";


    public static void main(String[] args) throws Exception {
        JsonListToExcel toExcel = new JsonListToExcel();
        //读txt文件接收json数组
        String filePath = "";
        //写进Excel
        String excelPath = "";
        if (args.length == 1) {
            //只传一个参数，就赋值给JSON文件
            filePath = args[0];
        } else if (args.length == 2) {
            filePath = args[0];
            excelPath = args[1];
        }
        String jsonStr = toExcel.readJsonFile(filePath);
        //处理json字符串,得到JSONList对象
        List<Map<String, Object>> maps = toExcel.dealJsonStr(jsonStr);

        int success = toExcel.writeExcel(maps, excelPath);
        System.out.println("【json转Excel】执行结果：" + (success == 0 ? "成功" : "失败"));
    }


    /**
     * 内容写进Excel
     *
     * @param maps      JSON转换过来的map数组
     * @param excelPath Excel文件路径
     * @return 成功表示  0：成功， 非0：失败
     */
    private int writeExcel(List<Map<String, Object>> maps, String excelPath) {
        excelPath = StringUtils.isRealEmpty(excelPath) ? EXCEL_FILE_PATH : excelPath;
        FileOutputStream fileOutputStream = null;
        Workbook workbook = null;
        try {
            //构建Excel数据
            List<String> heads = this.buildExcelHead(maps);
            List<List<Object>> dataList = this.buildExcelData(heads, maps);
            //System.out.println(JSONObject.toJSONString(dataList));
            File excelFile = new File(excelPath);
            System.out.println("【json转Excel】数据写入中");
            //创建  WorkBook 工作溥
            workbook = new HSSFWorkbook();
            //根据workBook获取当前sheet页
            Sheet sheet = workbook.createSheet();
            assert dataList != null;
            int rowSize = dataList.size() + 1;
            assert heads != null;
            int cellSize = heads.size();
            Row row = sheet.createRow(0);
            //写入第一行，表头
            for (int i = 0; i < cellSize; i++) {
                row.createCell(i).setCellValue(heads.get(i));
            }
            for (int i = 1; i < rowSize; i++) {
                //写入表数据
                Row rowI = sheet.createRow(i);
                List<Object> rowData = dataList.get(i - 1);
                for (int j = 0; j < cellSize; j++) {
                    if (rowData.get(j) instanceof String) {
                        rowI.createCell(j).setCellValue((String) rowData.get(j));
                    } else {
                        rowI.createCell(j).setCellValue(JSONObject.toJSONString(rowData.get(j)));
                    }
                }
            }
            //写入文件
            fileOutputStream = new FileOutputStream(excelFile);
            //清理输出流 fileOutputStream
            fileOutputStream.flush();
            //将 Workbook 中的数据通过流写入
            workbook.write(fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (workbook != null) {
                    workbook.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }


    /**
     * 构建Excel表头数据
     *
     * @param maps JSON转换过来的map数组
     * @return Excel表头数据
     */
    private List<String> buildExcelHead(List<Map<String, Object>> maps) {
        if (ListUtils.isRealEmpty(maps)) {
            return null;
        }
        //excel表头数据
        List<String> heads = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            Set<Map.Entry<String, Object>> entries = map.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                String key = entry.getKey();
                //如果表头heads列表中没有改元素，就加入list
                if (!heads.contains(key)) {
                    heads.add(key);
                }
            }
        }
        System.out.println("【json转Excel】构建表头数据成功");
        return heads;
    }

    /**
     * 构建Excel表数据
     *
     * @param heads excel的表头数组
     * @param maps  JSON转换过来的map数组
     * @return Excel表数据
     */
    private List<List<Object>> buildExcelData(List<String> heads, List<Map<String, Object>> maps) {
        if (ListUtils.isRealEmpty(heads)) {
            return null;
        }
        List<List<Object>> dataList = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            //遍历表头拿到对应的数据
            List<Object> rowData = new ArrayList<>();
            for (String head : heads) {
                Object value = map.get(head);
                rowData.add(value);
            }
            dataList.add(rowData);
        }
        System.out.println("【json转Excel】构建表数据成功");
        return dataList;
    }

    /**
     * 处理json字符串
     * 得到JSONList
     * 对应关系
     * list[Map<String,String>]  ==> [{},{}]
     *
     * @param jsonStr json字符串
     */
    private List<Map<String, Object>> dealJsonStr(String jsonStr) throws Exception {
        System.out.println("【json转Excel】开始对JSON数据进行处理");
        Object object = JSONObject.parse(jsonStr);
        //object转List
        if (!(object instanceof List)) {
            System.out.println("【json转Excel】json数据转成JSONArray时出错");
            throw new Exception("【json转Excel】json数据转成JSONArray时出错");
        }
        JSONArray list = (JSONArray) object;
        if (ListUtils.isRealEmpty(list)) {
            System.out.println("【json转Excel】json数组为空");
            throw new Exception("【json转Excel】json数组为空");
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object item : list) {
            Map<String, Object> jsonMap;
            try {
                jsonMap = (Map<String, Object>) item;
            } catch (ClassCastException e) {
                System.out.println("【json转Excel】json数组转换成map时出错，出错对象：" + JSONObject.toJSONString(item));
                throw new Exception("【json转Excel】json数组转换成map时出错，出错对象：" + JSONObject.toJSONString(item));
            }
            if (jsonMap == null) {
                System.out.println("【json转Excel】json数据有误，出错对象：" + JSONObject.toJSONString(item));
                throw new Exception("【json转Excel】json数据有误，出错对象：" + JSONObject.toJSONString(item));
            }
            result.add(jsonMap);
        }
        System.out.println("【json转Excel】json数据处理完毕");
        return result;
    }

    /**
     * 读txt文件接收json数组
     */
    private String readJsonFile(String filePath) throws Exception {
        //有传参就用传参，没传参用默认值
        filePath = StringUtils.isRealEmpty(filePath) ? JSON_LIST_FILE_PATH : filePath;
        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;

        try {
            File jsonListFile = new File(filePath);
            if (!jsonListFile.exists()) {
                System.out.println("【json转Excel】,json文件不存在，请检查路径。。");
                throw new Exception("【json转Excel】,json文件不存在，请检查路径");
            }
            fileInputStream = new FileInputStream(jsonListFile);
            inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
            bufferedReader = new BufferedReader(inputStreamReader);
            System.out.println("【json转Excel】读取json文件");
            String str;
            StringBuilder sb = new StringBuilder();
            while ((str = bufferedReader.readLine()) != null) {
                sb.append(str);
            }
            if (StringUtils.isRealEmpty(sb.toString())) {
                System.out.println("【json转Excel】,json文件内容为空");
                throw new Exception("【json转Excel】,json文件内容为空");
            }
            System.out.println("【json转Excel】json文件读取成功");
            return sb.toString();
        } catch (IOException e) {
            System.out.println("【json转Excel】读取json文件时出错");
            e.printStackTrace();
            throw new Exception("【json转Excel】读取json文件时出错");
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}

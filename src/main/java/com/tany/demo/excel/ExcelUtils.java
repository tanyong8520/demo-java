package com.tany.demo.excel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExcelUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtils.class);

    /**
     * 表格类型枚举
     */
    public enum ExcelType {
        XLSX("xlsx"), XLS("xls");
        private String type;

        ExcelType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    /**
     * excel数据模型
     */
    public static class ExcelModel {

        // 表头和属性名对应关系
        private LinkedHashMap<String, String> headerFieldRel;
        // 页面标题
        private String sheetName;
        // 开始行，第一行为0
        private int startRow = 0;
        // 开始列，第一列为0
        private int startColumn = 0;
        // 数据
        private List<Object> data = new ArrayList<>();

        public LinkedHashMap<String, String> getHeaderFieldRel() {
            return headerFieldRel;
        }

        public void setHeaderFieldRel(LinkedHashMap<String, String> headerFieldRel) {
            this.headerFieldRel = headerFieldRel;
        }

        public String getSheetName() {
            return sheetName;
        }

        public void setSheetName(String sheetName) {
            this.sheetName = sheetName;
        }

        public int getStartRow() {
            return startRow;
        }

        public void setStartRow(int startRow) {
            this.startRow = startRow;
        }

        public int getStartColumn() {
            return startColumn;
        }

        public void setStartColumn(int startColumn) {
            this.startColumn = startColumn;
        }

        public List<Object> getData() {
            return data;
        }

        public void setData(List<Object> data) {
            this.data = data;
        }
    }

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 读取excel文件
     *
     * @param filePath   文件路径
     * @param startRows  开始行 0代表第一行
     * @param sheetIndex sheet下标
     * @return 返回二维数组
     */
    public static String[][] read(String filePath, int startRows, int sheetIndex) throws IOException {
        String fileType = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length());
        Workbook wb;
        InputStream is = new FileInputStream(filePath);
        if (ExcelType.XLS.getType().equalsIgnoreCase(fileType)) {
            wb = new HSSFWorkbook(is);
        } else if (ExcelType.XLSX.getType().equalsIgnoreCase(fileType)) {
            wb = new XSSFWorkbook(is);
        } else {
            throw new IOException("不支持的文件格式");
        }

        Sheet sheet = wb.getSheetAt(sheetIndex);
        if (null == sheet) {
            return null;
        }
        int rowSize = sheet.getLastRowNum() + 1;
        int tempColSize = 0;
        List<String[]> list = new ArrayList<String[]>();
        for (int j = startRows; j < rowSize; j++) {
            Row row = sheet.getRow(j);
            if (row == null) {
                continue;
            }
            int colSize = row.getLastCellNum();
            if (tempColSize > colSize) {
                colSize = tempColSize;
            }
            String[] values = new String[colSize];
            for (short k = 0; k < colSize; k++) {
                Cell cell = row.getCell(k);
                if (cell == null) {
                    continue;
                }
                values[k] = getValue(cell);
            }
            list.add(values);
        }
        String[][] resultArray = new String[list.size()][tempColSize];
        for (int h = 0; h < list.size(); h++) {
            resultArray[h] = (String[]) list.get(h);
        }
        if (null != is) {
            is.close();
        }
        return resultArray;
    }

    private static String getValue(Cell cell) {
        String value = "";
        switch (cell.getCellTypeEnum()) {
            case STRING:
                value = cell.getStringCellValue();
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    if (null != date) {
                        value = new SimpleDateFormat(DATE_FORMAT).format(date);
                    }
                } else {
                    value = new DecimalFormat("0").format(cell.getNumericCellValue());
                }
                break;
            case BOOLEAN:
                value = cell.getBooleanCellValue() == true ? "Y" : "N";
                break;
            case FORMULA:
                // 导入时如果为公式生成的数据则无值
                if (!"".equals(cell.getStringCellValue())) {
                    value = cell.getStringCellValue();
                } else {
                    value = cell.getNumericCellValue() + "";
                }
                break;
            default:
                break;
        }
        return value;
    }

    /**
     * 数据写入excel文件
     *
     * @param path    输出文件路径与文件名
     * @param dataArr 写入数据-二位数组
     * @param titles  标题
     */
    public static void write(String path, String[][] dataArr, String[] titles) throws Exception {
        List<ExcelModel> models = new ArrayList<>(1);
        ExcelModel model = new ExcelModel();
        model.setStartColumn(0);
        model.setStartRow(0);
        model.setSheetName("sheet1");
        LinkedHashMap<String, String> headerFieldRel = new LinkedHashMap<>();
        for (String title : titles) {
            headerFieldRel.put(title, title);
        }
        model.setHeaderFieldRel(headerFieldRel);
        for (String[] data : dataArr) {
            Map<String, Object> d = new HashMap<>(data.length);
            for (int i = 0; i < titles.length; i++) {
                d.put(titles[i], data[i]);
            }
            model.getData().add(d);
        }

        write(path, models);
    }

    /**
     * 数据写入excel文件
     *
     * @param path  输出文件路径与文件名
     * @param datas 数据
     */
    public static void write(String path, List<ExcelModel> datas)
            throws Exception {
        OutputStream os = new FileOutputStream(path);
        ExcelType excelType = ExcelType.XLS;
        if (path.toLowerCase().endsWith(ExcelType.XLSX.getType())) {
            excelType = ExcelType.XLSX;
        }

        try {
            write(os, excelType, datas);
        } finally {
            os.close();
        }
    }

    /**
     * 将数据写入excel输出流
     *
     * @param out       输出流
     * @param excelType excel类型 2007以上为xlsx,2007以下为xls
     * @param datas     写入数据
     */
    public static void write(OutputStream out, ExcelType excelType, List<ExcelModel> datas)
            throws Exception {
        if (out == null) {
            throw new NullPointerException("outputStream is null");
        }

        Workbook wb = null;
        try {
            wb = getWoerBook(excelType, datas);
            // 写入数据
            wb.write(out);
        } catch (Exception e) {
            throw e;
        } finally {
            if (wb != null) {
                wb.close();
            }
        }
    }

    public static Workbook getWoerBook(ExcelType excelType, List<ExcelModel> datas) {
        // 创建工作文档对象
        Workbook wb;
        if (ExcelType.XLSX == excelType) {
            wb = new XSSFWorkbook();
        } else {
            wb = new HSSFWorkbook();
        }

        try {
            if (CollectionUtils.isNotEmpty(datas)) {
                for (ExcelModel model : datas) {
                    // 创建sheet对象
                    Sheet sheet = wb.createSheet(model.getSheetName());

                    int startRow = model.getStartRow();
                    int startCol = model.getStartColumn();

                    // 写表头
                    Row headerRow = sheet.createRow(startRow);
                    LinkedHashMap<String, String> headerFieldRel = model.getHeaderFieldRel();
                    List<String> headers = new ArrayList<>(headerFieldRel.keySet());
                    CellStyle headerStyle = getHeaderStyle(wb);
                    for (int i = 0; i < headers.size(); i++) {
                        Cell cell = headerRow.createCell(i + startCol);
                        cell.setCellValue(headers.get(i));
                        cell.setCellStyle(headerStyle);
                    }
                    startRow += 1;

                    // 写数据
                    List<Object> list = model.getData();
                    if (CollectionUtils.isNotEmpty(list)) {
                        CellStyle bodyStyle = getBodyStyle(wb);
                        int index = 0;
                        for (Object obj : list) {
                            if (obj == null) {
                                continue;
                            }
                            Row row = sheet.createRow(index + startRow);
                            for (int j = 0; j < headers.size(); j++) {
                                // 属性名或map的key
                                String fieldName = headerFieldRel.get(headers.get(j));
                                Cell cell = row.createCell(j + startCol);
                                cell.setCellStyle(bodyStyle);
                                setValue(cell, obj, fieldName);
                            }
                            index++;
                        }
                    }
                }
            } else {
                wb.createSheet("sheet1");
            }
        } catch (Exception e) {
            LOGGER.error("写入excel数据出错.", e);
            throw e;
        }
        return wb;
    }

    private static void setValue(Cell cell, Object obj, String fieldName) {
        Object value;
        try {
            if (obj instanceof Map) {
                value = ((Map) obj).get(fieldName);
            } else {
                Field field = obj.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                value = field.get(obj);
                field.setAccessible(false);
            }
        } catch (Exception e) {
            return;
        }

        if (value != null) {
            switch (value.getClass().getSimpleName()) {
                case "String":
                    cell.setCellValue((String) value);
                    break;
                case "Double":
                    cell.setCellValue((Double) value);
                    break;
                case "Float":
                    cell.setCellValue((Float) value);
                    break;
                case "Long":
                    cell.setCellValue((Long) value);
                    break;
                case "Integer":
                    cell.setCellValue((Integer) value);
                    break;
                case "BigDecimal":
                    cell.setCellValue(((BigDecimal) value).doubleValue());
                    break;
                case "Boolean":
                    cell.setCellValue((Boolean) value);
                    break;
                case "Date":
                    DateFormat df = new SimpleDateFormat(DATE_FORMAT);
                    cell.setCellValue(df.format((Date) value));
                    break;
                default:
                    cell.setCellValue(value.toString());
                    break;
            }
        }
    }

    private static CellStyle getHeaderStyle(Workbook wb) {
        // 定义表头格式 居中加粗，背景灰色，加边框
        CellStyle headerStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true); // 加粗
        headerStyle.setFont(font);
        headerStyle.setAlignment(HorizontalAlignment.CENTER); // 居中
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex()); // 填充灰色
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN); //下边框
        headerStyle.setBorderLeft(BorderStyle.THIN);//左边框
        headerStyle.setBorderTop(BorderStyle.THIN);//上边框
        headerStyle.setBorderRight(BorderStyle.THIN);//右边框

        return headerStyle;
    }

    private static CellStyle getBodyStyle(Workbook wb) {
        // 定义数据单元格格式 加边框
        CellStyle bodyStyle = wb.createCellStyle();
//        bodyStyle.setWrapText(true); // 自动换行
        bodyStyle.setBorderBottom(BorderStyle.THIN); //下边框
        bodyStyle.setBorderLeft(BorderStyle.THIN);//左边框
        bodyStyle.setBorderTop(BorderStyle.THIN);//上边框
        bodyStyle.setBorderRight(BorderStyle.THIN);//右边框

        return bodyStyle;
    }

//    public static void main(String[] args) throws Exception {
//        String path = "D:/excel-test.xlsx";
//        LinkedHashMap headerFieldRel = new LinkedHashMap<String, String>() {{
//            put("姓名", "name");
//            put("年龄", "age");
//            put("日期", "date");
//        }};
//        OutputStream os = new FileOutputStream(path);
//        List<ExcelModel> list = new ArrayList<>();
//        ExcelModel model = new ExcelModel();
//        model.setHeaderFieldRel(headerFieldRel);
//        model.setSheetName("first");
//        model.setStartRow(0);
//        model.setStartColumn(0);
//
//        Student s1 = new Student("张三", 29);
//        s1.setDate(new Date());
//        Student s2 = new Student("李四", 30);
//        s2.setDate(new Date());
//
//        model.getData().add(s1);
//        model.getData().add(s2);
//        list.add(model);
//
//        ExcelModel model2 = new ExcelModel();
//        model2.setHeaderFieldRel(headerFieldRel);
//        model2.setSheetName("second");
//        model2.setStartRow(0);
//        model2.setStartColumn(1);
//
//        Map map1 = new HashMap<String, Object>();
//        map1.put("name", "王五");
//        map1.put("age", 31);
//        Map map2 = new HashMap<String, Object>();
//        map2.put("name", "赵六");
//        map2.put("age", 32);
//
//        model2.getData().add(map1);
//        model2.getData().add(map2);
//        model2.getData().add(s1);
//        list.add(model2);
//
//        write(os, ExcelType.XLSX, list);
//        System.out.println("success");
//
//        String[][] results = read(path);
//        for (String[] arr : results) {
//            System.out.println(Arrays.toString(arr));
//        }
//    }
//
//    static class Student implements Serializable {
//
//        private static Logger log = LoggerFactory.getLogger(Student.class);
//
//        private String name;
//        private int age;
//        private Date date;
//
//        public Date getDate() {
//            return date;
//        }
//
//        public void setDate(Date date) {
//            this.date = date;
//        }
//
//        public Student(String name, int age) {
//            this.name = name;
//            this.age = age;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public int getAge() {
//            return age;
//        }
//
//        public void setAge(int age) {
//            this.age = age;
//        }
//    }
}

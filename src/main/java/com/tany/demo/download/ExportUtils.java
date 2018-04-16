package com.tany.demo.download;

import com.tany.demo.excel.ExcelUtils;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

public class ExportUtils {

    /**
     * 导出excel文件
     *
     * @param fileName 导出文件名称
     * @param datas    数据集合，集合中的一个元素为一个sheet的数据
     * @param response 数据写入response的输出流
     * @throws Exception 生成excel文件出错抛出异常
     */
    public static void exportExcel(String fileName, List<ExcelUtils.ExcelModel> datas, HttpServletResponse response)
            throws Exception {

        ExcelUtils.ExcelType excelType;
        if (fileName.toLowerCase().endsWith(".xlsx")) {
            excelType = ExcelUtils.ExcelType.XLSX;
        } else {
            excelType = ExcelUtils.ExcelType.XLS;
        }

        Workbook workbook = null;
        OutputStream os = null;
        try {
            response.reset();
            response.setContentType("application/octet-stream");
            response.addHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(fileName, "UTF-8"));

            workbook = ExcelUtils.getWoerBook(excelType, datas);
            os = response.getOutputStream();
            workbook.write(os);
        } finally {
            if (workbook != null) {
                workbook.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }
}

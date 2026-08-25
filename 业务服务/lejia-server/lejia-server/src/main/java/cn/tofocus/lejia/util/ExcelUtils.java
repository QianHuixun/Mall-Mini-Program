package cn.tofocus.lejia.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;


/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @author lsf 从 zjc 的拷贝而来
 * @version [版本号, 2019年10月11日]
 */

public class ExcelUtils {
    private final static Logger log = LoggerFactory.getLogger(ExcelUtils.class);

    private final static String EXCEL2003 = ".xls";
    private final static String EXCEL2007 = ".xlsx";

//    public static <T> void writeExcel(HttpServletResponse response, String filename, List<T> dataList, List<Column> titles){
//        HSSFWorkbook wb = new HSSFWorkbook();
//        Sheet sheet = wb.createSheet("Sheet1");
//
//        AtomicInteger ai = new AtomicInteger();
//        {
//            Row row = sheet.createRow(ai.getAndIncrement());
//            AtomicInteger aj = new AtomicInteger();
//            // 设置头部
//            titles.forEach(e -> {
//
//                String columnName = e.getTitle();
//
//                Cell cell = row.createCell(aj.getAndIncrement());
//
//                CellStyle cellStyle = wb.createCellStyle();
//                cellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
////                cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
////                cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
////
////                Font font = wb.createFont();
////                font.setBoldweight(Font.BOLDWEIGHT_NORMAL);
////                cellStyle.setFont(font);
//                cell.setCellStyle(cellStyle);
//                cell.setCellValue(columnName);
//            });
//        }
//
//        // 设置body
//        if (CollectionUtils.isNotEmpty(dataList)) {
//            dataList.forEach(t -> {
//                Row row = sheet.createRow(ai.getAndIncrement());
//                AtomicInteger aj = new AtomicInteger();
//
//                Class<?> c = t.getClass();
//
//                titles.forEach(e -> {
//                    Object value = "";
//                    String name = e.getName();
//                    String methodName = "get" + name.substring(0,1).toUpperCase()+ name.substring(1);
//                    try {
//                        Method method = c.getMethod(methodName);
//                        value = method.invoke(t);
//                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
//                        log.error(ex.getMessage());
//                    }
//                    Cell cell = row.createCell(aj.getAndIncrement());
//                    cell.setCellValue(value == null ? "" : value.toString());
//                });
//            });
//        }
//        //冻结窗格
//        wb.getSheet("Sheet1").createFreezePane(0, 1, 0, 1);
//        //浏览器下载excel
//        buildExcelDocument(filename, wb, response);
//        //生成excel文件
////        buildExcelFile(".\\default.xlsx",wb);
//    }

//    /**
//     * 浏览器下载excel
//     *
//     * @param fileName
//     * @param wb
//     * @param response
//     */
//
//    private static void buildExcelDocument(String fileName, HSSFWorkbook wb, HttpServletResponse response) {
//        try {
//            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
//            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
//            response.addHeader("Pargam", "no-cache");
//            response.addHeader("Cache-Control", "no-cache");
//            response.flushBuffer();
//            ServletOutputStream os = response.getOutputStream();
//            wb.write(os);
//            os.flush();
//            os.close();
//        } catch (IOException e) {
//            log.error(e.getMessage());
//        }
//    }

    /**
     * 生成excel文件
     *
     * @param path 生成excel路径
     * @param wb
     */
    public static void buildExcelFile(String path, Workbook wb) {

        File file = new File(path);
        file.deleteOnExit();
        try {
            wb.write(new FileOutputStream(file));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    
    /**
     * Excel导入
     */
	public static List<List<Object>> getUserListByExcel(InputStream in, String fileName) throws Exception {
        List<List<Object>> list = null;
        // 创建Excel工作薄
        Workbook work = getWorkbook(in, fileName);
        if (null == work) {
            throw new Exception("创建Excel工作薄为空！");
        }
        Sheet sheet = null;
        Row row = null;
        Cell cell = null;
        list = new ArrayList<List<Object>>();
        // 遍历Excel中所有的sheet
        for (int i = 0; i < work.getNumberOfSheets(); i++) {
            sheet = work.getSheetAt(i);
            if (sheet == null) {
                continue;
            }
            // 遍历当前sheet中的所有行
            // 包含头部，所以要小于等于最后一列数,这里也可以在初始值加上头部行数，以便跳过头部
            for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {
                // 读取一行
                row = sheet.getRow(j);
                // 去掉空行和表头
                if (row == null || row.getFirstCellNum() == j ) {
                    continue;
                }
                // 遍历所有的列
                List<Object> li = new ArrayList<Object>();
                for (int y = row.getFirstCellNum(); y < row.getLastCellNum(); y++) {
                    cell = row.getCell(y);
                    li.add(cell);
                }
                list.add(li);
            }
        }
        return list;
    }
    
    /**
     * 描述：根据文件后缀，自适应上传文件的版本
     */
    public static Workbook getWorkbook(InputStream inStr, String fileName) throws Exception {
        Workbook wb = null;
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        if (EXCEL2003.equals(fileType)) {
            wb = new HSSFWorkbook(inStr); // 2003-
        } else if (EXCEL2007.equals(fileType)) {
            wb = new XSSFWorkbook(inStr); // 2007+
        } else {
            throw new Exception("解析的文件格式有误！");
        }
        return wb;
    }
//    
//    /**
//     * 描述：对表格中数值进行格式化
//     */
//    public static Object getCellValue(Cell cell) {
//        Object value = null;
//        DecimalFormat df = new DecimalFormat("0"); // 格式化字符类型的数字
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss"); // 日期格式化
//        DecimalFormat df2 = new DecimalFormat("0"); // 格式化数字
//        if (cell.getCellType() == CellType..STRING) {
//            value = cell.getRichStringCellValue().getString();
//        } else if (cell.getCellType() == CellType.NUMERIC) {
//            if ("General".equals(cell.getCellStyle().getDataFormatString())) {
//                value = df.format(cell.getNumericCellValue());
//            } else if ("m/d/yy".equals(cell.getCellStyle().getDataFormatString())) {
//                value = sdf.format(cell.getDateCellValue());
//            } else {
//                value = df2.format(cell.getNumericCellValue());
//            }
//        } else if (cell.getCellType() == CellType.BOOLEAN) {
//            value = cell.getBooleanCellValue();
//        } else if (cell.getCellType() == CellType.BLANK) {
//            value = "";
//        }
//        return value;
//    }
    
    
}

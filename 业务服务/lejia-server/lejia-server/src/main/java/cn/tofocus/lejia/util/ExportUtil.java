package cn.tofocus.lejia.util;


import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import cn.tofocus.common.excel.ExcelUtil;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;

public class ExportUtil
{
    /**
     * 导出Excel
     * @param clazz ExcelMode
     * @param list  数据
     * @param response HttpServletResponse
     * @param fileName 文件名，不包括扩展名
     * @param sheetName sheet名
     * @param titles Excel最上部加入额外标题行（可选）
     */
    public static void exportData(Class<?> clazz, List<?> list, HttpServletResponse response, String fileName, String sheetName,
        String... titles)
    {
        try (OutputStream outputStream = response.getOutputStream();)
        {
            setXlsxResponse(response, fileName + ".xlsx");
            ExcelUtil.exportExcel(list, sheetName, response.getOutputStream(), clazz, titles);
        }
        catch (IOException e)
        {
            throw TofocusException.of(SysErrCode.UNKNOW_INTER_FAIL, e, "导出发生错误");
        }
    }
    
    /**
     * 导出Excel 并在前面加上标题行和日期范围行
     * @param clazz
     * @param list
     * @param response
     * @param title
     * @param startDate
     * @param endDate
     */
    public static void exportDataWithTitleDate(Class<?> clazz, List<?> list, HttpServletResponse response, String title,
        String startDate, String endDate)
    {
        String datestr = "日期：不限";
        if (startDate != null) datestr = "日期：" + startDate + " 至 " + endDate;
        exportData(clazz, list, response, title, title, title, datestr);
    }

    /**
     * 导出Excel 并在前面加上标题行
     * @param clazz
     * @param list
     * @param response
     * @param title
     */
    public static void exportDataWithTitle(Class<?> clazz, List<?> list, HttpServletResponse response, String title)
    {
        exportData(clazz, list, response, title, title, title);
    }
    
    public static void setXlsxResponse(HttpServletResponse response, String fileName)
        throws UnsupportedEncodingException
    {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;  CHARSET=utf8");
        response.setHeader("Content-Disposition",
            "attachment; filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"));
        response.addHeader("Pargam", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
    }
    
}

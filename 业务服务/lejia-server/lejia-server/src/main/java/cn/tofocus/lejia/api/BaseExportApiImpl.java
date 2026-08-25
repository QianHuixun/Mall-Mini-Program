package cn.tofocus.lejia.api;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import cn.tofocus.common.excel.ExcelHelper;

public abstract class BaseExportApiImpl
{
    @Autowired
    private ExcelHelper excelHelper;
    
    public void exportExcel(List<?> list, HttpServletResponse response, Class<?> excelModel, String fn)
    {
        OutputStream out = null;
        try
        {
            out = response.getOutputStream();
            String f = fn + ".xlsx";
            String fileName = java.net.URLEncoder.encode(f, "UTF-8");
            response.setHeader("Content-disposition", "attachment; filename=" + fileName);
            excelHelper.exportExcel(list, "Sheet1", out, excelModel, new String[] {fn});
            out.flush();
            out.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}

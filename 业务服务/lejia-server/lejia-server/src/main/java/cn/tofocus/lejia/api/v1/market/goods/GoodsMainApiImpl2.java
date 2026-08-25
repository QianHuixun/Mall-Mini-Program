package cn.tofocus.lejia.api.v1.market.goods;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.tofocus.core.Result;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.util.ExportUtil;
import io.swagger.v3.oas.annotations.Operation;

@RequestMapping("/v1/market/goods/main")
@Controller
public class GoodsMainApiImpl2
{
    
    @Operation(summary = "商品库模板下载", tags = ApiTags.custGoodsMain)
    @GetMapping(value = "/down/template")
    public Result<Boolean> downTemplate(HttpServletRequest request, HttpServletResponse response)
    {
        String templateFileName = "spkTem.xlsx";
        ClassPathResource resource = new ClassPathResource("templates/" + templateFileName);
        OutputStream out = null;
        try (InputStream inp = resource.getInputStream();)
        {
            out = response.getOutputStream();
            ExportUtil.setXlsxResponse(response, "商品库模板.xlsx");
            //将文件输入流复制到输出流
            IOUtils.copy(inp, out);
            out.flush();
            out.close();
        }
        catch (IOException e)
        {
            throw TofocusException.of(SysErrCode.UNKNOW_INTER_FAIL, e);
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
        return new Result<>(true);
    }
    
    @Operation(summary = "商品库模板下载", tags = ApiTags.custGoodsMain)
    @GetMapping(value = "/three/down/template")
    public Result<Boolean> downTemplateThree(HttpServletRequest request, HttpServletResponse response)
    {
        String templateFileName = "spkTemThree.xlsx";
        ClassPathResource resource = new ClassPathResource("templates/" + templateFileName);
        OutputStream out = null;
        try (InputStream inp = resource.getInputStream();)
        {
            out = response.getOutputStream();
            ExportUtil.setXlsxResponse(response, "三级分类模板.xlsx");
            //将文件输入流复制到输出流
            IOUtils.copy(inp, out);
            out.flush();
            out.close();
        }
        catch (IOException e)
        {
            throw TofocusException.of(SysErrCode.UNKNOW_INTER_FAIL, e);
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
        return new Result<>(true);
    }
}

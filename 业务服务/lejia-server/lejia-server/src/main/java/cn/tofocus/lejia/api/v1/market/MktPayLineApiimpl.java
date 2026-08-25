package cn.tofocus.lejia.api.v1.market;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.common.excel.ExcelHelper;
import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.PayDayExcel;
import cn.tofocus.lejia.bean.dto.market.MktMemberPayOnList;
import cn.tofocus.lejia.bean.dto.market.PayDayDTO;
import cn.tofocus.lejia.bean.dto.market.PayLineDTO;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.market.MktMemberPayDao;
import cn.tofocus.lejia.domain.PayBillManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RequestMapping("/v1/market/pay/line")
@RestController
public class MktPayLineApiimpl implements MktPayLineApi
{
    
    @Autowired
    private MktMemberPayDao dao;
    
    @Autowired
    private PayBillManager payBillManager;
    
    @Autowired(required = false)
    private ExcelHelper excelHelper;
    
    @Override
    public Result<PageResult<MktMemberPayOnList>> queryPayLines(int page, int pagesize, String mobile, String startTime,
        String endTime)
    {
        PageResult<MktMemberPayOnList> res = dao.queryPayLines(page, pagesize, mobile, startTime, endTime, CurrentSession.ascriptionPkey());
        return new Result<>(res);
    }
    
    @Override
    public Result<PageResult<PayLineDTO>> queryPayDetail(int page, int pagesize, Boolean buy, Boolean recharge,
        Boolean member, Boolean withdraw, String startTime, String endTime)
    {
        PageResult<PayLineDTO> res = payBillManager.queryPayLines(page, pagesize, buy, recharge, member, withdraw, startTime, endTime, CurrentSession.ascriptionPkey());
        return new Result<>(res);
    }
    
    @Override
    public Result<List<PayDayDTO>> queryDayPay(String startTime, String endTime, String companyPkey, String marketPkey)
    {
        List<PayDayDTO> res = payBillManager.queryPayLinesT(startTime, endTime, companyPkey, marketPkey);
        return new Result<>(res);
    }
    
    @Override
    public Result<List<PayDayDTO>> queryMonthPay(String startTime, String endTime, String companyPkey,
        String marketPkey)
    {
        List<PayDayDTO> res = payBillManager.queryMonthPayT(startTime, endTime, companyPkey, marketPkey);
        return new Result<>(res);
    }
    
    @Override
    public Result<Map<String, Object>> queryPayDetailNumCount(Boolean buy, Boolean recharge, Boolean member,
        Boolean withdraw, String startTime, String endTime)
    {
        Map<String, Object> res = payBillManager.queryPayDetailNumCount(buy, recharge, member, withdraw, startTime, endTime, CurrentSession.ascriptionPkey());
        return new Result<>(res);
    }
    
    @Operation(summary = "对账中心日汇总报表下载", tags = ApiTags.custPayLine)
    @GetMapping(value = "/down/month")
    public void downMonth(HttpServletRequest request, HttpServletResponse response,
        @RequestParam(value = "startTime", required = true) @Parameter(description = "开始时间") String startTime,
        @RequestParam(value = "endTime", required = true) @Parameter(description = "结束时间") String endTime,
        @RequestParam(value = "companyPkey", required = false) @Parameter(description = "公司pkey") String companyPkey,
        @RequestParam(value = "marketPkey", required = false) @Parameter(description = "市场pkey", required = false) String marketPkey)
    {
        OutputStream out = null;
        try
        {
            String fileName = new String("商户明细日报表.xlsx".getBytes(), "iso-8859-1");
            response.setHeader("Content-disposition", "attachment; filename = " + fileName);
            out = response.getOutputStream();
            List<PayDayDTO> list = payBillManager.queryPayLinesT(startTime, endTime, companyPkey, marketPkey);
            excelHelper.exportExcel(list, "Sheet1", out, PayDayExcel.class, null);
            out.flush();
            out.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
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
    
    @Operation(summary = "对账中心月汇总报表下载", tags = ApiTags.custPayLine)
    @GetMapping(value = "/down/year")
    public void downYear(HttpServletRequest request, HttpServletResponse response,
        @RequestParam(value = "startTime", required = true) @Parameter(description = "开始时间") String startTime,
        @RequestParam(value = "endTime", required = true) @Parameter(description = "结束时间") String endTime,
        @RequestParam(value = "companyPkey", required = false) @Parameter(description = "公司pkey") String companyPkey,
        @RequestParam(value = "marketPkey", required = false) @Parameter(description = "市场pkey", required = false) String marketPkey)
    {
        OutputStream out = null;
        try
        {
            String fileName = new String("商户明细月报表.xlsx".getBytes(), "iso-8859-1");
            response.setHeader("Content-disposition", "attachment; filename = " + fileName);
            out = response.getOutputStream();
            List<PayDayDTO> list = payBillManager.queryMonthPayT(startTime, endTime, companyPkey, marketPkey);
            excelHelper.exportExcel(list, "Sheet1", out, PayDayExcel.class, null);
            out.flush();
            out.close();
        }
        catch (IOException e)
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

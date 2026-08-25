package cn.tofocus.lejia.api.v1.vendor;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.data.NamedBean;
import cn.tofocus.core.log.LogApi;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.vendor.BankInfo;
import cn.tofocus.lejia.bean.dto.vendor.ReportInfo;
import cn.tofocus.lejia.bean.dto.vendor.SettlementInfo;
import cn.tofocus.lejia.bean.dto.vendor.SettlementProcess;
import cn.tofocus.lejia.bean.dto.vendor.VendorOrderInfo;
import cn.tofocus.lejia.bean.dto.vendor.VendorSettleDateInfo;
import cn.tofocus.lejia.bean.enums.SettlementType;
import cn.tofocus.lejia.bean.enums.v3.SettleSortType;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.domain.vendor.SettlementManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RequestMapping("/v1/vendor/settlement")
@RestController
public class SettlementApiImpl implements SettlementApi
{
    @Autowired
    private SettlementManager manager;
    
    @Override
    public Result<ReportInfo> queryReport(int page, int pagesize, String startTime, String endTime,
        List<String> marketKeys, SettleSortType sortType, Boolean sort)
    {
        return new Result<>(manager.getReport(page, pagesize, startTime, endTime, marketKeys, sortType, sort));
    }
    
    @LogApi(operation = "商户结算", format = "点击结算时间: {queryTime}, 结算时间: {startTime} - {endTime}  备注: {rem}")
    @Override
    public Result<Boolean> addReport(Date queryTime, String startTime, String endTime, String rem,
        List<String> marketKeys)
    {
        boolean sgin = true;
        sgin = manager.addReport(queryTime, startTime, endTime, rem, marketKeys);
        return new Result<>(sgin);
    }
    
    @Override
    public Result<List<NamedBean>> settlementList(SettlementType type)
    {
        String marketPkey = null;
        return new Result<>(manager.settlementList(marketPkey, type));
    }
    
    @Override
    public Result<SettlementInfo> queryLine(int page, int pagesize, List<String> marketKeys, 
        String startTime, String endTime, SettleSortType sortType, Boolean sort)
    {
        return new Result<>(manager.queryLine(page, pagesize, marketKeys, startTime, endTime, sortType, sort));
    }
    
    @Override
    public Result<List<SettlementProcess>> process(Long linePkey)
    {
        return new Result<>(manager.process(linePkey));
    }
    
    @Operation(summary = "商户结算导出", tags = ApiTags.ZYYSC_VENDOR_ORDER)
    @PostMapping(value = "/export")
    public void export(@RequestParam(name = "startTime") String startTime,
        @RequestParam(name = "endTime") String endTime, 
        @RequestParam(value = "marketKeys", required = false) @Parameter(description = "市场主键")List<String> marketKeys, 
        @RequestParam(value = "sortType", required = false) @Parameter(description = "排序") SettleSortType sortType,
        @RequestParam(value = "sort", required = false, defaultValue = "false") @Parameter(description = "true:正序，false:倒序") Boolean sort,
        HttpServletResponse response)
        throws Exception
    {
        try (OutputStream outputStream = response.getOutputStream();)
        {
            String marketPkey = CurrentSession.marketPkey();
            Integer ascription = CurrentSession.ascriptionPkey();
            if (!(Constant.Operation + ascription).equals(marketPkey) && (marketKeys == null || marketKeys.isEmpty()))
            {
                marketKeys = new ArrayList<>();
                marketKeys.add(marketPkey);
            }
            setXlsxResponse(response, "商户结算.xlsx");
            manager.export(marketKeys, startTime, endTime, sortType, sort, response.getOutputStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    @Operation(summary = "商户结算报表导出", tags = ApiTags.ZYYSC_VENDOR_ORDER)
    @PostMapping(value = "/export/line")
    public void exportLine(@RequestParam(value = "marketKeys", required = false) @Parameter(description = "市场主键")List<String> marketKeys,
        @RequestParam(value = "startTime", required = false) String startTime, 
        @RequestParam(value = "endTime", required = false) String endTime, 
        @RequestParam(value = "sortType", required = false) @Parameter(description = "排序") SettleSortType sortType,
        @RequestParam(value = "sort", required = false, defaultValue = "false") @Parameter(description = "true:正序，false:倒序") Boolean sort,
        HttpServletResponse response)
    {
        try (OutputStream outputStream = response.getOutputStream();)
        {
            manager.exportLine(marketKeys, startTime, endTime, sortType, sort, outputStream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    @Operation(summary = "商户对账导出", tags = ApiTags.ZYYSC_VENDOR_ORDER)
    @PostMapping(value = "/vendorBill/export")
    public void export(
        @RequestParam(value = "startDate", required = false) @Parameter(description = "付款时间-开始") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "付款时间-结束") String endDate,
        @RequestParam(value = "startSettlementDate", required = false) @Parameter(description = "结算时间-开始") String startSettlementDate,
        @RequestParam(value = "endSettlementDate", required = false) @Parameter(description = "结算时间-结束") String endSettlementDate,
        @RequestParam(value = "startVendorTime", required = false) @Parameter(description = "采购时间-开始") String startVendorTime,
        @RequestParam(value = "endVendorTime", required = false) @Parameter(description = "采购时间-结束") String endVendorTime,
        @RequestParam(value = "vendorName", required = false) @Parameter(description = "商户名称") String vendorName,
        @RequestParam(value = "booth", required = false) @Parameter(description = "摊位号") String booth,
        @RequestParam(value = "code", required = false) @Parameter(description = "订单号") String code,
        @RequestParam(value = "status", required = false) @Parameter(description = "结算状态") List<SettlementType> status,
        HttpServletResponse response)
        throws Exception
    {
        try (OutputStream outputStream = response.getOutputStream();)
        {
            String marketPkey = CurrentSession.marketPkey();
            Integer ascription = CurrentSession.ascriptionPkey();
            if ((Constant.Operation + ascription).endsWith(marketPkey)) marketPkey = null;
            setXlsxResponse(response, "商户对账.xlsx");
            manager.exportVendorBill(startDate, endDate, startSettlementDate, endSettlementDate, startVendorTime, endVendorTime, vendorName, booth, code, status, 
                marketPkey, ascription, response.getOutputStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    private void setXlsxResponse(HttpServletResponse response, String fileName)
        throws Exception
    {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;  CHARSET=utf8");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
        response.addHeader("Pargam", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
    }
    
    @Override
    public Result<VendorOrderInfo> check(int page, int pagesize, String startDate, String endDate,
        String startSettlementDate, String endSettlementDate, String startVendorTime, String endVendorTime,
        String vendorName, String booth, String code, List<SettlementType> status)
    {
        String marketPkey = CurrentSession.marketPkey();
        Integer ascription = CurrentSession.ascriptionPkey();
        if ((Constant.Operation + ascription).endsWith(marketPkey)) marketPkey = null;
        VendorOrderInfo res = manager.check(page, pagesize, startDate, endDate, startSettlementDate, endSettlementDate, startVendorTime, endVendorTime, vendorName, booth, code, status, marketPkey, ascription);
        return new Result<>(res);
    }
    
    @Override
    public Result<List<VendorSettleDateInfo>> getDate(List<String> marketKeys)
    {
        return new Result<>(manager.getDate(marketKeys));
    }
    
    @Override
    public Result<BankInfo> getBankInfo(Integer vendor)
    {
        return new Result<>(manager.getBankInfo(vendor));
    }
    
    // 跑批 修改订单的费率
    @PostMapping(value = "/run/commission")
    public Result<Boolean> export(@RequestParam(value = "date", required = false) Date date)
    {
        if (date == null)
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_WEEK, -1);
        }
        manager.runVendorOrderCommissionRate(date);
        return new Result<>(true);
    }

}

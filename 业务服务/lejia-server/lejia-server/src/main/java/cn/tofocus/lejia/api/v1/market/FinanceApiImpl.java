package cn.tofocus.lejia.api.v1.market;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;

import javax.servlet.http.HttpServletResponse;

import cn.tofocus.lejia.zx.beanV2.T21000007ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.common.excel.ExcelHelper;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.FundDetailsListExportExcel;
import cn.tofocus.lejia.bean.dto.SettlementBillExportExcel;
import cn.tofocus.lejia.bean.dto.finance.FundDetailsList;
import cn.tofocus.lejia.bean.dto.finance.FundDetailsTotal;
import cn.tofocus.lejia.bean.dto.finance.SettlementBillOnPage;
import cn.tofocus.lejia.bean.entity.zx.ZxUserInfo;
import cn.tofocus.lejia.bean.enums.SettlementType;
import cn.tofocus.lejia.bean.enums.ZxFileType;
import cn.tofocus.lejia.bean.enums.ZxWithdrawStatus;
import cn.tofocus.lejia.domain.FinanceManager;
import cn.tofocus.lejia.domain.TjZxFileManager;
import cn.tofocus.lejia.domain.TjZxManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RequestMapping("/v1/market/finance")
@RestController
public class FinanceApiImpl
{
    @Autowired
    private FinanceManager manager;
    
    @Autowired
    private TjZxManager tjZxManager;
    
    @Autowired
    private TjZxFileManager tjZxFileManager;
    
    @Autowired
    private ExcelHelper excelHelper;
    
    @Operation(summary = "获取结算账单列表", tags = ApiTags.FINANCE_MANAGER)
    @PostMapping(value = "/query")
    public Result<PageResult<SettlementBillOnPage>> querySettlementBill(
        @RequestParam(value = "page", defaultValue = "0", required = false) @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10", required = false) @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "farmer", required = false) @Parameter(description = "市场主键") String farmer,
        @RequestParam(value = "code", required = false) @Parameter(description = "订单编号") String code,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "开始时间 格式 yyyy-MM-dd") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "结束时间 格式 yyyy-MM-dd") String endDate,
        @RequestParam(value = "settlementType", required = false) SettlementType settlementType)
    {
        PageResult<SettlementBillOnPage> result = manager.querySettlementBill(page, pagesize, farmer, code, startDate, endDate, settlementType);
        return new Result<>(result);
    }
    
    @Operation(summary = "导出结算账单EXCEL", tags = ApiTags.FINANCE_MANAGER)
    @PostMapping(value = "/export/bill")
    public void exportSettlementBill(@RequestParam(value = "farmer", required = false) @Parameter(description = "市场主键") String farmer,
        @RequestParam(value = "code", required = false) @Parameter(description = "订单编号") String code,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "开始时间 格式 yyyy-MM-dd") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "结束时间 格式 yyyy-MM-dd") String endDate,
        @RequestParam(value = "settlementType", required = false) SettlementType settlementType,
        HttpServletResponse response)
    {
        PageResult<SettlementBillOnPage> result = manager.querySettlementBill(0, 50000, farmer, code, startDate, endDate, settlementType);
        OutputStream out = null;
        String fileName;
        try
        {
            fileName = new String("结算账单.xlsx".getBytes(), "iso-8859-1");
            response.setHeader("Content-disposition", "attachment; filename = " + fileName);
            out = response.getOutputStream();
            excelHelper.exportExcel(BeanUtil.beanListFrom(SettlementBillExportExcel.class, result.getContent()), "Sheet1", out, SettlementBillExportExcel.class, null);
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
    
    @Operation(summary = "资金明细-获取账户金额", tags = ApiTags.FINANCE_MANAGER)
    @PostMapping(value = "/details/sum")
    public Result<FundDetailsTotal> byFundDetailsTotal()
    {
        FundDetailsTotal res = manager.byFundDetailsTotal();
        return new Result<>(res);
    }
    
    @Operation(summary = "资金明细-获取账户明细", tags = ApiTags.FINANCE_MANAGER)
    @PostMapping(value = "/details/query")
    public Result<PageResult<FundDetailsList>> queryFundDetails(
        @RequestParam(value = "page", defaultValue = "0", required = false) @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10", required = false) @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "开始时间 格式 yyyy-MM-dd") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "结束时间 格式 yyyy-MM-dd") String endDate,
        @RequestParam(value = "status", required = false) ZxWithdrawStatus status)
    {
        PageResult<FundDetailsList> result = manager.queryFundDetails(page, pagesize, status, startDate, endDate);
        return new Result<>(result);
    }
    
    @Operation(summary = "资金明细-导出账户明细EXCEL", tags = ApiTags.FINANCE_MANAGER)
    @PostMapping(value = "/export/details/query")
    public void exportFundDetails(
        @RequestParam(value = "startDate", required = false) @Parameter(description = "开始时间 格式 yyyy-MM-dd") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "结束时间 格式 yyyy-MM-dd") String endDate,
        @RequestParam(value = "status", required = false) ZxWithdrawStatus status,
        HttpServletResponse response)
    {
        PageResult<FundDetailsList> result = manager.queryFundDetails(0, 50000, status, startDate, endDate);
        OutputStream out = null;
        String fileName;
        try
        {
            fileName = new String("资金明细.xlsx".getBytes(), "iso-8859-1");
            response.setHeader("Content-disposition", "attachment; filename = " + fileName);
            out = response.getOutputStream();
            excelHelper.exportExcel(BeanUtil.beanListFrom(FundDetailsListExportExcel.class, result.getContent()), "Sheet1", out, FundDetailsListExportExcel.class, null);
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
    
    
    @Operation(summary = "资金明细-提现", tags = ApiTags.FINANCE_MANAGER)
    @PostMapping(value = "/details/withdraw")
    public Result<Boolean> fundDetailsWithdraw(@RequestParam(value = "amt")BigDecimal amt)
    {
        Boolean res = tjZxManager.fundDetailsWithdraw(amt);
        return new Result<>(res);
    }
    
    
    // *********************************以下测试接口 查中信数据
    
    // 查询用户及平台商户 余额数据
    @PostMapping(value = "/test/t2206")
    public Result<String> t2206(String userId, String registerAttr)
    {
        String result = tjZxManager.t2206(userId, registerAttr);
        return new Result<>(result);
    }

    // 提现
    // withType:00-用户提现  01-平台提现
    @PostMapping(value = "/test/withdraw")
    public Result<String> withdraw(@RequestParam(value = "userId", required = false) String userId, BigDecimal amt)
    {
        Boolean result = tjZxManager.runWithdraw(userId, amt, userId == null ? "01" : "00");
        if (Boolean.TRUE.equals(result))
            return new Result<>("提现成功");
        else
            return new Result<>("提现失败");
    }

    // 天津提现所有商户余额
    @PostMapping(value = "/test/withdrawAllVendor")
    public Result<String> withdrawAllVendor()
    {
        tjZxManager.withdrawAllVendor();
        return new Result<>("处理完成");
    }

    // 登记簿交易明细查询 
    @PostMapping(value = "/test/t21000029")
    public Result<String> t21000029(String userId, String registerAttr, String date, String transType)
    {
        String result = tjZxManager.t21000029(userId, registerAttr, date, transType);
        return new Result<>(result);
    }

    // 文件处理状态查询
    @PostMapping(value = "/test/t21000032")
    public Result<String> t21000032(String fileName)
    {
        String result = tjZxManager.t21000032(fileName);
        return new Result<>(result);
    }

    // 已有文件上传
    @PostMapping(value = "/test/t21000031")
    public Result<String> t21000031(String fileName, String fileCount)
    {
        tjZxFileManager.sendFile(fileName  + ".ZIP", fileCount);
        return new Result<>("ok");
    }

    // 实时预付 实现平台商户担保交易登记簿和用户登记簿之间的资金划转
    @PostMapping(value = "/test/t22000007")
    public Result<String> t22000007(Integer pkey, String amt, String transType, String bussId)
    {
        tjZxManager.t22000007(pkey, amt, transType, bussId);
        return new Result<>("ok");
    }

    // 登记簿预付 
    @PostMapping(value = "/test/t21000028")
    public Result<String> t21000028(Integer pkey, String amt, String transType, String bussId, String dt, String tm)
    {
        tjZxManager.t21000028(pkey, amt, transType, bussId, dt, tm);
        return new Result<>("ok");
    }
    
    // 文件下载
    @PostMapping(value = "/test/t21000007")
    public Result<T21000007ResponseData> t21000007(String fileName)
    {
        T21000007ResponseData responseData = tjZxFileManager.downloadFile(fileName + ".ZIP");
        return new Result<>(responseData);
    }
    
    // 已有文件上传
    @PostMapping(value = "/test/t21000047")
    public Result<String> t21000047(BigDecimal amount, String userNm, String DEAL_TYPE, 
        String USER_C_ID, String TRANS_DT, String TRANS_TM, String FUND_TP)
    {
    	if(amount == null)
    		amount = BigDecimal.ZERO; 
    	tjZxManager.t21000047(amount, userNm, DEAL_TYPE, USER_C_ID, TRANS_DT, TRANS_TM, FUND_TP);
    	return new Result<>("ok");
    }

    // 重新生成文件
    @PostMapping(value = "/test/regenerateFile")
    public Result<String> regenerateFile(Integer pkey, String nextXuhao)
    {
        tjZxManager.regenerateFile(pkey, nextXuhao);
        return new Result<>("ok");
    }
    
    // 划拨文件处理
    @PostMapping(value = "/test/allocation")
    public Result<Boolean> runAllocation(
        @RequestParam(required = false, defaultValue = "ALLOCATION") ZxFileType type,
        @RequestParam(required = false) String start,
        @RequestParam(required = false) String end)
    {
        tjZxManager.runAllocation(type, start, end);
        return new Result<>(true);
    }

    // 434文件 商户钱包流水表bug 处理
    @PostMapping(value = "/bug/repair")
    public Result<Boolean> bugRepair(@RequestParam(value = "filePkey") Integer filePkey)
    {
        tjZxManager.bugRepair(filePkey);
        return new Result<>(true);
    }
}

package cn.tofocus.lejia.api.v1.vendor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.common.excel.ExcelHelper;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.Result;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.VendorPackingChargeExcel;
import cn.tofocus.lejia.bean.dto.vendor.PackingChargeStatistics;
import cn.tofocus.lejia.bean.dto.vendor.VendorPackingChargeInfo;
import cn.tofocus.lejia.domain.market.VendorOrderPackingChargeManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RequestMapping("/v1/vendor/packingCharge")
@RestController
public class VendorPackingChargeApiImpl
{
    @Autowired
    private VendorOrderPackingChargeManager manager;
    
    @Autowired(required = false)
    private ExcelHelper excelHelper;
    
    @Operation(summary = "打包物料费明细查询", tags = ApiTags.ZYYSC_VENDOR_PACKINGCHARGE)
    @PostMapping(value = "/query")
    public Result<PackingChargeStatistics> queryPackingCharge(@RequestParam(value = "page", defaultValue = "0", required = false) int page,
        @RequestParam(value = "pagesize", defaultValue = "10", required = false) int pagesize,
        @RequestParam(value = "code", required = false)@Parameter(description = "订单编号")String code, 
        @RequestParam(value = "vendorName", required = false)@Parameter(description = "商户名称")String vendorName, 
        @RequestParam(value = "booth", required = false)@Parameter(description = "摊位号")String booth,
        @RequestParam(value = "startDate", required = false) Date startDate, 
        @RequestParam(value = "endDate", required = false) Date endDate)
    {
        PackingChargeStatistics res = manager.queryPackingCharge(page, pagesize, code, vendorName, booth, startDate, endDate);
        return new Result<>(res);
    }
    
    @Operation(summary = "导出打包物料费明细", tags = ApiTags.ZYYSC_VENDOR_PACKINGCHARGE)
    @PostMapping(value = "/export")
    public void exportPackingCharge(
        @RequestParam(value = "code", required = false)@Parameter(description = "订单编号")String code, 
        @RequestParam(value = "vendorName", required = false)@Parameter(description = "商户名称")String vendorName, 
        @RequestParam(value = "booth", required = false)@Parameter(description = "摊位号")String booth,
        @RequestParam(value = "startDate", required = false) Date startDate, 
        @RequestParam(value = "endDate", required = false) Date endDate,
        HttpServletResponse response)
    {
        PackingChargeStatistics res = manager.queryPackingCharge(0, 10000, code, vendorName, booth, startDate, endDate);
        OutputStream out = null;
        try
        {
            String fileName = new String("打包物料费明细.xlsx".getBytes(), "iso-8859-1");
            response.setHeader("Content-disposition", "attachment; filename = " + fileName);
            out = response.getOutputStream();
            excelHelper.exportExcel(BeanUtil.beanListFrom(VendorPackingChargeExcel.class, res.getLines().getContent()),
                "Sheet1", out, VendorPackingChargeExcel.class, null);
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
    
    @Operation(summary = "设置打包物料费", tags = ApiTags.ZYYSC_VENDOR_PACKINGCHARGE)
    @PostMapping(value = "/put")
    public Result<Boolean> putPackingCharge(@RequestBody List<VendorPackingChargeInfo> infos)
    {
        return new Result<>(manager.putPackingCharge(infos));
    }
    
    @Operation(summary = "设置打包物料费", tags = ApiTags.ZYYSC_VENDOR_PACKINGCHARGE)
    @PostMapping(value = "/put/farmer")
    public Result<Boolean> putPackingChargeFarmer(@RequestParam(value = "farmer")String farmer)
    {
        return new Result<>(manager.putPackingChargeFarmer(farmer));
    }
    
}

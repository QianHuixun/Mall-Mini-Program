package cn.tofocus.lejia.bean.dto.order;

import cn.tofocus.core.page.PageResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商户采购报表 dto
 * @author  29701
 * @version  [版本号, 2021年10月14日]
 */
@Data
public class VendorOrderReport
{
    @Schema(description = "总采购笔数")
    private Integer purchaseNum;
    
    @Schema(description = "总采购金数")
    private String purchaseAmt;
    
    @Schema(description = "总采购金数")
    private PageResult<VendorOrderReportLine> lines;
    
    public VendorOrderReport()
    {
        super();
    }
    
    public VendorOrderReport(Integer purchaseNum, String purchaseAmt, PageResult<VendorOrderReportLine> lines)
    {
        super();
        this.purchaseNum = purchaseNum;
        this.purchaseAmt = purchaseAmt;
        this.lines = lines;
    }
    
}

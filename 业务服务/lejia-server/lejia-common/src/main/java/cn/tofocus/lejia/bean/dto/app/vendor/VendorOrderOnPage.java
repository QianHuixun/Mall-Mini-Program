package cn.tofocus.lejia.bean.dto.app.vendor;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.lejia.bean.enums.SettlementType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class VendorOrderOnPage
{
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "订单号")
    private String code;
    
    @Schema(description = "商品名称")
    private String goodsName;
    
    @Schema(description = "规格")
    private String spaceName;
    
    @Schema(description = "数量")
    private Integer num;
    
    @Schema(description = "总价")
    private BigDecimal totalPrice;
    
    @Schema(description = "订单金额")
    private BigDecimal amt;
    
    @Schema(description = "备注")
    private String remark;
    
    @Schema(description = "采购下单时间")
    private Date createdTime;
    
    public String getStatusName()
    {
        if (status != null)
        {
            if (SettlementType.NOT_START.equals(status))
            {
                return "待结算";
            }
            return status.getName();
        }
        return "";
    }
    
    @Schema(description = "开始日期")
    private Date startDate;
    
    @Schema(description = "结束日期")
    private Date endDate;
    
    
    @JsonIgnore
    private SettlementType status;
    
    @JsonIgnore
    private Integer orderPkey;
}

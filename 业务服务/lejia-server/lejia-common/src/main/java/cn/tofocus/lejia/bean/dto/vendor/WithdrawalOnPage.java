package cn.tofocus.lejia.bean.dto.vendor;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.db.dto.JoinEntity;
import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.enums.vendor.WithdrawalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WithdrawalOnPage
{
    @Schema(description = "主键")
    private Integer pkey;
    
    @Schema(description = "对应钱包账户明细主键")
    private Integer lineKey;
    
    @Schema(description = "商户主键")
    private Integer vendorKey;
    
    @JsonIgnore
    @JoinEntity(dataQuery = "mktVendorDao", from = "vendorKey")
    private MktVendor mktVendor;
    
    @Schema(description = "商户名称")
    public String getVendorName()
    {
        if (mktVendor != null) return mktVendor.getDisplayName();
        return "";
    }
    
    @Schema(description = "摊位号")
    public String getBooth()
    {
        if (mktVendor != null) return mktVendor.getBooth();
        return "";
    }
    
    @Schema(description = "打款状态")
    @JoinEnum(from = "status")
    private String statusName;
    
    private WithdrawalStatus status;
    
    @Schema(description = "提现金额")
    private BigDecimal amount;
    
    @Schema(description = "银行")
    private String bankname;
    
    @Schema(description = "持卡人")
    private String bankuser;
    
    @Schema(description = "银行卡号")
    private String bankcard;
    
    @Schema(description = "开户支行名称")
    private String bankBranchName;
    
    @Schema(description = "银行账号")
    private String pan;
    
    @Schema(description = "申请时间")
    private Date createdTime;
}

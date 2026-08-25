package cn.tofocus.lejia.bean.dto.order;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.db.dto.JoinDTO;
import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.enums.SettlementType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商户结算单项数据DTO
 */
@Data
public class SettlementDTO
{
    
    /**
    * 商户订单主键列表
    */
    @Schema(description = "主键列表")
    private List<Integer> pkeys;
    
    /**
     * 采购日期（按日划分）
     */
    @Schema(description = "采购日期")
    private String createdTime;
    
    /**
    * 商户
    */
    @Schema(description = "商户")
    private Integer vendor;
    
    @JoinDTO(dataQuery = "mktVendorDao", from = "vendor")
    @JsonIgnore
    private MktVendor mktVendor;
    
    /**
     * 商户名称
     */
    @Schema(description = "商户名称")
    public String getVendorName()
    {
        if (vendor != null && vendor == 0)
        {
            return "自采";
        }
        if (mktVendor != null)
        {
            return mktVendor.getDisplayName();
        }
        return null;
    }
    
    /**
     * 开户银行名称
     */
    @Schema(description = "开户银行名称")
    public String getBankname()
    {
        if (mktVendor != null)
        {
            return mktVendor.getBankname();
        }
        return null;
    }
    
    /**
     * 银行卡号
     */
    @Schema(description = "银行卡号")
    public String getBankcard()
    {
        if (mktVendor != null)
        {
            return mktVendor.getBankcard();
        }
        return null;
    }
    
    /**
     * 开户支行名称
     */
    @Schema(description = "开户支行名称")
    public String getBankBranchName()
    {
        if (mktVendor != null)
        {
            return mktVendor.getBankBranchName();
        }
        return null;
    }
    
    /**
     * 开户行大额行号
     */
    @Schema(description = "开户行大额行号")
    public String getBankNo()
    {
        if (mktVendor != null)
        {
            return mktVendor.getBankNo();
        }
        return null;
    }
    
    /**
     * 交易总笔数（按照佣金费率分开算）
     */
    @Schema(description = "交易总笔数")
    private Integer tradeCount;
    
    /**
     * 交易总金额（按照佣金费率分开算）
     */
    @Schema(description = "交易总金额")
    private BigDecimal tradePrice;
    
    /**
     * 佣金费率（按照佣金费率分开算）
     */
    @Schema(description = "佣金费率")
    private BigDecimal commissionRate;
    
    /**
     * 交易总佣金(元)（按照佣金费率分开算）
     */
    @Schema(description = "交易总佣金(元)")
    private BigDecimal commissions;
    
    /**
    * （按照佣金费率分开算）
    */
    @Schema(description = "采购/结算总金额(元)")
    private BigDecimal amt;
    
    /**
     * （按照佣金费率分开算）
     */
    @Schema(description = "结算状态英文")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private SettlementType status;
    
    @Schema(description = "结算状态中文")
    @JoinEnum(from = "status")
    private String statusName;
    
    @Schema(description = "结算备注")
    private String settlementRemark;
    
}

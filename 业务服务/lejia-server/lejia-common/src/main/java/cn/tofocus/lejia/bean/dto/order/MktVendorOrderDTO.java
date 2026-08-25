package cn.tofocus.lejia.bean.dto.order;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.db.dto.JoinDTO;
import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.enums.SettlementType;
import cn.tofocus.lejia.bean.enums.VendorOrderType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktVendorOrderDTO
{
    
    /**
    * pkey
    */
    @Schema(description = "pkey")
    @NotNull
    private Integer pkey;

    @JoinDTO(dataQuery = "mktOrderDao", from = "orderPkey")
    @JsonIgnore
    private MktOrder mktOrder;

    /**
     * 订单编号
     */
    @Schema(description = "订单编号")
    public String getCode(){
        if (Objects.nonNull(mktOrder))
        {
            return mktOrder.getCode();
        }
        else
        {
            return null;
        }
    }
    
    /**
    * 订单主键
    */
    @Schema(description = "订单主键")
    private Integer orderPkey;
    
    /**
    * 订单明细主键
    */
    @Schema(description = "订单明细主键")
    private Integer orderLinePkey;
    
    @Schema(description = "订单类型英文")
    private VendorOrderType vendorOrderType;
    
    @Schema(description = "订单类型中文")
    public String getVendorOrderTypeName()
    {
        if (Objects.nonNull(vendorOrderType))
        {
            return vendorOrderType.getName();
        }
        else
        {
            return null;
        }
    }
    
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
        if(vendor != null && vendor == 0)
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
     * 商品
     */
    @Schema(description = "商品ID")
    private Integer goods;
    
    /**
     * 商品名称
     */
    @Schema(description = "商品名称")
    private String goodsName;
    
    @Schema(description = "规格ID")
    private Integer space;
    
    @Schema(description = "规格名称")
    private String spaceName;
    
    /**
     * 数量
     */
    @Schema(description = "数量")
    private Integer num;
    
    /**
    * 采购价格（单价）
    */
    @Schema(description = "采购价格（单价）")
    private BigDecimal price;
    
    @Schema(description = "销售价格（单价）")
    private BigDecimal orderPrice;

    /**
     * 总价
     */
    @Schema(description = "总价")
//    private BigDecimal totalPrice;
    public BigDecimal getTotalPrice()
    {
        if(price != null && num != null)
        {
            return price.multiply(new BigDecimal(num));
        }
        return null;
    }
    /**
     * 佣金费率
     */
    @Schema(description = "佣金费率")
    private BigDecimal commissionRate;
    
    @Schema(description = "佣金费率")
    public String getCommissionRateStr()
    {
        if (commissionRate != null) return commissionRate.stripTrailingZeros().toPlainString() + "%";
        return "-";
    }

    /**
     * 交易佣金
     */
    @Schema(description = "交易佣金")
    private BigDecimal commissions;

    /**
    * 结算金额
    */
    @Schema(description = "结算金额")
    private BigDecimal amt;
    
    /**
     * 采购时间
     */
    @Schema(description = "采购时间")
    private Date createdTime;
    
    /**
    * 备注
    */
    @Schema(description = "备注")
    private String remark;
    
    /**
     * 开户行
     */
    @Schema(description = "开户行")
    public String getBankname()
    {
        if (mktVendor != null)
        {
            return mktVendor.getBankname();
        }
        return null;
    }
    
    /**
     * 开户人
     */
    @Schema(description = "开户人")
    public String getBankuser()
    {
        if (mktVendor != null)
        {
            return mktVendor.getBankuser();
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
    
    
    @Schema(description = "结算状态英文")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private SettlementType status;
    
    @Schema(description = "结算状态中文")
    @JoinEnum(from = "status")
    private String statusName;
    
    /**
    * 市场
    */
    @Schema(description = "市场")
    private String farmer;
    
    /**
    * 公司
    */
    @Schema(description = "公司")
    private String company;
    
    /**
    * 建档员
    */
    @Schema(description = "建档员")
    private Integer updateBy;
}

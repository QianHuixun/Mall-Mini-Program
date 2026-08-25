package cn.tofocus.lejia.bean.dto.v3;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.lejia.bean.enums.DistributionType;
import cn.tofocus.lejia.bean.enums.MType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GwcOrderV3Info
{
    @Schema(description = "配送方式")
    private DistributionType distributionType;
    
    @Schema(description = "商品明细")
    private List<GwcOrderGoodsV3OnList> goodsList;
    
    @Schema(description = "自提地点主键")
    private Integer pickupPkey;
    
    @Schema(description = "配送时间/自提时间")
    private String pstime;

    @Schema(description = "是否允许选择配送时间/自提时间")
    private boolean allowedPickPstime = true;
    
    @Schema(description = "备注")
    private String remark;
    
    @Schema(description = "市场主键")
    private String farmer;
    
    @Schema(description = "市场主键")
    private String farmerName;
    
    @Schema(description = "供应商主键")
    private Integer supplier;
    
    @Schema(description = "供应商主键")
    private String supplierName;
    
    @Schema(description = "推荐人")
    private Integer tjr;
    
    
    // **************************************

    private MType mType;
    
    @Schema(description = "订单金额")
    private BigDecimal sales;
    
    @Schema(description = "订单积分")
    private Integer pointn;
    
    @Schema(description = "商品图片")
    private List<String> goodsPhotos;

    @Schema(description = "原始配送费")
    private BigDecimal originPostage;
    
    @Schema(description = "配送费")
    private BigDecimal postage;
    
    @Schema(description = "是否开启配送")
    private Boolean delivery;
    
    @Schema(description = "是否开启自提")
    private Boolean pickup;
    
    @Schema(description = "起步价")
    private BigDecimal startingPrice;
    
    @Schema(description = "自提地点")
    private List<GwcSupplierPickupLocationInfo> splList;
    
    @Schema(description = "优惠券主键")
    private Integer card;
    
    @Schema(description = "卡券优惠")
    private BigDecimal cardAmt;
    
    @Schema(description = "配送卡券主键")
    private Integer cardPostage;
    
    @Schema(description = "配送卡券名称")
    private String cardPostageName;

    @Schema(description = "配送费优惠金额")
    private BigDecimal cardPostageAmt;
    
    @Schema(description = "卡券编号")
    private String cardCode;
    
    @Schema(description = "是否有卡券可用")
    private Boolean cardUsable = false;
    
    @Schema(description = "是否有配送费卡券可用")
    private Boolean cardPostageUsable = false;
    
    @Schema(description = "是否可以使用卡券可用")
    private Boolean isCard = false;
    
    @JsonIgnore
    private Boolean sf;
}

package cn.tofocus.lejia.bean.dto.v2.card;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import cn.tofocus.db.dto.JoinEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.db.dto.JoinDTO;
import cn.tofocus.lejia.bean.enums.CardCouponType;
import cn.tofocus.lejia.bean.enums.CardStatus;
import cn.tofocus.lejia.bean.enums.CardUserOrderType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MemberCardV2OnList
{
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "标题")
    private String title;
    
    @Schema(description = "状态 初始/已使用/已过期")
    private CardStatus status;
    
    @Schema(description = "卡券价值")
    private BigDecimal cost;
    
    @Schema(description = "最低消费")
    private BigDecimal limitCost;
    
    @Schema(description = "开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;
    
    @Schema(description = "到期日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;
    
    @Schema(description = "市场")
    @JoinDTO(dataQuery = "sysFarmerDao", from = "userFarmer")
    private String userFarmerName;
    
    @JsonIgnore
    private String userFarmer;
    
    @JsonIgnore
    private Integer userVendor;
    
    @Schema(description = "限制商户使用的商户名称")
    private String userVendorName;
    
    @Schema(description = "使用分类")
    @JoinDTO(dataQuery = "mktGtypeDao", from = "userType")
    private String userTypeName;
    
    private Integer userType;
    
    @Schema(description = "使用商品")
    private String userGoodsName;
    
    @JsonIgnore
    private Integer userGoods;
    @JsonIgnore
    private List<Integer> userGoodsList;
    @JsonIgnore
    private List<Integer> userMtype;
    @JsonIgnore
    private Integer ascription;

    @Schema(description = "优惠券编码")
    private String cardNumber;
    
    @Schema(description = "使用订单类型")
    private CardUserOrderType userOrderType;

    @JoinEnum(from = "userOrderType")
    @Schema(description = "使用订单类型名称")
    private String userOrderTypeName;
    
    @Schema(description = "是否失效,false:未失效")
    private Boolean invalid;
    
    @Schema(description = "是否已读")
    private Boolean isRead;
    @JsonIgnore
    private Integer card;
    
    @Schema(description = "优惠券类型")
    private CardCouponType type;
    
    @Schema(description = "免邮费, true免邮费")
    private Boolean avoidPostage;
    
    @Schema(description = "优惠券类型名称")
    @JoinEnum(from = "type")
    private String typeName;
    
    @Schema(description = "限制专区")
    private String mtypeName;
    
    @Schema(description = "不可用使用规则")
    private String notAvailable;
}

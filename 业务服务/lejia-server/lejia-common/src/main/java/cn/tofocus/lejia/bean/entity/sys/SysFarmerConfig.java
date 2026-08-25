package cn.tofocus.lejia.bean.entity.sys;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.core.query.param.valid.ListStrLength;
import cn.tofocus.db.ListConverter;
import cn.tofocus.db.file.FileUrl;
import cn.tofocus.lejia.bean.enums.CommissionType;
import cn.tofocus.lejia.bean.enums.ConfigGoodsType;
import cn.tofocus.lejia.bean.enums.SettlementMethodType;
import cn.tofocus.lejia.bean.enums.v4.DeliveryDate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Entity
@Data
@Table(name = "sys_farmer_config")
@JsonIgnoreProperties({"yjTime", "yjPos"})
public class SysFarmerConfig implements HasPkey<String>
{
    
    @Id
    @Schema(description = "pkey", hidden = true)
    private String pkey;
    
    @Schema(description = "营业状态 营业/休息")
    @Column(name = "y_status")
    private Boolean yStatus;

    @Schema(description = "营业时间起始")
    private String yytb;
    
    @Schema(description = "营业时间结束")
    private String yyte;
    
    @Schema(description = "地址")
    private String addr;
    
    @Schema(description = "经度")
    private BigDecimal longitude;
    
    @Schema(description = "纬度")
    private BigDecimal latitude;
    
    @Schema(description = "夜间时间配置")
    private String yjTime;
    
    @Schema(description = "夜间运费配置")
    private Integer yjPos;
    
    @Schema(description = "配送范围")
    private BigDecimal deliveryRange;
    
    @Schema(description = "配送时间")
    @Convert(converter = ListConverter.class)
    @ListStrLength(length = 1000)
    private List<String> psTime;
    
    @Schema(description = "异常订单数量")
    private Integer abnormalNum;
    
    @Schema(description = "包邮金额")
    private BigDecimal freeDelivery;
    
    @Schema(description = "是否免运费")
    private Boolean isFree;
    
    @Schema(description = "满减运费1")
    private BigDecimal reachOne;
    
    @Schema(description = "满减运费2")
    private BigDecimal reachTwo;
    
    @Schema(description = "减少运费1")
    private BigDecimal reductionDeliveryOne;
    
    @Schema(description = "减少运费2")
    private BigDecimal reductionDeliveryTwo;
    
    @Schema(description = "是否减少运费1")
    private Boolean isReductionOne;
    
    @Schema(description = "是否减少运费2")
    private Boolean isReductionTwo;
    
    @Schema(description = "会员办理图片")
    @FileUrl
    private String memberPhoto;
    
    @Schema(description = "系统自动派单")
    @Column(nullable = false, columnDefinition = "bit")
    private Boolean automaticCourier;
    
    @Schema(description = "系统自动采购")
    @Column(nullable = false, columnDefinition = "bit")
    private Boolean automaticPurchase;
    
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Schema(description = "商户结算方式")
    private SettlementMethodType settlementMethod;
    
    @Schema(description = "配送配置,false: 统一配送")
    @Column(nullable = false, columnDefinition = "bit")
    private Boolean distributionConfig;
    
    @Schema(description = "统一设定金额")
    @Column(precision = 16, scale = 2)
    private BigDecimal fee;
    
    @Schema(description = "起步价")
    @Column(precision = 16, scale = 2)
    private BigDecimal startingPrice;
    
    @Schema(description = "配送日期")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private DeliveryDate deliveryDate;

    @Schema(description = "第三运力应用id")
    private String wanliAppId;
    
    @Schema(description = "第三运力应用密钥")
    private String wanliSecret;
    
    @Schema(description = "第三运力门店id")
    private String storeId;
    
    @Schema(description = "第三运力店铺id")
    private String shopId;

    @Schema(description = "微信客服企业ID")
    @Column(length = 32)
    private String customerServiceId;
    
    @Schema(description = "微信客服链接")
    @Column(length = 100)
    private String customerServiceLink;
    
    @Schema(description = "星期一")
    private Boolean monday;
    
    @Schema(description = "星期二")
    private Boolean tuesday;
    
    @Schema(description = "星期三")
    private Boolean wednesday;
    
    @Schema(description = "星期四")
    private Boolean thursday;
    
    @Schema(description = "星期五")
    private Boolean friday;
    
    @Schema(description = "星期六")
    private Boolean saturday;
    
    @Schema(description = "星期日")
    private Boolean sunday;
    
    @Schema(description = "是否收打包费,true:收")
    private Boolean isPackingCharge;
    
    @Schema(description = "是否是民营企业,true:是")
    private Boolean isEnterprise;
    
    @Schema(description = "手续费承担")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private CommissionType commissionType;
    
    @Schema(description = "集团方抽佣比例")
    private BigDecimal commissionRate;
    
    @Schema(description = "活动用户抽佣比例")
    private BigDecimal memberCommissionRate;
    
    @Schema(description = "商品展示类型")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private ConfigGoodsType goodsType;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}
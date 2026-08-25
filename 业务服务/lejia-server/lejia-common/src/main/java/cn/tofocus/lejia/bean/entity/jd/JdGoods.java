package cn.tofocus.lejia.bean.entity.jd;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.core.query.param.valid.ListStrLength;
import cn.tofocus.db.ListConverter;
import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@Table(name = "jd_goods")
@FieldNameConstants(innerTypeName = "F")
public class JdGoods implements HasPkey<Long>
{
    
    @Id
    @Schema(description = "skuid")
    private Long pkey;
    
    @Schema(description = "分类")
    private Long category;
    
    @Schema(description = "二级分类")
    private Long twoCategory;
    
    @Schema(description = "三级分类")
    private Long threeCategory;
    
    @Schema(description = "分类名称")
    private String categoryName;
    
    @Schema(description = "二级分类名称")
    private String twoCategoryName;
    
    @Schema(description = "三级分类名称")
    private String threeCategoryName;
    
    @Schema(description = "标题")
    private String title;
    
    @Column(length = 10)
    @Schema(description = "标签")
    private String tag;
    
    @Convert(converter = ListConverter.class)
    @ListStrLength(length = 5000)
    @Schema(description = "照片1")
    private List<String> photo1;
    
    @Schema(description = "照片2")
    private String photo2;
    
    @Schema(description = "照片3")
    private String photo3;
    
    @Schema(description = "重量")
    private String weight;
    
    @Schema(description = "售卖单位")
    private String saleUnit;
    
    @Schema(description = "规格型号")
    private String seoModel;
    
    @Schema(description = "标准编号")
    private String serialNumber;
    
    @Schema(description = "描述")
    private String description;
    
    @Schema(description = "正文")
    @Convert(converter = ListConverter.class)
    @ListStrLength(length = 1000)
    private List<String> content;

    // 为长文本字段，在用不到的时候尽量不查出该字段
    @Schema(description = "商品详情-富文本内容")
    @Column(columnDefinition = "text")
    private String content2;

    // 为长文本字段，在用不到的时候尽量不查出该字段
    @Schema(description = "商品详情")
    @Column(columnDefinition = "text")
    private String introduce;

    // 为长文本字段，在用不到的时候尽量不查出该字段
    @Schema(description = "PC商品详情")
    @Column(columnDefinition = "text")
    private String introducePc;

    // 为长文本字段，在用不到的时候尽量不查出该字段
    @Schema(description = "移动端商品详情")
    @Column(columnDefinition = "text")
    private String introduceApp;

    // 为长文本字段，在用不到的时候尽量不查出该字段
    @Schema(description = "微信商品详情")
    @Column(columnDefinition = "text")
    private String introduceWechat;
    
    @Schema(description = "起售日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;
    
    @Schema(description = "到期日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;
    
    @Schema(description = "发货日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date sendDate;
    
    @Schema(description = "浏览数量")
    private Integer viewCount;
    
    @Schema(description = "销售数量")
    private Integer xsNum;
    
    @Schema(description = "限购数量")
    private Integer purchaseNum;
    
    @Schema(description = "最低起购量")
    private Integer lowestBuy;
    
    @Schema(description = "价格")
    private BigDecimal price;
    
    @Schema(description = "税额")
    private BigDecimal taxPrice;
    
    @Schema(description = "京东价，仅供参考")
    private BigDecimal jdPrice;
    
    @Schema(description = "京东销售价，实际下单价格以此为准")
    private BigDecimal salePrice;
    
    @Schema(description = "未税价，当此参数返回null或者返回值小于0时，表示暂无报价，建议客户前台不上架该SKU")
    private BigDecimal nakedPrice;
    
    @Schema(description = "税率")
    private BigDecimal taxRatePercentage;
    
    @Schema(description = "当前商品是否含有促销活动，当返回true时，需要配合【商品促销信息接口】查询对应的商品促销限购数量")
    private Boolean hasPromotion;
    
    @Schema(description = "促销类型；1:到手价 2:一口价")
    private Integer promotionType;
    
    @Schema(description = "促销原价")
    private BigDecimal originalPrice;
    
    @Schema(description = "cid下总可购买次数")
    private Integer limitedNum;
    
    @Schema(description = "cid下剩余可购买次数")
    private Integer remainNum;
    
    @Schema(description = "库存状态,33:有货 现货-下单立即发货 ; 39: 有货 在途-正在内部配货，预计2-6天到达本仓库; 40: 有货 可配货-下单后从有货仓库配货; 36: 预订 ; 34: 无货; 99: 无货开预定，该状态(99)的查询需要依赖合同是否开通'无货开预定'，并且到货周期略长，请谨慎使用。")
    private Integer stockState;
    
    @Schema(description = "主站上下架状态 (1上架 0下架)")
    private Integer skuState;
    
    @Schema(description = "主商品ID")
    private Long spuId;
    
    @Schema(description = "主商品名称")
    private String spuName;
    
    @Column(columnDefinition = "tinyint")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Schema(description = "用户可见范围")
    private MemberVisibleRange visibleRange;
    
    @Schema(description = "排序字段")
    private Integer sort;
    
    @Schema(description = "备注")
    private String remark;
    
    @Schema(description = "市场")
    private String farmer;
    
    @Schema(description = "启用标志")
    private Boolean enabled;
    
    @Schema(description = "是否已删除")
    private Boolean idDel;
    
    @Schema(description = "最后更新时间")
    @LastModifiedDate
    private Date updateTime;
    
    @Schema(description = "归属主键")
    private Integer ascription;
    
    @Schema(description = "规格")
    private String space1;
    
    @Schema(description = "规格")
    private String space2;
    
    @Schema(description = "规格")
    private String space3;
    
    @Schema(description = "规格")
    private String space4;
    
    @Schema(description = "规格")
    private String space5;
    
    @Schema(description = "规格")
    private String space6;
    
    @Schema(description = "规格")
    private String space7;
    
    @Schema(description = "规格")
    private String space8;
    
    @Schema(description = "规格")
    private String space9;
    
    @Schema(description = "规格")
    private String space10;
    
    @Schema(description = "商品池")
    private String bizPoolId;
}

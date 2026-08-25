package cn.tofocus.lejia.bean.entity.goods;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.core.query.param.valid.ListStrLength;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.db.ListConverter;
import cn.tofocus.db.file.FileUrl;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * 商品(在售)
 *
 * @author lai 2020-06-15
 */

@Entity
@Data
@Table(name = "mkt_goods")
@FieldNameConstants(innerTypeName = "F")
public class MktGoods implements HasPkey<Integer>
{

    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_goods")
    @Schema(description = "pkey")
    private Integer pkey;

    @Schema(description = "分类")
    private Integer gtype;

    @Schema(description = "商品库")
    private Integer goodsMain;
    
    @Schema(description = "三级分类")
    private Integer threeGtype;
    
    @Schema(description = "商户主键")
    private Integer vendor;

    @Schema(description = "供应商主键")
    @Column
    private Integer supplier;

    @Schema(description = "商品属性：积分/市场/会员/特价/分享/砍价/团购/预售")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private MType mType;
    
    @Schema(description = "是否可加工")
    private Boolean isProcess;

    @Schema(description = "标题")
    private String title;

    @Column(length = 10)
    @Schema(description = "标签")
    private String tag;

    @Schema(description = "照片1")
    @FileUrl
    @Convert(converter = ListConverter.class)
    @ListStrLength(length = 1000)
    private List<String> photo1;

    @Schema(description = "照片2")
    @FileUrl
    private String photo2;

    @Schema(description = "照片3")
    @FileUrl
    private String photo3;

    @Schema(description = "标准编号")
    private String serialNumber;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "正文")
    @FileUrl
    @Convert(converter = ListConverter.class)
    @ListStrLength(length = 1000)
    private List<String> content;
    
    @Schema(description = "商品详情-富文本内容")
    @Column(columnDefinition = "text")
    private String content2;

    @Schema(description = "起售日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;

    @Schema(description = "到期日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;

    @Schema(description = "发货日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date sendDate;
    
//    @Schema(description = "发货结束日期")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
//    private Date sendEndDate;

    @Schema(description = "浏览数量")
    private Integer viewCount;

    @Schema(description = "销售数量")
    private Integer xsNum;

    // 如果超出限购数量报错，请使用LejiaErrCode.GOODS_NUM_PURCHASENUM，前端有逻辑判断该异常code
    @Schema(description = "限购数量")
    private Integer purchaseNum;
    
    @Schema(description = "扩展内容  砍价、团购使用")
    private String extendCon;
    
    @Schema(description = "价格", required = true)
    private BigDecimal price;

    @Schema(description = "是否免邮", required = true)
    private Boolean isPostage;

    @Column(columnDefinition = "tinyint")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Schema(description = "用户可见范围")
    private MemberVisibleRange visibleRange;
    
    @Schema(description = "排序字段", required = true)
    private Integer sort;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "市场")
    private String farmer;

    @Schema(description = "公司")
    private String company;

    @Schema(description = "启用标志")
    private Boolean enabled;

    @Schema(description = "是否已删除")
    private Boolean idDel;

    @Schema(description = "最后更新时间")
    @LastModifiedDate
    private Date updateTime;

    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;

    @Schema(description = "建档员")
    @CreatedBy
    private Integer createdBy;

    @Schema(description = "版本")
    @Column(nullable = false, columnDefinition = "smallint(6)")
    private Integer rowVension;

    @Schema(description = "是否“猜我喜欢”")
    @Column(columnDefinition = "tinyint(4)")
    private Boolean guessLike;
    
    @Schema(description = "是否自提")
    private Boolean pickupType;
    
    @Schema(description = "猜我喜欢-排序")
    private Integer guessSort;

    @Schema(description = "是否专区推荐")
    @Column(columnDefinition = "tinyint(4)")
    private Boolean zoneRecommend;
    
    @Schema(description = "归属主键")
    private Integer ascription;

}

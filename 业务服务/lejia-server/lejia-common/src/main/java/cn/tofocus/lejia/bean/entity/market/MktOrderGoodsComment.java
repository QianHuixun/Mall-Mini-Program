package cn.tofocus.lejia.bean.entity.market;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.core.query.param.valid.ListStrLength;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.db.ListConverter;
import cn.tofocus.db.file.FileUrl;
import cn.tofocus.lejia.bean.enums.CommentApplyStatus;
import cn.tofocus.lejia.bean.enums.CommentReplyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * @author czy
 * @version [版本号, 2025/7/29]
 */
@Data
@Entity
@Schema(description = "订单明细评价")
@Table(name = "mkt_order_goods_comment")
@FieldNameConstants(innerTypeName = "F")
public class MktOrderGoodsComment implements HasPkey<Integer>
{
    @Id
    @Column
    @Schema(description = "主键")
    @AutoRedisID(domain = "zyysc", sequence = "mkt_order_goods_comment")
    private Integer pkey;
    
    @Schema(description = "订单主键")
    @Column
    private Integer orderPkey;
    
    @Schema(description = "评价用户")
    @Column
    private Integer member;
    
    @Schema(description = "分类")
    @Column
    private Integer gtype;
    
    @Schema(description = "商品pkey")
    @Column
    private Integer goods;
    
    @Schema(description = "商品名称")
    @Column(length = 100)
    private String goodsName;
    
    @Schema(description = "评分")
    @Column
    private Integer score;
    
    @Schema(description = "内容")
    @Column(length = 300)
    private String content;

    @Schema(description = "图片")
    @FileUrl
    @Convert(converter = ListConverter.class)
    @ListStrLength(length = 1000)
    @Column(length = 1000, columnDefinition = "varchar(1000)")
    private List<String> photo;
    
    @Schema(description = "回复内容")
    @Column(length = 300)
    private String replyContent;
    
    @Column(columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Schema(description = "回复状态")
    private CommentReplyStatus replyStatus;

    @Schema(description = "回复时间")
    @Column
    private Date replyTime;
    
    @Column(columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Schema(description = "审核状态")
    private CommentApplyStatus applyStatus;
    
    @Schema(description = "市场")
    @Column(length = 40)
    private String farmer;
    
    @Schema(description = "公司")
    @Column(length = 40)
    private String company;
    
    @Schema(description = "建档时间")
    @CreatedDate
    @Column
    private Date createdTime;
    
    @Schema(description = "归属主键")
    @Column
    private Integer ascription;
    
    public static MktOrderGoodsComment of(MktOrderLine orderLine)
    {
        MktOrderGoodsComment bean = new MktOrderGoodsComment();
        bean.setOrderPkey(orderLine.getOrderPkey());
        bean.setGtype(orderLine.getGtype());
        bean.setGoods(orderLine.getGoods().intValue());
        bean.setGoodsName(orderLine.getGoodsName());
        bean.setFarmer(orderLine.getFarmer());
        bean.setCompany(orderLine.getCompany());
        bean.setAscription(orderLine.getAscription());
        return bean;
    }
}

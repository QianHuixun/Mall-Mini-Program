package cn.tofocus.lejia.bean.entity.goods;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.lejia.bean.enums.GoodsRecommendZone;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

/**
 * @author czy
 * @version [版本号, 2025/7/8]
 */
@Data
@Entity
@Schema(description = "推荐商品区域关联")
@Table(name = "mkt_goods_recommend_zone")
@FieldNameConstants(innerTypeName = "F")
public class MktGoodsRecommendZone implements HasPkey<String>
{
    @Id
    @Column(length = 40)
    @Schema(description = "主键")
    private String pkey;
    
    @Column
    @Schema(description = "推荐商品主键")
    private Integer goodsRecommend;
    
    @Column(columnDefinition = "tinyint")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Schema(description = "推荐区域")
    private GoodsRecommendZone zone;

    @Schema(description = "市场")
    @Column(length = 40, nullable = false)
    private String farmer;

    @Schema(description = "公司")
    @Column(length = 40, nullable = false)
    private String company;

    @Schema(description = "建档时间")
    @Column(nullable = false)
    @CreatedDate
    private Date createdTime;

    @Schema(description = "归属主键")
    @Column(nullable = false)
    private Integer ascription;
    
    public String makePkey(Integer goodsRecommend, GoodsRecommendZone zone)
    {
        return goodsRecommend + "_" + zone.getIndex();
    }

    public void setPkey(String pkey)
    {
        this.pkey = pkey;
    }

    public void setPkey(Integer goodsRecommend, GoodsRecommendZone zone)
    {
        setGoodsRecommend(goodsRecommend);
        setZone(zone);
        setPkey(makePkey(goodsRecommend, zone));
    }
}

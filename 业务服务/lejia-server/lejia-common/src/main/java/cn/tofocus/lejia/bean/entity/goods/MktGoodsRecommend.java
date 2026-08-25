package cn.tofocus.lejia.bean.entity.goods;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * @author czy
 * @version [版本号, 2025/7/8]
 */
@Data
@Entity
@Schema(description = "推荐商品")
@Table(name = "mkt_goods_recommend")
@FieldNameConstants(innerTypeName = "F")
public class MktGoodsRecommend implements HasPkey<Integer>
{
    @Id
    @Column
    @Schema(description = "主键")
    @AutoRedisID(domain = "zyysc", sequence = "mkt_goods_recommend")
    private Integer pkey;
    
    @Column
    @Schema(description = "商品主键")
    private Integer goods;
    
    @Column
    @Schema(description = "排序")
    private Integer sort;

    @Column(length = 40)
    @Schema(description = "商品所属市场，冗余")
    private String goodsFarmer;

    // 为空，表示运营端配置默认推荐商品；不为空，表示市场商品推荐商品
    @Column
    @Schema(description = "来源商品主键")
    private Integer sourceGoods;
    
    @Schema(description = "市场")
    private String farmer;
    
    @Schema(description = "公司")
    private String company;
    
    @Schema(description = "更新时间")
    @LastModifiedDate
    private Date updatedTime;
    
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
    
    @Schema(description = "建档员")
    @CreatedBy
    private Integer createdBy;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}

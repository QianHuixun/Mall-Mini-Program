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
 * @version [版本号, 2025/7/15]
 */
@Data
@Entity
@Schema(description = "商品卖点")
@Table(name = "mkt_goods_selling_point")
@FieldNameConstants(innerTypeName = "F")
public class MktGoodsSellingPoint implements HasPkey<Integer>
{
    @Id
    @Column
    @Schema(description = "主键")
    @AutoRedisID(domain = "zyysc", sequence = "mkt_goods_selling_point")
    private Integer pkey;
    
    @Column
    @Schema(description = "商品主键")
    private Integer goods;
    
    @Column(length = 10)
    @Schema(description = "名称")
    private String name;
    
    @Column(length = 10)
    @Schema(description = "内容")
    private String content;

    @Column
    @Schema(description = "最后更新时间")
    @LastModifiedDate
    private Date updatedTime;

    @Column
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;

    @Column
    @Schema(description = "建档员")
    @CreatedBy
    private Integer createdBy;

    @Column
    @Schema(description = "归属主键")
    private Integer ascription;
}

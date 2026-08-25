package cn.tofocus.lejia.bean.entity.market;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
*  购物车
* @author zdw 2020-07-16
*/

@Entity
@Data
@Table(name = "mkt_gwc")
@FieldNameConstants(innerTypeName = "F")
public class MktGwc implements HasPkey<Integer>
{
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_gwc")
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "用户")
    @Column(name = "member_key")
    private Integer member;
    
    @Schema(description = "商品")
    private Integer goods;
    
    @Schema(description = "规格")
    private Integer space;
    
    @Schema(description = "数量")
    private Integer num;
    
    @Schema(description = "关联主键")
    private Integer association;
    
    @Schema(description = "关联名称")
    private String associationName;
    
    @Schema(description = "京东商品主键")
    private Long skuId;
    
    @Schema(description = "京东商品spu主键")
    private Long spuId;
    
    @Schema(description = "是否是京东商品")
    private Boolean isJd;
    
    @Schema(description = "市场")
    private String farmer;
    
    @Schema(description = "公司")
    private String company;
    
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
    
    @Schema(description = "归属主键")
    private Integer ascription;
    
}

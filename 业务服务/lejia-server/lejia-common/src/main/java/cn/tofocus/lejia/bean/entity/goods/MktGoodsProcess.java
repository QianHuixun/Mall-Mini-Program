package cn.tofocus.lejia.bean.entity.goods;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 
 * 加工商品和普通商品关联
 */
@Entity
@Data
@Table(name = "mkt_goods_process")
public class MktGoodsProcess implements HasPkey<Integer>
{
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_goods_process")
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "商品")
    private Integer goods;
    
    @Schema(description = "对应加工商品主键")
    private Integer process;
    
    @Schema(description = "市场")
    private String farmer;
    
    @Schema(description = "公司")
    private String company;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}

package cn.tofocus.lejia.bean.entity.h5;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.db.file.FileUrl;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@Table(name = "h5_goods_space")
@FieldNameConstants(innerTypeName = "F")
public class H5GoodsSpace implements HasPkey<Integer>
{
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "h5_goods_space")
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "商品")
    private Integer goods;
    
    @Schema(description = "规格")
    private String space;
    
    @Schema(description = "照片1")
    @FileUrl
    private String photo1;
    
    @Schema(description = "毛重")
    private BigDecimal weight;
    
    @Schema(description = "原价")
    private BigDecimal priceOld;
    
    @Schema(description = "价格")
    private BigDecimal price;
    
    @Schema(description = "库存数量")
    private Integer kcNum;

    @Schema(description = "销售数量")
    private Integer xsNum;
    
    @Schema(description = "门锁密码时间-开始")
    private Date boxSd;
    
    @Schema(description = "门锁密码时间-结束")
    private Date boxEd;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}

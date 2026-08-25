package cn.tofocus.lejia.bean.entity.jd;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.common.cachemap.bean.HasPkey;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@Table(name = "jd_goods_service")
@FieldNameConstants(innerTypeName = "F")
public class JdGoodsService implements HasPkey<String>
{
    @Id
    @Schema(description = "pkey")
    private String pkey;
    
    @Schema(description = "内容")
    private String content;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}

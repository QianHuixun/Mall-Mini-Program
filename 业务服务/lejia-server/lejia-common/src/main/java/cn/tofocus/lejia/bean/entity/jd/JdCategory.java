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
@Table(name = "jd_category")
@FieldNameConstants(innerTypeName = "F")
public class JdCategory implements HasPkey<Long>
{
    @Id
    @Schema(description = "pkey")
    private Long pkey;
    
    @Schema(description = "0：一级分类；1：二级分类；2：三级分类")
    private Integer categoryLevel;
    
    @Schema(description = "父分类ID")
    private Long parentId;
    
    @Schema(description = "当前分类名称")
    private String categoryName;
    
    @Schema(description = "1：有效；0：无效")
    private Integer needShow;
    
    @Schema(description = "排序")
    private Integer orderSort;

    // 关联的商城二级分类 pkey（mkt_goods_main.pkey），建立在该京东二级分类（categoryLevel=1）之上
    @Schema(description = "商城分类")
    private Integer mallCategory;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}

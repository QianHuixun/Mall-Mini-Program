package cn.tofocus.lejia.bean.entity.goods;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoUUID;
import cn.tofocus.lejia.bean.enums.GoodsSortType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@Table(name = "mkt_three_gtype_sort", indexes = {@Index(name = "idx_gtype", columnList = "gtype"),
    @Index(name = "idx_goodsMain", columnList = "goodsMain"),
    @Index(name = "idx_threeGtype", columnList = "threeGtype"),
    @Index(name = "idx_groupByGtype", columnList = "farmer,gtypeEnable,goodsMainEnable,threeGtypeEnable,sortType,gtypeSort,goodsMainSort,sortValue"),
    @Index(name = "idx_groupByGoodsMain", columnList = "farmer,gtypeEnable,goodsMainEnable,threeGtypeEnable,sortType,gtype,goodsMainSort,sortValue"),})
@FieldNameConstants(innerTypeName = "F")
public class ThreeGtypeSortEntity implements HasPkey<String>
{
    @Id
    @AutoUUID
    private String pkey;
    
    /**
     * 以下为筛选使用的threeGtype冗余属性
     */
    @Schema(description = "三级分类")
    private Integer threeGtype;
    
    @Schema(description = "三级分类是否启用")
    private boolean threeGtypeEnable;
    
    @Schema(description = "市场")
    private String farmer;
    
    @Schema(description = "三级分类排序")
    private Integer threeGtypeSort;
    
    /**
     * 以下为threeGtype排序条件
     */
    @Schema(description = "排序条件")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private GoodsSortType sortType;
    
    @Schema(description = "排序值")
    private BigDecimal sortValue;
    
    /**
     * 以下为筛选使用的gtype冗余属性
     */
    @Schema(description = "一级分类")
    private Integer gtype;
    
    @Schema(description = "一级分类是否启用")
    private boolean gtypeEnable;
    
    @Schema(description = "一级分类排序")
    private Integer gtypeSort;
    
    /**
     * 以下为筛选使用的goodsMain冗余属性
     */
    @Schema(description = "二级分类")
    private Integer goodsMain;
    
    @Schema(description = "二级分类是否启用")
    private boolean goodsMainEnable;
    
    @Schema(description = "二级分类排序")
    private Integer goodsMainSort;
    
    /**
     * 以下为三级分类的显示商品
     */
    @Schema(description = "商品主键")
    private Integer goods;
    
    @Schema(description = "规格主键")
    private Integer space;
    
    @Schema(description = "商户主键")
    private Integer vendor;
    
}

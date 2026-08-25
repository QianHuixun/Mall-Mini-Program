package cn.tofocus.lejia.bean.entity.goods;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商品库二级分类
 *
*/

@Entity
@Data
@Table(name = "mkt_goods_main")
@FieldNameConstants(innerTypeName = "F")
public class MktGoodsMain implements HasPkey<Integer>
{
    /**
     * pkey
     */
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_goods_main")
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "分类")
    private Integer gtype;

    @Schema(description = "关联运营端二级分类")
    private Integer sysTwoGtype;
    
    @Transient
    private String gtypeName;
    
    @Schema(description = "名称")
    private String name;
    
    /**
    * 排序
    */
    @Schema(description = "排序")
    private Integer sort;
    
    /**
    * 备注
    */
    @Schema(description = "备注")
    private String remark;
    
    /**
    * 启用标志
    */
    @Schema(description = "启用标志")
    private Boolean enabled;
    
    /**
    * 是否已删除
    */
    @Schema(description = "是否已删除")
    private Boolean idDel;
    
    @Schema(description = "市场")
    private String farmer;
    
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
    
    @Schema(description = "建档员")
    @CreatedBy
    private Integer createdBy;
    
    @Schema(description = "版本")
    @Column(nullable = false, columnDefinition = "smallint(6)")
    private Integer rowVension;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}

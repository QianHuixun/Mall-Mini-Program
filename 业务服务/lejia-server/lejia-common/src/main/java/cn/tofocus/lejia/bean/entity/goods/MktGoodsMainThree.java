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
 * 商品库三级分类
 *
*/

@Entity
@Data
@Table(name = "mkt_goods_main_three")
@FieldNameConstants(innerTypeName = "F")
public class MktGoodsMainThree implements HasPkey<Integer>
{
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_goods_main_three")
    @Schema(description = "pkey")
    private Integer pkey;
    
    /**
    * 一级分类pkey（mkt_gtype）
    */
    @Schema(description = "分类")
    private Integer gtype;
    
    @Transient
    private String gtypeName;
    
    @Schema(description = "二级分类")
    private Integer twoGtype;
    
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
    
    /**
    * 建档时间
    */
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
    
    /**
    * 建档员
    */
    @Schema(description = "建档员")
    @CreatedBy
    private Integer createdBy;
    
    /**
    * 版本
    */
    @Schema(description = "版本")
    @Column(nullable = false, columnDefinition = "smallint(6)")
    private Integer rowVension;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}

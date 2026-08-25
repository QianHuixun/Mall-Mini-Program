package cn.tofocus.lejia.bean.entity.market;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.db.file.FileUrl;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  分类
*/

@Entity
@Data
@Table(name = "mkt_gtype")
@FieldNameConstants(innerTypeName = "F")
public class MktGtype implements HasPkey<Integer>
{
    
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_gtype")
    @Schema(description = "pkey")
    private Integer pkey;
    
    /**
    * 名称
    */
    @Schema(description = "名称")
    private String name;
    
    /**
    * 图标
    */
    @Schema(description = "图标")
    @FileUrl
    private String photo;
    
    /**
    * 排序
    */
    @Schema(description = "排序")
    private Integer sort;
    
    @Schema(description = "市场排序")
    private Integer marketSort;
    
    @Schema(description = "积分排序")
    private Integer pointSort;
    
    /**
     * 积分商城
     */
    @Schema(description = "积分商城")
    private Boolean showPoint;
    
    /**
     * 市场商城
     */
    @Schema(description = "市场商城")
    private Boolean showMarket;
    
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
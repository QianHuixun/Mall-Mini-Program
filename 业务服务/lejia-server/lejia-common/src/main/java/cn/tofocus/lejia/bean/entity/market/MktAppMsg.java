package cn.tofocus.lejia.bean.entity.market;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  mkt_app_msg
* @author lai 2020-06-15
*/

@Entity
@Data
@Table(name = "mkt_app_msg")
public class MktAppMsg implements HasPkey<Integer>
{
    
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_app_msg")
    /**
    * pkey
    */
    @Schema(description = "pkey")
    private Integer pkey;
    
    /**
    * 主题
    */
    @Schema(description = "主题")
    private String title;
    
    /**
    * 底部标语
    */
    @Schema(description = "底部标语")
    private String bottom;
    
    /**
    * 联系电话
    */
    @Schema(description = "联系电话")
    private String tel;
    
    /**
    * 介绍
    */
    @Schema(description = "介绍")
    private String content;
    
    /**
    * 最后更新时间
    */
    @Schema(description = "最后更新时间")
    private Date updateTime;
    
    /**
    * 建档时间
    */
    @Schema(description = "建档时间")
    private Date createdTime;
    
    /**
    * 建档员
    */
    @Schema(description = "建档员")
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
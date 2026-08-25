package cn.tofocus.lejia.bean.entity.sys;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.db.file.FileUrl;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  推广
* @author zdw 2022-05-25
*/

@Entity
@Data
@Table(name = "mkt_promote")
public class MktPromote implements HasPkey<Integer>
{
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_promote")
    @Schema(description = "pkey", required = true)
    private Integer pkey;
    
    @Schema(description = "标题", required = false)
    private String title;
    
    @Schema(description = "内容", required = false)
    private String content;
    
    @Schema(description = "图片", required = false)
    @FileUrl
    private String photo;
    
    @Schema(description = "启用标志", required = true)
    private Boolean enabled;
    
    @Schema(description = "市场")
    @Column(length = 40)
    private String farmer;
    
    @Schema(description = "更新时间", required = true)
    @LastModifiedDate
    private Date updatedTime;
    
    @Schema(description = "建档时间", required = true)
    @CreatedDate
    private Date createdTime;
    
    @Schema(description = "归属主键")
    private Integer ascription;
    
}
package cn.tofocus.lejia.bean.entity.zx;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  向中信请求记录
* @author zdw 2021-12-07
*/

@Entity
@Data
@Table(name = "zx_post_record")
public class ZxPostRecord implements HasPkey<Integer>
{
    
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "zx_post_record")
    @Schema(description = "pkey", required = true)
    private Integer pkey;
    
    @Schema(description = "请求接口", required = false)
    private String reqInterface;
    
    @Schema(description = "请求内容", required = false)
    @Column(columnDefinition = "text")
    private String reqContent;
    
    @Schema(description = "返回结果", required = false)
    @Column(columnDefinition = "text")
    private String content;
    
    @Schema(description = "返回时间", required = false)
    private Date time;
    
    @Schema(description = "创建时间", required = false)
    @CreatedDate
    private Date createdTime;
    
    @Schema(description = "建档员", required = false)
    @CreatedBy
    private Integer createdBy;
    
}
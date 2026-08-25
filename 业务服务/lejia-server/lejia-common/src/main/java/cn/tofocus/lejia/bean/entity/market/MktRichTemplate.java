package cn.tofocus.lejia.bean.entity.market;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.bean.enums.RichType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  富文本模板
* @author zdw 2021-12-01
*/

@Entity
@Data
@Table(name = "mkt_rich_template")
public class MktRichTemplate implements HasPkey<Integer>
{
    
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_rich_template")
    @Schema(description = "pkey", required = true)
    private Integer pkey;
    
    @Schema(description = "模板类型", required = false)
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private RichType type;
    
    @Schema(description = "模板内容", required = false)
    @Column(columnDefinition = "text")
    private String content;
    
    @Schema(description = "市场主键", required = true)
    private String farmer;
    
    @Schema(description = "建档时间", required = true)
    @LastModifiedDate
    private Date updatedTime;
    
    @Schema(description = "建档时间", required = true)
    @CreatedDate
    private Date createdTime;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}
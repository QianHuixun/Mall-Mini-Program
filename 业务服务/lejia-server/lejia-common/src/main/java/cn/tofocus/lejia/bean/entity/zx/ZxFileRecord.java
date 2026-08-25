package cn.tofocus.lejia.bean.entity.zx;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.bean.enums.ZxFileStatus;
import cn.tofocus.lejia.bean.enums.ZxFileType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Entity
@Data
@Table(name = "zx_file_record")
public class ZxFileRecord implements HasPkey<Integer>
{
    
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "zx_file_record")
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "结算主键")
    private String settlementKey;
    
    @Schema(description = "文件名称")
    private String name;
    
    @Schema(description = "文件路径")
    private String savePath;
    
    @Schema(description = "文件类型")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private ZxFileType type;
    
    @Schema(description = "文件内容")
    @Column(columnDefinition = "text")
    private String content;
    
    @Schema(description = "文件状态")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private ZxFileStatus status;
    
    @Schema(description = "异常内容")
    private String abnormalContent;
    
    @Schema(description = "文件上传时间")
    private Date uploadDate;
    
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}
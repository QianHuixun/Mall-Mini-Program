package cn.tofocus.file.db.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.file.bean.FileInfoV3;
import cn.tofocus.file.bean.UploadType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@Entity
//@formatter:off
@Table(name = "file_ref2", 
    indexes = {@Index(name = "idx_time", columnList = "createdTime"),
               @Index(name = "idx_md5", columnList = "md5,size")})
//@formatter:on
@FieldNameConstants(innerTypeName = "F")
@Schema(description = "文件引用")
public class FileRef2Entity implements HasPkey<Long>
{
    @Id
    @Column(nullable = false)
    private Long pkey; //自增主键
    
    @NotNull
    @Schema(description = "文件内容的md5")
    @Column(nullable = false, length = 32)
    private String md5; //文件内容的md5
    
    @NotNull
    @Schema(description = "文件大小")
    @Column(nullable = false)
    private long size; //文件大小
    
    @NotNull
    @Schema(description = "类型")
    @Column(nullable = false, columnDefinition = "tinyint")
    private UploadType type;
    
    @Schema(description = "原文件名")
    @Size(max = 255)
    @Column(length = 255)
    private String fileName; //原文件名
    
    @Schema(description = "扩展名")
    @Size(max = 40)
    @Column(length = 40)
    private String extName; //扩展名
    
    @Schema(description = "标题")
    @Size(max = 255)
    @Column(length = 255)
    private String title; //标题
    
    @Schema(description = "备注")
    @Size(max = 255)
    @Column(length = 255)
    private String memo;
    
    @Schema(description = "来源应用")
    @Size(max = 40)
    @Column(length = 40)
    private String appid; //来源应用
    
    @Schema(description = "来源用户")
    private Long userkey; //来源用户
    
    @CreatedDate
    @Column(updatable = false)
    @Schema(description = "创建时间")
    private Date createdTime;
    
    public FileInfoV3 toFileInfo(String baseUrl)
    {
        FileInfoV3 info = new FileInfoV3(pkey, baseUrl, type.viewUrl(), type.downUrl(), extName, md5);
        info.setCreatedTime(createdTime);
        info.setExtName(extName);
        info.setFileName(fileName);
        info.setMd5(md5);
        info.setSize(size);
        info.setTitle(title);
        info.setMemo(memo);
        return info;
    }
}

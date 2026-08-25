package cn.tofocus.file.db.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.file.db.key.FileRecordKey;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@Entity
@Table(name = "file_record")
@FieldNameConstants(innerTypeName = "F")
@IdClass(FileRecordKey.class)
@Schema(description = "文件记录")
public class FileRecordEntity implements HasPkey<FileRecordKey>
{
    @Id
    @Schema(description = "文件内容的md5")
    @Column(length = 32)
    @Size(max = 32)
    private String md5; //文件内容的md5
    
    @Id
    @Schema(description = "文件大小")
    private long size; //文件大小

    @Schema(description = "内容MIME类型")
    @Column(length = 100)
    @Size(max = 100)
    private String contentType; //内容MIME类型

    @Schema(description = "宽")
    private Integer width;

    @Schema(description = "高")
    private Integer height;

    @CreatedDate
    @Schema(description = "创建时间")
    @Column(updatable = false)
    private Date createdTime;
    
    @Override
    public FileRecordKey getPkey()
    {
        return new FileRecordKey(md5, size);
    }

    @Override
    public void setPkey(FileRecordKey pkey)
    {
        md5 = pkey.getMd5();
        size = pkey.getSize();
    }
    
    public boolean isImage()
    {
        return contentType != null && contentType.startsWith("image");
    }

}

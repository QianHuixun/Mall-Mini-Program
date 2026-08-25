package cn.tofocus.file.bean;

import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FileRecordInfo
{
    @Schema(description = "文件内容的md5")
    private String md5; //文件内容的md5
    
    @Schema(description = "文件大小")
    private long size; //文件大小
    
    @Schema(description = "内容MIME类型")
    private String contentType; //内容MIME类型
    
    @Schema(description = "创建时间")
    private Date createdTime;
}

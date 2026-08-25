package cn.tofocus.file.bean;

import java.util.Date;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "文件记录")
public class FileRecord
{
    @Schema(description = "文件内容的md5")
    private String md5; //文件内容的md5
    
    @Schema(description = "文件大小")
    private long size; //文件大小

    @Schema(description = "内容MIME类型")
    private String contentType; //内容MIME类型

    @Schema(description = "宽")
    private Integer width;

    @Schema(description = "高")
    private Integer height;

    @Schema(description = "创建时间")
    private Date createdTime;

}

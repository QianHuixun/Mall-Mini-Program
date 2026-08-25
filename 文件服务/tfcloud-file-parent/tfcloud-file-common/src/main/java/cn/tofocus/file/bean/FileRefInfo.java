package cn.tofocus.file.bean;

import java.util.Date;

import cn.tofocus.db.dto.UserName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FileRefInfo
{
    private Long pkey; //自增主键
    
    @Schema(description = "文件内容的md5")
    private String md5; //文件内容的md5
    
    @Schema(description = "文件大小")
    private long size; //文件大小
    
    @Schema(description = "内容MIME类型")
    private String contentType; //内容MIME类型
    
    @Schema(description = "类型")
    private UploadType type;
    
    @Schema(description = "原文件名")
    private String fileName; //原文件名
    
    @Schema(description = "扩展名")
    private String extName; //扩展名
    
    @Schema(description = "标题")
    private String title; //标题
    
    @Schema(description = "备注")
    private String memo;
    
    @Schema(description = "来源应用")
    private String appid; //来源应用
    
    @Schema(description = "来源用户")
    private Long userkey; //来源用户

    @Schema(description = "来源用户")
    @UserName(from = "userkey")
    private String userName; //来源用户
    
    @Schema(description = "创建时间")
    private Date createdTime;

    @Schema(description = "查看地址")
    private String url;
}

package cn.tofocus.file.bean;

import java.util.Date;

import cn.tofocus.db.file.FileUrlUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FileInfoV3
{
    @Schema(description = "主键")
    private long id;
    
    @Schema(description = "查看地址")
    private String url;
    
    @Schema(description = "下载地址")
    private String downloadUrl;
    
    @Schema(description = "文件大小")
    private long size;
    
    @Schema(description = "文件MD5")
    private String md5;
    
    @Schema(description = "内容MIME类型")
    private String contentType;
    
    @Schema(description = "原文件名")
    private String fileName; //原文件名
    
    @Schema(description = "扩展名")
    private String extName; //扩展名
    
    private String title;
    
    private String memo;
    
    private Date createdTime;
    
    public FileInfoV3()
    {
    }
    
    public FileInfoV3(Long id, String baseUrl, String url, String downloadUrl, String ext, String md5)
    {
        this.id = id;
        this.url = FileUrlUtil.buildUrl(baseUrl, url, id, ext, md5);
        this.downloadUrl = FileUrlUtil.buildUrl(baseUrl, downloadUrl, id, ext, md5);
    }
    
}

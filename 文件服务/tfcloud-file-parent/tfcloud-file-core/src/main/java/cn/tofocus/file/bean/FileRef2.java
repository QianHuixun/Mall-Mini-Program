package cn.tofocus.file.bean;

import java.util.Date;

import cn.tofocus.common.cachemap.bean.HasPkey;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "文件引用")
public class FileRef2 implements HasPkey<Long>
{
    private Long pkey; //自增主键
    
    @Schema(description = "文件内容的md5")
    private String md5; //文件内容的md5
    
    @Schema(description = "文件大小")
    private long size; //文件大小
    
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

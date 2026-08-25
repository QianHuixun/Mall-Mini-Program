package cn.tofocus.file.bean;

import java.util.Date;

import cn.tofocus.common.util.NumUtil;
import cn.tofocus.db.dto.JoinDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;

@Data
public class FileAccessInfo
{
    private String fileMd5;
    
    @Parameter(description = "文件Pkey")
    private long filePkey;
    
    @Parameter(description = "文件大小")
    private long size; //文件大小

    @Schema(description = "文件引用")
    private FileRefInfo ref;
    
    private ThumbType thumb;
    
    private String ip;
    
    private String referer;
    
    private String deviceType;
    
    private String deviceName;
    
    private String os;
    
    private String osVersion;
    
    private String agentType;
    
    private String agentName;
    
    private String agentVersion;
    
    private String status;
    
    @Parameter(description = "访问时间")
    private Date accessTime;

    @Schema(description = "大小")
    private String getSizeStr()
    {
        return NumUtil.byteSizeToStr(size);
    }
}

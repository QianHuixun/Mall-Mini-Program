package cn.tofocus.file.bean;

import cn.tofocus.common.util.NumUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class OrphanFileRecord
{
    @Schema(description = "孤儿文件数")
    private long orphanRec;
    
    @Schema(description = "孤儿引用数")
    private long orphanRef;
    
    @Schema(description = "文件总数")
    private long recCount;

    @Schema(description = "文件总大小")
    private long recSize;

    @Schema(description = "文件总大小")
    public String getRecSizeStr()
    {
        return NumUtil.byteSizeToStr(recSize);
    }
    
    @Schema(description = "引用总数")
    private long refCount;
    
    @Schema(description = "旧引用总数")
    private long oldRefCount;
}

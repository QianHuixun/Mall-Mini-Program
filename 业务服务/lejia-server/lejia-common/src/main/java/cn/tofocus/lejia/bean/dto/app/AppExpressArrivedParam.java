package cn.tofocus.lejia.bean.dto.app;

import java.util.List;

import cn.tofocus.core.query.param.valid.ListStrLength;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppExpressArrivedParam
{
    @Schema(description = "主键")
    private Integer pkey;
    
    @Schema(description = "送达照片")
    @ListStrLength(length = 2000)
    private List<String> photo;
}

package cn.tofocus.lejia.bean.dto.app.goods;

import cn.tofocus.lejia.bean.dto.PkeyNameDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "app分类-第一级DTO")
public class AppGtypeDTOV2
{
    @Schema(description = "pkey")
    private Integer pkey;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "图标")
    private String photo;

    @Schema(description = "第二级分类", hidden = true)
    private List<PkeyNameDTO> second;

}

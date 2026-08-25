package cn.tofocus.lejia.bean.dto.app.goods;

import java.util.ArrayList;
import java.util.List;

import cn.tofocus.lejia.bean.dto.PkeyNameDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "商城一二级分类嵌套DTO")
public class AppMallGtypeTwoLevelsDTO
{
    @Schema(description = "一级分类pkey")
    private Integer pkey;

    @Schema(description = "一级分类名称")
    private String name;

    @Schema(description = "图标")
    private String photo;

    @Schema(description = "二级分类列表")
    private List<PkeyNameDTO> second = new ArrayList<>();
}

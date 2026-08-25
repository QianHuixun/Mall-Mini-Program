package cn.tofocus.lejia.bean.dto.app.goods;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

@Data
public class AppGtypeDTO {
    @Schema(description = "pkey")
    private Integer pkey;

    @Schema(description = "分类名称")
    private String name;

    /**
     * 图标
     */
    @Schema(description = "图标")
    private String photo;
    
//    public String getPhoto()
//    {
//        if(StringUtils.isNotBlank(photo))
//            return photo + "&thumb=small";
//        return photo;
//    }


    @Schema(description = "该分类下的商品")
    private List<AppGtypeGoodsDTO> goodsList = new ArrayList<>();

}

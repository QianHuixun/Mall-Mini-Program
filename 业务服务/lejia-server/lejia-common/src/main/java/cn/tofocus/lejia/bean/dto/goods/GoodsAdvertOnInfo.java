package cn.tofocus.lejia.bean.dto.goods;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import cn.tofocus.lejia.bean.enums.MType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GoodsAdvertOnInfo
{
    private Integer pkey;
    
    @Schema(description = "标题")
    private String title;
    
    @Schema(description = "照片1")
    private List<String> photo1 = new ArrayList<String>();
    
    @Schema(description = "商品属性")
    private MType mType;
    
    @Schema(description = "商品属性名称")
    private String mTypeName;
    
    @Schema(description = "建档时间")
    private Date createdTime;
}

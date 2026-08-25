package cn.tofocus.lejia.bean.dto.app.market;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.db.dto.JoinProperty;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppOrderLineCommentDTO
{
    @Schema(description = "主键")
    private Integer pkey;
    
    @Schema(description = "订单明细主键")
    private Integer orderLine;
    
    @Schema(description = "商品pkey")
    private Integer goods;
    
    @Schema(description = "商品名称")
    private String goodsName;
    
    @Schema(description = "规格pkey")
    private Integer space;
    
    @Schema(description = "规格名称")
    private String spaceName;
    
    @JsonIgnore
    @JoinProperty(dataQuery = "mktGoodsDao", from = "goods", propertyName = "photo1", type = MktGoods.class)
    private List<String> goodsPhoto1;
    
    @JsonIgnore
    @JoinProperty(dataQuery = "mktGoodsDao", from = "goods", propertyName = "photo3", type = MktGoods.class)
    private String goodsPhoto3;
    
    @Schema(description = "商品图片")
    public String getGoodsPhoto()
    {
        if (StringUtils.isNotBlank(goodsPhoto3))
            return goodsPhoto3;
        if (goodsPhoto1 != null && !goodsPhoto1.isEmpty())
            return goodsPhoto1.get(0);
        return null;
    }
    
    @Schema(description = "评分")
    private Integer score;
    
    @Schema(description = "内容")
    private String content;
    
    @Schema(description = "图片")
    private List<String> photo;
    
    @Schema(description = "回复内容")
    private String replyContent;
}

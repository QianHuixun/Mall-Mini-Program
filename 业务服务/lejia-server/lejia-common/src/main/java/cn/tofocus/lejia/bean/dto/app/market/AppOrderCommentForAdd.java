package cn.tofocus.lejia.bean.dto.app.market;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.*;

import cn.tofocus.core.query.param.valid.ListStrLength;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppOrderCommentForAdd
{
    @NotNull(message = "订单主键不能为空")
    @Schema(description = "订单主键")
    private Integer orderPkey;
    
    @NotEmpty(message = "评价列表不能为空")
    @Valid
    private List<AppOrderGoodsCommentForAdd> lines;
    
    @Data
    public static class AppOrderGoodsCommentForAdd
    {
        @NotNull(message = "商品主键不能为空")
        @Schema(description = "商品主键")
        private Integer goods;
        
        @NotNull(message = "评分不能为空")
        @Min(value = 1, message = "评分最低为1星")
        @Max(value = 5, message = "评分最高为5星")
        @Schema(description = "评分")
        private Integer score;
        
        @Schema(description = "内容")
        @Size(max = 200, message = "评价内容最多200字")
        private String content;
        
        @Schema(description = "图片")
        @Size(max = 6, message = "最多上传6张图片")
        @ListStrLength(length = 1000)
        private List<String> photo;
    }
}

package cn.tofocus.lejia.bean.dto.market;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktOrderGoodsCommentReplyDTO
{
    @NotNull(message = "主键不能为空")
    @Schema(description = "主键")
    private Integer pkey;
    
    @NotBlank(message = "回复内容不能为空")
    @Size(max = 300, message = "回复内容不允许超过300字")
    @Schema(description = "回复内容")
    private String replyContent;
}

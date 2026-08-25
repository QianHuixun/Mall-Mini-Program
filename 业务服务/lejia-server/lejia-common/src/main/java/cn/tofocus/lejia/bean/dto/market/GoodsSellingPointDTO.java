package cn.tofocus.lejia.bean.dto.market;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class GoodsSellingPointDTO
{
    @Schema(description = "主键")
    private Integer pkey;

    @Size(max = 6, message = "卖点名称不允许超过6个字")
    @Schema(description = "卖点名称")
    private String name;

    @Size(max = 6, message = "卖点内容不允许超过6个字")
    @Schema(description = "卖点内容")
    private String content;
}

package cn.tofocus.lejia.bean.dto.market.jd;

import java.util.Date;

import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.lejia.bean.enums.jd.JdGoodsUpdType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class JdGoodsUpdNoticeOnPage
{
    @Schema(description = "主键")
    private Long pkey;
    
    @Schema(description = "变更类型")
    private JdGoodsUpdType type;
    
    @JoinEnum(from = "type")
    @Schema(description = "变更类型名称")
    private String typeName;
    
    @Schema(description = "skuid")
    private Long jdGoods;

    @Schema(description = "商品名称")
    private String title;
    
    @Schema(description = "说明")
    private String description;
    
    @Schema(description = "创建时间")
    private Date createdTime;

    @Schema(description = "主商品ID")
    private Long spuId;
}

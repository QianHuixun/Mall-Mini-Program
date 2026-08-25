package cn.tofocus.lejia.bean.dto.market;

import java.util.List;

import cn.tofocus.lejia.bean.enums.MType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商品供应库分页列表
 * 外层信息通过mktGoods查询
 */
@Data
@Schema(description = "商品供应库分页查询入参")
public class MktSupplyParamDTO
{
    @Schema(description = "商品pkeys")
    private List<Integer> goodPkeys;
    
    @Schema(description = "商品属性：积分/市场/会员/特价/分享/砍价/团购/预售")
    private MType mType;
    
    @Schema(description = "市场pkey")
    private String farmer;
    
    @Schema(description = "是否启用")
    private Boolean enabled;
    
    @Schema(description = "商品分类pkey")
    private Integer gtype;
    
    @Schema(description = "商品名称")
    private String goodsName;
    
    @Schema(description = "页号（不传默认为0）", example = "0")
    private Integer page;
    
    @Schema(description = "每页大小（不传默认为10）", example = "10")
    private Integer pagesize;
    
}

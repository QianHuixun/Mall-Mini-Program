package cn.tofocus.lejia.bean.dto.market;

import java.util.Date;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktGtypeOnList
{
    /**
     * pkey
     */
    @Schema(description = "pkey", hidden = true)
    private Integer pkey;
    
    /**
    * 名称
    */
    @Schema(description = "名称", required = true)
    private String name;
    
    /**
    * 图标
    */
    @Schema(description = "图标")
    private String photo;
    
    /**
    * 排序
    */
    @Schema(description = "排序", required = true)
    private Integer sort;
    
    @Schema(description = "市场排序")
    private Integer marketSort;
    
    @Schema(description = "积分排序")
    private Integer pointSort;
    
    /**
     * 积分商城
     */
    @Schema(description = "积分商城", required = true)
    private Boolean showPoint;
    
    /**
     * 市场商城
     */
    @Schema(description = "市场商城", required = true)
    private Boolean showMarket;
    
    /**
    * 备注
    */
    @Schema(description = "备注")
    private String remark;
    
    /**
    * 启用标志
    */
    @Schema(description = "启用标志", required = true)
    private Boolean enabled;
    
    /**
    * 建档时间
    */
    @Schema(description = "建档时间", hidden = true)
    private Date createdTime;
    
    @Schema(description = "商品名称")
    private List<MktGoodsMainSimple> goodsList;
}

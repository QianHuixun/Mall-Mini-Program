package cn.tofocus.lejia.bean.dto.market;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.db.dto.JoinDTO;
import cn.tofocus.lejia.bean.entity.goods.MktSpaceKc;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktGoodsSpaceOnList
{
    @Schema(description = "pkey")
    private Integer pkey;
    
    // 1:新增   2:修改  3:删除
    @Schema(description = "status")
    private Integer status;
    
    /**
    * 商品
    */
    @Schema(description = "商品")
    private Integer goods;
    
    @Schema(description = "规格")
    private String space;
    
    @JoinDTO(dataQuery = "mktSpaceKcDao")
    @JsonIgnore
    private MktSpaceKc spaceKc;
    
    @Schema(description = "照片1")
    private String photo1;
    
    /**
    * 毛重
    */
    @Schema(description = "毛重")
    private BigDecimal weight;
    
    /**
    * 价格
    */
    @Schema(description = "价格")
    private BigDecimal price;
    
    /**
    * 原价
    */
    @Schema(description = "原价")
    private BigDecimal priceOld;
    
    /**
     * 会员价
     */
    @Schema(description = "会员价")
    private BigDecimal priceMember;
    
    /**
    * 积分
    */
    @Schema(description = "积分")
    private Integer point;
    
    /**
    * 佣金
    */
    @Schema(description = "佣金")
    private BigDecimal comm;
    
    private Integer kcNum;
    
    public void setKcNum(Integer kcNum)
    {
        this.kcNum = kcNum;
    }
    
    @Schema(description = "库存数量")
    public Integer getKcNum()
    {
        if (spaceKc != null) return spaceKc.getKcNum();
        return kcNum;
    }
    
    /**
    * 销售数量
    */
    @Schema(description = "销售数量")
    private Integer xsNum;
    
    @Schema(description = "购物车商品数量")
    private Integer gwcNum = 0;
}

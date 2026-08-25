package cn.tofocus.lejia.bean.dto.app.goods;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.db.dto.JoinDTO;
import cn.tofocus.lejia.bean.entity.goods.MktSpaceKc;

@Data
public class AppSpaceDetailsDTO
{
	@Schema(description = "pkey")
    private Integer pkey;

    /**
    * 商品
    */
	@Schema(description = "商品")
    private Integer goods;

    /**
    * 规格
    */
	@Schema(description = "规格")
    private String space;

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
    * 积分
    */
	@Schema(description = "积分")
	private Integer point;
	
	/**
    * 佣金
    */
	@Schema(description = "佣金")
	private BigDecimal comm;
		
    @Schema(description = "库存数量")
    public Integer getKcNum()
    {
        if (spaceKc != null) return spaceKc.getKcNum();
        return 0;
    }

    /**
    * 销售数量
    */
	@Schema(description = "销售数量")
    private Integer xsNum;
	
    @Schema(description = "购物车商品数量")
    private Integer gwcNum = 0;
    
    @JoinDTO(dataQuery = "mktSpaceKcDao")
    @JsonIgnore
    private MktSpaceKc spaceKc;
}

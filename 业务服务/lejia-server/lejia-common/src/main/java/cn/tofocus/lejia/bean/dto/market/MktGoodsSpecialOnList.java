package cn.tofocus.lejia.bean.dto.market;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class MktGoodsSpecialOnList 
{
	/**
	 * pkey
	 */
    private Integer pkey;

    /**
    * 会员设置 按固定/按比例
    */
    private Boolean type;

    /**
    * 价格
    */
    private BigDecimal price;

    /**
    * 到期日期
    */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" , timezone = "GMT+8")
    private String endDate;

    /**
    * 启用标志
    */
    private Boolean enabled;

    /**
    * 建档时间
    */
    private Date createdTime;
}

package cn.tofocus.lejia.bean.dto.market;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class MktGoodsShareOnList 
{
	/**
	 * pkey
	 */
    private Integer pkey;

    /**
    * 分享设置 按固定/按比例
    */
    private Boolean type;

    /**
    * 佣金值
    */
    private Integer comm;

    /**
    * 到期日期
    */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" , timezone = "GMT+8")
    private Date endDate;

    /**
    * 启用标志
    */
    private Boolean enabled;

    /**
    * 建档时间
    */
    private Date createdTime;
}

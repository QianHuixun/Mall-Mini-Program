package cn.tofocus.lejia.bean.dto.market;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class MktMemberConsumption 
{
	private String code;
	private String farmer;
	private BigDecimal consumption;
	private String goodsName;
	private Date createdTime;
}

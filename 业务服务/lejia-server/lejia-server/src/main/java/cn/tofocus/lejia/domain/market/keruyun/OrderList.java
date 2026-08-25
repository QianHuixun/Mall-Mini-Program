package cn.tofocus.lejia.domain.market.keruyun;

import lombok.Data;

@Data
public class OrderList {
	private Long shopIdenty;
	private Integer[] source;
	private Long startTime;
	private Long endTime;
	private Integer timeType;
	private int pageNo;
	private int pageSize;
}

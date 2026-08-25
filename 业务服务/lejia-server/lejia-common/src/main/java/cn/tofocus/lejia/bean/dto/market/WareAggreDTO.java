package cn.tofocus.lejia.bean.dto.market;

import cn.tofocus.lejia.bean.enums.WareType;
import lombok.Data;

@Data
public class WareAggreDTO {
	private WareType wareType;
	private String typeName;
	private Integer num;
}

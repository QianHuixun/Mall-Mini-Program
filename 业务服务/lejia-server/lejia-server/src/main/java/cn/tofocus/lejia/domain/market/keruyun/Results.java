package cn.tofocus.lejia.domain.market.keruyun;

import com.alibaba.fastjson.JSONArray;

import lombok.Data;

@Data
public class Results {
	private int code;
	private String message;
	private String messageUuid;
	private String apiMessage;
	private JSONArray result;
}

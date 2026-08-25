package cn.tofocus.lejia.domain.market.keruyun;

import com.alibaba.fastjson.JSONObject;

import lombok.Data;

@Data
public class Result {
	private int code;
	private String message;
	private String messageUuid;
	private String apiMessage;
	private JSONObject result;
}

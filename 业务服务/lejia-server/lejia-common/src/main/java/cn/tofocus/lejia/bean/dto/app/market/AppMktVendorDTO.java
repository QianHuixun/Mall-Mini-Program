package cn.tofocus.lejia.bean.dto.app.market;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppMktVendorDTO 
{
    /**
    * 商户名称
    */
	@Schema(description = "商店名称")
    private String name;

    /**
    * 我的积分
    */
	@Schema(description = "我的积分")
    private int points;
	
	private String url;
}

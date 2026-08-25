package cn.tofocus.lejia.bean.dto.sys;

import cn.tofocus.lejia.bean.dto.market.SysFarmerOnList;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SysCompanyOnList 
{
	private String pkey;

	@Schema(description = "名称")
    private String name;

	@Schema(description = "管理员")
    private String manager;

	@Schema(description = "登陆帐号")
    private String mobile;

	@Schema(description = "地址")
    private String addr;

	@Schema(description = "授权数量")
    private Integer grantNum;

	@Schema(description = "启用标志")
    private Boolean enabled;
	
	@Schema(description = "市场列表")
	private List<SysFarmerOnList> markets;
	
    /**
    * 建档时间
    */
	@Schema(description = "建档时间")
    private Date createdTime;
}

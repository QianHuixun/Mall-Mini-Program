package cn.tofocus.lejia.bean.dto.market;

import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktKryVendorOnList 
{
	/**
	 * pkey
	 */
	@Schema(description = "pkey")
    private Integer pkey;

	/**
    * 店名
    */
	@Schema(description = "店名")
    private String name;

	/**
    * 负责人
    */
	@Schema(description = "管理员")
    private String manager;

	/**
    * 手机号码
    */
	@Schema(description = "手机号码")
    private String mobile;

	/**
    * 客如云id
    */
	@Schema(description = "token")
    private String token;
		
	/**
    * 客如云id
    */
	@Schema(description = "客如云id")
    private Long uuid;

	/**
    * 备注
    */
	@Schema(description = "备注")
    private String remark;

	/**
    * 启用标志
    */
	@Schema(description = "启用标志")
    private Boolean enabled;

	/**
    * 建档时间
    */
	@Schema(description = "建档时间")
    private Date createdTime;
}

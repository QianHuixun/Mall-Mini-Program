package cn.tofocus.lejia.bean.dto.app.market;

import cn.tofocus.lejia.bean.entity.sys.SysFarmerConfig;
import cn.tofocus.lejia.bean.enums.ConfigGoodsType;
import cn.tofocus.lejia.bean.enums.v5.FarmerType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class SysFarmerAppOnList
{
	@Schema(description = "pkey", hidden = true)
	private String pkey;

	@Schema(description = "菜场名称")
	private String name;

    private FarmerType type;
    
	@Schema(description = "菜场编码")
	private String code;

	@Schema(description = "管理员")
	private String manager;

	@Schema(description = "负责人手机")
	private String mobile;

	@Schema(description = "市场logo")
	private String logo;

	@Schema(description = "介绍")
	private String content;

	@Schema(description = "售后电话")
	private String tel;

	@Schema(description = "市场照片1")
	private String photo1;

	@Schema(description = "市场照片2")
	private String photo2;

	@Schema(description = "市场照片3")
	private String photo3;
	
	@Schema(description = "公司pkey")
	private String comPkey;

	@Schema(description = "启用标志")
	private Boolean enabled;

	@Schema(description = "市场相关配置信息")
	private SysFarmerConfig config;
	
	@Schema(description = "距离")
	private BigDecimal distance;

	@Schema(description = "是否在配送范围内")
	private Boolean inRange;
	
    @Schema(description = "预计送达时间")
    private String pstime;
	
	private String weekTime;
	
	private String dayTime;
	
    @Schema(description = "商品展示类型")
    private ConfigGoodsType goodsType;
	
}

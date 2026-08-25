package cn.tofocus.lejia.bean.dto.market;

import java.util.Date;

import cn.tofocus.common.util.StringUtil;
import cn.tofocus.db.dto.JoinDTO;
import cn.tofocus.lejia.bean.enums.v2.VendorZxStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktVendorOnList 
{
	/**
	 * pkey
	 */
	@Schema(description = "pkey")
    private Integer pkey;

	/**
	 * 市场pkey
	 */
	@Schema(description = "市场pkey")
	private String farmer;

	/**
	 * 市场名称
	 */
	@Schema(description = "市场pkey")
	@JoinDTO(dataQuery = "sysFarmerDao", from = "farmer")
	private String farmerName;

	/**
    * 商户名称
    */
	@Schema(description = "商户名称")
    private String name;

    @Schema(description = "商户展示名称")
    private String displayName;
    
    @Schema(description = "摊位号")
    private String booth;
	/**
	 * 经营范围
	 */
	@Schema(description = "经营范围")
	private String businessScope;

	/**
	 * 手机号码
	 */
	@Schema(description = "手机号码")
	private String mobile;
	
    @Schema(description = "中信银行审核结果")
    private VendorZxStatus zxStatus;
    
    public String getZxStatusName()
    {
        if (zxStatus != null) return zxStatus.getName();
        return "";
    }

    @Schema(description = "中信-身份证号码")
    private String zxIdentity;
    
	/**
	 * 银行账户名称
	 */
	@Schema(description = "银行账户名称")
	private String bankname;

	/**
	 * 开户人
	 */
	@Schema(description = "开户人")
	private String bankuser;

    @Schema(description = "银行卡绑定手机")
    private String bankuserMoblie;
    
	/**
	 * 银行卡号
	 */
	@Schema(description = "银行卡号")
	private String bankcard;

	/**
	 * 地址
	 */
	@Schema(description = "地址")
	private String addr;

	/**
	 * 商户积分
	 */
	@Schema(description = "商户积分")
	private Integer points;

    /**
    * 启用标志
    */
	@Schema(description = "启用标志", hidden = true)
    private Boolean enabled;
	
    /**
    * 建档时间
    */
	@Schema(description = "建档时间", hidden = true)
    private Date createdTime;

	@Schema(description = "是否注册中信账户")
	private boolean zxRegistered = false;

}

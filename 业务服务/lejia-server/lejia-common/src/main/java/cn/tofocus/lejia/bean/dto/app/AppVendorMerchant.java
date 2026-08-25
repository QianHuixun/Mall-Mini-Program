package cn.tofocus.lejia.bean.dto.app;

import cn.tofocus.lejia.bean.dto.market.MktVendorFileDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 商户小程序DTO
 * @author geshaojian
 */
@Data
public class AppVendorMerchant
{
	@Schema(description = "主键")
	private Integer pkey;

	@Schema(description = "商户名称")
	@NotBlank(message = "商户名称不能为空")
	private String name;

	@Schema(description = "手机号码")
	@NotBlank(message = "手机号码不能为空")
	private String mobile;

	/**
	 * 银行账户名称
	 */
	@Schema(description = "银行账户名称")
	private String bankname;

	/**
	 * 银行卡号
	 */
	@Schema(description = "银行卡号")
	private String bankcard;

	/**
	 * 开户支行名称
	 */
	@Schema(description = "开户支行名称")
	private String bankBranchName;

	/**
	 * 开户行大额行号
	 */
	@Schema(description = "开户行大额行号")
	private String bankNo;

	/**
	 * 地址
	 */
	@Schema(description = "地址")
	private String addr;

	@Schema(description = "头像、视频、个性宣传")
	private List<MktVendorFileDTO> files;
}
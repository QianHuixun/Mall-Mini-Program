package cn.tofocus.lejia.bean.dto.app;

import cn.tofocus.lejia.bean.dto.market.MktVendorFileDTO;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorBigData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 商城小程序DTO
 * @author geshaojian
 */
@Data
public class AppVendor
{
	@Schema(description = "主键")
	private Integer pkey;

	@Schema(description = "商户名称", required = true)
	@NotBlank(message = "商户名称不能为空")
	private String name;
	
    @Schema(description = "摊位号")
    private String booth;
    
    @Schema(description = "在售商品数量")
    private Integer goodsNum;

	@Schema(description = "手机号码")
	@NotBlank(message = "手机号码不能为空")
	private String mobile;

	@Schema(description = "经营范围中文，仅仅查询详情get接口显示用")
	private List<String> businessScopesName;

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

    private String farmer;
    
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

	@Schema(description = "商户简介")
	@Size(max = 50, message = "商户简介最多50字")
	private String shortContent;

	@Schema(description = "头像、视频、个性宣传")
	private List<MktVendorFileDTO> files;

	@Schema(description = "风采展示详情内容")
	private MktVendorBigData mktVendorBigData;
	
	@Schema(description = "是否收藏")
	private Boolean isCollection;
	
	@Schema(description = "收藏主键")
	private Integer collectionPkey;
}
package cn.tofocus.lejia.bean.dto.market;

import java.util.List;

import cn.tofocus.lejia.bean.enums.v2.VendorZxStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 合作商户查询入参
 * @author geshaojian
 */
@Data
public class MktVendorQueryParamDTO
{
	@Schema(description = "页号" , example = "0")
	private Integer page;

	@Schema(description = "每页大小" , example = "10")
	private Integer pagesize;

	@Schema(description = "主键列表")
	private List<Integer> pkeys;

	@Schema(description = "商户名")
	private String name;
	
    private String displayName;

	@Schema(description = "手机号码")
	private String mobile;

	@Schema(description = "市场主键列表")
	private List<String> marketPkeys;

	@Schema(description = "经营范围")
	private List<Integer> scopes;

	@Schema(description = "仅运营端-市场商城商户传递一个标记：market")
	private String flag;
	
    @Schema(description = "中信银行审核结果")
    private VendorZxStatus zxStatus;
}

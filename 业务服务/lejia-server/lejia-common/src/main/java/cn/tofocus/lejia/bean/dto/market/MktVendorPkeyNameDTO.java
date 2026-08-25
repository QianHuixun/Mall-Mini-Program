package cn.tofocus.lejia.bean.dto.market;

import cn.tofocus.lejia.bean.dto.PkeyNameDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktVendorPkeyNameDTO extends PkeyNameDTO
{
	/**
	 * 是否不可用
	 */
	@Schema(description = "是否不可用")
	private Boolean disabled;
}

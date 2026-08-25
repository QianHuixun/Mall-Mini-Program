package cn.tofocus.lejia.bean.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PkeyNameDTO
{
	@Schema(description = "主键")
	private Integer pkey;

	@Schema(description = "名称")
	private String name;
}

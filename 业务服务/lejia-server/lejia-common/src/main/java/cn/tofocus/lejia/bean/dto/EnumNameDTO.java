package cn.tofocus.lejia.bean.dto;

import cn.tofocus.core.enums.IBaseDbEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EnumNameDTO
{
	/**
	 * 中文
	 */
	@Schema(description = "中文")
	private String chinese;

	/**
	 * 英文
	 */
	@Schema(description = "英文")
	private String english;

	/**
	 * 枚举dto列表
	 * @param clazz IBaseDbEnum接口的实现类
	 * @return      结果
	 */
	public static List<EnumNameDTO> getList(Class<? extends IBaseDbEnum> clazz)
	{
		// 初始化结果
		List<EnumNameDTO> result = new ArrayList<>();

		IBaseDbEnum[] enumConstants = clazz.getEnumConstants();
		for (IBaseDbEnum enumItem: enumConstants)
		{
			EnumNameDTO item = new EnumNameDTO();
			item.setEnglish(enumItem.toString());
			item.setChinese(enumItem.getName());
			result.add(item);
		};
		return result;
	}

	public static void main(String[] args)
	{
	}
}

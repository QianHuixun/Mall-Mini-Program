package cn.tofocus.lejia.bean.dto.market;

import java.util.List;

import javax.persistence.Convert;

import org.apache.commons.lang.StringUtils;

import cn.tofocus.db.ListConverter;
import cn.tofocus.lejia.bean.entity.market.MktCookfd;
import cn.tofocus.lejia.bean.entity.market.MktCookfdLine;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktCookfdUpDTO {
	/**
	 * pkey
	 */
	@Schema(description = "pkey", hidden = true)
	private Integer pkey;

	/**
	 * 名称
	 */
	@Schema(description = "名称", required = true)
	private String name;

	/**
	 * 今日推荐
	 */
	@Schema(description = "今日推荐", required = true)
	private Boolean recom;

    /**
     * 分类
     */
    @Schema(description = "分类")
    private Integer ctype;
    
	/**
	 * 照片1
	 */
	@Schema(description = "照片1", required = true)
	private List<String> photo1;

	/**
	 * 照片2
	 */
	@Schema(description = "照片2")
	@Convert(converter = ListConverter.class)
	private List<String> photo2;

	@Schema(description = "照片3")
	@Convert(converter = ListConverter.class)
	private List<String> photo3;

	/**
	 * 排序
	 */
	@Schema(description = "排序", required = true)
	private Integer sort;

	/**
	 * 描述
	 */
	@Schema(description = "描述")
	private String descp;

	/**
	 * 正文
	 */
	@Schema(description = "正文")
	private List<String> content;

	@Schema(description = "菜谱清单")
	private List<MktCookfdLine> lines;

	public MktCookfd conversionCookfd(MktCookfd cookfd, MktCookfdUpDTO entity) 
	{
		if (StringUtils.isNotBlank(entity.getName()))
			cookfd.setName(entity.getName());
		if (entity.getSort() != null)
			cookfd.setSort(entity.getSort());
		if (StringUtils.isNotBlank(entity.getDescp()))
			cookfd.setDescp(entity.getDescp());
		if (entity.getContent() != null)
			cookfd.setContent(entity.getContent());
		if (entity.getPhoto1() != null)
			cookfd.setPhoto1(entity.getPhoto1());
		if (entity.getPhoto2() != null)
			cookfd.setPhoto2(entity.getPhoto2());
		if (entity.getPhoto3() != null)
			cookfd.setPhoto3(entity.getPhoto3());
		if(entity.getCtype() != null)
			cookfd.setCtype(entity.getCtype());
		return cookfd;
	}
}

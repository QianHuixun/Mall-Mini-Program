package cn.tofocus.lejia.bean.dto.app.market;

import cn.tofocus.lejia.bean.enums.PrizeStatus;
import cn.tofocus.lejia.bean.enums.PrizeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class MktAppMemberDrawOnList {

	/**
	 * pkey
	 */
	@Schema(description = "pkey")
	private Integer pkey;

	/**
	 * 用户
	 */
	@Schema(description = "用户")
	private Integer member;

	/**
	 * 状态 初始/已发
	 */
	@Schema(description = "状态 初始/已发")
	@Column(nullable = false, columnDefinition = "tinyint(4)")
	private PrizeStatus status;

	/**
	 * 礼品类型 积分/优惠券/礼品券/礼品/
	 */
	@Schema(description = "礼品类型 积分/优惠券/实物/谢谢惠顾")
	@Column(nullable = false, columnDefinition = "tinyint(4)")
	private PrizeType pType;

	/**
	 * 奖品id
	 */
	@Schema(description = "奖品id")
	private Integer prize;


	@Schema(description = "商品名称")
	private String name;

	/**
	 * 图片
	 */
	@Schema(description = "图片")
	private List<String> photo = new ArrayList<>();

	/**
	 * 列表小图
	 */
	@Schema(description = "列表小图")
	private String wrapperPhoto;

	public String getWrapperPhoto() {
		return getPhoto().size() > 0 ? getPhoto().get(0) : "";
	}

	/**
	 * 奖品值
	 */
	@Schema(description = "奖品值")
	private Integer pvalue;
	@JsonIgnore
	private String addr;
	private Boolean isAddr;
	@Schema(description = "快递公司")
	private String logistics;
	@Schema(description = "快递单号")
	private String express;
	
	/**
	 * 中奖描述
	 */
	@Schema(description = "中奖描述")
	private String descp;
	

	/**
	 * 中奖时间
	 */
	@Schema(description = "中奖时间")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" , timezone = "GMT+8")
	private Date createdTime;
}

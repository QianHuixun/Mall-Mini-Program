package cn.tofocus.lejia.bean.dto.market;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Convert;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.core.query.param.valid.ListStrLength;
import cn.tofocus.db.ListConverter;
import cn.tofocus.db.file.FileUrl;
import cn.tofocus.lejia.bean.enums.RefundStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktRefundOnList 
{
	/**
	 * 主键
	 */
	@Schema(description = "主键", required = true)
    private Integer pkey;

	/**
	* 单据号
	*/
 	@Schema(description = "单据号", required = true)
    private String code;
 	
//	/**
//    * 订单
//    */
//	@Schema(description = "订单", required = true)
//    private Integer orderNum;

	/**
    * 状态 申请中/同意/已退款/拒绝
    */
	@Schema(description = "状态 申请中/同意/已退款/拒绝", required = false)
    private RefundStatus status;
	private String statusName;

	/**
    * 用户
    */
	@Schema(description = "用户", required = true)
    private Integer member;
	private String memberName;

	/**
    * 退款理由
    */
	@Schema(description = "退款理由", required = true)
    private String reason;

	/**
    * 照片
    */
	@Schema(description = "照片", required = false)
	@FileUrl
	@ListStrLength(length = 1000)
	@Convert(converter = ListConverter.class)
    private List<String> photo;

	
	/**
    * 订单金额
    */
	@Schema(description = "订单金额", required = true)
    private BigDecimal amtall;

	/**
    * 退款金额
    */
	@Schema(description = "退款金额", required = true)
    private BigDecimal amtre;

	/**
    * 处理意见
    */
	@Schema(description = "处理意见", required = false)
    private String delDesc;

	/**
    * 处理员
    */
	@Schema(description = "处理员", required = false)
    private Integer delBy;

	/**
    * 处理时间
    */
	@Schema(description = "处理时间", required = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
    private Date delTime;

	/**
    * 退款时间
    */
	@Schema(description = "退款时间", required = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
    private Date reTime;




}

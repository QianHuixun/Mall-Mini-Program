package cn.tofocus.lejia.bean.dto.app.market;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.lejia.bean.enums.RefundStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  退款
* @author zdw 2020-07-20
*/

@Data
public class MktAppRefundDTO implements HasPkey<Integer> {
   


	/**
	 * 主键
	 */
    @Id
	@Schema(description = "主键", required = true)
    private Integer pkey;

	/**
    * 订单
    */
	@Schema(description = "订单", required = true)
    private Integer orderNum;

	/**
    * 状态 申请中/同意/已退款/拒绝
    */
	@Schema(description = "状态 申请中/同意/已退款/拒绝", required = false)
	@Column(nullable = false, columnDefinition = "tinyint(4)")
    private RefundStatus status;

	/**
    * 用户
    */
	@Schema(description = "用户", required = true)
    private Integer member;

	/**
    * 退款理由
    */
	@Schema(description = "退款理由", required = true)
    private String reason;

    /**
    * 照片1
    */
	@Schema(description = "照片")
    private List<String> photo = new ArrayList<String>();

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

	/**
    * 市场
    */
	@Schema(description = "市场", required = true)
    private String farmer;

	/**
    * 公司
    */
	@Schema(description = "公司", required = true)
    private String company;

	/**
    * 建档时间
    */
	@Schema(description = "建档时间", required = true)
	@CreatedDate
    private Date createdTime;
}
package cn.tofocus.lejia.bean.entity.member;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.bean.enums.MemberPType;
import cn.tofocus.lejia.bean.enums.PayStatus;
import cn.tofocus.lejia.bean.enums.PayType;
import lombok.Data;
import javax.persistence.Column;

/**
*  会员年费
* @author zdw 2020-07-29
*/

@Entity
@Data
@Table(name="mkt_member_pay")
public class MktMemberPay implements HasPkey<Integer> {
   


	/**
	 * 主键
	 */
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_member_pay")
	@Schema(description = "主键", required = true)
    private Integer pkey;

	/**
    * 用户
    */
	@Schema(description = "用户", required = false)
	@Column(name="member_key")
    private Integer member;

	/**
    * 类型 年费/充值
    */
	@Schema(description = "p_type", required = true)
	@Column(nullable = false, columnDefinition = "tinyint(4)")
    private MemberPType pType;

	/**
    * 订单号
    */
	@Schema(description = "订单号", required = true)
    private String orderNumber;

	/**
    * 状态 初始/支付成功/支付失败
    */
	@Schema(description = "状态 初始/支付成功/支付失败", required = true)
	@Column(nullable = false, columnDefinition = "tinyint(4)")
    private PayStatus status;

	/**
    * 支付类型 微信/支付宝/电子帐户
    */
	@Schema(description = "支付类型 微信/支付宝/电子帐户", required = true)
	@Column(nullable = false, columnDefinition = "tinyint(4)")
    private PayType payType;

	/**
    * 支付金额
    */
	@Schema(description = "支付金额", required = true)
    private BigDecimal amt;

	/**
    * 支付成功时间
    */
	@Schema(description = "支付成功时间", required = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
    private Date payTime;

	/**
    * 建档时间
    */
	@Schema(description = "建档时间", required = true)
	@CreatedDate
    private Date createdTime;

    @Schema(description = "归属主键")
    private Integer ascription;

}
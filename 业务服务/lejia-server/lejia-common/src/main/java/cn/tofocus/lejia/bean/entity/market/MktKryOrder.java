package cn.tofocus.lejia.bean.entity.market;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.bean.enums.KryStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  客如云订单
* @author zdw 2020-07-13
*/

@Entity
@Data
@Table(name="mkt_kry_order")
public class MktKryOrder implements HasPkey<Integer> {
   


	/**
	 * pkey
	 */
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_kry_order")
	@Schema(description = "pkey")
    private Integer pkey;

    @Schema(description = "客如云id")
    private Long uuid;
    
	/**
    * 订单号
    */
	@Schema(description = "订单ID")
    private Long orderId;
	
	/**
	 * 订单号
	 */
	@Schema(description = "订单号")
	@Column(name = "kc_code")
	private String code;

	/**
    * 订单状态 已完成/其他
    */
	@Schema(description = "订单状态 已完成/其他")
	@Column(nullable = false, columnDefinition = "tinyint(4)")
    private KryStatus status;

	/**
    * 订单来源
    */
	@Schema(description = "订单来源")
    private String source;

	/**
    * 商户实收金额
    */
	@Schema(description = "商户实收金额")
    private Long receivedAmount;

	/**
    * 用户实付金额
    */
	@Schema(description = "用户实付金额")
    private Long custRealPay;

	/**
    * 订单原始金额
    */
	@Schema(description = "订单原始金额")
    private Long tradeAmount;

	/**
    * 优惠总金额
    */
	@Schema(description = "优惠总金额")
    private Long privilegeAmount;

	/**
    * 顾客id
    */
	@Schema(description = "顾客id")
    private Long customerId;

	/**
    * 顾客昵称
    */
	@Schema(description = "顾客昵称")
    private String custmerName;

	/**
    * 会员id
    */
	@Schema(description = "会员id")
    private Long memberId;

	/**
    * 会员手机
    */
	@Schema(description = "会员手机")
    private String mobile;

	/**
    * 订单时间
    */
	@Schema(description = "订单时间")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
    private Date orderTime;

	/**
    * 建档时间
    */
	@Schema(description = "建档时间")
	@CreatedDate
    private Date createdTime;

    @Schema(description = "归属主键")
    private Integer ascription;

}
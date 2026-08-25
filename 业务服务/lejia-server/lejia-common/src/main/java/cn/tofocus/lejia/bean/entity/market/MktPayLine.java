package cn.tofocus.lejia.bean.entity.market;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.bean.enums.PayType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  支付流水
* @author zdw 2020-07-24
*/

@Entity
@Data
@Table(name="mkt_pay_line")
public class MktPayLine implements HasPkey<Integer> {
   


	/**
	 * 主键
	 */
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_pay_line")
	@Schema(description = "主键", required = true)
    private Integer pkey;

	/**
    * 订单号
    */
	@Schema(description = "订单号", required = false)
    private String orderNumber;

	/**
    * 流水号
    */
	@Schema(description = "流水号", required = false)
	@Column(name = "kc_code")
    private String code;

	@Schema(description = "状态", required = false)
	private String status;
	
	/**
    * 支付类型 微信/支付宝/电子帐户
    */
	@Schema(description = "支付类型 微信/支付宝/电子帐户", required = false)
	@Column(nullable = false, columnDefinition = "tinyint(4)")
    private PayType payType;

	/**
    * 支付金额
    */
	@Schema(description = "支付金额", required = false)
    private String amt;

	/**
    * 支付成功时间
    */
	@Schema(description = "支付成功时间", required = false)
    private String payTime;

	/**
    * 建档时间
    */
	@Schema(description = "建档时间", required = false)
	@CreatedDate
    private Date createdTime;

    @Schema(description = "归属主键")
    private Integer ascription;

}
package cn.tofocus.lejia.bean.entity.market;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.bean.enums.PType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
*  mkt_point_pay
* @author pty 2020-07-30
*/

@Entity
@Data
@Table(name="mkt_point_pay")
public class MktPointPay implements HasPkey<Integer> {
   
	/**
	 * 主键
	 */
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_point_pay")
	@Schema(description = "主键", required = true)
    private Integer pkey;

	/**
	 * 用户
	 */
	@Schema(description = "用户")
	@Column(name="member_key")
	private Integer member;

	/**
    * 订单号
    */
	@Schema(description = "订单号", required = false)
    private String orderNumber;

	/**
    * 类型 抽奖/消费
    */
	@Schema(description = "类型 抽奖/消费")
	@Column(nullable = false, columnDefinition = "tinyint(4)")
    private PType pType;

	/**
	 * 积分值
	 */
	@Schema(description = "积分值")
	private Integer points;

	/**
    * 建档时间
    */
	@Schema(description = "建档时间", required = false)
	@CreatedDate
    private Date createdTime;

    @Schema(description = "归属主键")
    private Integer ascription;

}

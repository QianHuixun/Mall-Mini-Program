package cn.tofocus.lejia.bean.entity.market;


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
import cn.tofocus.lejia.bean.enums.CommDrawStatus;
import lombok.Data;
import javax.persistence.Column;

/**
*  提现表
* @author zdw 2020-09-22
*/

@Entity
@Data
@Table(name="mkt_comm_draw")
public class MktCommDraw implements HasPkey<Integer> {
   


	/**
	 * pkey
	 */
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_comm_draw")
	@Schema(description = "pkey", required = true)
    private Integer pkey;

	/**
    * 订单号
    */
	@Schema(description = "订单号", required = true)
    private String orderNumber;

	/**
    * 用户
    */
	@Schema(description = "用户", required = true)
	@Column(name="member_key")
    private Integer member;

	/**
    * 佣金值
    */
	@Schema(description = "佣金值", required = true)
    private BigDecimal comms;

	/**
    * 状态 初始/已发
    */
	@Schema(description = "状态 初始/已发", required = true)
	@Column(nullable = false, columnDefinition = "tinyint(4)")  
    private CommDrawStatus status;

	/**
    * 流水号
    */
	@Schema(description = "流水号", required = false)
    private String bankCode;

	/**
    * 备注
    */
	@Schema(description = "备注", required = false)
    private String remark;

	/**
    * 确认时间
    */
	@Schema(description = "确认时间", required = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
    private Date checkTime;

	/**
    * 确认人
    */
	@Schema(description = "确认人", required = false)
    private Integer checkBy;

	/**
    * 建档时间
    */
	@Schema(description = "建档时间", required = true)
	@CreatedDate
    private Date createdTime;

    @Schema(description = "归属主键")
    private Integer ascription;

}
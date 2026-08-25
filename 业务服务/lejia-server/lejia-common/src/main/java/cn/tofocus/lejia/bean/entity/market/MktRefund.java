package cn.tofocus.lejia.bean.entity.market;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.core.query.param.valid.ListStrLength;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.db.ListConverter;
import cn.tofocus.db.file.FileUrl;
import cn.tofocus.lejia.bean.enums.RefundStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  退款
* @author zdw 2020-07-20
*/

@Entity
@Data
@Table(name="mkt_refund")
public class MktRefund implements HasPkey<Integer> {
   


	/**
	 * 主键
	 */
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_refund")
	@Schema(description = "主键")
    private Integer pkey;

    /**
     * 单据号
     */
 	@Schema(description = "单据号")
 	@Column(name = "kc_code")
    private String code;
    
	/**
    * 订单
    */
	@Schema(description = "订单")
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
	@Schema(description = "用户")
	@Column(name="member_key")
    private Integer member;

	/**
    * 退款理由
    */
	@Schema(description = "退款理由")
    private String reason;

	/**
    * 照片
    */
	@Schema(description = "照片", required = false)
	@FileUrl
	@Convert(converter = ListConverter.class)
	@ListStrLength(length = 2000)
    private List<String> photo;

	
	/**
    * 订单金额
    */
	@Schema(description = "订单金额")
    private BigDecimal amtall;

	/**
    * 退款金额
    */
	@Schema(description = "退款金额")
    private BigDecimal amtre;

	/**
    * 处理意见
    */
	@Schema(description = "处理意见", required = false)
	@Column(length = 1000)
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
	@Schema(description = "市场")
    private String farmer;

	/**
    * 公司
    */
	@Schema(description = "公司")
    private String company;

	/**
    * 建档时间
    */
	@Schema(description = "建档时间")
	@CreatedDate
    private Date createdTime;

    @Schema(description = "归属主键")
    private Integer ascription;

}
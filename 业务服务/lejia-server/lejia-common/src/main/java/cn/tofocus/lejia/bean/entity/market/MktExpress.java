package cn.tofocus.lejia.bean.entity.market;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.core.query.param.valid.ListStrLength;
import cn.tofocus.db.ListConverter;
import cn.tofocus.db.file.FileUrl;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.bean.enums.ExpressStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  跑脚单
* @author zdw 2020-07-21
*/

@Entity
@Data
@Table(name="mkt_express")
@FieldNameConstants(innerTypeName = "F")
public class MktExpress implements HasPkey<Integer> 
{
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_express")
	@Schema(description = "pkey")
    private Integer pkey;

	@Schema(description = "单据号")
	@Column(name = "kc_code")
    private String code;

	@Schema(description = "订单")
    private Integer orderId;

	@Schema(description = "状态 初始/已派单/已揽货/已到货/拒收")
	@Column(nullable = false, columnDefinition = "tinyint(4)")
    private ExpressStatus status;

	@Schema(description = "状态名称")
	private String statusName;
	
	@Schema(description = "快递员主键")
    private Integer courier;
	
	@Schema(description = "快递员姓名")
	private String courierName;
	
	@Schema(description = "快递员号码")
	private String courierMobile;

	@Schema(description = "派单时间")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
	@CreatedDate
    private Date pdTime;

	@Schema(description = "接单时间")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
    private Date jdTime;

	@Schema(description = "到货时间")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
    private Date qrTime;

	@Schema(description = "送达照片")
	@FileUrl
	@Convert(converter = ListConverter.class)
	@ListStrLength(length = 2000)
	private List<String> photo;

	@Schema(description = "市场")
    private String farmer;

	@Schema(description = "公司")
    private String company;

	@Schema(description = "建档时间")
	@CreatedDate
    private Date createdTime;
	
    @Schema(description = "归属主键")
    private Integer ascription;
}
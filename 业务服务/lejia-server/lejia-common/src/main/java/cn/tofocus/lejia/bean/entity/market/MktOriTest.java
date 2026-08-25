package cn.tofocus.lejia.bean.entity.market;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Entity
@Data
@Table(name = "mkt_ori_test")
public class MktOriTest implements HasPkey<Integer> 
{
	/**
	 * pkey
	 */
	@Id
	@AutoRedisID(domain = "zyysc", sequence = "mkt_ori_test")
	@Schema(description = "pkey")
	private Integer pkey;

	/**
	 * 检测商户
	 */
	@Schema(description = "检测商户")
	@ExcelProperty("检测商户")
	private String merchant;
	
	/**
	 * 检测商品 MktGoods
	 */
	@Schema(description = "检测商品 MktGoods")
	@ExcelProperty("检测商品")
	private String goods;

	/**
	 * 检测项目
	 */
	@Schema(description = "检测项目")
	@ExcelProperty("检测项目")
	private String entry;

	/**
	 * 检测结果
	 */
	@Schema(description = "检测结果")
	@ExcelProperty("检测结果")
	private Boolean testResult;

	/**
	 * 检测日期
	 */
	@Schema(description = "检测日期")
	@ExcelProperty("检测日期")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" , timezone = "GMT+8")
	private Date testDate;

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

	/**
	 * 建档员
	 */
	@Schema(description = "建档员")
	@CreatedBy
	private Integer createdBy;

	/**
	 * 版本
	 */
	@Schema(description = "版本")
	@Column(nullable = false, columnDefinition = "smallint(6)")
	private Integer rowVension;
	
    @Schema(description = "归属主键")
    private Integer ascription;
}

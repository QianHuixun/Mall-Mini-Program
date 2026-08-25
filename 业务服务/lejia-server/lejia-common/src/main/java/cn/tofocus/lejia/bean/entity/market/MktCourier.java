package cn.tofocus.lejia.bean.entity.market;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 快递员
 * 
 * @author lai 2020-06-15
 */

@Entity
@Data
@Table(name = "mkt_courier")
public class MktCourier implements HasPkey<Integer> {

	/**
	 * pkey
	 */
	@Id
	@AutoRedisID(domain = "zyysc", sequence = "mkt_courier")
	@Schema(description = "pkey")
	private Integer pkey;

	/**
	 * 姓名
	 */
	@Schema(description = "姓名")
	private String name;

	/**
	 * 手机
	 */
	@Schema(description = "手机")
	private String mobile;

	/**
	 * unionid
	 */
	@Schema(description = "unionid")
	private String unionid;

	/**
	 * openid1
	 */
	@Schema(description = "openid1")
	private String openid1;

	/**
	 * openid2
	 */
	@Schema(description = "openid2")
	private String openid2;

	/**
	 * 备注
	 */
	@Schema(description = "备注")
	private String remark;

	/**
	 * 启用标志
	 */
	@Schema(description = "启用标志")
	private Boolean enabled;

	/**
	 * 是否已删除
	 */
	@Schema(description = "是否已删除")
	private Boolean idDel;

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
	 * 最后更新时间
	 */
	@Schema(description = "最后更新时间")
	@LastModifiedDate
	private Date updateTime;

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
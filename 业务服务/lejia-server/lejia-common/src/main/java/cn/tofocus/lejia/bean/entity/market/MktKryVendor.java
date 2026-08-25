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
*  客如云商户
* @author zdw 2020-07-13
*/

@Entity
@Data
@Table(name="mkt_kry_vendor")
public class MktKryVendor implements HasPkey<Integer> {
   


	/**
	 * pkey
	 */
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_kry_vendor")
	@Schema(description = "pkey")
    private Integer pkey;

	/**
    * 店名
    */
	@Schema(description = "店名")
    private String name;

	/**
    * 管理员
    */
	@Schema(description = "管理员")
    private String manager;

	/**
    * 手机号码
    */
	@Schema(description = "手机号码")
    private String mobile;

	/**
    * 客如云id
    */
	@Schema(description = "token")
    private String token;
	
	/**
    * 客如云id
    */
	@Schema(description = "客如云id")
    private Long uuid;

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
    * 最后更新时间
    */
	@Schema(description = "最后更新时间")
	@LastModifiedDate
    private Date updateTime;

	/**
    * 建档员
    */
	@Schema(description = "建档员")
	@CreatedBy
    private Integer createdBy;

	/**
    * 建档时间
    */
	@Schema(description = "建档时间")
	@CreatedDate
    private Date createdTime;

	/**
    * 版本
    */
	@Schema(description = "版本")
	@Column(nullable = false, columnDefinition = "smallint(6)")
    private Integer rowVension;

    @Schema(description = "归属主键")
    private Integer ascription;

}
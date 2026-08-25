package cn.tofocus.lejia.bean.entity.market;

import cn.tofocus.lejia.bean.enums.AddrType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.LastModifiedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import lombok.Data;

/**
*  mkt_addr
* @author lai 2020-06-15
*/

@Entity
@Data
@Table(name="mkt_addr")
@FieldNameConstants(innerTypeName = "F")
public class MktAddr implements HasPkey<Integer> {
   

	/**
	 * pkey
	 */
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_addr")
	@Schema(description = "pkey")
    private Integer pkey;

    /**
    * 用户
    */
	@Schema(description = "用户")
	@Column(name="member_key")
    private Integer member;

    @Column(columnDefinition = "tinyint")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Schema(description = "类型")
    private AddrType type;

    @Column(length = 40)
    @Size(max = 40)
    @Schema(description = "省")
    private String pro;

    @Column(length = 40)
    @Size(max = 40)
    @Schema(description = "市")
    private String city;

    @Column(length = 40)
    @Size(max = 40)
    @Schema(description = "区")
    private String area;

    @Column(length = 40)
    @Size(max = 40)
    @Schema(description = "街道")
    private String town;

    /**
    * 地址
    */
	@Schema(description = "地址")
    private String addr;

    /**
    * 详细地址
    */
    @Deprecated
	@Schema(description = "详细地址")
    private String addrDetail;

	private String addrCode;
    /**
    * 收货人
    */
	@Schema(description = "收货人")
    private String name;

    /**
    * 收货人手机
    */
	@Schema(description = "收货人手机")
    private String mobile;

    /**
    * 默认地址
    */
	@Schema(description = "默认地址")
    private Boolean defaultAddr;

    /**
    * 经度
    */
	@Schema(description = "经度")
    private BigDecimal longitude;

    /**
    * 纬度
    */
	@Schema(description = "纬度")
    private BigDecimal latitude;

    /**
    * 修改时间
    */
	@Schema(description = "修改时间")
	@LastModifiedDate
    private Date updateTime;

    @Schema(description = "归属主键")
    private Integer ascription;

}
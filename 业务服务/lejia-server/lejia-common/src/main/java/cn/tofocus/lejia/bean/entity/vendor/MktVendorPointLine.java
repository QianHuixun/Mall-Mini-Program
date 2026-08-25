package cn.tofocus.lejia.bean.entity.vendor;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.bean.enums.SourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  mkt_vendor
*/

@Entity
@Data
@Table(name="mkt_vendor_point_line")
public class MktVendorPointLine implements HasPkey<Integer> {
   

	/**
	 * pkey
	 */
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_vendor_point_line")
	@Schema(description = "pkey")
    private Integer pkey;

    @Schema(description = "商户 pkey")
    private Integer vendor;
    
    /**
     * 积分
     */
    @Schema(description = "积分")
    private Integer points;
    
    @Schema(description = "余额")
    private Integer balance;
    
    /**
     * 购买+/消费-/活动+/手动+-
     */
    @Schema(description = "积分来源")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private SourceType source;
    
    @Schema(description = "关联流水")
    private Integer formId;
    
    @Schema(description = "支付会员")
	@Column(name="member_key")
    private Integer member;
    
    /**
    * 建档时间
    */
	@Schema(description = "建档时间")
	@CreatedDate
    private Date createdTime;

    @Schema(description = "归属主键")
    private Integer ascription;
}
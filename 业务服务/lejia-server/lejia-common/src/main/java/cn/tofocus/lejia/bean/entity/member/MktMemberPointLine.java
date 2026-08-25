package cn.tofocus.lejia.bean.entity.member;

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
*  mkt_member_point_line
*/

@Entity
@Data
@Table(name="mkt_member_point_line")
public class MktMemberPointLine implements HasPkey<Integer> {
   

	/**
	 * pkey
	 */
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_member_point_line")
	@Schema(description = "pkey")
    private Integer pkey;

    /**
    * 用户
    */
	@Schema(description = "用户")
	@Column(name="member_key")
    private Integer member;

    /**
    * 借贷标志 借(-)/贷(+)
    */
	@Schema(description = "借贷标志 借(-)/贷(+)")
	@Column(nullable = false, columnDefinition = "tinyint(4)")
    private Boolean direct;

    /**
    * 积分值
    */
	@Schema(description = "积分值")
    private Integer points;

    /**
    * 余额
    */
	@Schema(description = "余额")
    private Integer balance;

    /**
    * 积分来源 购买+/消费-/手动+-
    */
	@Schema(description = "积分来源 购买+/消费-/手动+-")
	@Column(nullable = false, columnDefinition = "tinyint(4)")
    private SourceType source;

    /**
    * 来源单据
    */
	@Schema(description = "来源单据")
    private String formId;

	private String remark;
    /**
    * 建档时间
    */
	@Schema(description = "建档时间")
	@CreatedDate
    private Date createdTime;

    @Schema(description = "归属主键")
    private Integer ascription;

}
package cn.tofocus.lejia.bean.entity.member;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.LastModifiedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  mkt_member_point
* @author lai 2020-06-15
*/

@Entity
@Data
@Table(name="mkt_member_point")
public class MktMemberPoint implements HasPkey<Integer> {
   

	/**
	 * pkey
	 */
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_member_point")
	@Schema(description = "pkey")
    private Integer pkey;

    /**
    * 积分值
    */
	@Schema(description = "积分值")
    private Integer points;

    /**
    * 锁定积分
    */
	@Schema(description = "锁定积分")
    private Integer lockPoints;

    /**
    * 最后更新时间
    */
	@Schema(description = "最后更新时间")
	@LastModifiedDate
    private Date updateTime;

    @Schema(description = "归属主键")
    private Integer ascription;

}
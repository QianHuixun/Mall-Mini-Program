package cn.tofocus.lejia.bean.entity.member;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import lombok.Data;

/**
*  mkt_member_sign
* @author zdw 2020-07-16
*/

@Entity
@Data
@Table(name="mkt_member_sign")
public class MktMemberSign implements HasPkey<Integer> {
   


	/**
	 * pkey
	 */
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_member_sign")
	@Schema(description = "pkey", required = true)
    private Integer pkey;

	/**
    * 用户
    */
	@Schema(description = "用户", required = true)
	@Column(name="member_key")
    private Integer member;

	/**
    * 签到日期
    */
	@Schema(description = "签到日期", required = true)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" , timezone = "GMT+8")
    private Date signDate;

	/**
    * 连续签到天数
    */
	@Schema(description = "连续签到天数", required = true)
    private Integer signNum;

	/**
    * 所获积分
    */
	@Schema(description = "所获积分", required = true)
    private Integer points;

	/**
    * 建档时间
    */
	@Schema(description = "建档时间", required = true)
	@CreatedDate
    private Date createdTime;

    @Schema(description = "归属主键")
    private Integer ascription;

}
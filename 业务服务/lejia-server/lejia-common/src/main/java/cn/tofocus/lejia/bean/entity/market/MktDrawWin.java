package cn.tofocus.lejia.bean.entity.market;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.bean.enums.PrizeStatus;
import cn.tofocus.lejia.bean.enums.PrizeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  中奖记录
*/

@Entity
@Data
@Table(name="mkt_draw_win")
public class MktDrawWin implements HasPkey<Integer> {
   

	/**
	 * pkey
	 */
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_draw_win")
	@Schema(description = "pkey")
    private Integer pkey;

    /**
    * 用户
    */
	@Schema(description = "用户")
	@Column(name="member_key")
    private Integer member;

    /**
    * 状态 初始/已发
    */
	@Schema(description = "状态 初始/已发")
	@Column(nullable = false, columnDefinition = "tinyint(4)")
    private PrizeStatus status;

    /**
    * 礼品类型 积分/优惠券/礼品券/礼品/
    */
	@Schema(description = "礼品类型 积分/优惠券/实物/谢谢惠顾")
	@Column(nullable = false, columnDefinition = "tinyint(4)")
    private PrizeType pType;

    /**
    * 奖品id
    */
	@Schema(description = "奖品id")
    private Integer prize;

    /**
    * 中奖描述
    */
	@Schema(description = "中奖描述")
    private String descp;

    /**
    * 收货地址
    */
	@Schema(description = "收货地址")
    private String addr;
	
	@Schema(description = "快递公司")
	private String logistics;
	@Schema(description = "快递单号")
	private String express;
	
	@Schema(description = "发奖时间")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
	private Date sendTime;
	
    /**
    * 中奖时间
    */
	@Schema(description = "中奖时间")
	@CreatedDate
    private Date createdTime;

    @Schema(description = "归属主键")
    private Integer ascription;
   

}
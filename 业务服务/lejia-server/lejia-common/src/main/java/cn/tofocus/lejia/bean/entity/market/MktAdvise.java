package cn.tofocus.lejia.bean.entity.market;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  建议反馈
* @author lai 2020-06-15
*/

@Entity
@Data
@Table(name="mkt_advise")
public class MktAdvise implements HasPkey<Integer> {
   

	/**
	 * pkey
	 */
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_advise")
	@Schema(description = "pkey")
    private Integer pkey;

    /**
    * 正文
    */
	@Schema(description = "正文")
    private String content;

    /**
    * 提交人
    */
	@Schema(description = "提交人")
	@Column(name="member_key")
    private Integer member;

    /**
    * 提交人手机
    */
	@Schema(description = "提交人手机")
    private String mobile;

    /**
     * 市场
     */
    @Schema(description = "市场")
    private String farmer;
    
    /**
    * 建档时间
    */
	@Schema(description = "建档时间")
	@CreatedDate
    private Date createdTime;

    @Schema(description = "归属主键")
    private Integer ascription;
   

}
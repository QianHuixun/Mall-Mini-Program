package cn.tofocus.lejia.bean.entity.market;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import lombok.Data;

/**
*  我的收藏
* @author zdw 2020-07-20
*/

@Entity
@Data
@Table(name="mkt_collection")
public class MktCollection implements HasPkey<Integer> {
   


	/**
	 * pkey
	 */
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_collection")
	@Schema(description = "pkey", required = true)
    private Integer pkey;

	/**
    * 用户
    */
	@Schema(description = "用户", required = true)
	@Column(name="member_key")
    private Integer member;

	/**
    * 类型 菜谱/商品
    */
	@Schema(description = "类型 菜谱/商品", required = true)
	@Column(nullable = false, columnDefinition = "tinyint(4)")
    private Integer ctype;

	/**
    * 对象主键
    */
	@Schema(description = "对象主键", required = true)
    private Integer objKey;

	/**
    * 市场
    */
	@Schema(description = "市场", required = true)
    private String farmer;

	/**
    * 公司
    */
	@Schema(description = "公司", required = true)
    private String company;

	/**
    * 建档时间
    */
	@Schema(description = "建档时间", required = true)
	@CreatedDate
    private Date createdTime;

    @Schema(description = "归属主键")
    private Integer ascription;

}
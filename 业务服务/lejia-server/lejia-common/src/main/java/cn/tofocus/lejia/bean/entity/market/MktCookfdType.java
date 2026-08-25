package cn.tofocus.lejia.bean.entity.market;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  菜谱分类
* @author zdw 2020-08-12
*/

@Entity
@Data
@Table(name="mkt_cookfd_type")
public class MktCookfdType implements HasPkey<Integer> {
   


	/**
	 * pkey
	 */
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_cookfd_type")
	@Schema(description = "pkey", required = true)
    private Integer pkey;

	/**
    * 名称
    */
	@Schema(description = "名称", required = true)
    private String name;

	/**
    * 排序
    */
	@Schema(description = "排序", required = true)
    private Integer sort;

	/**
    * 启用标志
    */
	@Schema(description = "启用标志", required = true)
    private Boolean enabled;

	/**
    * 是否已删除
    */
	@Schema(description = "是否已删除", required = true)
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
    * 建档时间
    */
	@Schema(description = "建档时间", required = true)
	@CreatedDate
    private Date createdTime;

	/**
    * 建档员
    */
	@Schema(description = "建档员", required = true)
	@CreatedBy
    private Integer createdBy;

	/**
    * 版本
    */
	@Schema(description = "版本", required = true)
	@Column(nullable = false, columnDefinition = "smallint(6)")
    private Integer rowVension;

    @Schema(description = "归属主键")
    private Integer ascription;

}
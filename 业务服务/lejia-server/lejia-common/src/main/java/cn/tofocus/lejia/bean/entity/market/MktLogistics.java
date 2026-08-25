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
*  快递公司
* @author lai 2020-06-15
*/

@Entity
@Data
@Table(name="mkt_logistics")
public class MktLogistics implements HasPkey<Integer> {
   

	/**
	 * pkey
	 */
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_logistics")
	@Schema(description = "pkey")
    private Integer pkey;

    /**
    * 名称
    */
	@Schema(description = "名称")
    private String name;

    /**
    * 描述
    */
	@Schema(description = "描述")
    private String descp;

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
    * 建档时间
    */
	@Schema(description = "建档时间")
	@CreatedDate
    private Date createdTime;

    /**
    * 建档员
    */
	@Schema(description = "建档员")
	@CreatedBy
    private Integer createdBy;

    /**
    * 版本
    */
	@Schema(description = "版本")
	@Column(nullable = false, columnDefinition = "smallint(6)")
    private Integer rowVension;

    @Schema(description = "归属主键")
    private Integer ascription;

}
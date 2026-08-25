package cn.tofocus.lejia.bean.entity.goods;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.bean.enums.GoodsCutType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  砍价商品
* @author lai 2020-06-15
*/

@Entity
@Data
@Table(name="mkt_goods_cut")
public class MktGoodsCut implements HasPkey<Integer> {
   

	/**
	 * pkey
	 */
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_goods_cut")
	@Schema(description = "pkey")
    private Integer pkey;

    /**
    * 砍价设置 按固定/按比例
    */
	@Schema(description = "砍价设置 按固定/按比例")
	@Column(nullable = false, columnDefinition = "tinyint(4)")
    private GoodsCutType type;

    /**
    * 佣金值
    */
	@Schema(description = "佣金值")
    private Integer comm;

    /**
    * 到期日期
    */
	@Schema(description = "到期日期")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" , timezone = "GMT+8")
    private Date endDate;

    /**
    * 启用标志
    */
	@Schema(description = "启用标志")
    private Boolean enabled;

    /**
    * 建档时间
    */
	@Schema(description = "建档时间")
    private Date createdTime;

    /**
    * 建档员
    */
	@Schema(description = "建档员")
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
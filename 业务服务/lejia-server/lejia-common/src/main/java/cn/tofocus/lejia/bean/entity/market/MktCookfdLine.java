package cn.tofocus.lejia.bean.entity.market;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  食材清单
* @author lai 2020-06-15
*/

@Entity
@Data
@Table(name="mkt_cookfd_line")
public class MktCookfdLine implements HasPkey<Integer> {
   

	/**
	 * pkey
	 */
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_cookfd_line")
	@Schema(description = "pkey", hidden = true)
    private Integer pkey;
    
	// 1:新增  2:修改  3:删除
	@Transient
	@Schema(description = "状态", hidden = true)
	private Integer status = 0;
    
	/**
    * 菜谱
    */
	@Schema(description = "菜谱", hidden = true)
    private Integer cookfd;

    /**
    * 商品
    */
	@Schema(description = "商品", required = true)
    private Integer goods;
	@Transient
	@Schema(description = "商品名称")
	private String goodsName = "";
    /**
    * 规格
    */
	@Schema(description = "规格", required = true)
    private Integer space;
	@Transient
	@Schema(description = "规格名称")
	private String spaceName = "";
    /**
    * 数量
    */
	@Schema(description = "数量", required = true)
    private Integer num;

    /**
    * 排序
    */
	@Schema(description = "排序", required = true)
    private Integer sort;

    /**
    * 备注
    */
	@Schema(description = "备注")
    private String remark;

    @Schema(description = "归属主键")
    private Integer ascription;

}
package cn.tofocus.lejia.bean.entity.market;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.core.query.param.valid.ListStrLength;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.db.ListConverter;
import cn.tofocus.db.file.FileUrl;
import cn.tofocus.lejia.bean.enums.PrizeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  礼品配置
*/

@Entity
@Data
@Table(name="mkt_draw_prize")
public class MktDrawPrize implements HasPkey<Integer> {
   
	/**
	 * pkey
	 */
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_draw_prize")
	@Schema(description = "pkey")
    private Integer pkey;

    /**
    * 礼品类型 积分/优惠券/礼品券/礼品/
    */
	@Schema(description = "礼品类型 积分/优惠券/礼品券/礼品/")
	@Column(nullable = false, columnDefinition = "tinyint(4)")
    private PrizeType pType;

	@Schema(description = "商品名称")
	private String name;
	
    /**
    * 中奖概率（%）
    */
	@Schema(description = "中奖概率（%）")
    private Integer probability;

    /**
    * 图片
    */
	@Schema(description = "图片")
	@FileUrl
	@Convert(converter = ListConverter.class)
    @ListStrLength(length = 200)
    private List<String> photo;

    /**
    * 奖品值
    */
	@Schema(description = "奖品值")
    private Integer pvalue;

    /**
    * 中奖描述
    */
	@Schema(description = "中奖描述")
    private String descp;

    /**
    * 排序
    */
	@Schema(description = "排序")
    private Integer sort;

    /**
    * 启用标志
    */
	@Schema(description = "启用标志")
    private Boolean enabled;

    /**
    * 最后更新时间
    */
	@Schema(description = "最后更新时间")
	@LastModifiedDate
    private Date updateTime;

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

    @Schema(description = "归属主键")
    private Integer ascription;

}
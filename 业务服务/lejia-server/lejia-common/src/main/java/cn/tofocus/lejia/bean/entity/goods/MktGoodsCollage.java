package cn.tofocus.lejia.bean.entity.goods;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import lombok.Data;

/**
*  拼团商品
* @author lai 2020-06-15
*/

@Entity
@Data
@Table(name="mkt_goods_collage")
public class MktGoodsCollage implements HasPkey<Integer> {
   

    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_goods_collage")
    /**
    * pkey
    */
	@Schema(description = "pkey")
    private Integer pkey;

    /**
    * 拼团价
    */
	@Schema(description = "拼团价")
    private BigDecimal price;

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

    /**
    * 版本
    */
	@Schema(description = "版本")
	@Column(nullable = false, columnDefinition = "smallint(6)")
    private Integer rowVension;

    @Schema(description = "归属主键")
    private Integer ascription;

}
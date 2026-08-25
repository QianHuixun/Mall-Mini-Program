package cn.tofocus.lejia.bean.entity.vendor;


import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.LastModifiedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  mkt_vendor_goods
* @author zdw 2020-10-09
*/

@Entity
@Data
@Table(name="mkt_vendor_goods")
public class MktVendorGoods implements HasPkey<Integer> {
   


	/**
	 * pkey
	 */
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_vendor_goods")
	@Schema(description = "pkey", required = true)
    private Integer pkey;

	/**
    * 商户
    */
	@Schema(description = "商户", required = true)
    private Integer vendor;

	/**
    * goods
    */
	@Schema(description = "goods", required = true)
    private Integer goods;
	
   /**
    * price
    */
    @Schema(description = "最后采购价格", required = true)
    private BigDecimal price;

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
    * 最后更新时间
    */
	@Schema(description = "最后更新时间", required = true)
	@LastModifiedDate
    private Date updateTime;

    @Schema(description = "归属主键")
    private Integer ascription;

}
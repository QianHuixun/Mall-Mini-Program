package cn.tofocus.lejia.bean.entity.vendor;

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
*  mkt_vendor
* @author lai 2020-06-15
*/

@Entity
@Data
@Table(name="mkt_vendor_point")
public class MktVendorPoint implements HasPkey<Integer> {
   

	/**
	 * pkey
	 */
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_vendor_point")
	@Schema(description = "pkey")
    private Integer pkey;

    /**
     * 积分
     */
    @Schema(description = "积分")
    private Integer points;
    
    /**
    * 修改时间
    */
	@Schema(description = "修改时间")
	@LastModifiedDate
    private Date updateTime;
   
    @Schema(description = "归属主键")
    private Integer ascription;
}
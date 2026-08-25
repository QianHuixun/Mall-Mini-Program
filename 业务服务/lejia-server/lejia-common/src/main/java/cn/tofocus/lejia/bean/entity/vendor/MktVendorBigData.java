package cn.tofocus.lejia.bean.entity.vendor;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.bean.enums.VendorFileType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * mkt_vendor_bigdata
 * 商户大数据表
 * @author geshaojian
 * @date   2021-10-09
 */
@Entity
@Data
@Table(name = "mkt_vendor_bigdata")
public class MktVendorBigData implements HasPkey<Integer>
{
    /**
     * pkey
     */
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_vendor_bigdata")
    @Schema(description = "pkey")
    private Integer pkey;

    /**
     * 说明
     */
    @Schema(description = "说明")
    @Column(columnDefinition = "text")
    private String content;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}
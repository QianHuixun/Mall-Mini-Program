package cn.tofocus.lejia.bean.entity.vendor;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.bean.enums.SettlementMethodType;
import cn.tofocus.lejia.bean.enums.VendorFileType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * mkt_vendor_file
 * 商户文件表
 * @author geshaojian
 * @date   2021-10-09
 */
@Entity
@Data
@Table(name = "mkt_vendor_file")
public class MktVendorFile implements HasPkey<Integer>
{

    /**
     * pkey
     */
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_vendor_file")
    @Schema(description = "pkey")
    private Integer pkey;

    /**
     * 商户表主键
     */
    @Schema(description = "商户表主键")
    private Integer vendorPkey;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 文件地址
     */
    @Schema(description = "文件地址")
    private String url;

    /**
     * 类型
     */
    @Schema(description = "类型")
    @Column(columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private VendorFileType type;

    /**
     * 启用标志
     */
    @Schema(description = "启用标志")
    private Boolean enabled;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}
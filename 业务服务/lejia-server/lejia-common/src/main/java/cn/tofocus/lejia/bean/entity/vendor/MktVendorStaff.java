package cn.tofocus.lejia.bean.entity.vendor;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  商户店员
* @author zdw 2022-01-27
*/

@Entity
@Data
@Table(name = "mkt_vendor_staff")
@FieldNameConstants(innerTypeName = "F")
public class MktVendorStaff implements HasPkey<Integer>
{
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_vendor_staff")
    @Schema(description = "pkey", required = true)
    private Integer pkey;
    
    @Schema(description = "商户主键", required = false)
    private Integer vendor;
    
    @Schema(description = "商户名称", required = false)
    private String vendorName;
    
    @Schema(description = "姓名", required = true)
    private String name;
    
    @Schema(description = "手机号码", required = true)
    private String mobile;
    
    @Schema(description = "openid1", required = false)
    private String openid1;
    
    @Schema(description = "openid2", required = false)
    private String openid2;
    
    @Schema(description = "启用标志", required = true)
    private Boolean enabled;
    
    @Schema(description = "是否已删除", required = true)
    private Boolean idDel;
    
    @Schema(description = "市场", required = true)
    private String farmer;
    
    @Schema(description = "建档时间", required = true)
    @CreatedDate
    private Date createdTime;
    
    @Schema(description = "归属主键")
    private Integer ascription;
    
}
package cn.tofocus.lejia.bean.entity.vendor;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.bean.enums.ShowType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Entity
@Data
@Table(name = "mkt_vendor_boutique")
public class MktVendorBoutique implements HasPkey<Integer>
{
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_vendor_boutique")
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "商户主键")
    private Integer vendor;
    
    @Schema(description = "标签")
    private String label;
    
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Schema(description = "展示类型1")
    private ShowType showType1;
    
    @Schema(description = "展示内容1")
    private String showContent1;
    
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Schema(description = "展示类型2")
    private ShowType showType2;
    
    @Schema(description = "展示内容2")
    private String showContent2;
    
    @Schema(description = "排序")
    private Integer sort;
    
    @Schema(description = "启用标志")
    private Boolean enabled;
    
    @Schema(description = "市场")
    private String farmer;
    
    @Schema(description = "公司")
    private String company;
    
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}

package cn.tofocus.lejia.bean.entity.sys;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.bean.enums.MType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  sys_farmer_mtype
* @author zdw 2022-01-27
*/

@Entity
@Data
@Table(name = "sys_farmer_mtype")
public class SysFarmerMtype implements HasPkey<Integer>
{
    
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "sys_farmer_mtype")
    @Schema(description = "pkey", required = true)
    private Integer pkey;
    
    @Schema(description = "市场主键", required = true)
    private String farmer;
    
    @Schema(description = "积分/市场/会员/特价/分享/砍价/团购/预售", required = true)
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private MType mType;
    
    public String getMTypeName()
    {
        return mType.getName();
    }
    
    @Schema(description = "是否开启配送", required = true)
    private Boolean delivery;
    
    @Schema(description = "是否开启自提", required = true)
    private Boolean pickup;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}
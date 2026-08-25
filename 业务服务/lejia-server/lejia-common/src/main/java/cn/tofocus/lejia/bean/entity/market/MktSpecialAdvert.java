package cn.tofocus.lejia.bean.entity.market;


import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.ListConverter;
import cn.tofocus.lejia.bean.entity.hasPkey.AdvertPkey;
import cn.tofocus.lejia.bean.enums.AdvertPosition;
import cn.tofocus.lejia.bean.enums.LocationType;
import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  专区广告
* @author zdw 2021-09-30
*/

@Entity
@Data
@Table(name = "mkt_special_advert")
@IdClass(AdvertPkey.class)
public class MktSpecialAdvert implements HasPkey<AdvertPkey>
{
    
    @Id
    @Schema(description = "id", required = true)
    private Integer id;
    
    @Schema(description = "广告主键", required = true)
    private Integer advertKey;
    
    @Schema(description = "市场主键", required = true)
    private String farmer;
    
    @Schema(description = "位置 1号/2号/3号/4号/5号", required = true)
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private AdvertPosition position;
    
    @Schema(description = "启用标志", required = true)
    private Boolean enabled;

    @Override
    public AdvertPkey getPkey()
    {
        AdvertPkey pkey = new AdvertPkey(advertKey, id);
        return pkey;
    }

    @Override
    public void setPkey(AdvertPkey pkey)
    {
        setId(pkey.getId());
        setAdvertKey(pkey.getAdvertKey());
    }
    
    @Schema(description = "归属主键")
    private Integer ascription;

    

    
}
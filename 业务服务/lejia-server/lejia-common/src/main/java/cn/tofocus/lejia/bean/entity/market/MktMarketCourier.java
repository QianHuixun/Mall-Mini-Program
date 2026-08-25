package cn.tofocus.lejia.bean.entity.market;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.springframework.data.annotation.LastModifiedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
//import cn.tofocus.db.LineID;
//import cn.tofocus.db.ParentID;
import cn.tofocus.lejia.bean.entity.hasPkey.CourierPkey;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  市场骑手派单顺序
* @author zdw 2021-09-22
*/

@Entity
@Data
@Table(name = "mkt_market_courier")
@IdClass(CourierPkey.class)
public class MktMarketCourier implements HasPkey<CourierPkey>
{
    
    //    @AutoRedisID(domain = "zyysc", sequence="mkt_market_courier")
    @Id
//    @LineID
    @Schema(description = "id", required = true)
    private Integer id;
    
    @Id
//    @ParentID
    @Schema(description = "market", required = true)
    private String market;
    
    @Schema(description = "骑手主键", required = false)
    private Integer courierKey;
    
    @Schema(description = "排序", required = false)
    private Integer sort;
    
    @Schema(description = "flag", required = true)
    @Column(nullable = false, columnDefinition = "bit")
    private Boolean flag;
    
    @Schema(description = "已派单数量", required = false)
    private Integer num;
    
    @Schema(description = "当前时间", required = false)
    @Column(columnDefinition = "date")
    private Date nowDate;
    
    @Schema(description = "更新时间", required = false)
    @LastModifiedDate
    private Date updatedTime;
    
    @Override
    public CourierPkey getPkey()
    {
        CourierPkey pkey = new CourierPkey(market, id);
        return pkey;
    }
    
    @Override
    public void setPkey(CourierPkey pkey)
    {
        setId(pkey.getId());
        setMarket(pkey.getMarket());
    }
    
    @Schema(description = "归属主键")
    private Integer ascription;
}
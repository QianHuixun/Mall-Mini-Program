package cn.tofocus.lejia.bean.entity.market;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  mkt_order_code
* @author zdw 2022-09-09
*/

@Entity
@Data
@Table(name = "mkt_order_code")
public class MktOrderCode implements HasPkey<Integer>
{
    
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_order_code")
    @Schema(description = "pkey", required = true)
    private Integer pkey;
    
    @Schema(description = "order_pkey", required = true)
    private Integer orderPkey;
    
    @Schema(description = "订单号", required = true)
    @Column(name = "kc_code")
    private String code;
    
    @Schema(description = "建档时间", required = true)
    @CreatedDate
    private Date createdTime;
    
}
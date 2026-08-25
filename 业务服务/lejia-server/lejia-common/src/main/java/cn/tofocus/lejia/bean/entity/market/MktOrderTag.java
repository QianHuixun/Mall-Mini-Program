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
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@Table(name="mkt_order_tag")
@FieldNameConstants(innerTypeName = "F")
public class MktOrderTag implements HasPkey<String>
{
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_order_tag")
    @Schema(description = "主键")
    private String pkey;

    @Schema(description = "订单主键")
    private Integer orderPkey;
    
    @Schema(description = "标签主键")
    private Integer tag;
    
    @Schema(description = "标签名称")
    private String tagName;
    
    @Column
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
    
    @Column
    @Schema(description = "归属主键")
    private Integer ascription;
    
}

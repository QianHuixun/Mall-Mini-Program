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

/**
 * @author czy
 * @version [版本号, 2024/12/3]
 */
@Data
@Entity
@Schema(description = "订单物流节点表")
@Table(name = "mkt_order_express_route")
@FieldNameConstants(innerTypeName = "F")
public class MktOrderExpressRoute implements HasPkey<Long>
{
    @Id
    @Column
    @Schema(description = "主键")
    @AutoRedisID(domain = "zyysc", sequence = "mkt_order_express_route")
    private Long pkey;

    @Column
    @Schema(description = "物流单主键")
    private Long orderExpress;
    
    @Column(length = 20)
    @Schema(description = "物流单号")
    private String expressNo;
    
    // 冗余
    @Column
    @Schema(description = "订单主键")
    private Integer orderPkey;
    
    // 冗余
    @Column(length = 20)
    @Schema(description = "订单号")
    private String kcCode;
    
    // 此运单号可能与MktOrderExpress的waybillNo不同，此处可能是子运单号
    @Column(length = 20)
    @Schema(description = "快递运单号")
    private String mailNo;

    // 如果有，用于去重
    @Column(length = 50)
    @Schema(description = "快递公司路由节点编号")
    private String thirdId;
    
    @Column
    @Schema(description = "路由节点产生的时间")
    private Date time;
    
    @Column(length = 100)
    @Schema(description = "路由节点发生的城市")
    private String address;
    
    @Column(length = 20)
    @Schema(description = "路由节点操作码")
    private String opCode;
    
    @Column(length = 200)
    @Schema(description = "路由节点具体描述")
    private String description;
    
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
    
    @Column
    @Schema(description = "归属主键")
    private Integer ascription;
}

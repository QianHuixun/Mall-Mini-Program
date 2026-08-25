package cn.tofocus.lejia.bean.entity.market;



import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@Entity
@Schema(description = "第三发配送单号流水")
@Table(name = "mkt_order_delivery_msg")
@FieldNameConstants(innerTypeName = "F")
public class MktOrderDeliveryMsgEntity implements HasPkey<String>
{
    @Id
    @Column(length=64)
    @Schema(description = "pkey=配送订单号")
    private String pkey;
    
    @Schema(description = "系统订单号")
    @Column(length=64)
    private String orderNo;
    

    
    
    @Schema(description = "第三方的配送订单号")
    @Column(length=64)
    private String thirdPartyOrderNo;
    
    @Schema(description = "店铺ID")
    @Column(length=64)
    private String shopId;
    
    /**
     * 市场
     */
    @Schema(description = "市场")
    private String farmer;

    /**
     * 公司
     */
    @Schema(description = "公司")
    private String company;
    
    /**
    * 建档时间
    */
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
    
    

}

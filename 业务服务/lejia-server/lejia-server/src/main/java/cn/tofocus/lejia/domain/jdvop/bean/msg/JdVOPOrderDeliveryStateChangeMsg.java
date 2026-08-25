package cn.tofocus.lejia.domain.jdvop.bean.msg;

import lombok.Data;

/**
 * 订单妥投消息
 * {"orderId":"京东订单编号", "state":"1是妥投，2是拒收"}
 */
@Data
public class JdVOPOrderDeliveryStateChangeMsg
{
    private Long orderId;
    
    // 1是妥投，2是拒收
    private Integer state;
}

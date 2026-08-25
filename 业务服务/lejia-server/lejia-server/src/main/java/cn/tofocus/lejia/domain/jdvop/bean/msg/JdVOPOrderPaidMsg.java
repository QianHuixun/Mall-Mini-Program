package cn.tofocus.lejia.domain.jdvop.bean.msg;

import lombok.Data;

/**
 * 新订单消息
 * {"orderId":京东订单号, "pin":"京东账号"}
 * 另外将消息体的传送时间插入作为订单创建时间
 */
@Data
public class JdVOPOrderPaidMsg
{
    private Long orderId;
    
    private String pin;

    private long time;
}

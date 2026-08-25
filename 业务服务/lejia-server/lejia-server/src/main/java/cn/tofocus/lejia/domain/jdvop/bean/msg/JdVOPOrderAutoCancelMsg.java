package cn.tofocus.lejia.domain.jdvop.bean.msg;

import lombok.Data;

/**
 * 7天未支付取消消息/未确认取消消息
 * {"orderId": 京东订单编号, "cancelType": 取消类型}
 */
@Data
public class JdVOPOrderAutoCancelMsg
{
    private Long orderId;
    
    // 1: 7天未支付取消消息; 2: 未确认取消
    private Integer cancelType;
}

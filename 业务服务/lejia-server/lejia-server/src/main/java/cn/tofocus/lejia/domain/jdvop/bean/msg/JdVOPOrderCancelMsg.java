package cn.tofocus.lejia.domain.jdvop.bean.msg;

import lombok.Data;

/**
 * 订单取消消息
 * {"orderId": 京东订单编号 ," state": 取消结果(0:失败,1:成功)}
 */
@Data
public class JdVOPOrderCancelMsg
{
    private Long orderId;
    
    // 0:取消失败,1:取消成功
    private Integer state;
}

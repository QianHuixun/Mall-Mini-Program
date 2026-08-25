package cn.tofocus.lejia.domain.jdvop.bean.msg;

import lombok.Data;

/**
 * 配送单生成成功消息
 * {"orderId": 京东订单编号 }
 */
@Data
public class JdVOPOrderDeliveryInfoMsg
{
    private Long orderId;
}

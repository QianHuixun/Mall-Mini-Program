package cn.tofocus.lejia.domain.jdvop.bean.msg;

import lombok.Data;

/**
 * 支付失败消息
 * {"orderId": 京东订单编号}
 */
@Data
public class JdVOPOrderPayFailedMsg
{
    private Long orderId;
}

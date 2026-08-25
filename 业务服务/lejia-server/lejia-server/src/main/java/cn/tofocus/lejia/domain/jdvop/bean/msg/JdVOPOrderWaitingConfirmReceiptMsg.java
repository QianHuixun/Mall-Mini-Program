package cn.tofocus.lejia.domain.jdvop.bean.msg;

import lombok.Data;

/**
 * 订单等待确认收货消息
 * {"orderId":京东订单号}
 */
@Data
public class JdVOPOrderWaitingConfirmReceiptMsg
{
    private Long orderId;
}

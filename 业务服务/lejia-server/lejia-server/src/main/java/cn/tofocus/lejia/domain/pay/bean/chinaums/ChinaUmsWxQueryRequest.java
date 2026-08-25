package cn.tofocus.lejia.domain.pay.bean.chinaums;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChinaUmsWxQueryRequest extends ChinaUmsRequest
{
    // （可空）消息ID，原样返回 <=64
    private String msgId;
    
    // （可空）商户订单号
    private String merOrderId;
    
    // （可空）支付订单号
    private String targetOrderId;
}

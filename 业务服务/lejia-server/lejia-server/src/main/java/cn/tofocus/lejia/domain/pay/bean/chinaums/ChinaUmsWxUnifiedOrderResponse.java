package cn.tofocus.lejia.domain.pay.bean.chinaums;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChinaUmsWxUnifiedOrderResponse extends ChinaUmsResponse
{
    // （可空）消息ID
    private String msgId;
    
    // （可空）商户名称
    private String merName;
    
    // （可空）商户订单号
    private String merOrderId;
    
    // （可空）平台流水号
    private String seqId;
    
    // （可空）清分ID 如果来源方传了bankRefId就等于bankRefId，否则等于seqId
    private String settleRefId;
    
    // （可空）交易状态
    private String status;
    
    // （可空）支付总金额
    private Integer totalAmount;
    
    // （可空）第三方订单号
    private String targetOrderId;
    
    // （可空）目标平台代码
    private String targetSys;
    
    // （可空）目标平台的状态
    private String targetStatus;
    
    // （可空）小程序支付用的请求报文，带有签名信息
    private Object miniPayRequest;
    
    // （可空）支付渠道商户号 各渠道情况不同，酌情转换。
    private String targetMid;
    
    // （可空）营销联盟优惠金额 仅享受联盟优惠的订单，查询返回
    private Integer yxlmAmount;
}

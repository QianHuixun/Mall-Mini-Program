package cn.tofocus.lejia.domain.pay.bean.chinaums;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChinaUmsRefundRequest extends ChinaUmsRequest
{
    // 商户订单号
    private String merOrderId;
    
    // 要退货的金额
    // 若下单接口中上送了分账标记字段divisionFlag，则该字段refundAmount=subOrders中totalAmount之和+platformAmount
    private Integer refundAmount;
    
    // （可空）消息ID 原样返回 <=64
    private String msgId;
    
    // （可空）支付订单号
    private String targetOrderId;
    
    // （可空）退款订单号
    // 多次退款必传，每次退款上送的refundOrderId值需不同，若多次退货，且后续退货上送的merOrderId和refundOrderId字段与之前退货上送的值一致，将不会走退货逻辑，而是返回已有退货订单的退货信息，遵循商户订单号生成规范
    private String refundOrderId;
    
    // （可空）平台商户退款分账金额
    // 若原交易是分账交易，则分账金额必传，且退款接口platformAmount小于下单接口中上送的platformAmount
    private String platformAmount;
    
    // （可空）子订单信息
    // 若原交易有分账，退款时必填。且退款接口totalAmount小于下单接口中上送的subOrders中对应mid下的totalAmount。
    private List<SubOrder> subOrders;
    
    // （可空）退货说明 <=255
    private String refundDesc;
    
    // （可空）银联营销代码
    private String ylyxId;
    
    // （可空）银联营销名称
    private String ylyxName;
    
    // （可空）银联营销二级子商户简称
    private String ylyxMerAbbr;
    
    @Data
    public class SubOrder
    {
        // （可空）子商户号
        private String mid;
        
        // （可空）商户子订单号
        private String merOrderId;
        
        // （可空）商户退款子订单号
        private String refundOrderId;
        
        // （可空）子商户分账金额
        private Integer totalAmount;
    }
}

package cn.tofocus.lejia.domain.pay.bean.chinaums;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChinaUmsRefundQueryResponse extends ChinaUmsResponse
{
    // （可空）商户出资优惠金额
    // 目前支持微信返回，其他渠道产品规划中
    private Integer refundMerchantContribute;

    // （可空）其他出资优惠金额
    // 目前支持微信返回，其他渠道产品规划中
    private Integer refundOtherContribute;
    
    // （可空）消息ID，原样返回
    private String msgId;
    
    // （可空）退款状态
    // UNKNOWN	不明确的交易状态	需要调退款查询接口确认退款结果
    // SUCCESS	退款成功
    // FAIL	退款失败
    // PROCESSING	退款处理中
    private String refundStatus;
    
    // （可空）退货订单号
    private String refundOrderId;
    
    // （可空）目标系统退货订单号
    private String refundTargetOrderId;
    
    // （可空）平台流水号
    private String seqId;
    
    // （可空）清分ID
    // 如果来源方传了bankRefId就等于bankRefId，否则等于seqId
    private String settleRefId;
    
    // （可空）交易状态
    private String status;
    
    // （可空）支付总金额
    private Integer totalAmount;
    
    // （可空）商户名称
    private String merName;
    
    // （可空）商户订单号
    private String merOrderId;
    
    // （可空）第三方订单号
    private String targetOrderId;
    
    // （可空）目标平台代码
    private String targetSys;
    
    // （可空）目标平台状态
    private String targetStatus;
    
    // （可空）支付渠道商户号
    private String targetMid;
    
    // （可空）银行卡号
    private String bankCardNo;
    
    // （可空）银行信息
    private String bankInfo;
    
    // （可空）退款渠道列表
    // 示例：支付宝余额:33
    private String refundFunds;
    
    // （可空）退款渠道描述
    private String refundFundsDesc;
    
    // （可空）支付时间 格式yyyy-MM-dd HH:mm:ss
    private String payTime;
    
    // （可空）结算日期 格式yyyy-MM-dd
    private String settleDate;
    
    // （可空）商户实退金额
    private String sendBackAmount;
    
    // （可空）营销联盟优惠金额
    // 仅享受联盟优惠的订单，查询返回
    private Integer yxlmAmount;
    
    // （可空）实付部分退款金额
    private Integer refundInvoiceAmount;
    
    // （可空）卡属性 DEBIT_CARD（借记卡）；CREDIT_CARD（贷记卡）
    private String cardAttr;
    
    // （可空）支付宝渠道支卡通最终核销金额
    private String mCardAmt;
    
    // （可空）优惠退货金额（合计）
    private Integer totalRefundPromotionAmt;
    
    // （可空）订单优惠状态 0：订单无优惠 1：订单有优惠但未找到 2：订单有优惠且信息完整
    private Integer orderPromotionStatus;
    
    // （可空）活动列表
    // 单品营销优惠活动列表
    private List<Object> eventList;
    
    // （可空）原文信息
    // 支付渠道侧信息chnlInfo： chnlCode：支付渠道名称（ACP/ALIPAY/WXPAY） promotionDetail：微信优惠详情（base64编码，channel_code= WXPAY场景必填，具体格式参考微信refund_detail字段说明） cupPromotionInfo：银联活动优惠详情（base64编码，channel_code= ACP场景必填，具体格式参考银联couponInfo） issAddiData：银联付款方附加数据（base64编码，channel_code= ACP场景必填，具体格式参考银联issAddnData） 收单机构侧信息acqInfo： refund_detail_item_list:支付宝优惠信息(base64编码，channel_code= ALIPAY场景必填，具体格式参考支付宝refund_detail_item_list) acqCode：收单机构名称（UMS） refundPromotionList：退货优惠详情（base64编码，acq_code =UMS场景必填，具体格式参考银联商务字段说明）
    private Object oriInfo;
}

package cn.tofocus.lejia.domain.pay.bean.chinaums;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChinaUmsWxQueryResponse extends ChinaUmsResponse
{
    // 业务类型，原样返回
    private String instMid;
    
    // 支付渠道列表 格式为：方式:金额（单位：分）
    private String billFunds;
    
    // 支付渠道描述
    private String billFundsDesc;
    
    // 支付时间 格式yyyy-MM-dd HH:mm:ss
    private String payTime;
    
    // （可空）商户出资优惠金额，目前支持微信返回，其他渠道产品规划中
    private Integer couponMerchantContribute;
    
    // （可空）其他出资优惠金额，目前支持微信返回，其他渠道产品规划中
    private Integer couponOtherContribute;
    
    // （可空）消息ID，原样返回
    private String msgId;
    
    // （可空）平台流水号
    private String seqId;
    
    // （可空）清分ID 如果来源方传了bankRefId就等于bankRefId，否则等于seqId
    private String settleRefId;
    
    // （可空）检索参考号 用在银联体系交易中
    private String refId;
    
    // （可空）交易状态
    // NEW_ORDER	新订单
    // UNKNOWN	不明确的交易状态
    // TRADE_CLOSED	在指定时间段内未支付时关闭的交易；在交易完成全额退款成功时关闭的交易；支付失败的交易。	TRADE_CLOSED的交易不允许进行任何操作。
    // WAIT_BUYER_PAY	交易创建，等待买家付款。
    // TRADE_SUCCESS	支付成功
    // TRADE_REFUND	订单转入退货流程	退货可能是部分也可能是全部。
    private String status;
    
    // （可空）支付总金额
    private String totalAmount;
    
    // （可空）商户名称
    private String merName;
    
    // （可空）商户订单号
    private String merOrderId;
    
    // （可空）目标平台单号
    private String targetOrderId;
    
    // （可空）目标平台代码
    private String targetSys;
    
    // （可空）目标平台的状态
    private String targetStatus;
    
    // （可空）买家ID
    private String buyerId;
    
    // （可空）支付渠道商户号 各渠道情况不同，酌情转换
    private String targetMid;
    
    // （可空）银行卡号
    private String bankCardNo;
    
    // （可空）银行信息
    private String bankInfo;
    
    // （可空）买家付款的金额 支付宝会有
    private Integer buyerPayAmount;
    
    // （可空）买家用户名
    private String buyerUsername;
    
    // （可空）网付计算的优惠金额
    private Integer couponAmount;
    
    // （可空）交易中可给用户开具发票的金额
    private Integer invoiceAmount;
    
    // （可空）商户实收金额 支付宝会有
    private Integer receiptAmount;
    
    // （可空）结算日期 格式yyyy-MM-dd
    private String settleDate;
    
    // （可空）子买家ID 如微信的subOpenId
    private String subBuyerId;
    
    // （可空）微信活动ID
    private String activityIds;
    
    // （可空）营销联盟优惠金额 仅享受联盟优惠的订单，查询返回
    private Integer yxlmAmount;
    
    // （可空）卡属性 DEBIT_CARD（借记卡）；CREDIT_CARD（贷记卡）
    private String cardAttr;
    
    // （可空）支付宝渠道支卡通最终核销金额
    private String mCardAmt;
    
    // （可空）优惠金额（合计）
    private Integer totalPromotionAmt;
    
    // （可空）优惠状态 0：订单无优惠 1：订单有优惠但未找到 2：订单有优惠且找到
    private Integer orderPromotionStatus;
    
    // （可空）优惠活动活动列表
    // 暂时用不到，先不实现结构
    private Object promotionList;
}

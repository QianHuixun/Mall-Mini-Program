package cn.tofocus.lejia.bean.entity.zx;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@Schema(description = "第三方支付渠道回调")
@Table(name = "third_pay_line")
@FieldNameConstants(innerTypeName = "F")
public class ThirdPayLineEntity implements HasPkey<Integer>
{
    
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "third_pay_line")
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "支付时间 格式yyyy-mm-dd hh:mm:ss")
    private String payTime;
    
    @Schema(description = "商户号")
    private String mid;
    
    @Schema(description = "终端号")
    private String tid;
    
    @Schema(description = "业务类型 minidefault")
    private String instMid;
    
    @Schema(description = "附加数据")
    private String attachedData;
    
    @Schema(description = "支付银行信息")
    private String bankCardNo;
    
    @Schema(description = "资金渠道")
    private String billFunds;
    
    @Schema(description = "资金渠道说明")
    private String billFundsDesc;
    
    @Schema(description = "买家id")
    private String buyerId;
    
    @Schema(description = "买家用户名")
    private String buyerUsername;
    
    @Schema(description = "实付金额")
    private Integer buyerPayAmount;
    
    @Schema(description = "订单金额，单位分")
    private Integer totalAmount;
    
    @Schema(description = "开票金额")
    private Integer invoiceAmount;
    
    @Schema(description = "商户订单号")
    private String merOrderId;
    
    @Schema(description = "实收金额")
    private Integer receiptAmount;
    
    @Schema(description = "支付银行卡参考号")
    private String refId;
    
    @Schema(description = "退款金额 退货交易")
    private Integer refundAmount;
    
    @Schema(description = "退款说明 退货交易")
    private String refundDesc;
    
    @Schema(description = "系统交易流水号")
    private String seqId;
    
    @Schema(description = "结算日期 格式yyyy-mm-dd")
    private String settleDate;
    
    @Schema(description = "交易状态")
    private String status;
    
    @Schema(description = "买家子id")
    private String subBuyerId;
    
    @Schema(description = "渠道订单号")
    private String targetOrderId;
    
    @Schema(description = "支付渠道")
    private String targetSys;
    
    @Schema(description = "商户出资优惠金额")
    private String couponMerchantContribute;
    
    @Schema(description = "其他出资优惠金额")
    private String couponOtherContribute;
    
    @Schema(description = "微信活动id")
    private String activityIds;
    
    @Schema(description = "退货渠道订单号")
    private String refundTargetOrderId;
    
    @Schema(description = "退货时间")
    private String refundPayTime;
    
    @Schema(description = "结算日期")
    private String refundSettleDate;
    
    @Schema(description = "订单详情")
    private String orderDesc;
    
    @Schema(description = "订单创建时间")
    private String createTime;
    
    @Schema(description = "商户uuid")
    private String mchntUuid;
    
    @Schema(description = "转接系统")
    private String connectSys;
    
    @Schema(description = "商户所属分支机构代码")
    private String subInst;
    
    @Schema(description = "联盟优惠金额")
    private Integer yxlmAmount;
    
    @Schema(description = "退货外部订单号")
    private String refundExtOrderId;
    
    @Schema(description = "商品交易单号")
    private String goodsTradeNo;
    
    @Schema(description = "外部订单号")
    private String extOrderId;
    
    @Schema(description = "担保交易状态")
    private String secureStatus;
    
    @Schema(description = "担保完成金额")
    private String completeAmount;
    
    @Schema(description = "退货订单号")
    private String refundOrderId;
    
    @Schema(description = "渠道优惠金额 单位：分")
    private Integer couponAmount;
    
    @Schema(description = "银行信息")
    private String bankInfo;
    
}
package cn.tofocus.lejia.domain.pay.bean.chinaums;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChinaUmsWxUnifiedOrderRequest extends ChinaUmsRequest
{
    // 商户订单号
    private String merOrderId;
    
    // 支付总金额 单位分
    // 若divisionFlag为true，则： totalAmount =subOrders字段中的所有totalAmount值之和+platformAmount值 =goods中的所有subOrderAmount值之和。
    private Integer totalAmount;
    
    // 微信子商户appId
    private String subAppId;
    
    // 交易类型 微信小程序值为MINI
    private String tradeType;
    
    // 用户子标识
    private String subOpenId;
    
    // （可空）消息ID，原样返回 <=64
    private String msgId;
    
    // （可空）商品信息
    private List<Goods> goods;
    
    // （可空）商户附加数据 <=255
    private String attachedData;
    
    // （可空）订单过期时间 为空则使用系统默认过期时间（30分钟），格式yyyy-MM-dd HH:mm:ss
    private String expireTime;
    
    // （可空）商品标记 用于优惠活动 <=32
    private String goodsTag;
    
    // （可空）账单描述 微信支付时 上送值长度不超过128字节
    private String orderDesc;
    
    // （可空）订单原始金额 单位分，用于记录前端系统打折前的金额
    private Integer originalAmount;
    
    // （可空）商品ID
    private String productId;
    
    // （可空）分账标记 若为true，则goods字段和subOrders字段不能同时为空；且secureTransaction字段上送false或不上送。
    private Boolean divisionFlag;
    
    // （可空）异步分账标记 若为true，则goods字段和subOrders字段不能同时为空；且secureTransaction字段上送false或不上送。 退货订单不允许做子订单操作 已确认的子订单，不允许隔天再确认
    private Boolean asynDivisionFlag;
    
    // （可空）平台商户分账金额 若分账标记传，则分账金额必传
    private Integer platformAmount;
    
    // （可空）子订单信息
    // 在传分账标记的情况下，若传子商户号，子商户分账金额必传，即ubOrders每个元素的mid和totalAmount非空且mid不超过20个。（分账方案subOrders里子商户分账总额+platformAmount要与支付总额totalAmount相等）。
    // 在传分账标记的情况下，接口中goods和subOrders二者必传其一；
    //     若传goods则分账信息会按goods中每个商品的总额占支付总额减平台分账金额等比例生成；
    //     若传subOrders，则分账信息则严格按subOrders里的分账方案生成。
    List<SubOrder> subOrders;
    
    // （可空）支付结果通知地址 <=255
    private String notifyUrl;
    
    // （可空）担保交易标识 取值：true或false，默认false
    // 若上送为true，则交易的金额将会被暂缓结算。 调用担保完成接口后，完成部分金额会在t+1日结算给商户，剩余部分金额退还用户。 调用担保撤销接口，则全部资金退还给用户。 30天后没有主动调用担保完成 且没有主动调用担保撤销的交易 将会自动按撤销处理。
    private String secureTransaction;
    
    // （可空）用户子标识 支付宝必传，需要商户自行调用支付宝接口获取
    private String userId;
    
    // （可空）是否需要限制信用卡支付 取值：true或false，默认false
    private String limitCreditCard;
    
    // （可空）花呗分期数 取值：仅支持3、6、12
    private Integer installmentNumber;
    
    // （可空）实名认证姓名 Base64编码 <=32
    private String name;
    
    // （可空）实名认证手机号 Base64编码 <=20
    private String mobile;
    
    // （可空）实名认证证件类型（身份证：IDENTITY_CARD）
    private String certType;
    
    // （可空）实名认证证件号 Base64编码 银联云闪付支持上送身份证后六位（当身份证最后一位是 X时，上送 X前面的 6位数字），注意同样需要Base64编码 <=64
    private String certNo;
    
    // （可空）是否需要实名认证 需要实名认证时置为T
    private String fixBuyer;
    
    // （可空）返佣字段 目前支持支付宝渠道
    // 无用暂未实现结构
    
    // （可空）手续费比例 新悦融益业务 该字段必传
    // 无用暂未实现结构
    
    // （可空）确认成本补贴 新悦融益业务 该字段必传
    // 无用暂未实现结构
    
    // （可空）预授权交易标识 取值：true或false，默认false。若上送为true，则交易的金额将会被银行冻结。调用预授权完成接口后，完成部分金额会在t+1日结算给商户，剩余部分金额在用户银行卡中解冻。（仅云闪付小程序支持）
    private Boolean preauthTransaction;
    
    // （可空）客户端IP 用户客户端的ip地址
    private String clientIp;
    
    @Data
    public class Goods
    {
        // （可空）商品ID <=64
        private String goodsId;
        
        // （可空）商品名称 <=256
        private String goodsName;
        
        // （可空）商品数量
        private Integer quantity;
        
        // （可空）商品单价 单位分
        private Integer price;
        
        // （可空）商品分类 <=64
        private String goodsCategory;
        
        // （可空）商品说明 <=1024
        private String body;
        
        // （可空）商品单位
        private String unit;
        
        // （可空）商品折扣
        private String discount;
        
        // （可空）子商户号
        private String subMerchantId;
        
        // （可空）商户子订单号
        private String merOrderId;
        
        // （可空）子商户商品总额，单位：分
        private Integer subOrderAmount;
    }
    
    @Data
    public class SubOrder
    {
        // （可空）子商户号
        private String mid;
        
        // （可空）商户子订单号
        private String merOrderId;
        
        // （可空）子商户分账金额
        private Integer totalAmount;
    }
}

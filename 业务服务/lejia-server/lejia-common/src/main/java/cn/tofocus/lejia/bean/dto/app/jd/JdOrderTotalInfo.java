package cn.tofocus.lejia.bean.dto.app.jd;

import java.math.BigDecimal;
import java.util.List;

import cn.tofocus.lejia.bean.dto.app.wxPay.WxPayData;
import cn.tofocus.lejia.bean.enums.OrderType;
import cn.tofocus.lejia.bean.enums.PayType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class JdOrderTotalInfo
{
    @Schema(description = "收货地址主键,提交订单只需传这个参数")
    private Integer addrPkey;
    
    @Schema(description = "订单明细,提交订单只需传这个参数")
    private List<JdOrderInfo> infos;
    
    @Schema(description = "支付类型,提交订单只需传这个参数")
    private PayType payType;
    
    
    /** 以下数据 不需要传参进来   */
    @Schema(description = "合计商品金额(没扣除其他费用)")
    private BigDecimal goodsSumAmto;
    
    @Schema(description = "合计商品金额(最后支付的金额)")
    private BigDecimal goodsSumAmtn;
    
    @Schema(description = "合计商品积分")
    private Integer sumPointn;
    
    @Schema(description = "合计配送费")
    private BigDecimal sumPostage;
    
    @Schema(description = "支付成功后,用于跳转订单详情")
    private Integer orderPkey;
    
    @Schema(description = "订单类型 砍价/团购/预售/佣金/市场/积分")
    private OrderType orderType;
    
    @Schema(description = "用户民生豆")
    private BigDecimal myMsd;
    
    @Schema(description = "地址")
    private String addr;
    
    @Schema(description = "详细地址")
    private String addrDetail;
    
    @Schema(description = "省")
    private String pro;

    @Schema(description = "市")
    private String city;
    
    @Schema(description = "收货人")
    private String name;
    
    @Schema(description = "收货人手机")
    private String mobile;
    
    @Schema(description = "微信支付数据")
    private WxPayData wxPayData;
    
    @Schema(description = "是否可用热力豆支付")
    private Boolean msdPay = true;
}

package cn.tofocus.lejia.util.print.bean;

import java.math.BigDecimal;
import java.util.List;

import cn.tofocus.lejia.bean.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CustomerPrintBean
{
    private OrderStatus status;
    
    private String marketName;//  市场名
    
    private String qrcode;//url
    
    private String orderNumber;
    
    private String orderTime;//格式 2023-12-12 12:12:12
    
    private String orderTrace;//订单追溯码
    
    private String pickUp;//取货方式
    
    private String marketMobile;//市场电话
    
    private String receivedTime;//期望送达时间
    
    private String remarket;//备注
    
    private String address;//地址
    
    private String name;//用户名字
    
    private String mobile;//用户手机号
    
    private BigDecimal goodsAmt;//订单金额
    
    private String deliveryMode;//配送方式
    
    private BigDecimal deliveryFee;//配送费用
    
    private BigDecimal discountAmt; //优惠金额
    
    private Integer totalCount;//总 件数
    
    private BigDecimal totalAmt;//合计 金额
    
    private List<PrintOriInfo> ori;
    
    @Schema(description = "退款金额")
    private BigDecimal refundAmt;
    
    @Schema(description = "小票内容")
    private String content;
    
    @Schema(description = "图1")
    private String photo1;
    
    @Schema(description = "图1文字")
    private String photo1Text;
    
    @Schema(description = "图2")
    private String photo2;
    
    @Schema(description = "图2文字")
    private String photo2Text;
}

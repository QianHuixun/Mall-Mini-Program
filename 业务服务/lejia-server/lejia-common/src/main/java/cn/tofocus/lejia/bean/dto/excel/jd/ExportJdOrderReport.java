package cn.tofocus.lejia.bean.dto.excel.jd;

import java.math.BigDecimal;

import com.alibaba.excel.annotation.ExcelProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ExportJdOrderReport
{
    
    @ExcelProperty("订单号")
    private String code;
    
    @ExcelProperty("商品金额")
    private BigDecimal amto;
    
    @ExcelProperty("邮费")
    private BigDecimal postage;
    
    @ExcelProperty("商品退款")
    private BigDecimal refundGoodsAmt;
    
    @ExcelProperty("邮费退款")
    private BigDecimal refundPostage;
    
    @ExcelProperty("微信支付金额")
    private BigDecimal weixinAmt;
    
    @ExcelProperty("热力豆支付金额")
    private BigDecimal otherAmt;
    
    @ExcelProperty("微信支付退款金额")
    private BigDecimal refundWeixinAmt;
    
    @ExcelProperty("热力豆支付退款金额")
    private BigDecimal refundOtherAmt;
    
    @ExcelProperty("京东商品金额")
    private BigDecimal jdGoodsAmt;

    @ExcelProperty("京东邮费")
    private BigDecimal oldPostage;
    
    @ExcelProperty("京东商品退款")
    private BigDecimal refundJdGoodsAmt;
    
    @ExcelProperty("京东合计")
    private BigDecimal jdAmt = BigDecimal.ZERO;
}

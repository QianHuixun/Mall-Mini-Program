package cn.tofocus.lejia.bean.dto.excel.order;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.excel.annotation.ExcelProperty;

import lombok.Data;

@Data
public class ExportMktGoodsOrderLineOnPage
{
    @ExcelProperty("订单号")
    private String kcCode;
    
    @ExcelProperty("商品名称")
    private String goodsName;
    
    @ExcelProperty("规格")
    private String spaceName;
    
    @ExcelProperty("数量")
    private Integer num;
    
    @ExcelProperty("商品金额")
    public BigDecimal amt;
    
    @ExcelProperty("优惠")
    public BigDecimal discount;
    
    @ExcelProperty("实付金额")
    private BigDecimal couponAmt;
    
    @ExcelProperty("退款金额")
    private BigDecimal refundAmt;
    
    @ExcelProperty("用户手机号")
    private String memberMobile;
    
    @ExcelProperty("配送方式")
    private String deliveryTypeName;
    
    @ExcelProperty("订单状态")
    private String statusName;
    
    @ExcelProperty("付款时间")
    private Date createdTime;
}

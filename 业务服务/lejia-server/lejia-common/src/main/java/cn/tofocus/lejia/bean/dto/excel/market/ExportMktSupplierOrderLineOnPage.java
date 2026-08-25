package cn.tofocus.lejia.bean.dto.excel.market;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.excel.annotation.ExcelProperty;

import lombok.Data;

@Data
public class ExportMktSupplierOrderLineOnPage
{
    @ExcelProperty("订单编号")
    private String kcCode;
    
    @ExcelProperty("供应商")
    private String supplierName;
    
    @ExcelProperty("商品名")
    private String goodsName;
    
    @ExcelProperty("商品规格")
    private String spaceName;
    
    @ExcelProperty("数量")
    private Integer num;
    
    @ExcelProperty("商品单价")
    private BigDecimal pricen;
    
    @ExcelProperty("商品总价")
    private BigDecimal amt;
    
    @ExcelProperty("商品退款")
    private BigDecimal refundAmt;
    
    @ExcelProperty("合计金额")
    private BigDecimal sumGoodsAmt;
    
    @ExcelProperty("积分单价")
    private Integer point;
    
    @ExcelProperty("积分总价")
    private Integer pointSum;
    
    @ExcelProperty("积分退款")
    private Integer refundPoint;
    
    @ExcelProperty("购买用户")
    private String memberMobile;
    
    @ExcelProperty("用户标签")
    private String tagName;
    
    @ExcelProperty("支付方式")
    private String payTypeName;
    
    @ExcelProperty("付款时间")
    private Date createdTime;
}

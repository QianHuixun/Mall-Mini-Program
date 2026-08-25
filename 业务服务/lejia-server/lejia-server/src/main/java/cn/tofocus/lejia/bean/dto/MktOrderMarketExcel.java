package cn.tofocus.lejia.bean.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.BooleanEnum;

import lombok.Data;

@Data
@ColumnWidth(15)
@HeadFontStyle(fontHeightInPoints = 15, bold = BooleanEnum.FALSE)
@HeadStyle(fillForegroundColor = 1)
public class MktOrderMarketExcel
{
    
    @ExcelProperty("订单号")
    private String code;
    
    @ExcelProperty("状态")
    private String statusName;
    
    @ExcelProperty("商品类型")
    private String orderTypeName;
    
    @ExcelProperty("付款时间")
    private Date createdTime;
    
    @ExcelProperty("付款类型 ")
    private String payTypeName;

    @ExcelProperty("购买用户")
    private String memberMobile;
    
    @ExcelProperty("配送时间")
    private String pstime;
    
    @ExcelProperty("配送方式")
    private String distributionTypeName;
    
    @ExcelProperty("骑手类型")
    private String expressTypeName;
    
    @ExcelProperty("配送状态")
    private String ThirdPartyStatusName;
    
    @ExcelProperty("商品价格")
    private BigDecimal amto;
    
    @ExcelProperty("邮费")
    private BigDecimal postage;
    
    @ExcelProperty("商品优惠")
    private BigDecimal cardAmt;
    
    @ExcelProperty("配送优惠")
    private BigDecimal cardPostageAmt;
    
    @ExcelProperty("总价")
    private BigDecimal amtall;
    
    @ExcelProperty("支付价格")
    private BigDecimal amtn;
    
    @ExcelProperty("退款金额")
    private BigDecimal refundAmt;
    
    @ExcelProperty("退款积分")
    private Integer refundPoint;
    
    @ExcelProperty("采购金额")
    private BigDecimal purchaseAmt;
    
    @ExcelProperty("采购状态")
    private String cgCheckName;
    
    @ExcelProperty("收货人姓名")
    private String name;
    
    @ExcelProperty("收货人手机")
    private String mobile;
    
    @ExcelProperty("收货人地址")
    private String addr;
    
}

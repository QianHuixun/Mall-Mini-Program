package cn.tofocus.lejia.bean.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.BooleanEnum;

import cn.tofocus.db.dto.JoinProperty;
import cn.tofocus.lejia.bean.enums.DistributionType;
import lombok.Data;

@Data
@ColumnWidth(15)
@HeadFontStyle(fontHeightInPoints = 15, bold = BooleanEnum.FALSE)
@HeadStyle(fillForegroundColor = 1)
public class MktOrderOnListExcel
{
    @ExcelIgnore
    private Integer pkey;
    
    /**
    * 订单号
    */
    @ExcelProperty("订单号")
    private String code;
    
    @ExcelProperty("状态")
    private String statusName;
    
    @ExcelProperty("购买用户")
    private String memberMobile;
    
    @ExcelProperty("用户标签")
    private String tagName;
    
    @ExcelIgnore
    private DistributionType distributionType;
    
    @ExcelProperty("配送方式")
    private String distributionTypeName;
    
    public String getDistributionTypeName()
    {
        if (distributionType == null) return null;
        if (distributionType == DistributionType.PICKUP) return "自提";
        if (distributionType == DistributionType.IMMEDIATELY) return "配送";
        return distributionType.getName();
    }
    
    @ExcelProperty("付款时间")
    @ColumnWidth(25)
    private Date createdTime;
    
    @ExcelProperty("付款类型 ")
    private String payTypeName;
    
    @ExcelProperty("商品类型")
    private String orderTypeName;
    
    @ExcelProperty("商品价格")
    private BigDecimal amto;
    
    /**
    * 支付积分
    */
    @ExcelProperty("支付积分")
    private Integer pointn;
    
    @ExcelProperty("邮费")
    private BigDecimal postage;
    
    @ExcelProperty("商品优惠")
    private BigDecimal cardAmt;
    
    @ExcelProperty("配送优惠")
    private BigDecimal cardPostageAmt;
    
    @ExcelProperty("总价")
    private BigDecimal amtall;
    
    /**
    * 支付金额
    */
    @ExcelProperty("支付价格")
    private BigDecimal amtn;
    
    @ExcelProperty("退款金额")
    private BigDecimal refundAmt;
    
    @ExcelProperty("退款积分")
    private Integer refundPoint;
    
    /**
    * 配送时间
    */
    @ExcelProperty("发货时间")
    @ColumnWidth(35)
    private String pstime;
    
    /**
    * 市场
    */
    @ExcelProperty("快递公司")
    private String logistics;
    
    @JoinProperty(dataQuery = "mktOrderDescDao", propertyName = "name")
    @ExcelProperty("收货人姓名")
    private String receiver;
    
    @JoinProperty(dataQuery = "mktOrderDescDao", propertyName = "mobile")
    @ExcelProperty("收货人手机号")
    @ColumnWidth(25)
    private String receiverMobile;
    
    @JoinProperty(dataQuery = "mktOrderDescDao", propertyName = "addr")
    @ExcelProperty("收货地址/自提点")
    @ColumnWidth(25)
    private String receiverAddr;
    
}

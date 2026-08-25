package cn.tofocus.lejia.bean.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.BooleanEnum;

import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.db.dto.JoinProperty;
import cn.tofocus.lejia.annotation.ExcelMergeCol;
import cn.tofocus.lejia.annotation.ExcelMergeColBase;
import cn.tofocus.lejia.bean.enums.DistributionType;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.OrderType;
import cn.tofocus.lejia.bean.enums.PayType;
import com.alibaba.excel.enums.poi.VerticalAlignmentEnum;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@ColumnWidth(15)
@HeadFontStyle(fontHeightInPoints = 15, bold = BooleanEnum.FALSE)
@HeadStyle(fillForegroundColor = 1)
@ContentStyle(verticalAlignment = VerticalAlignmentEnum.CENTER)
@FieldNameConstants(innerTypeName = "F")
public class MktOrderLineExcel
{
    @ExcelIgnore
    private Integer pkey;
    
    @ExcelIgnore
    private Integer orderPkey;
    
    @ExcelMergeColBase
    @ExcelProperty(value = "订单号", index = 0)
    @ColumnWidth(17)
    private String code;
    
    @ExcelIgnore
    private OrderStatus status;
    
    @ExcelMergeCol
    @ExcelProperty(value = "状态", index = 1)
    @ColumnWidth(10)
    @JoinEnum(from = "status")
    private String statusName;
    
    @ExcelIgnore
    private DistributionType distributionType;
    
    @ExcelMergeCol
    @ExcelProperty(value = "配送方式", index = 2)
    private String distributionTypeName;
    
    public String getDistributionTypeName()
    {
        if (distributionType == null)
            return null;
        if (distributionType == DistributionType.PICKUP)
            return "自提";
        if (distributionType == DistributionType.IMMEDIATELY)
            return "配送";
        return distributionType.getName();
    }
    
    @ExcelIgnore
    private Integer supplier;
    
    @JoinProperty(dataQuery = "mktSupplierDao", from = "supplier", propertyName = "name")
    @ExcelProperty(value = "供应商", index = 3)
    @ColumnWidth(20)
    private String supplierName;
    
    @ExcelProperty(value = "商品名称", index = 4)
    @ColumnWidth(35)
    private String goodsName;
    
    @ExcelProperty(value = "商品规格", index = 5)
    @ColumnWidth(20)
    private String spaceName;
    
    @ExcelProperty(value = "数量", index = 6)
    private Integer num;
    
    @ExcelProperty(value = "商品单价", index = 7)
    private BigDecimal pricen;
    
    @ExcelProperty(value = "商品总价", index = 8)
    private BigDecimal lineAmt;
    
    public BigDecimal getLineAmt()
    {
        if (pricen == null || num == null)
            return BigDecimal.ZERO;
        return pricen.multiply(new BigDecimal(num)).setScale(2, RoundingMode.HALF_UP);
    }
    
    @ExcelProperty(value = "商品退款", index = 9)
    private BigDecimal lineRefundAmt;
    
    @ExcelProperty(value = "合计金额", index = 10)
    private BigDecimal sumGoodsAmt;
    
    public BigDecimal getSumGoodsAmt()
    {
        if (lineRefundAmt != null)
            return getLineAmt().subtract(lineRefundAmt);
        return getLineAmt();
    }
    
    @ExcelProperty(value = "积分单价", index = 11)
    private Integer point;
    
    @ExcelProperty(value = "积分总价", index = 12)
    private Integer linePointSum;
    
    public Integer getLinePointSum()
    {
        if (num != null && point != null)
            return num * point;
        return null;
    }
    
    @ExcelProperty(value = "积分退款", index = 13)
    private Integer lineRefundPoint;
    
    @ExcelIgnore
    private BigDecimal lineCouponAmt;
    
    @ExcelIgnore
    private Integer member;
    
    @ExcelIgnore
    private Integer memberKey;
    
    public Integer getMember()
    {
        return this.memberKey;
    }
    
    @ExcelMergeCol
    @JoinProperty(dataQuery = "mktMemberDao", from = "member", propertyName = "mobile")
    @ExcelProperty(value = "购买用户", index = 14)
    private String memberMobile;
    
    @ExcelMergeCol
    @ExcelProperty(value = "用户标签", index = 15)
    private String tagName;
    
    @ExcelMergeCol
    @ExcelProperty(value = "付款时间", index = 16)
    @ColumnWidth(25)
    private Date createdTime;
    
    @ExcelIgnore
    private PayType payType;
    
    @ExcelMergeCol
    @ExcelProperty(value = "付款类型", index = 17)
    private String payTypeName;
    
    public String getPayTypeName()
    {
        return this.payType == null ? "" : this.payType.getName();
    }
    
    @ExcelIgnore
    private OrderType orderType;
    
    @ExcelMergeCol
    @ExcelProperty(value = "商品类型", index = 18)
    @JoinEnum(from = "orderType")
    private String orderTypeName;
    
    @ExcelMergeCol
    @ExcelProperty(value = "商品价格", index = 19)
    private BigDecimal amto;
    
    @ExcelMergeCol
    @ExcelProperty(value = "支付积分", index = 20)
    private Integer pointn;
    
    @ExcelIgnore
    private BigDecimal oldPostage;
    
    @ExcelMergeCol
    @ExcelProperty(value = "邮费", index = 21)
    private BigDecimal postage;
    
    public BigDecimal getPostage()
    {
        if (oldPostage != null)
            return oldPostage;
        return postage;
    }
    
    @ExcelMergeCol
    @ExcelProperty(value = "商品优惠", index = 22)
    private BigDecimal cardAmt;
    
    @ExcelMergeCol
    @ExcelProperty(value = "配送优惠", index = 23)
    private BigDecimal cardPostageAmt;
    
    @ExcelMergeCol
    @ExcelProperty(value = "总价", index = 24)
    private BigDecimal amtall;
    
    @ExcelMergeCol
    @ExcelProperty(value = "支付价格", index = 25)
    private BigDecimal amtn;
    
    @ExcelMergeCol
    @ExcelProperty(value = "退款金额", index = 26)
    private BigDecimal refundAmt;
    
    @ExcelMergeCol
    @ExcelProperty(value = "退款积分", index = 27)
    private Integer refundPoint;
    
    @ExcelMergeCol
    @ExcelProperty(value = "发货时间", index = 28)
    @ColumnWidth(35)
    private String pstime;
    
    @ExcelMergeCol
    @JoinProperty(dataQuery = "mktOrderDescDao", from = "orderPkey", propertyName = "logistics")
    @ExcelProperty(value = "快递公司", index = 29)
    private String logistics;
    
    @ExcelMergeCol
    @JoinProperty(dataQuery = "mktOrderDescDao", from = "orderPkey", propertyName = "name")
    @ExcelProperty(value = "收货人姓名", index = 30)
    private String receiver;
    
    @ExcelMergeCol
    @JoinProperty(dataQuery = "mktOrderDescDao", from = "orderPkey", propertyName = "mobile")
    @ExcelProperty(value = "收货人手机号", index = 31)
    @ColumnWidth(20)
    private String receiverMobile;
    
    @ExcelMergeCol
    @JoinProperty(dataQuery = "mktOrderDescDao", from = "orderPkey", propertyName = "addr")
    @ExcelProperty(value = "收货地址/自提点", index = 32)
    @ColumnWidth(35)
    private String receiverAddr;

    @ExcelIgnore
    @JoinProperty(dataQuery = "mktOrderDescDao", from = "orderPkey", propertyName = "fhTime")
    private Date fhTime;
    
    @ExcelIgnore
    private String pickupTime;
}

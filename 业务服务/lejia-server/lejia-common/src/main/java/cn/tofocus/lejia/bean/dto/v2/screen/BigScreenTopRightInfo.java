package cn.tofocus.lejia.bean.dto.v2.screen;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.lejia.bean.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class BigScreenTopRightInfo
{
    @Schema(description = "销量额")
    public String getSales()
    {
        if (orderSales != null) return orderSales.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
        return "";
    }
    
    @Schema(description = "访客人数")
    private int visitor;
    
    @Schema(description = "订单数")
    private int orderNum;
    
    @Schema(description = "退款金额")
    public String getRefund()
    {
        if (orderRefund != null)
            return orderRefund.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
        return "0.00";
    }
    
    @Schema(description = "支付转化率")
    public String getConversionRates()
    {
        if (conversion != null)
            return conversion.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
        return "0.00";
    }
    
    @Schema(description = "客单价")
    public String getCustomerPrice()
    {
        if (customer != null)
            return customer.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
        return "0.00";
    }
    
    @Schema(description = "复购率")
    public String getRepurchaseRate()
    {
        if (repurchase != null)
        {
            if(repurchase.compareTo(new BigDecimal("100")) == 1)
                repurchase = new BigDecimal("100");
            return repurchase.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
        }
        return "0.00";
    }
    
    @Schema(description = "实时交易额折线图")
    private List<RealTimeSalesOnList> rtsList;
    
    @Schema(description = "当前市场数")
    private Integer farmerCount = 0;
    
    // 销量额
    @JsonIgnore
    private BigDecimal orderSales;
    
    // 退款金额
    @JsonIgnore
    private BigDecimal orderRefund;
    
    // 支付转化率
    @JsonIgnore
    private BigDecimal conversion;
    
    // 客单价
    @JsonIgnore
    private BigDecimal customer;
    
    // 复购率
    @JsonIgnore
    private BigDecimal repurchase;
    
    // 状态
    @JsonIgnore
    private OrderStatus status;
    
}

package cn.tofocus.core.bean.entity.trade;

import java.math.BigDecimal;

import cn.tofocus.core.enums.ReceiptLoadEnum;
import lombok.Data;

@Data
public class StatisticsItem
{
    private ReceiptLoadEnum receiptLoad;

    private int num;
    
    private BigDecimal amount;
}

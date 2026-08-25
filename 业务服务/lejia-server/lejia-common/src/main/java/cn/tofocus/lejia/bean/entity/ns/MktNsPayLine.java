package cn.tofocus.lejia.bean.entity.ns;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  mkt_ns_pay_line
* @author zdw 2022-06-07
*/

@Entity
@Data
@Table(name = "mkt_ns_pay_line")
public class MktNsPayLine implements HasPkey<Integer>
{
    
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_ns_pay_line")
    @Schema(description = "pkey", required = true)
    private Integer pkey;
    
    @Schema(description = "返回状态码", required = false)
    private String returnCode;
    
    @Schema(description = "返回信息", required = false)
    private String returnMsg;
    
    @Schema(description = "错误代码", required = false)
    private String errCode;
    
    @Schema(description = "错误代码描述", required = false)
    private String errMsg;
    
    @Schema(description = "交易类型", required = false)
    private String noticeType;
    
    @Schema(description = "第三方订单号", required = false)
    private String transactionId;
    
    @Schema(description = "商户订单号", required = false)
    private String outTradeNo;
    
    @Schema(description = "总金额", required = false)
    private String totalFee;
    
    @Schema(description = "货币种类", required = false)
    private String feeType;
    
    @Schema(description = "支付完成时间", required = false)
    private String timeEnd;
    
    @Schema(description = "付款银行", required = false)
    private String bankType;
    
    @Schema(description = "现金支付金额", required = false)
    private String cashFee;
    
    @Schema(description = "现金券金额", required = false)
    private String couponFee;
    
    @Schema(description = "应结订单金额=订单金额-非充值 代金券金额，应结订单金额<=订单 金额。", required = false)
    private String settlementTotalFee;
    
    @Schema(description = "附加信息", required = false)
    private String attach;
    
    @Schema(description = "支付通道", required = false)
    private String rout;
    
    @Schema(description = "对账日期", required = false)
    private String billDate;
    
    @Schema(description = "是否已对账", required = false)
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private Boolean bill;
    
    @Schema(description = "建档时间", required = false)
    @CreatedDate
    private Date createdTime;
    
}
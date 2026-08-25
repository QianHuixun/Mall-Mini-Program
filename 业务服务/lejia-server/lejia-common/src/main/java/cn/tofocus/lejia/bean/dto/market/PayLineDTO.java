package cn.tofocus.lejia.bean.dto.market;

import java.util.Date;

import javax.persistence.Column;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.lejia.bean.enums.PayType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 对账中心 明细表 使用
 * @author Administrator
 *
 */
@Data
public class PayLineDTO
{
    
    @Schema(description = "支付成功时间", required = false)
    private String payTime;
    
    @Schema(description = "类型")
    private String buyType;
    
    @Schema(description = "支付金额", required = false)
    private String amt;
    
    @Schema(description = "手续费")
    private String handlingFee;
    
    @Schema(description = "支付方式")
    private String payTypeN;
    
    @Schema(description = "流水号", required = false)
    @Column(name = "kc_code")
    private String code;
    
    @Schema(description = "订单号", required = false)
    private String orderNumber;
    
    @Schema(description = "状态", required = false)
    private String status;
    
    @JsonIgnore
    private PayType payType;
    
    private String name;
    
    private String mobile;
    
    @JsonIgnore
    private Integer member;

    @JsonIgnore
    private String cashFee;
    
    @JsonIgnore
    private Date createdTime;
    
    @JsonIgnore
    private String outTradeNo;
    
    @JsonIgnore
    private String noticeType;
}

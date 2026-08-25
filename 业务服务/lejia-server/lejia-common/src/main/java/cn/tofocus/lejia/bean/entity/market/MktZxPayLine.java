package cn.tofocus.lejia.bean.entity.market;

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
*  中信支付回调记录
* @author zdw 2021-11-18
*/

@Entity
@Data
@Table(name = "mkt_zx_pay_line")
public class MktZxPayLine implements HasPkey<Integer>
{
    
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_zx_pay_line")
    @Schema(description = "pkey", required = false)
    private Integer pkey;
    
    @Schema(description = "返回状态码", required = false)
    private String return_code;
    
    @Schema(description = "返回信息", required = false)
    private String return_msg;
    
    @Schema(description = "商户号", required = false)
    private String mch_id;
    
    @Schema(description = "设备号", required = false)
    private String device_info;
    
    @Schema(description = "错误代码", required = false)
    private String err_code;
    
    @Schema(description = "错误代码描述", required = false)
    private String err_msg;
    
    @Schema(description = "交易类型", required = false)
    private String trade_type;
    
    @Schema(description = "第三方订单号", required = false)
    private String transaction_id;
    
    @Schema(description = "商户订单号", required = false)
    private String out_trade_no;
    
    @Schema(description = "总金额", required = false)
    private Integer total_fee;
    
    @Schema(description = "货币种类", required = false)
    private String fee_type;
    
    @Schema(description = "支付完成时间", required = false)
    private String time_end;
    
    @Schema(description = "用户标识 用户在服务商 appid 下的唯一标识", required = false)
    private String openid;
    
    @Schema(description = "付款银行", required = false)
    private String bank_type;
    
    @Schema(description = "商户appid", required = false)
    private String sub_appid;
    
    @Schema(description = "用户openid", required = false)
    private String sub_openid;
    
    @Schema(description = "现金支付金额", required = false)
    private Integer cash_fee;
    
    @Schema(description = "现金券金额", required = false)
    private Integer coupon_fee;
    
    @Schema(description = "附加信息", required = false)
    private String attach;
    
    @Schema(description = "清算日期【银联二维码】", required = false)
    @Column(name = "settledate")
    private String settleDate;
    
    @Schema(description = "卡属性【银联二维码】", required = false)
    @Column(name = "cardattr")
    private String cardAttr;
    
    @Schema(description = "建档时间", required = false)
    @CreatedDate
    private Date createdTime;
    
    @Schema(description = "归属主键")
    private Integer ascription;
    
}
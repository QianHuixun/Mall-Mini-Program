package cn.tofocus.lejia.bean.dto.app;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.persistence.Column;
import javax.xml.bind.annotation.XmlAccessType;  

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  中信支付回调记录
* @author zdw 2021-11-18
*/

@Data
@XmlAccessorType(XmlAccessType.FIELD)  
@XmlRootElement(name = "stream")  
@XmlType(propOrder = {   
    "return_code",  
    "result_code",
    "return_msg",   
    "mch_id",   
    "device_info",   
    "err_code",
    "err_msg",
    "trade_type",
    "transaction_id",
    "out_trade_no",
    "total_fee",
    "fee_type",
    "time_end",
    "openid",
    "bank_type",
    "sub_appid",
    "sub_openid",
    "cash_fee",
    "coupon_fee",
    "attach",
    "settleDate",
    "cardAttr"
}) 
public class MktZxPayLineDto
{
    
    @Schema(description = "返回状态码", required = false)
    private String return_code;
    private String result_code;
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
    @Column(name = "settleDate")
    private String settleDate;
    
    @Schema(description = "卡属性【银联二维码】", required = false)
    @Column(name = "cardAttr")
    private String cardAttr;
    
    public MktZxPayLineDto(){}

    public MktZxPayLineDto(String return_code,String result_code, String return_msg, String mch_id, String device_info, String err_code,
        String err_msg, String trade_type, String transaction_id, String out_trade_no, Integer total_fee,
        String fee_type, String time_end, String openid, String bank_type, String sub_appid, String sub_openid,
        Integer cash_fee, Integer coupon_fee, String attach, String settleDate, String cardAttr)
    {
        super();
        this.return_code = return_code;
        this.result_code = result_code;
        this.return_msg = return_msg;
        this.mch_id = mch_id;
        this.device_info = device_info;
        this.err_code = err_code;
        this.err_msg = err_msg;
        this.trade_type = trade_type;
        this.transaction_id = transaction_id;
        this.out_trade_no = out_trade_no;
        this.total_fee = total_fee;
        this.fee_type = fee_type;
        this.time_end = time_end;
        this.openid = openid;
        this.bank_type = bank_type;
        this.sub_appid = sub_appid;
        this.sub_openid = sub_openid;
        this.cash_fee = cash_fee;
        this.coupon_fee = coupon_fee;
        this.attach = attach;
        this.settleDate = settleDate;
        this.cardAttr = cardAttr;
    }
    
    
}
package cn.tofocus.lejia.zx.bean.pay;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class Native
{
    @Schema(description = "接口类型")
    private String service;
    
    @Schema(description = "商户号")
    private String mch_id;
    
    @Schema(description = "商户订单号")
    private String out_trade_no;
    
    @Schema(description = "总金额")
    private String total_fee;
    
    @Schema(description = "终端IP")
    private String mch_create_ip;
    
    @Schema(description = "通知地址")
    private String notify_url;
    
    @Schema(description = "商品描述")
    private String body;
    
    @Schema(description = "交易类型")
    private String trade_type;
    
    @Schema(description = "用户标识【微信】")
    private String openid;
    
    @Schema(description = "用户标识【微信】")
    private String sub_openid;
    
    @Schema(description = "子商户公众账号ID【微信】")
    private String sub_appid;
    
    @Schema(description = "终端信息")
    private String terminal_info;
    
    @Schema(description = "版本号")
    private String version;
    
    @Schema(description = "签名")
    private String sign;
}

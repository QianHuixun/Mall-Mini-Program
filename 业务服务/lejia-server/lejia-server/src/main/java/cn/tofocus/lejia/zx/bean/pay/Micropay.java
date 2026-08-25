package cn.tofocus.lejia.zx.bean.pay;

import lombok.Data;

@Data
public class Micropay
{
    private String service;
    private String mch_id;
    private String out_trade_no;
    private String body;
    private String total_fee;
    private String mch_create_ip;
    private String auth_code;
    private String sign;
}

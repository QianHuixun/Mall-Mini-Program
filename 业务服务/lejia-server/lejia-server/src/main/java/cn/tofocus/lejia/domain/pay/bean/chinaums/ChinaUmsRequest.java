package cn.tofocus.lejia.domain.pay.bean.chinaums;

import lombok.Data;

@Data
public abstract class ChinaUmsRequest
{
    // 报文请求时间 格式yyyy-MM-dd HH:mm:ss
    private String requestTimestamp;
    
    // 商户号
    private String mid;
    
    // 终端号
    private String tid;
    
    // （可空）请求系统预留字段 <=255
    private String srcReserve;
    
    // （可空）业务类型 MINIDEFAULT
    private String instMid;
}

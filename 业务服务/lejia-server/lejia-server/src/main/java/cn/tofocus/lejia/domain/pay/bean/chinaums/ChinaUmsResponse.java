package cn.tofocus.lejia.domain.pay.bean.chinaums;

import lombok.Data;

@Data
public abstract class ChinaUmsResponse
{
    // 错误代码
    private String errCode;
    
    // （可空）错误说明
    private String errMsg;
    
    // 报文响应时间 格式yyyy-MM-dd HH:mm:ss
    private String responseTimestamp;
    
    // 商户号
    private String mid;
    
    // 终端号
    private String tid;
    
    // （可空）请求系统预留字段 <=255
    private String srcReserve;
    
    public boolean isSuccess()
    {
        return "SUCCESS".equals(errCode);
    }
}

package cn.tofocus.lejia.bean.dto.app.jd;

import java.util.Date;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class JdOrderDeliveryInfo
{
    @Schema(description = "物流信息")
    private List<LogisticInfo> logisticInfoList;
    
    @Schema(description = "路由信息")
    private List<TrackInfo> trackInfoList;
    
    @Data
    public static class LogisticInfo
    {
        @Schema(description = "承运人")
        private String deliveryCarrier;
        
        @Schema(description = "配送单号")
        private String deliveryOrderId;
    }
    
    @Data
    public static class TrackInfo
    {
        @Schema(description = "记录时间")
        private Date trackMsgTime;
        
        @Schema(description = "路由内容")
        private String trackContent;
        
        @Schema(description = "操作人")
        private String trackOperator;
    }
}

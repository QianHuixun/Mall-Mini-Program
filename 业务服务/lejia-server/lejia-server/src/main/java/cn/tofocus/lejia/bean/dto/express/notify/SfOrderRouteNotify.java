package cn.tofocus.lejia.bean.dto.express.notify;

import java.util.Date;

import cn.tofocus.common.util.StringUtil;
import cn.tofocus.lejia.bean.enums.express.OrderExpressStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SfOrderRouteNotify
{
    // 每一个id代表一条不同的路由节点信息
    @Schema(description = "路由节点信息编号")
    private String id;
    
    @Schema(description = "顺丰运单号")
    private String mailno;
    
    @Schema(description = "客户订单号")
    private String orderid;
    
    // 格式：YYYY-MM-DD HH24:MM:SS，示例：2012-7-30 09:30:00
    @Schema(description = "路由节点产生的时间")
    private Date acceptTime;
    
    @Schema(description = "路由节点发生的城市")
    private String acceptAddress;
    
    @Schema(description = "路由节点操作码")
    private String opCode;
    
    @Schema(description = "路由节点具体描述")
    private String remark;
    
    @Schema(description = "经度,需配置顾客编码相应业务")
    private String deptLng;
    
    @Schema(description = "纬度,需配置顾客编码相应业务")
    private String deptLat;
    
    @Schema(description = "异常编码,需配置顾客编码相应业务")
    private String reasonCode;
    
    @Schema(description = "异常描述,需配置顾客编码相应业务")
    private String reasonName;

    @JsonIgnore
    public String getDescription()
    {
        if (StringUtil.isNotBlank(this.remark)) return this.remark;
        if (opCode == null) return null;
        switch (this.opCode)
        {
            case "30":
                return "快件已装车,准备发往 【集散中心】";
            case "31":
                return "快件到达 【集散中心】";
            case "33":
                return "派件异常原因";
            case "3036":
                return "快件准备送往下一站";
            case "44":
                return "正在派送途中,请您准备签收";
            case "50":
                return "顺丰已收件";
            case "70":
                return "派件不成功";
            case "80":
                return "已签收,感谢使用顺丰,期待再次为您服务";
            case "8000":
                return "在官网”运单资料&签收图”,可查看签收人信";
            case "130":
                return "快件到达顺丰店/站";
            case "123":
                return "快件正送往顺丰店/站";
            case "607":
                return "代理收件";
            case "99":
                return "应客户要求,快件正在转寄中";
            case "648":
                return "快件已退回/转寄";
            default:
                return null;
        }
    }
}

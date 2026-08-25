package cn.tofocus.lejia.bean.dto.express.notify;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.common.util.StringUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SfOrderStatusNotify
{
    @Schema(description = "客户订单号")
    private String orderNo;
    
    @Schema(description = "顺丰运单号")
    private String waybillNo;
    
    @Schema(description = "订单状态")
    private String orderStateCode;
    
    @Schema(description = "订单状态描述")
    private String orderStateDesc;
    
    @Schema(description = "收件员工工号")
    private String empCode;
    
    @Schema(description = "收件员手机号")
    private String empPhone;
    
    @Schema(description = "网点")
    private String netCode;
    
    @Schema(description = "最晚上门时间")
    private Date lastTime;
    
    @Schema(description = "客户预约时间")
    private Date bookTime;
    
    @Schema(description = "承运商代码(SF)")
    private String carrierCode;
    
    @Schema(description = "状态操作时间")
    private Date createTm;
    
    @JsonIgnore
    public String getDescription()
    {
        if (StringUtil.isNotBlank(this.orderStateDesc)) return this.orderStateDesc;
        if (this.orderStateCode == null) return null;
        switch (this.orderStateCode)
        {
            case "04":
                return "调度失败/等待";
            case "04-40001":
                return "调度成功+收派员信息";
            case "04-40037":
                return "下单已接收";
            case "04-40002-40021":
                return "电话号码异常(电话错误/空号/停机)";
            case "04-40002-40022":
                return "无法进入客户处,且客户不愿出来";
            case "04-40002-40004":
                return "暂未联系上客户(电话无人接听/无法接通/关机)";
            case "04-40002-40005":
                return "经客户同意,更改收派时间";
            case "04-40002-40027":
                return "天气原因,延误收派件";
            case "04-40002-40028":
                return "交通原因,延误收派件";
            case "04-40002-40031":
                return "不符合收件要求,无法揽收";
            case "04-40002-40014":
                return "客户取消寄件";
            case "04-40002-40015":
                return "客户重复下单";
            case "04-40002-40016":
                return "下错单需转单";
            case "04-40002-40035":
                return "工作量过大,延误收派件";
            case "04-40002-40036":
                return "重货需转单";
            case "00-40045-1000":
                return "调度取消订单";
            case "00-2000":
                return "客户已取消订单";
            case "05-40003":
                return "已正常收件状态";
            case "00":
                return "客户取消（此状态无状态描述）";
            default:
                return null;
        }
    }
    
    @JsonIgnore
    public boolean isError()
    {
        return "04-40002-40021".equals(this.orderStateCode) || "04-40002-40022".equals(this.orderStateCode)
            || "04-40002-40004".equals(this.orderStateCode) || "04-40002-40005".equals(this.orderStateCode)
            || "04-40002-40027".equals(this.orderStateCode) || "04-40002-40028".equals(this.orderStateCode)
            || "04-40002-40031".equals(this.orderStateCode) || "04-40002-40014".equals(this.orderStateCode)
            || "04-40002-40015".equals(this.orderStateCode) || "04-40002-40016".equals(this.orderStateCode)
            || "04-40002-40035".equals(this.orderStateCode) || "04-40002-40036".equals(this.orderStateCode);
    }
}

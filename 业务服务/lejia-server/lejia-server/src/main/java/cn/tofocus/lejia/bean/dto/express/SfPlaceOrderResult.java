package cn.tofocus.lejia.bean.dto.express;

import java.util.List;

import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import cn.tofocus.core.json.JsonUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SfPlaceOrderResult
{
    @Schema(description = "客户订单号")
    private String orderid;
    
    // 填写后母单号必填
    @Schema(description = "顺丰运单号列表JSON字符串")
    private String waybillNoInfoList;
    
    @Schema(description = "顺丰运单号列表")
    public List<SfWaybillNoInfo> getWaybillNoInfos()
    {
        return JsonUtil.getBean(waybillNoInfoList, new TypeReference<List<SfWaybillNoInfo>>()
        {
        });
    }
    
    // 可用于顺丰电子面单标签打印
    @Schema(description = "原寄地区域代码")
    private String origincode;
    
    // 可用于顺丰电子面单标签打印
    @Schema(description = "目的地区域代码")
    private String destcode;
    
    // 1-人工确认；2-可收派；3-不可以收派；
    @Schema(description = "筛单结果")
    @JsonProperty("filter_result")
    private String filterResult;
    
    // filter_result=3时必填,不可以收派的原因代码：1-收方超范围；2-派方超范围；3-其它原因；
    // 高峰管控提示信息；【数字】:【高峰管控提示信息】(如：4-温馨提示；1-春运延时)
    @Schema(description = "备注")
    private String remark;
    
    @Schema(description = "原寄地中转场")
    private String sourceTransferCode;
    
    @Schema(description = "原寄地城市代码")
    private String sourceCityCode;
    
    @Schema(description = "原寄地网点代码")
    private String sourceDeptCode;
    
    @Schema(description = "原寄地单元区域")
    private String sourceTeamCode;
    
    @Schema(description = "目的地城市代码")
    private String destCityCode;
    
    @Schema(description = "目的地网点代码")
    private String destDeptCode;
    
    @Schema(description = "目的地网点代码映射码")
    private String destDeptCodeMapping;
    
    @Schema(description = "目的地单元区域")
    private String destTeamCode;
    
    @Schema(description = "目的地单元区域映射码")
    private String destTeamCodeMapping;
    
    @Schema(description = "目的地中转场")
    private String destTransferCode;
    
    @Schema(description = "路由标签信息")
    private String destRouteLabel;
    
    @Schema(description = "产品名称")
    private String proName;
    
    @Schema(description = "快件内容")
    private String cargoTypeCode;
    
    @Schema(description = "时效代码")
    private String limitTypeCode;
    
    @Schema(description = "产品类型")
    private String expressTypeCode;
    
    @Schema(description = "入港映射码")
    private String codingMapping;
    
    @Schema(description = "出港映射码")
    private String codingMappingOut;
    
    // 0:不需要打印XB，1:需要打印XB
    @Schema(description = "XB标志")
    private String xbFlag;
    
    // 返回值总共有9位,每位只有0和1两种,0表示按丰密面单默认的规则,1是显示,
    // 顺序如下,如111110000表示打印寄方姓名、寄方电话、寄方公司名、寄方地址和重量,收方姓名、收方电话、收方公司和收方地址按丰密面单默认规则
    // 1-寄方姓名；2-寄方电话；3-寄方公司名；4-寄方地址；5-重量；6-收方姓名；7-收方电话；8-收方公司名；9-收方地址；
    @Schema(description = "打印标志")
    private String printFlag;
    
    @Schema(description = "二维码")
    private String twoDimensionCode;
    
    // 值为二维码中的K4
    @Schema(description = "时效类型")
    private String proCode;
    
    // 根据托寄物判断需要打印的图标(重货,蟹类,生鲜,易碎，Z标)返回值有8位，
    // 每一位只有0和1两种，0表示按运单默认的规则，1表示显示。后面两位默认0备用。
    // 顺序如下：重货，蟹类，生鲜，易碎，医药类，Z标,酒类，0。
    // 如：00000000表示不需要打印重货，蟹类，生鲜，易碎，医药，Z标，酒类，备用
    @Schema(description = "打印图标")
    private String printIcon;
    
    @Schema(description = "AB标")
    private String abFlag;
    
    // 返回代码:0-系统异常；1-未找到面单；
    @Schema(description = "查询出现异常时返回信息")
    private String errMsg;
    
    @Schema(description = "目的地口岸代码")
    private String destPortCode;
    
    @Schema(description = "目的国别")
    private String destCountry;
    
    @Schema(description = "目的地邮编")
    private String destPostCode;
    
    // 保留两位小数，数字类型，可补位
    @Schema(description = "总价值")
    private String goodsValueTotal;
    
    @Schema(description = "币种")
    private String currencySymbol;
    
    @Schema(description = "件数")
    private String goodsNumber;
    
    // 下单时，当选择签回单增值服务时，会回传此字段，规则同“twoDimensionCode”
    @Schema(description = "签回单二维码")
    private String twoDimensionCode2;
    
    @Schema(description = "ab标扩展")
    private String newIcon;
    
    @Schema(description = "打印图标扩展")
    private String newAbflag;
}

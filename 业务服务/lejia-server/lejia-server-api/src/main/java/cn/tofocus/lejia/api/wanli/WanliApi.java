package cn.tofocus.lejia.api.wanli;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;

import cn.tofocus.core.Result;
import cn.tofocus.core.data.NamedBean;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.wanli.OrderBillingOnInfo;
import cn.tofocus.lejia.bean.dto.wanli.WanliCourierOnInfo;
import cn.tofocus.lejia.bean.enums.CancelType;
import io.swagger.v3.oas.annotations.Operation;

public interface WanliApi
{
    @Operation(summary = "获取计价", tags = ApiTags.WANLI)
    @PostMapping(value = "/order/billing")
    public Result<List<OrderBillingOnInfo>> orderBilling(@RequestParam(value = "pkey") Integer pkey);
    
    @Operation(summary = "第三方派单", tags = ApiTags.WANLI)
    @PostMapping(value = "/order/create")
    public Result<Boolean> orderCreate(@RequestParam(value = "pkey") Integer pkey,
        @RequestParam(value = "multipleSupplierCodes") List<Integer> multipleSupplierCodes);
    
    @Operation(summary = "第三方回调", tags = ApiTags.WANLI)
    @PostMapping(value = "/{farmer}/callback")
    public String callback(@PathVariable String farmer, @RequestBody JSONObject json);
    
    @Operation(summary = "订单取消", tags = ApiTags.WANLI)
    @PostMapping(value = "/order/cancel")
    public Result<Boolean> cancleOrder(@RequestParam(value = "cancelType") CancelType cancelType,
        @RequestParam(value = "pkey") Integer pkey);
    
    @Operation(summary = "获取骑手定位信息", tags = ApiTags.WANLI)
    @PostMapping(value = "/order/courier")
    public Result<WanliCourierOnInfo> getCourier(@RequestParam(value = "pkey") Integer pkey);
    
    @Operation(summary = "取消理由枚举", tags = ApiTags.WANLI)
    @PostMapping(value = "/cancelType/list")
    Result<List<NamedBean>> cancelTypeList();
    
    @Operation(summary = "测试回调", tags = ApiTags.WANLI)
    @PostMapping(value = "/order/test")
    Result<Boolean> ordertest(@RequestParam(value = "orderNo") String orderNo,
        @RequestParam(value = "status") Integer status);
    
    @Operation(summary = "已经送达", tags = ApiTags.WANLI)
    @PostMapping(value = "/order/reach")
    public Result<Boolean> orderReach(@RequestParam(value = "pkey") Integer pkey);
    
}

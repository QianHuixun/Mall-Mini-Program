package cn.tofocus.lejia.api.h5;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.h5.H5OrderInfo;
import cn.tofocus.lejia.bean.enums.h5.H5PayType;
import cn.tofocus.lejia.domain.h5.H5OrderManager;
import io.swagger.v3.oas.annotations.Operation;

@RequestMapping("/v1/h5/order")
@RestController
public class H5OrderApiImpl
{
    @Autowired
    private H5OrderManager manager;
    
    @Operation(summary = "获取订单列表", tags = ApiTags.H5_WEB_ORDER)
    @PostMapping(value = "/query")
    public Result<PageResult<H5OrderInfo>> query(
        @RequestParam(value = "page", defaultValue = "0", required = false) int page,
        @RequestParam(value = "pagesize", defaultValue = "10", required = false) int pagesize)
    {
        PageResult<H5OrderInfo> res = manager.query(page, pagesize);
        return new Result<>(res);
    }
    
    @Operation(summary = "获取下单页面", tags = ApiTags.H5_WEB_ORDER)
    @PostMapping(value = "/buy")
    public Result<H5OrderInfo> buy(@RequestParam(value = "space") Integer space)
    {
        H5OrderInfo res = manager.buy(space);
        return new Result<>(res);
    }
    
    @Operation(summary = "提交订单", tags = ApiTags.H5_WEB_ORDER)
    @PostMapping(value = "/commitOrder")
    public Result<Boolean> commitOrder(@RequestParam(value = "space") Integer space,
        @RequestParam(value = "remark", required = false) String remark,
        @RequestParam(value = "payType", required = false, defaultValue = "ORDER_ELECTRONIC_ACCOUNT") H5PayType payType)
    {
        Boolean res = manager.commitOrder(space, remark, payType);
        return new Result<>(res);
    }
}

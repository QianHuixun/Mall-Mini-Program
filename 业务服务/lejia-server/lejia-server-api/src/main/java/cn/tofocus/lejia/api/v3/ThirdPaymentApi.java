package cn.tofocus.lejia.api.v3;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.XaszAssociationOnInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

public interface ThirdPaymentApi
{
    @Operation(summary = "新增第三方支付渠道", tags = ApiTags.THIRDPAYMENT)
    @PostMapping(value = "/ins")
    public Result<Boolean> ins(@RequestBody XaszAssociationOnInfo dto);
    
    @Operation(summary = "编辑第三方支付渠道", tags = ApiTags.THIRDPAYMENT)
    @PostMapping(value = "/upd")
    public Result<Boolean> upd(@RequestBody XaszAssociationOnInfo dto);
    
    @Operation(summary = "删除第三方支付渠道", tags = ApiTags.THIRDPAYMENT)
    @PostMapping(value = "/del")
    public Result<Boolean> del(@RequestParam(value = "pkey")Integer pkey);
    
    @Operation(summary = "获取第三方支付渠道列表", tags = ApiTags.THIRDPAYMENT)
    @PostMapping(value = "/query")
    public Result<PageResult<XaszAssociationOnInfo>> query(@RequestParam(value = "page", defaultValue = "0", required = false) @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10", required = false) @Parameter(description = "每页大小") int pagesize);
    
    @Operation(summary = "获取心安食足市场名称", tags = ApiTags.THIRDPAYMENT)
    @PostMapping(value = "/list/market")
    public Result<Map<Integer,String>> listMarket();
    
}

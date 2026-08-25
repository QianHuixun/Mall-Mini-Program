package cn.tofocus.lejia.app.v1.market;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.market.MktOriTestOnList;
import cn.tofocus.lejia.bean.dto.market.MktOriVenOnList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@FeignClient(value = "lejia-server", contextId = "lejia-server-app-ori", path = "/v1/app/market/ori",
        fallbackFactory = AppOriApiFallback.class, configuration = FeignConfig.class)
public interface AppOriApi {
    @Operation(summary = "获取溯源信息列表", tags = AppTags.mobileOri)
    @PostMapping(value = "/ven/query")
    public Result<PageResult<MktOriVenOnList>> queryOriVen(
            @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号", hidden = true) int page,
            @RequestParam(value = "pagesize", defaultValue = "10000") @Parameter(description = "每页大小", hidden = true) int pagesize,
            @RequestParam(value = "merchant", required = false) @Parameter(description = "溯源商户", hidden = true) String merchant,
            @RequestParam(value = "goods", required = false) @Parameter(description = "溯源商品 ", hidden = true) String goods,
            @RequestParam(value = "vendor", required = false) @Parameter(description = "供应商", hidden = true) String vendor);

    @Operation(summary = "获取检测信息列表", tags = AppTags.mobileOri)
    @PostMapping(value = "/test/query")
    public Result<PageResult<MktOriTestOnList>> queryOriTest(
            @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号", hidden = false) int page,
            @RequestParam(value = "pagesize", defaultValue = "10000") @Parameter(description = "每页大小", hidden = false) int pagesize,
            @RequestParam(value = "merchant", required = false) @Parameter(description = "检测商户", hidden = true) String merchant,
            @RequestParam(value = "startDate", required = false) @Parameter(description = "检查时间-开始", hidden = true) Date startDate,
            @RequestParam(value = "endDate", required = false) @Parameter(description = "检查时间-结束", hidden = true) Date endDate,
            @RequestParam(value = "goods", required = false) @Parameter(description = "检测商品 ", hidden = true) String goods,
            @RequestParam(value = "entry", required = false) @Parameter(description = "检测项目", hidden = true) String entry,
            @RequestParam(value = "testResult", required = false) @Parameter(description = "检测结果", hidden = true) Boolean testResult);
}

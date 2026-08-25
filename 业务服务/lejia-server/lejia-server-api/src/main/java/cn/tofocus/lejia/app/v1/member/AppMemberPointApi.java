package cn.tofocus.lejia.app.v1.member;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.market.AppMemberPointDTO;
import cn.tofocus.lejia.bean.dto.app.market.AppMemberPointLineOnList;
import cn.tofocus.lejia.bean.dto.app.market.AppMktVendorDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "lejia-server", contextId = "lejia-server-app-member-point", path = "/v1/app/market/lm/member/point", fallbackFactory = AppMemberPointFallback.class, configuration = FeignConfig.class)
public interface AppMemberPointApi {
    @Operation(summary = "页面信息", tags = AppTags.mobilePoint)
    @PostMapping("/loadIndex")
    public Result<AppMktVendorDTO> loadIndex(@Parameter(description = "商户ecode") String ecode);

    @Operation(summary = "发起支付", tags = AppTags.mobilePoint)
    @PostMapping("/payPoints")
    public Result<Boolean> payPoints(@Parameter(description = "商户ecode") String ecode, @Parameter(description = "支付积分") int points);

    @Operation(summary = "获取积分信息", tags = AppTags.mobilePoint)
    @PostMapping(value = "/get")
    public Result<AppMemberPointDTO> get();

    @Operation(summary = "获取积分流水", tags = AppTags.mobilePoint)
    @PostMapping(value = "/line")
    public Result<PageResult<AppMemberPointLineOnList>> line(
            @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号", hidden = true) int page,
            @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小", hidden = true) int pagesize,
            @RequestParam(value = "direct", required = false) @Parameter(description = "借贷标志 借(-)/贷(+)") Boolean direct);
}

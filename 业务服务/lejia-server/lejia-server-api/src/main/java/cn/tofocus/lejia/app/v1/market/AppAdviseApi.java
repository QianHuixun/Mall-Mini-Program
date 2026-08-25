package cn.tofocus.lejia.app.v1.market;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.app.v1.member.AppMemberFallback;
import cn.tofocus.lejia.bean.dto.app.market.AppAdviseDetailsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-app-advise", path = "/v1/app/market/lm/advise",
        fallbackFactory = AppMemberFallback.class, configuration = FeignConfig.class)
public interface AppAdviseApi {

    @Operation(summary = "新增建议反馈", tags = AppTags.mobileAdvise)
    @PostMapping("/ins")
    public Result<AppAdviseDetailsDTO> insAdvise(@RequestParam(value = "content") @Parameter(description = "正文") String content);

}

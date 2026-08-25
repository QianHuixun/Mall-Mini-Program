package cn.tofocus.lejia.api.v2;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.DropStringDown;
import io.swagger.v3.oas.annotations.Operation;

public interface MarketV2Api
{
    @Operation(summary = "获取市场下拉", tags = ApiTags.LEJIA_V2_MARKET, description = "运营端：所有市场，公司端：公司下所有市场，市场端：自己市场")
    @PostMapping(value = "/drop")
    Result<List<DropStringDown>> listDropName(
        @RequestParam(value = "includeAscription", defaultValue = "false") boolean includeAscription);
}

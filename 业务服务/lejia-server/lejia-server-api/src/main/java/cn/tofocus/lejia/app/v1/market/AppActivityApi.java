package cn.tofocus.lejia.app.v1.market;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.market.AppActivityDistributeOnPage;
import cn.tofocus.lejia.bean.dto.app.market.AppActivityInfo;
import cn.tofocus.lejia.bean.dto.app.wxPay.WxPayData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

public interface AppActivityApi
{
    @Operation(summary = "获取卡券活动信息", tags = AppTags.ACTIVITY)
    @PostMapping(value = "/activity/get")
    Result<AppActivityInfo> get(@RequestParam(value = "pkey") @Parameter(description = "卡券活动主键") Integer pkey);
    
    @Operation(summary = "参与卡券活动", tags = AppTags.ACTIVITY)
    @PostMapping(value = "/lm/activity/join")
    Result<WxPayData> join(@RequestParam(value = "pkey") @Parameter(description = "卡券活动主键") Integer pkey);
    
    @Operation(summary = "查询活动分发列表", tags = AppTags.ACTIVITY)
    @PostMapping(value = "/lm/activity/distribute/query")
    Result<PageResult<AppActivityDistributeOnPage>> queryDistributeActivity(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize);
    
    @Operation(summary = "获取会员福利卡券活动列表", tags = AppTags.ACTIVITY)
    @PostMapping(value = "/activity/welfare")
    Result<List<AppActivityInfo>> listWelfare();
}

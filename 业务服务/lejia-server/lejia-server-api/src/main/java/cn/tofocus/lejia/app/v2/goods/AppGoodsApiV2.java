package cn.tofocus.lejia.app.v2.goods;

import org.springframework.web.bind.annotation.PostMapping;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.v2.goods.PresaleTimeOnInfo;
import io.swagger.v3.oas.annotations.Operation;

public interface AppGoodsApiV2
{
    @Operation(summary = "获取抢购时间", tags = AppTags.mobileGoodsV2)
    @PostMapping(value = "/get/presaleTime")
    public Result<PresaleTimeOnInfo> getPresaleTime();
    
}

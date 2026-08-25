package cn.tofocus.lejia.api.v2;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.CardStatisticsInfo;
import cn.tofocus.lejia.bean.dto.v2.gift.MktGiftV2Info;
import cn.tofocus.lejia.bean.dto.v2.gift.MktGiftV2OnPage;
import cn.tofocus.lejia.bean.dto.v2.gift.MktMemberGiftV2OnPage;
import cn.tofocus.lejia.bean.enums.CardStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

public interface GiftV2Api
{
    @Operation(summary = "获取礼品券列表", tags = ApiTags.custGift_V2)
    @PostMapping(value = "/query")
    Result<PageResult<MktGiftV2OnPage>> query(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号", hidden = true) int page,
        @RequestParam(value = "pagesize", defaultValue = "10000") @Parameter(description = "每页大小", hidden = true) int pagesize,
        @RequestParam(value = "title", required = false) @Parameter(description = "标题") String title,
        @RequestParam(value = "enabled", required = false) @Parameter(description = "启停状态") Boolean enabled,
        @RequestParam(value = "invalid", required = false) @Parameter(description = "是否失效") Boolean invalid);
    
    @Operation(summary = "获取已使用礼品券列表", tags = ApiTags.custGift_V2)
    @PostMapping(value = "/query/use")
    public Result<PageResult<MktMemberGiftV2OnPage>> queryUse(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "userFarmer", required = false) @Parameter(description = "核销市场") String userFarmer,
        @RequestParam(value = "startTime", required = false) @Parameter(description = "开始时间") String startTime,
        @RequestParam(value = "endTime", required = false) @Parameter(description = "结束时间") String endTime,
        @RequestParam(value = "st", required = false) @Parameter(description = "开始时间-领取") String st,
        @RequestParam(value = "et", required = false) @Parameter(description = "结束时间-领取")String et,
        @RequestParam(value = "mobile", required = false) @Parameter(description = "手机号") String mobile,
        @RequestParam(value = "title", required = false) @Parameter(description = "卡券名称") String title,
        @RequestParam(value = "status", required = false) @Parameter(description = "卡券状态") CardStatus status,
        @RequestParam(value = "invalid", required = false) @Parameter(description = "卡券状态,false:未失效") Boolean invalid);
    
    @Operation(summary = "礼品券发放记录合计", tags = ApiTags.custGift_V2)
    @PostMapping(value = "/query/use/sum")
    public Result<CardStatisticsInfo> queryUseSum(
        @RequestParam(value = "userFarmer", required = false) @Parameter(description = "核销市场") String userFarmer,
        @RequestParam(value = "startTime", required = false) @Parameter(description = "开始时间") String startTime,
        @RequestParam(value = "endTime", required = false) @Parameter(description = "结束时间") String endTime,
        @RequestParam(value = "st", required = false) @Parameter(description = "开始时间-领取") String st,
        @RequestParam(value = "et", required = false) @Parameter(description = "结束时间-领取")String et,
        @RequestParam(value = "mobile", required = false) @Parameter(description = "手机号") String mobile,
        @RequestParam(value = "title", required = false) @Parameter(description = "卡券名称") String title,
        @RequestParam(value = "status", required = false) @Parameter(description = "卡券状态") CardStatus status,
        @RequestParam(value = "invalid", required = false) @Parameter(description = "卡券状态,false:未失效") Boolean invalid);
    
    @Operation(summary = "获取礼品券", tags = ApiTags.custGift_V2)
    @PostMapping("/get")
    Result<MktGiftV2Info> get(@RequestParam(value = "pkey") @Parameter(description = "主键") Integer pkey);
    
    @Operation(summary = "新增礼品券", tags = ApiTags.custGift_V2)
    @PostMapping("/ins")
    Result<Boolean> ins(@RequestBody @Valid MktGiftV2Info entity);
    
    @Operation(summary = "修改礼品券", tags = ApiTags.custGift_V2)
    @PostMapping(value = "/upd")
    Result<Boolean> upd(@RequestBody @Valid MktGiftV2Info entity);
    
    @Operation(summary = "礼品券失效", tags = ApiTags.custGift_V2)
    @PostMapping(value = "/invalid")
    Result<Boolean> invalid(@RequestParam(value = "pkey") Integer pkey);
    
    @Operation(summary = "礼品券启用", tags = ApiTags.custGift_V2)
    @PostMapping(value = "/enable/start")
    Result<Boolean> start(@RequestParam(name = "pkey") Integer pkey);
    
    @Operation(summary = "礼品券停用", tags = ApiTags.custGift_V2)
    @PostMapping(value = "/enable/stop")
    Result<Boolean> stop(@RequestParam(name = "pkey") Integer pkey);
    
    @Operation(summary = "【临时】发放礼品券", tags = ApiTags.custGift_V2)
    @PostMapping(value = "/issue")
    Result<Boolean> issue(@RequestParam(name = "pkey") Integer pkey,
        @RequestParam(name = "member", required = false) Integer member,
        @RequestParam(name = "mobile", required = false) String mobile, @RequestParam(name = "num") Integer num);
}

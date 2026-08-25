package cn.tofocus.lejia.api.v1.market;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.MktActivityInfo;
import cn.tofocus.lejia.bean.dto.market.MktActivityIssueOnPage;
import cn.tofocus.lejia.bean.dto.market.MktActivityOnList;
import cn.tofocus.lejia.bean.dto.market.MktActivityOnPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;

public interface MktActivityApi
{
    @Operation(summary = "查询卡券活动", tags = ApiTags.ACTIVITY)
    @PostMapping(value = "/query")
    Result<PageResult<MktActivityOnPage>> query(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "name", required = false) @Parameter(description = "名称") String name,
        @RequestParam(value = "enabled", required = false) @Parameter(description = "是否启用") Boolean enabled,
        @RequestParam(value = "farmer", required = false) @Parameter(description = "市场（运营中心查询指定市场）") String farmer);
    
    @Operation(summary = "列表卡券活动", tags = ApiTags.ACTIVITY)
    @PostMapping(value = "/list")
    Result<List<MktActivityOnList>> list(
        @RequestParam(value = "name", required = false) @Parameter(description = "名称") String name,
        @RequestParam(value = "enabled", required = false) @Parameter(description = "是否启用") Boolean enabled,
        @RequestParam(value = "farmer", required = false) @Parameter(description = "市场（运营中心查询指定市场）") String farmer);
    
    @Operation(summary = "查询卡券活动发放记录", tags = ApiTags.ACTIVITY)
    @PostMapping(value = "/issue/query")
    Result<PageResult<MktActivityIssueOnPage>> queryIssue(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "memberMobile", required = false) @Parameter(description = "会员手机号") String memberMobile,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "开始时间") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "结束时间") String endDate,
        @RequestParam(value = "activity", required = false) @Parameter(description = "卡券活动") Integer activity);
    
    @Operation(summary = "获取卡券活动", tags = ApiTags.ACTIVITY)
    @PostMapping(value = "/get")
    Result<MktActivityInfo> get(@RequestParam(value = "pkey") @Parameter(description = "主键") Integer pkey);
    
    @Operation(summary = "新增卡券活动", tags = ApiTags.ACTIVITY)
    @PostMapping(value = "/add")
    Result<Boolean> add(@RequestBody @Valid MktActivityInfo info);
    
    @Operation(summary = "编辑卡券活动", tags = ApiTags.ACTIVITY)
    @PostMapping(value = "/upd")
    Result<Boolean> upd(@RequestBody @Valid MktActivityInfo info);
    
    @Operation(summary = "启停卡券活动", tags = ApiTags.ACTIVITY)
    @PostMapping(value = "/enable")
    Result<Boolean> enable(@RequestParam(value = "pkey") @Parameter(description = "主键") Integer pkey,
        @RequestParam(value = "enabled") @Parameter(description = "是否启用") Boolean enabled);
}

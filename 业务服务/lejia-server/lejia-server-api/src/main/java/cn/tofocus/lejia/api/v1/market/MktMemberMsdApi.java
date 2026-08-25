package cn.tofocus.lejia.api.v1.market;

import java.util.List;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.config.MsdPayConfig;
import cn.tofocus.lejia.bean.dto.market.MktMemberMsdAdjustDTO;
import cn.tofocus.lejia.bean.dto.market.MktMemberMsdLineOnPage;
import cn.tofocus.lejia.bean.dto.market.MktMemberMsdOnPage;
import cn.tofocus.lejia.bean.dto.market.MktMemberMsdTagDrop;
import cn.tofocus.lejia.bean.enums.MsdOperationType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

public interface MktMemberMsdApi
{
    @Operation(summary = "民生豆标签下拉", tags = ApiTags.custMemberMsd)
    @PostMapping("/tag/list/drop")
    @Deprecated
    Result<List<MktMemberMsdTagDrop>> listTagDrop();
    
    @Operation(summary = "查询民生豆账户", tags = ApiTags.custMemberMsd)
    @PostMapping("/query")
    Result<PageResult<MktMemberMsdOnPage>> query(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "mobile", required = false) @Parameter(description = "手机") String mobile,
        @RequestParam(value = "tags", required = false) @Parameter(description = "标签主键") List<Integer> tags);
    
    @Operation(summary = "清空民生豆余额", tags = ApiTags.custMemberMsd)
    @PostMapping("/balance/clear")
    Result<Boolean> clearBalance(
        @RequestParam(value = "tags", required = false) @Parameter(description = "标签主键") List<Integer> tags);
    
    @Operation(summary = "调整民生豆余额", tags = ApiTags.custMemberMsd)
    @PostMapping("/balance/adjust")
    Result<Boolean> adjustBalance(@RequestBody @Valid MktMemberMsdAdjustDTO dto);
    
    @Operation(summary = "查询民生豆明细", tags = ApiTags.custMemberMsd)
    @PostMapping("/line/query")
    Result<PageResult<MktMemberMsdLineOnPage>> queryLine(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "mobile", required = false) @Parameter(description = "手机") String mobile,
        @RequestParam(value = "tags", required = false) @Parameter(description = "标签主键") List<Integer> tags,
        @RequestParam(value = "operationTypes", required = false) @Parameter(description = "操作类型") List<MsdOperationType> operationTypes,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "开始日期") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "结束日期") String endDate,
        @RequestParam(value = "remark", required = false) @Parameter(description = "备注") String remark);
    
    @Operation(summary = "获取热力豆配置", tags = ApiTags.custMemberMsd)
    @PostMapping("/get/config")
    Result<MsdPayConfig> getMsdPayConfig();
    
    @Operation(summary = "设置热力豆配置", tags = ApiTags.custMemberMsd)
    @PostMapping("/set/config")
    Result<Boolean> setMsdPayConfig(@RequestBody MsdPayConfig config);
    
}

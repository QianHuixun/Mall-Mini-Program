package cn.tofocus.lejia.api.v1.market;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.MktOperatingStatisticsDTO;
import cn.tofocus.lejia.bean.dto.market.MktOperatingStatisticsOnList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

public interface MktOperatingStatisticsApi
{
    @Operation(summary = "查询经营数据统计列表", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/query")
    public Result<PageResult<MktOperatingStatisticsOnList>> queryOperatingStatistics(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "farmer", required = false) @Parameter(description = "市场") String farmer,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "开始时间") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "结束时间") String endDate);
    
    @Operation(summary = "经营数据统计行", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/count")
    public Result<MktOperatingStatisticsDTO> countOperatingStatistics(
        @RequestParam(value = "farmer", required = false) @Parameter(description = "市场") String farmer,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "开始时间") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "结束时间") String endDate);
}

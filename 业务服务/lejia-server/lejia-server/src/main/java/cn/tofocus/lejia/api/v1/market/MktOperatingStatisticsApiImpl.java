package cn.tofocus.lejia.api.v1.market;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.MktOperatingStatisticsDTO;
import cn.tofocus.lejia.bean.dto.market.MktOperatingStatisticsOnList;
import cn.tofocus.lejia.domain.market.StatisticsManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RequestMapping("/v1/sys/data/statistics")
@RestController
public class MktOperatingStatisticsApiImpl implements MktOperatingStatisticsApi
{
    
    @Autowired
    private StatisticsManager manager;
    
    @Override
    public Result<PageResult<MktOperatingStatisticsOnList>> queryOperatingStatistics(int page, int pagesize,
        String farmer, String startDate, String endDate)
    {
        return new Result<>(manager.queryOperatingStatistics(page, pagesize, farmer, startDate, endDate));
    }
    
    @Operation(summary = "导出经营数据统计", tags = ApiTags.custDataCenter)
    @PostMapping("/export/operatingStatistics")
    public void exportOperatingStatistics(
        @RequestParam(value = "farmer", required = false) @Parameter(description = "市场") String farmer,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "开始时间") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "结束时间") String endDate,
        HttpServletResponse response)
    {
        manager.exportMemberInfo(farmer, startDate, endDate, response);
    }
    
    @Override
    public Result<MktOperatingStatisticsDTO> countOperatingStatistics(String farmer, String startDate, String endDate)
    {
        return new Result<>(manager.countOperatingStatistics(farmer, startDate, endDate));
    }
    
}

package cn.tofocus.lejia.domain.market;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.MktOperatingStatisticsDTO;
import cn.tofocus.lejia.bean.dto.market.MktOperatingStatisticsOnList;
import cn.tofocus.lejia.bean.excel.ExportOperatingStatistics;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.market.MktOperatingStatisticsDao;
import cn.tofocus.lejia.util.ExportUtil;

@Component
public class StatisticsManager
{
    @Autowired
    private MktOperatingStatisticsDao operatingStatisticsDao;
    
    public PageResult<MktOperatingStatisticsOnList> queryOperatingStatistics(int page, int pagesize, String farmer,
        String startDate, String endDate)
    {
        Integer ascriptionPkey = CurrentSession.ascriptionPkey();
        PageResult<MktOperatingStatisticsOnList> result = operatingStatisticsDao.queryOperatingStatistics(page,
            pagesize,
            farmer,
            startDate,
            endDate,
            ascriptionPkey,
            CurrentSession.marketPkey(),
            MktOperatingStatisticsOnList.class);
        return result;
    }
    
    public void exportMemberInfo(String farmer, String startDate, String endDate, HttpServletResponse response)
    {
        String excelName = "经营数据统计";
        Integer ascriptionPkey = CurrentSession.ascriptionPkey();
        PageResult<ExportOperatingStatistics> result = operatingStatisticsDao.queryOperatingStatistics(0,
            10000,
            farmer,
            startDate,
            endDate,
            ascriptionPkey,
            CurrentSession.marketPkey(),
            ExportOperatingStatistics.class);
        ExportUtil.exportData(ExportOperatingStatistics.class,
            result.getContent(),
            response,
            excelName,
            excelName,
            excelName);
    }
    
    public MktOperatingStatisticsDTO countOperatingStatistics(String farmer, String startDate, String endDate)
    {
        Integer ascriptionPkey = CurrentSession.ascriptionPkey();
        List<MktOperatingStatisticsDTO> list = operatingStatisticsDao.countOperatingStatistics(farmer,
            startDate,
            endDate,
            ascriptionPkey,
            CurrentSession.marketPkey(),
            MktOperatingStatisticsDTO.class);
        return list.get(0);
    }
    
}

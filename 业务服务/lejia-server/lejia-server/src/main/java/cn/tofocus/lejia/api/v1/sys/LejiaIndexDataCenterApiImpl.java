package cn.tofocus.lejia.api.v1.sys;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.domain.IndexDataCenterManager;

@RequestMapping("/v1/sys/index/data/center")
@RestController
public class LejiaIndexDataCenterApiImpl implements LejiaIndexDataCenterApi
{
    @Autowired
    private IndexDataCenterManager manager;
    
    @Override
    public Result<Map<String, Object>> yesterdayTodayCompared()
    {
        return new Result<>(manager.yesterdayTodayCompared());
    }
    
    @Override
    public Result<List<Map<String, Object>>> salesStatus()
    {
        try
        {
            return new Result<>(manager.salesStatus());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new Result<>();
    }
    
    @Override
    public Result<List<Map<String, Object>>> farmerSales()
    {
        return new Result<>(manager.farmerSales());
    }
    
    @Override
    public Result<List<Map<String, Object>>> mTypeSales()
    {
        return new Result<>(manager.mTypeSales());
    }
    
    @Override
    public Result<List<Map<String, Object>>> getGoodsSales()
    {
        return new Result<>(manager.getGoodsSales());
    }
    
    @Override
    public Result<List<Map<String, Object>>> kcWarning()
    {
        return new Result<>(manager.kcWarning());
    }
    
    @Override
    public Result<Boolean> manualRunTask(Integer ascription)
    {
        manager.yesterdayData(ascription);
        manager.initReport(ascription);
        return new Result<>();
    }
    
    @Override
    public Result<Integer> getDeliveredOrder()
    {
        return new Result<>(manager.getDeliveredOrder());
    }
    
    @Override
    public Result<Integer> getRefundOrder()
    {
        return new Result<>(manager.getRefundOrder());
    }
}

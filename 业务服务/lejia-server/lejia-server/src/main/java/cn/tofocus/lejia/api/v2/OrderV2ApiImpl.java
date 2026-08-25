package cn.tofocus.lejia.api.v2;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.app.market.MktAppOrderV2Info;
import cn.tofocus.lejia.bean.dto.market.MktOrderOnList;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.OrderType;
import cn.tofocus.lejia.bean.enums.PurchaseStatus;
import cn.tofocus.lejia.cache.SpaceKcCache;
import cn.tofocus.lejia.exception.LejiaErrCode;

@RequestMapping("/v2/market/order")
@RestController
public class OrderV2ApiImpl implements OrderV2Api
{
    @Autowired
    private SpaceKcCache spaceKcCache;
    
    @Override
    public Result<PageResult<MktOrderOnList>> queryOrder(int page, int pagesize, String startDate, String endDate,
        OrderStatus status, String code, String mobile, OrderType orderType, PurchaseStatus purchaseStatus,
        String vrifyCode, Boolean priceAbnormal, Boolean priceAbnormalFinsh)
    {
        return null;
    }
    
    @Override
    public Result<Map<String, Object>> queryOrderSum(String startDate, String endDate, OrderStatus status, String code,
        String mobile, OrderType orderType, PurchaseStatus purchaseStatus, String vrifyCode, Boolean priceAbnormal,
        Boolean priceAbnormalFinsh)
    {
        return null;
    }
    
    @Override
    public Result<String> test(String pkey)
    {
        
        try
        {
            //            Long set = spaceKcCache.set(pkey, 25l);
            //            System.out.println("set: " + set);
            Long increment = spaceKcCache.increment("22", 22L, null);
            Long decrement = spaceKcCache.decrement(pkey, 20l, null);
            System.out.println("increment: " + decrement);
        }
        catch (NoSuchFieldError e)
        {
            System.out.println("222: " + e.getMessage());
            throw TofocusException.of(LejiaErrCode.GOODS_NONUM);
        }
        return null;
    }

    @Override
    public Result<MktAppOrderV2Info> loadOrder(Integer pkey)
    {
        return null;
    }
    
}

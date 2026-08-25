package cn.tofocus.lejia.app.v4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.GroupResult;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.app.AppVendorGtypeInfo;
import cn.tofocus.lejia.bean.dto.app.goods.AppGoodsV4OnList;
import cn.tofocus.lejia.bean.dto.goods.GoodsListItem;
import cn.tofocus.lejia.bean.dto.goods.GoodsListItemV2;
import cn.tofocus.lejia.bean.enums.GoodsSortType;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.domain.app.AppGoodsV4Manager2;
import io.swagger.v3.oas.annotations.Parameter;

@RequestMapping("/v4/app/market/goods")
@RestController
public class AppGoodsV4ApiImpl implements AppGoodsV4Api
{
    @Autowired
    private AppGoodsV4Manager2 manager;
    
    //    @Autowired
    //    private AppGoodsV4Manager manager; 
    
    @Override
    public Result<PageResult<AppGoodsV4OnList>> queryAppGoods(Integer page, Integer pagesize, Integer gtype,
        Integer goodsMain)
    {
        return new Result<>(manager.queryAppGoodsV4(page, pagesize, gtype, goodsMain));
    }
    
    @Override
    public Result<PageResult<AppGoodsV4OnList>> queryThreeGtypeAppGoods(Integer page, Integer pagesize,
        Integer threeGtype, GoodsSortType goodsSortType, Boolean sortDesc)
    {
        return new Result<>(manager.queryThreeGtypeAppGoodsV4(page, pagesize, threeGtype, goodsSortType, sortDesc));
    }
    
    @Override
    public Result<PageResult<AppVendorGtypeInfo>> queryGtypeVendor(Integer page, Integer pagesize, Integer gtype,
        String name)
    {
        return new Result<>(manager.queryGtypeVendor(page, pagesize, gtype, name));
    }
    
    @Override
    public Result<GroupResult<String, GoodsListItemV2>> queryAppGtypeGoods(int from, int limit, Integer gtype,
        GoodsSortType goodsSortType, Boolean sortDesc)
    {
        //        return new Result<>(manager.queryAppGtypeGoods(from, limit, gtype));
//        return new Result<>(manager.queryAppGtypeGoods(from, limit, gtype, goodsSortType, sortDesc));
        return new Result<>(manager.queryAppGtypeGoodsSQL(from, limit, gtype, goodsSortType, sortDesc, MobileSession.farmerPkey(), null));
    }
    
    @Override
    public Result<GroupResult<String, GoodsListItemV2>> queryAppGoodsMainGoods(int from, int limit, Integer goodsMain,
        GoodsSortType goodsSortType, Boolean sortDesc, Boolean limitGoodsMain, int deliveryType)
    {
        return new Result<>(manager.queryAppGoodsMainGoodsSQL(from, limit, goodsMain, goodsSortType, sortDesc, limitGoodsMain, deliveryType));
//        return new Result<>(manager.queryAppGoodsMainGoods(from, limit, goodsMain, goodsSortType, sortDesc, limitGoodsMain));
    }
    
    @Override
    public Result<Boolean> correlationGoodsMain(Integer goodsMain)
    {
        return new Result<>(manager.correlationGoodsMain(goodsMain));
    }
    
    @Override
    public Result<GroupResult<String, GoodsListItemV2>> queryAppGoodsMainGoodsTest(int from, int limit, Integer goodsMain,
        GoodsSortType goodsSortType, Boolean sortDesc, Boolean limitGoodsMain)
    {
        return new Result<>(manager.queryAppGoodsMainGoods(from, limit, goodsMain, goodsSortType, sortDesc, limitGoodsMain));
    }
    
    @Override
    public Result<GroupResult<String, AppVendorGtypeInfo>> queryAppGtypeVendor(int from, int limit, Integer gtype,
        String name)
    {
        return new Result<>(manager.queryAppGtypeVendor(from, limit, gtype, name));
    }
    
    @Override
    public Result<GroupResult<String, GoodsListItem>> queryAppGoodsMainVendorGoods(int from, int limit, Integer vendor,
        String name, Integer goodsMain, Boolean priceSort, Boolean xsNumSort)
    {
        return new Result<>(
            manager.queryAppGoodsMainVendorGoods(from, limit, vendor, name, goodsMain, priceSort, xsNumSort));
    }
    
}

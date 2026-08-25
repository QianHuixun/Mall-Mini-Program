package cn.tofocus.lejia.api.v1.market.goods;

import java.util.List;

import cn.tofocus.lejia.bean.dto.goods.TwoGtypeDropWithGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.log.LogApi;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.goods.GtypeDropInfo;
import cn.tofocus.lejia.bean.dto.goods.GtypeDropV2Info;
import cn.tofocus.lejia.bean.dto.market.DropIntegerDown;
import cn.tofocus.lejia.bean.dto.market.MktGtypeOnList;
import cn.tofocus.lejia.domain.market.goods.GtypeManager;

@RequestMapping("/v1/market/goods/gtype")
@RestController
public class GoodsGtypeApiImpl implements GoodsGtypeApi
{
    @Autowired
    private GtypeManager gtypeManager;
    
    @Override
    @LogApi(operation = "新增商品分类", format = "新增商品分类名称:{entity.name}", resultFormat = "")
    public Result<MktGtypeOnList> insGtype(MktGtypeOnList entity)
    {
        MktGtypeOnList insGtype = gtypeManager.insGtype(entity);
        return new Result<>(insGtype);
    }
    
    @Override
    public Result<MktGtypeOnList> getGtype(Integer pkey)
    {
        return new Result<>(gtypeManager.getGtype(pkey));
    }
    
    @Override
    public Result<PageResult<MktGtypeOnList>> queryGtype(int page, int pagesize, String gtyprName, Boolean showPoint,
        Boolean showMarket)
    {
        return new Result<>(gtypeManager.queryGtype(page, pagesize, gtyprName, showPoint, showMarket));
    }
    
    @Override
    @LogApi(operation = "修改商品分类", format = "修改商品分类 名称:{name}")
    public Result<MktGtypeOnList> updGtype(Integer pkey, String name, Integer sort, Integer marketSort,
        Integer pointSort, String photo, String remark, Boolean showPoint, Boolean showMarket)
    {
        return new Result<>(
            gtypeManager.updGtype(pkey, name, sort, marketSort, pointSort, photo, remark, showPoint, showMarket));
    }
    
    @Override
    @LogApi(operation = "删除商品分类", format = "删除商品分类")
    public Result<Boolean> delGtype(Integer pkey)
    {
        return new Result<>(gtypeManager.delGtype(pkey));
    }
    
    @Override
    @LogApi(operation = "启动商品分类", format = "启动商品分类")
    public Result<Boolean> startGtype(Integer pkey)
    {
        return new Result<>(gtypeManager.enabledGtype(pkey, true));
    }
    
    @Override
    @LogApi(operation = "停止商品分类", format = "停止商品分类")
    public Result<Boolean> stopGtype(Integer pkey)
    {
        return new Result<>(gtypeManager.enabledGtype(pkey, false));
    }
    
    @Override
    public Result<List<GtypeDropInfo>> dropyGtype(Integer key)
    {
        return new Result<>(gtypeManager.dropyGtype(key));
    }

    @Override
    public Result<List<GtypeDropV2Info>> dropyGtypeV2(Integer key)
    {
        return new Result<>(gtypeManager.dropyGtypeV2(key));
    }
    
    @Override
    public Result<List<TwoGtypeDropWithGoods>> dropTwoGtypeWithGoods(String farmer)
    {
        return new Result<>(gtypeManager.dropTwoGtypeWithGoods(farmer));
    }

    @Override
    public Result<List<DropIntegerDown>> dropCardGtype(String farmer)
    {
        return new Result<>(gtypeManager.dropCardGtype(farmer));
    }
}

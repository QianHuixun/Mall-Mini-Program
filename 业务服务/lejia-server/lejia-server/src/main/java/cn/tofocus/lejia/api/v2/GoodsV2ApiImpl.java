package cn.tofocus.lejia.api.v2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.PkeyNameDTO;
import cn.tofocus.lejia.bean.dto.goods.GoodsCouponInfo;
import cn.tofocus.lejia.bean.dto.goods.GoodsCouponOnPage;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.domain.v2.GoodsV2Manager;

@RequestMapping("/v2/market/goods")
@RestController
public class GoodsV2ApiImpl implements GoodsV2Api
{
    @Autowired
    private GoodsV2Manager manager;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Override
    public Result<Integer> insGoodsCoupon(GoodsCouponInfo entity)
    {
        return new Result<>(manager.insGoodsCoupon(entity));
    }
    
    @Override
    public Result<Integer> updGoodsCoupon(GoodsCouponInfo entity)
    {
        return new Result<>(manager.updGoodsCoupon(entity));
    }
    
    @Override
    public Result<Boolean> invalidGoodsCoupon(Integer pkey)
    {
        return new Result<>(manager.invalidGoodsCoupon(pkey));
    }
    
    @Override
    public Result<GoodsCouponInfo> getGoods(Integer pkey)
    {
        return new Result<>(manager.getGoods(pkey));
    }
    
    @Override
    public Result<PageResult<GoodsCouponOnPage>> queryGoods(Integer page, Integer pagesize, String title,
        Integer goodsMain, Boolean enabled)
    {
        return new Result<>(manager.queryGoods(page, pagesize, title, goodsMain, enabled));
    }

    @Override
    public Result<Boolean> startGoods(List<Integer> pkeys)
    {
        return new Result<>(manager.enabledGoods(pkeys, true));
    }

    @Override
    public Result<Boolean> stopGoods(List<Integer> pkeys)
    {
        return new Result<>(manager.enabledGoods(pkeys, false));
    }

    @Override
    public Result<List<PkeyNameDTO>> dropMarketGoodsV2(String farmer, Integer gtype, List<Integer> mtype)
    {
        return new Result<>(goodsDao.dropMarketGoodsV2(farmer, gtype, CurrentSession.ascriptionPkey(), mtype));
    }
    
}

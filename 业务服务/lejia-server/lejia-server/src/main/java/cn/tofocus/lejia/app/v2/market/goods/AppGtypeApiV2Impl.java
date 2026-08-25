package cn.tofocus.lejia.app.v2.market.goods;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.app.v2.goods.AppGoodsGtypeApiV2;
import cn.tofocus.lejia.bean.dto.PkeyNameDTO;
import cn.tofocus.lejia.bean.dto.app.goods.AppGtypeDTO;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.domain.app.AppGoodsGtypeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/v2/app/market/goods/gtype")
@RestController
public class AppGtypeApiV2Impl implements AppGoodsGtypeApiV2
{
    @Autowired
    private AppGoodsGtypeManager appGoodsGtypeManager;

    @Override
    public Result<List<PkeyNameDTO>> queryGtype(Integer gtype, boolean hasRecommend) {
        return new Result<>(appGoodsGtypeManager.queryGtype(gtype, hasRecommend));
    }

    @Override
    public Result<List<AppGtypeDTO>> listGtype(Boolean showPoint, Boolean showMarket, Boolean flag, MType mtype)
    {
        return new Result<>(appGoodsGtypeManager.queryGtypeV2(showPoint, showMarket, flag, mtype));
    }

    @Override
    public Result<List<PkeyNameDTO>> queryVendorGtype(Integer vendor)
    {
        return new Result<>(appGoodsGtypeManager.queryVendorGtype(vendor));
    }

    @Override
    public Result<List<PkeyNameDTO>> queryVendorOneGtype()
    {
        return new Result<>(appGoodsGtypeManager.queryVendorOneGtype());
    }
}

package cn.tofocus.lejia.api.v1.market.goods;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsBox;
import cn.tofocus.lejia.bean.entity.market.MktDesktop;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.goods.MktGoodsBoxDao;
import cn.tofocus.lejia.dao.market.MktDesktopDao;
import io.swagger.v3.oas.annotations.Operation;

@RequestMapping("/v1/market/goods/box/manager")
@RestController
public class GoodsBoxApiImpl
{
    @Autowired
    private MktGoodsBoxDao goodsBoxDao;
    
    @Autowired
    private MktDesktopDao desktopDao;
    
    @Operation(summary = "新增包厢商品和桌位关联", tags = ApiTags.GOODS_BOX)
    @PostMapping("/addAll")
    public Result<Integer> addAll(@RequestParam(value = "goodsKey")List<Integer> goodsKey, 
        @RequestParam(value = "desktop")Integer desktop, 
        @RequestParam(value = "lockId", required = false)String lockId,
        @RequestParam(value = "noonPrice", required = false)BigDecimal noonPrice,
        @RequestParam(value = "nightPrice", required = false)BigDecimal nightPrice)
    {
        List<MktGoodsBox> list = new ArrayList<>();
        for(Integer goods : goodsKey)
        {
            MktGoodsBox exec = goodsBoxDao.selectOne().eq("goods", goods).eq("desktop", desktop).exec();
            if(exec != null)
                continue;
            MktDesktop mktDesktop = desktopDao.get(desktop);
            if(mktDesktop == null)
                continue;
            MktGoodsBox bean = new MktGoodsBox();
            bean.setGoods(goods);
            bean.setDesktop(desktop);
            bean.setDesktopName(mktDesktop.getName());
            bean.setNoonPrice(noonPrice);
            bean.setNightPrice(nightPrice);
            bean.setFarmer(mktDesktop.getFarmer());
            bean.setCompany(mktDesktop.getCompany());
            bean.setAscription(mktDesktop.getAscription());
            bean.setLockId(lockId);
            list.add(bean);
        }
        goodsBoxDao.addAll(list);
        return new Result<>(list.size());
    }
}

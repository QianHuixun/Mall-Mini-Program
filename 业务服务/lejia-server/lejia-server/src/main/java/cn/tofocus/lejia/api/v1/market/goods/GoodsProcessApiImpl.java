package cn.tofocus.lejia.api.v1.market.goods;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsProcess;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsProcessDao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RequestMapping("/v1/market/goods/process/manager")
@RestController
public class GoodsProcessApiImpl
{
    @Autowired
    private MktGoodsProcessDao goodsProcessDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Operation(summary = "新增关联商品", tags = ApiTags.GOODS_PROCESS)
    @PostMapping("/addAll")
    public Result<Integer> addAll(@RequestParam(value = "goodsKey")List<Integer> goodsKey, 
        @RequestParam(value = "process")@Parameter(description = "该参数是加工商品的规格主键")List<Integer> process)
    {
        String marketPkey = CurrentSession.marketPkey();
        Integer ascriptionPkey = CurrentSession.ascriptionPkey();
        String companyPkey = CurrentSession.companyPkey();
        List<MktGoodsProcess> list = new ArrayList<>();
        List<MktGoods> goodsUpdList = new ArrayList<>();
        for(Integer p : process)
        {
            for(Integer goods : goodsKey)
            {
                MktGoodsProcess exec = goodsProcessDao.selectOne().eq("goods", goods).eq("process", p).exec();
                if(exec != null)
                    continue;
                MktGoodsProcess bean = new MktGoodsProcess();
                bean.setGoods(goods);
                bean.setProcess(p);
                bean.setFarmer(marketPkey);
                bean.setCompany(companyPkey);
                bean.setAscription(ascriptionPkey);
                list.add(bean);
                MktGoods mktGoods = goodsDao.get(goods);
                if(mktGoods != null)
                {
                    mktGoods.setIsProcess(true);
                    goodsUpdList.add(mktGoods);
                }
            }
        }
        goodsProcessDao.addAll(list);
        goodsDao.updateAll(goodsUpdList);
        return new Result<>(list.size());
    }
    
    
//    @Operation(summary = "新增关联商品", tags = ApiTags.GOODS_PROCESS)
//    @PostMapping("/addAll")
//    public Result<Integer> addAll(@RequestBody List<GoodsProcessUpdInfo> infos)
//    {
//        String marketPkey = CurrentSession.marketPkey();
//        Integer ascriptionPkey = CurrentSession.ascriptionPkey();
//        String companyPkey = CurrentSession.companyPkey();
//        List<MktGoodsProcess> list = new ArrayList<>();
//        for(GoodsProcessUpdInfo gp : infos)
//        {
//            MktGoodsProcess exec = goodsProcessDao.selectOne().eq("goods", gp.getGoods()).eq("process", gp.getProcess()).exec();
//            if(exec != null)
//                continue;
//            MktGoodsProcess bean = new MktGoodsProcess();
//            bean.setGoods(gp.getGoods());
//            bean.setProcess(gp.getProcess());
//            bean.setFarmer(marketPkey);
//            bean.setCompany(companyPkey);
//            bean.setAscription(ascriptionPkey);
//            list.add(bean);
//        }
//        goodsProcessDao.addAll(list);
//        return new Result<>(list.size());
//    }
}

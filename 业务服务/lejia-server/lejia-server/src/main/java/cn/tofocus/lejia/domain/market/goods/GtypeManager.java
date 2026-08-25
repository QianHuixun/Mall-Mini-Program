package cn.tofocus.lejia.domain.market.goods;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.data.KeyName;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.bean.dto.goods.*;
import cn.tofocus.lejia.bean.dto.market.DropIntegerDown;
import cn.tofocus.lejia.bean.dto.market.MktGoodsMainSimple;
import cn.tofocus.lejia.bean.dto.market.MktGtypeOnList;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsMain;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsMainThree;
import cn.tofocus.lejia.bean.entity.market.MktGtype;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsMainDao;
import cn.tofocus.lejia.dao.goods.MktGoodsMainThreeDao;
import cn.tofocus.lejia.dao.market.MktGtypeDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.domain.GoodListQueryer;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.exception.WsaleErrCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GtypeManager
{
    @Autowired
    private MktGtypeDao gtypeDao;
    
    @Autowired
    private MktGoodsMainDao goodsMainDao;
    
    @Autowired
    private MktGoodsMainThreeDao goodsMainThreeDao;
    
    @Autowired
    private SysFarmerDao farmerDao;

    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private GoodListQueryer goodListQueryer;
    
    public MktGtypeOnList insGtype(MktGtypeOnList entity)
    {
        MktGtype exec =
            gtypeDao.selectOne()
            .eq("farmer", CurrentSession.marketPkey())
            .eq("name", entity.getName()).eq("ascription", CurrentSession.ascriptionPkey()).exec();
        if (exec != null && !exec.getIdDel()) throw TofocusException.of(WsaleErrCode.GOODS_TYPE_NAME_REPEAT);
        MktGtype gtype = BeanUtil.beanFrom(MktGtype.class, entity);
        gtype.setRowVension(1);
        gtype.setEnabled(true);
        gtype.setIdDel(false);
        gtype.setFarmer(CurrentSession.marketPkey());
        gtype.setAscription(CurrentSession.ascriptionPkey());
        if (exec != null) gtype.setPkey(exec.getPkey());
        if (entity.getShowPoint() == null) gtype.setShowPoint(false);
        if (entity.getShowMarket() == null) gtype.setShowMarket(false);
        if (entity.getSort() == null) gtype.setSort(0);
        if (entity.getMarketSort() == null) gtype.setMarketSort(0);
        if (entity.getPointSort() == null) gtype.setPointSort(0);
        MktGtype add = gtypeDao.put(gtype);
        return BeanUtil.beanFrom(MktGtypeOnList.class, add);
    }
    
    public MktGtypeOnList getGtype(Integer pkey)
    {
        MktGtype gtype = gtypeDao.getGtype(pkey);
        if (gtype == null) return null;
        return BeanUtil.beanFrom(MktGtypeOnList.class, gtype);
    }
    
    public PageResult<MktGtypeOnList> queryGtype(int page, int pagesize, String gtyprName, Boolean showPoint,
        Boolean showMarket)
    {
        PageResult<MktGtype> pageResult = gtypeDao
            .queryGtype(page, pagesize, gtyprName, CurrentSession.marketPkey(), CurrentSession.ascriptionPkey());
        PageResult<MktGtypeOnList> result = BeanUtil.beanPageFrom(MktGtypeOnList.class, pageResult);
        assembleGoodsName(result.getContent());
        return result;
    }
    
    public void assembleGoodsName(List<MktGtypeOnList> list)
    {
        List<Integer> gtypeList = list.stream().map(MktGtypeOnList::getPkey).collect(Collectors.toList());
        if (!gtypeList.isEmpty())
        {
            List<MktGoodsMain> exec = goodsMainDao.select()
                .in("gtype", gtypeList)
                .eq("enabled", true)
                .eq("idDel", false)
                .eq("farmer", CurrentSession.marketPkey())
                .eq("ascription", CurrentSession.ascriptionPkey())
                .exec();
            for (MktGtypeOnList bean : list)
            {
                bean.setGoodsList(new ArrayList<MktGoodsMainSimple>());
                for (MktGoodsMain gm : exec)
                {
                    if (bean.getPkey().equals(gm.getGtype()))
                    {
                        bean.getGoodsList().add(BeanUtil.beanFrom(MktGoodsMainSimple.class, gm));
                    }
                }
            }
        }
    }
    
    public MktGtypeOnList updGtype(Integer pkey, String name, Integer sort, Integer marketSort, Integer pointSort,
        String photo, String remark, Boolean showPoint, Boolean showMarket)
    {
        MktGtype gtype = gtypeDao.get(pkey);
        String gName = gtype.getName();
        gtype.setPhoto(photo);
        if ("优惠券".equals(gName) || "礼券".equals(gName))
        {
            if (pointSort != null) gtype.setPointSort(pointSort);
            MktGtype update = gtypeDao.update(gtype);
            return BeanUtil.beanFrom(MktGtypeOnList.class, update);
        }
        
        if (StringUtils.isNotBlank(name))
        {
            MktGtype exec =
                gtypeDao.selectOne().eq("farmer", CurrentSession.marketPkey()).eq("name", name).eq("ascription", CurrentSession.ascriptionPkey()).exec();
            if (exec != null && !exec.getIdDel() && exec.getPkey().intValue() != pkey.intValue())
                throw TofocusException.of(WsaleErrCode.GOODS_TYPE_NAME_REPEAT);
            if (exec != null && exec.getIdDel())
            {
                exec.setName(name + "_del_" + System.currentTimeMillis());
                gtypeDao.update(exec);
            }
            gtype.setName(name);
        }
        if (StringUtils.isNotBlank(remark)) gtype.setRemark(remark);
        if (sort != null) gtype.setSort(sort);
        if (marketSort != null) gtype.setMarketSort(marketSort);
        if (pointSort != null) gtype.setPointSort(pointSort);
        if (showMarket != null) gtype.setShowMarket(showMarket);
        if (showPoint != null) gtype.setShowPoint(showPoint);
        MktGtype update = null;
        update = gtypeDao.update(gtype);
        // 异步修改 缓存队列
//        appGoodsV4Manager.openThread(CurrentSession.marketPkey(), CurrentSession.ascriptionPkey());
        
        return BeanUtil.beanFrom(MktGtypeOnList.class, update);
    }
    
    public Boolean delGtype(Integer pkey)
    {
        MktGtype gtype = gtypeDao.get(pkey);
        if (gtype.getEnabled()) throw TofocusException.of(WsaleErrCode.NOT_DELETED);
        String name = gtype.getName();
        
        if (CurrentSession.marketPkey().startsWith(Constant.Operation) && ("优惠券".equals(name) || "礼券".equals(name)))
            throw TofocusException.of(WsaleErrCode.GTYPE_NOT_DEL);
        gtype.setIdDel(true);
        gtypeDao.update(gtype);
        goodListQueryer.delGtype(pkey);
        
        List<MktGoodsMain> gmList = goodsMainDao.listSortFalse(pkey, null, gtype.getFarmer(), gtype.getAscription());
        for (MktGoodsMain gm : gmList)
        {
            gm.setEnabled(false);
            gm.setIdDel(true);
        }
        goodsMainDao.updateAll(gmList);
        
        List<MktGoodsMainThree> gmtList =
            goodsMainThreeDao.listGeSort(null, pkey, null, gtype.getFarmer(), gtype.getAscription());
        for (MktGoodsMainThree gmt : gmtList)
        {
            gmt.setEnabled(false);
            gmt.setIdDel(true);
        }
        goodsMainThreeDao.updateAll(gmtList);
        return true;
    }
    
    public Boolean enabledGtype(Integer pkey, Boolean flag)
    {
        MktGtype gtype = gtypeDao.getGtype(pkey);
        gtype.setEnabled(flag);
        MktGtype update = gtypeDao.update(gtype);
        // 异步修改 缓存队列
        //        appGoodsV4Manager.openThread(null, CurrentSession.ascriptionPkey());
        goodListQueryer.switchGtype(pkey, flag);
        return update.getEnabled() == flag;
    }
    
    public List<GtypeDropInfo> dropyGtype(Integer key)
    {
        String name = null;
        if (key.intValue() == 1)
        {
            name = "优惠券";
        }
        if (key.intValue() == 2)
        {
            name = "礼券";
        }
        Integer ascription = CurrentSession.ascriptionPkey();
        List<MktGtypeOnList> list = gtypeDao.select()
            .eq("name", name)
            .eq("ascription", ascription)
            .eq("idDel", false)
            .sort("sort")
            .sort("pkey")
            .execDto(MktGtypeOnList.class);
        assembleGoodsName(list);
        return BeanUtil.beanListFrom(GtypeDropInfo.class, list);
    }
    
    public List<GtypeDropV2Info> dropyGtypeV2(Integer key)
    {
        String name = null;
        if (key.intValue() == 1)
        {
            name = "优惠券";
        }
        if (key.intValue() == 2)
        {
            name = "礼券";
        }
        Integer ascription = CurrentSession.ascriptionPkey();
        String marketPkey = CurrentSession.marketPkey();
        List<GtypeDropV2Info> list = gtypeDao.select()
            .eq("name", name)
            .eq("ascription", ascription)
            .eq("idDel", false)
            .eq("farmer", marketPkey)
            .iF(!marketPkey.startsWith(Constant.Operation))
            .eq("showMarket", true)
            .notEq("name", "优惠券")
            .notEq("name", "礼券")
            .endIf()
            .iF(marketPkey.startsWith(Constant.Operation) && key.intValue() == 0)
            .eq("showPoint", true)
            .notEq("name", "优惠券")
            .notEq("name", "礼券")
            .endIf()
            .sort("sort", false)
            .sort("pkey")
            .execDto(GtypeDropV2Info.class);
        for (GtypeDropV2Info gd : list)
        {
            List<TwoGtypeDrop> twoGtypeList = goodsMainDao.select()
                .eq("gtype", gd.getPkey())
                .eq("enabled", true)
                .eq("farmer", marketPkey)
                .eq("idDel", false)
                .sort("sort", false)
                .eq("ascription", ascription)
                .execDto(TwoGtypeDrop.class);
            for (TwoGtypeDrop tg : twoGtypeList)
            {
                List<ThreeGtypeDropInfo> threeGtypeList = goodsMainThreeDao.select()
                    .eq("twoGtype", tg.getPkey())
                    .eq("farmer", marketPkey)
                    .eq("enabled", true)
                    .eq("idDel", false)
                    .eq("ascription", ascription)
                    .sort("sort", false)
                    .execDto(ThreeGtypeDropInfo.class);
                tg.setThreeGtypeList(threeGtypeList);
            }
            gd.setThreeGtypeList(twoGtypeList);
        }
        return list;
    }

    public List<TwoGtypeDropWithGoods> dropTwoGtypeWithGoods(String farmerPkey)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        String currentFarmer = CurrentSession.marketPkey();
        if (currentFarmer == null)
            throw TofocusException.of(LejiaErrCode.MEMBER_NOT_LOGIN);
        SysFarmer farmer = farmerDao.get(farmerPkey);
        if (farmer == null || !farmer.getAscription().equals(ascription))
            throw TofocusException.of(LejiaErrCode.MARKET_INEXISTENCE);
        // 市场端只能选自己和运营端
        if (!currentFarmer.startsWith(Constant.Operation) && !farmerPkey.startsWith(Constant.Operation)
            && !currentFarmer.equals(farmerPkey))
            throw TofocusException.of(LejiaErrCode.DATA_NOT_ALLOWD, "找不到市场");
        List<TwoGtypeDropWithGoods> list = gtypeDao.select()
            .eq("ascription", ascription)
            .eq("idDel", false)
            .eq("farmer", farmerPkey)
            .iF(!farmerPkey.startsWith(Constant.Operation))
            .eq("showMarket", true)
            .notEq("name", "优惠券")
            .notEq("name", "礼券")
            .endIf()
            .iF(farmerPkey.startsWith(Constant.Operation))
            .eq("showPoint", true)
            .notEq("name", "优惠券")
            .notEq("name", "礼券")
            .endIf()
            .sort("sort", false)
            .sort("pkey")
            .execDto(TwoGtypeDropWithGoods.class);
        // 二级类目
        List<MktGoodsMain> twoList = goodsMainDao.select()
            .eq("enabled", true)
            .eq("farmer", farmerPkey)
            .eq("idDel", false)
            .sort("sort", false)
            .eq("ascription", ascription)
            .execDto(MktGoodsMain.class);
        Map<Integer, List<TwoGtypeDropWithGoods.SecondGtype>> twoMap = new HashMap<>();
        for (MktGoodsMain two : twoList)
        {
            List<TwoGtypeDropWithGoods.SecondGtype> children = twoMap.computeIfAbsent(two.getGtype(), k -> new ArrayList<>());
            TwoGtypeDropWithGoods.SecondGtype line = new TwoGtypeDropWithGoods.SecondGtype();
            line.setPkey(two.getPkey());
            line.setName(two.getName());
            children.add(line);
        }
        // 商品
        List<GoodsWithGoodsMainDTO> goodsList = goodsDao.select()
            .eq(MktGoods.F.ascription, ascription)
            .eq(MktGoods.F.farmer, farmerPkey)
            .eq(MktGoods.F.enabled, true)
            .eq(MktGoods.F.idDel, false)
            .sort(MktGoods.F.sort, true)
            .sort(MktGoods.F.pkey, true)
            .execDto(GoodsWithGoodsMainDTO.class);
        Map<Integer, List<KeyName<Integer>>> goodsMap = new HashMap<>();
        for (GoodsWithGoodsMainDTO goods : goodsList)
        {
            List<KeyName<Integer>> children = goodsMap.computeIfAbsent(goods.getGoodsMain(), k -> new ArrayList<>());
            KeyName<Integer> line = new KeyName<>(goods.getPkey(), goods.getTitle());
            children.add(line);
        }
        // 组装
        for (TwoGtypeDropWithGoods one : list)
        {
            List<TwoGtypeDropWithGoods.SecondGtype> children = twoMap.getOrDefault(one.getPkey(), Collections.EMPTY_LIST);
            for (TwoGtypeDropWithGoods.SecondGtype two : children)
            {
                List<KeyName<Integer>> twoChildren = goodsMap.getOrDefault(two.getPkey(), Collections.EMPTY_LIST);
                two.setChildren(twoChildren);
            }
            one.setChildren(children);
        }
        return list;
    }
    
    public List<DropIntegerDown> dropCardGtype(String farmer)
    {
        Integer ascriptionPkey = CurrentSession.ascriptionPkey();
        String marketPkey = CurrentSession.marketPkey();
        if(marketPkey.startsWith(Constant.Operation) && StringUtils.isBlank(farmer))
            farmer = CurrentSession.marketPkey();
        return gtypeDao.select()
            .eq("idDel", false)
            .eq("farmer", farmer)
            .eq("ascription", ascriptionPkey)
            .sort("sort", true)
            .sort("pkey")
            .execDto(DropIntegerDown.class);
    }
    
}

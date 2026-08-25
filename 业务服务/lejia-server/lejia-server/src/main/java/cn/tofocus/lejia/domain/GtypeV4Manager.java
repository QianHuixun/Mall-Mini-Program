package cn.tofocus.lejia.domain;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.lejia.bean.dto.GtypeCorresponding;
import cn.tofocus.lejia.bean.dto.GtypeV4ExportExcel;
import cn.tofocus.lejia.bean.dto.gtype.GtypeThreeUpdV4Info;
import cn.tofocus.lejia.bean.dto.gtype.GtypeV4OnList;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsMain;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsMainThree;
import cn.tofocus.lejia.bean.entity.market.MktGtype;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.sys.SysAscription;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.enums.DistributionType;
import cn.tofocus.lejia.bean.enums.ExpressType;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.cache.GoodsMainLinshiMap;
import cn.tofocus.lejia.cache.GoodsMainThreeLinshiMap;
import cn.tofocus.lejia.cache.GtypeLinshiMap;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsMainDao;
import cn.tofocus.lejia.dao.goods.MktGoodsMainThreeDao;
import cn.tofocus.lejia.dao.market.MktGtypeDao;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.sys.SysAscriptionDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.domain.app.AppGoodsV4Manager2;
import cn.tofocus.lejia.exception.WsaleErrCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GtypeV4Manager
{
    @Autowired
    private MktGtypeDao gtypeDao;
    
    @Autowired
    private MktGoodsMainDao goodsMainDao;
    
    @Autowired
    private MktGoodsMainThreeDao goodsMainThreeDao;
    
    @Autowired
    private MktVendorDao vendorDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private SysAscriptionDao sysAscriptionDao;
    
    @Autowired
    private GtypeLinshiMap gtypeLinshiMap;
    
    @Autowired
    private GoodsMainLinshiMap goodsMainLinshiMap;
    
    @Autowired
    private GoodsMainThreeLinshiMap goodsMainThreeLinshiMap;
    
    @Autowired
    private SysFarmerDao farmerDao;
    
    @Autowired
    private AppGoodsV4Manager2 appGoodsV4Manager;
    
    @Autowired
    private GoodListQueryer goodListQueryer;
    
    @Autowired
    private MktOrderDao orderDao;
    
    // 新分类,web端获取分类列表
    public List<GtypeV4OnList> listGtypeV4(Boolean enabled, String name)
    {
        long k1 = System.currentTimeMillis();
        Integer ascription = CurrentSession.ascriptionPkey();
        String farmer = CurrentSession.marketPkey();
        List<GtypeV4OnList> res = new ArrayList<>();
        if (StringUtils.isNotBlank(name))
        {
            List<MktGoodsMainThree> threeList = goodsMainThreeDao
                .listGtypeThreeV4OnList(enabled, name, null, null, farmer, ascription, MktGoodsMainThree.class);
            List<MktGoodsMain> twoList =
                goodsMainDao.listGtypeThreeV4OnList(enabled, name, null, farmer, ascription, MktGoodsMain.class);
            List<GtypeV4OnList> oneList =
                gtypeDao.listGtypeThreeV4OnList(enabled, name, farmer, ascription, GtypeV4OnList.class);
            Map<Integer, GtypeV4OnList> oneMap = new HashMap<>();
            Map<Integer, GtypeV4OnList> twoMap = new HashMap<>();
            if(oneList != null)
                oneList.forEach(e -> oneMap.put(e.getPkey(), e));
            if(twoList != null)
            {
                twoList.forEach(e -> {
                    twoMap.put(e.getPkey(), BeanUtil.beanFrom(GtypeV4OnList.class, e));
                    if (oneMap.containsKey(e.getGtype()))
                    {
                        GtypeV4OnList dto = oneMap.get(e.getGtype());
                        if (dto.getGtypeLowerList() == null)
                        {
                            dto.setGtypeLowerList(new ArrayList<>());
                        }
                        GtypeV4OnList gt = BeanUtil.beanFrom(GtypeV4OnList.class, e);
                        gt.setHigherLevelPkey(e.getGtype());
                        if(gt.getSysTwoGtype() != null)
                        {
                            MktGoodsMain sysTwoGtype = goodsMainDao.get(gt.getSysTwoGtype());
                            if(sysTwoGtype != null)
                            {
                                gt.setSysTwoGtypeName(sysTwoGtype.getName());
                            }
                        }
                        dto.getGtypeLowerList().add(gt);
                    }
                    else
                    {
                        MktGtype gtype = gtypeDao.getGtype(e.getGtype());
                        GtypeV4OnList dto = BeanUtil.beanFrom(GtypeV4OnList.class, gtype);
                        List<GtypeV4OnList> gtypeLowerList = new ArrayList<>();
                        GtypeV4OnList gt = BeanUtil.beanFrom(GtypeV4OnList.class, e);
                        gt.setHigherLevelPkey(e.getGtype());
                        if(gt.getSysTwoGtype() != null)
                        {
                            MktGoodsMain sysTwoGtype = goodsMainDao.get(gt.getSysTwoGtype());
                            if(sysTwoGtype != null)
                            {
                                gt.setSysTwoGtypeName(sysTwoGtype.getName());
                            }
                        }
                        gtypeLowerList.add(gt);
                        dto.setGtypeLowerList(gtypeLowerList);
                        oneMap.put(e.getGtype(), dto);
                    }
                });
            }
            
            if(threeList != null)
            {
                threeList.forEach(e -> {
                    if (twoMap.containsKey(e.getTwoGtype()))
                    {
                        GtypeV4OnList twoDto = twoMap.get(e.getTwoGtype());
                        if (twoDto.getGtypeLowerList() == null)
                        {
                            List<GtypeV4OnList> gtypeLowerList = new ArrayList<>();
                            twoDto.setGtypeLowerList(gtypeLowerList);
                        }
                        GtypeV4OnList gtt = BeanUtil.beanFrom(GtypeV4OnList.class, e);
                        gtt.setHigherLevelPkey(e.getTwoGtype());
                        gtt.setGtype(e.getGtype());
                        twoDto.getGtypeLowerList().add(gtt);
                    }
                    else
                    {
                        if (oneMap.containsKey(e.getGtype()))
                        {
                            MktGoodsMain goodsMain = goodsMainDao.getGoodsMain(e.getTwoGtype());
                            GtypeV4OnList twoDto = BeanUtil.beanFrom(GtypeV4OnList.class, goodsMain);
                            twoDto.setHigherLevelPkey(e.getGtype());
                            List<GtypeV4OnList> gtypeLowerList = new ArrayList<>();
                            GtypeV4OnList gtt = BeanUtil.beanFrom(GtypeV4OnList.class, e);
                            gtt.setHigherLevelPkey(e.getTwoGtype());
                            gtt.setGtype(e.getGtype());
                            gtypeLowerList.add(gtt);
                            twoDto.setGtypeLowerList(gtypeLowerList);
                            oneMap.get(e.getGtype()).getGtypeLowerList().add(twoDto);
                            twoMap.put(e.getTwoGtype(), twoDto);
                        }
                        else
                        {
                            MktGtype gtype = gtypeDao.getGtype(e.getGtype());
                            GtypeV4OnList dto = BeanUtil.beanFrom(GtypeV4OnList.class, gtype);
                            MktGoodsMain goodsMain = goodsMainDao.getGoodsMain(e.getTwoGtype());
                            GtypeV4OnList twoDto = BeanUtil.beanFrom(GtypeV4OnList.class, goodsMain);
                            twoDto.setHigherLevelPkey(e.getGtype());
                            List<GtypeV4OnList> gtypeLowerList = new ArrayList<>();
                            GtypeV4OnList gtt = BeanUtil.beanFrom(GtypeV4OnList.class, e);
                            gtt.setHigherLevelPkey(e.getTwoGtype());
                            gtt.setGtype(e.getGtype());
                            gtypeLowerList.add(gtt);
                            twoDto.setGtypeLowerList(gtypeLowerList);
                            
                            List<GtypeV4OnList> gtypeLowerTList = new ArrayList<>();
                            gtypeLowerTList.add(twoDto);
                            dto.setGtypeLowerList(gtypeLowerTList);
                            
                            oneMap.put(e.getGtype(), dto);
                            twoMap.put(e.getTwoGtype(), twoDto);
                        }
                    }
                });
            }
            
            res.addAll(oneMap.values());
            for(GtypeV4OnList g : res)
            {
                g.setLevel(1);
                if(g.getGtypeLowerList() != null)
                {
                    for (GtypeV4OnList gt : g.getGtypeLowerList())
                    {
                        gt.setLevel(2);
                        if(gt.getSysTwoGtype() != null)
                        {
                            MktGoodsMain sysTwoGtype = goodsMainDao.get(gt.getSysTwoGtype());
                            if(sysTwoGtype != null)
                            {
                                gt.setSysTwoGtypeName(sysTwoGtype.getName());
                            }
                        }
                        if(gt.getGtypeLowerList() != null)
                        {
                            for (GtypeV4OnList gtt : gt.getGtypeLowerList())
                            {
                                gtt.setLevel(3);
                            }
                        }
                    }
                }
            }
        }
        else
        {
            res = gtypeDao.listGtypeThreeV4OnList(enabled, name, farmer, ascription, GtypeV4OnList.class);
            for (GtypeV4OnList g : res)
            {
                g.setLevel(1);
                List<GtypeV4OnList> list = goodsMainDao
                    .listGtypeThreeV4OnList(enabled, name, g.getPkey(), farmer, ascription, GtypeV4OnList.class);
                for (GtypeV4OnList gt : list)
                {
                    gt.setLevel(2);
                    gt.setHigherLevelPkey(g.getPkey());
                    if(gt.getSysTwoGtype() != null)
                    {
                        MktGoodsMain sysTwoGtype = goodsMainDao.get(gt.getSysTwoGtype());
                        if(sysTwoGtype != null)
                        {
                            gt.setSysTwoGtypeName(sysTwoGtype.getName());
                        }
                    }
                    List<GtypeV4OnList> threeList = goodsMainThreeDao.listGtypeThreeV4OnList(enabled,
                        name,
                        g.getPkey(),
                        gt.getPkey(),
                        farmer,
                        ascription,
                        GtypeV4OnList.class);
                    for (GtypeV4OnList gtt : threeList)
                    {
                        gtt.setHigherLevelPkey(gt.getPkey());
                        gtt.setGtype(gt.getGtype());
                        gtt.setLevel(3);
                    }
                    gt.setGtypeLowerList(threeList);
                }
                g.setGtypeLowerList(list);
            }
        }
        
        long k2 = System.currentTimeMillis();
        System.out.println("耗时: " + (k2 - k1) / 1000);
        return res;
    }
    
    // 新增或编辑 分类
    public Boolean putGtype(GtypeThreeUpdV4Info info)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        String marketPkey = CurrentSession.marketPkey();
        // 一级分类
        if (info.getGtype() == null && info.getGtypeTwo() == null)
        {
            MktGtype gtype = new MktGtype();
            if (info.getPkey() == null)
            {
                if (Boolean.TRUE.equals(gtypeDao.checkGtypeName(info.getPkey(), info.getName(), marketPkey, ascription)))
                    throw TofocusException.of(WsaleErrCode.GOODS_TYPE_NAME_REPEAT);
                Integer ySort = gtypeDao.getSort(info.getSort(), marketPkey, ascription);
                updGtypeSort(info.getSort(), ySort, marketPkey, ascription);
                gtype.setShowMarket(true);
                gtype.setShowPoint(true);
                gtype.setMarketSort(0);
                gtype.setPointSort(0);
                gtype.setIdDel(false);
                gtype.setEnabled(true);
                gtype.setFarmer(marketPkey);
                gtype.setAscription(ascription);
                gtype.setRowVension(4);
            }
            else
            {
                gtype = gtypeDao.getGtype(info.getPkey());
                // 名字不同时,更新
                if (!gtype.getName().equals(info.getName())
                    && Boolean.TRUE.equals(gtypeDao.checkGtypeName(info.getPkey(), info.getName(), marketPkey, ascription)))
                    throw TofocusException.of(WsaleErrCode.GOODS_TYPE_NAME_REPEAT);
                // 排序不同时,更新
                if (!gtype.getSort().equals(info.getSort()))
                    updGtypeSort(info.getSort(), gtype.getSort(), marketPkey, ascription);
            }
            gtype.setName(info.getName());
            gtype.setSort(info.getSort());
            if(info.getSort() == null)
            {
                Integer maxSort = gtypeDao.maxSort(marketPkey, ascription);
                gtype.setSort(maxSort + 1);
            }
            gtype.setPhoto(info.getPhoto());
            gtypeDao.put(gtype);
        }
        // 二级分类
        if (info.getGtype() != null && info.getGtypeTwo() == null)
        {
            checkGtypeExistence(info.getGtype());
            MktGoodsMain goodsMain = new MktGoodsMain();
            // 判断是否名字重复
            if (Boolean.TRUE
                .equals(goodsMainDao.checkGtypeName(info.getPkey(), info.getName(), info.getGtype(), marketPkey, ascription)))
                throw TofocusException.of(WsaleErrCode.GOODS_TYPE_NAME_REPEAT);
            if (info.getPkey() == null)
            {
                Integer ySort = goodsMainDao.getSort(info.getSort(), info.getGtype(), marketPkey, ascription);
                if(info.getSort() != null)
                    updGtypeTwoSort(info.getSort(), ySort, info.getGtype(), marketPkey, ascription);
                goodsMain.setIdDel(false);
                goodsMain.setEnabled(true);
                goodsMain.setFarmer(marketPkey);
                goodsMain.setAscription(ascription);
                goodsMain.setRowVension(4);
            }
            else
            {
                goodsMain = goodsMainDao.getGoodsMain(info.getPkey());
                // 排序不同时,更新
                if (!goodsMain.getSort().equals(info.getSort()) || !goodsMain.getGtype().equals(info.getGtype()))
                    updGtypeTwoSort(info.getSort(), goodsMain.getSort(), info.getGtype(), marketPkey, ascription);
            }
            // 2026-01-05 新增 增加运营端关联
            goodsMain.setSysTwoGtype(info.getSysTwoGtype());
            goodsMain.setGtype(info.getGtype());
            goodsMain.setName(info.getName());
            goodsMain.setSort(info.getSort());
            if(info.getSort() == null)
            {
                Integer maxSort = goodsMainDao.maxSort(info.getGtype(), marketPkey, ascription);
                goodsMain.setSort(maxSort + 1);
            }
            goodsMainDao.put(goodsMain);
        }
        // 三级分类
        if (info.getGtype() != null && info.getGtypeTwo() != null)
        {
            checkGtypeTwoExistence(info.getGtype(), info.getGtypeTwo());
            MktGoodsMainThree goodsMainThree = new MktGoodsMainThree();
            // 判断是否名字重复
            if (Boolean.TRUE.equals(goodsMainThreeDao
                .checkGtypeName(info.getPkey(), info.getName(), info.getGtype(), info.getGtypeTwo(), marketPkey, ascription)))
                throw TofocusException.of(WsaleErrCode.GOODS_TYPE_NAME_REPEAT);
            if (info.getPkey() == null)
            {
                Integer ySort = goodsMainThreeDao
                    .getSort(info.getSort(), info.getGtype(), info.getGtypeTwo(), marketPkey, ascription);
                updGtypeThreeSort(info.getSort(), ySort, info.getGtype(), info.getGtypeTwo(), marketPkey, ascription);
                goodsMainThree.setIdDel(false);
                goodsMainThree.setEnabled(true);
                goodsMainThree.setFarmer(marketPkey);
                goodsMainThree.setAscription(ascription);
                goodsMainThree.setRowVension(4);
            }
            else
            {
                goodsMainThree = goodsMainThreeDao.getGoodsMain(info.getPkey());
                // 排序不同时,更新
                if (!goodsMainThree.getSort().equals(info.getSort())
                    || !goodsMainThree.getGtype().equals(info.getGtype())
                    || !goodsMainThree.getTwoGtype().equals(info.getGtypeTwo()))
                    updGtypeThreeSort(info.getSort(),
                        goodsMainThree.getSort(),
                        info.getGtype(),
                        info.getGtypeTwo(),
                        marketPkey,
                        ascription);
            }
            goodsMainThree.setGtype(info.getGtype());
            goodsMainThree.setTwoGtype(info.getGtypeTwo());
            goodsMainThree.setName(info.getName());
            goodsMainThree.setSort(info.getSort());
            if(info.getSort() == null) 
            {
                Integer maxSort = goodsMainThreeDao.maxSort(info.getGtypeTwo(), marketPkey, ascription);
                goodsMainThree.setSort(maxSort + 1);
            }
            goodsMainThreeDao.put(goodsMainThree);
        }
        // 异步修改 缓存队列
//        appGoodsV4Manager.openThread(CurrentSession.marketPkey(), null);
        return true;
    }
    
//    public Boolean dragGtypeSort(Integer pkey, Integer sort, int level)
//    {
//        Integer ascription = CurrentSession.ascriptionPkey();
//        String marketPkey = CurrentSession.marketPkey();
//        if (level == 1)
//        {
//            MktGtype gtype = gtypeDao.get(pkey);
//            updGtypeSort(sort, gtype.getSort(), marketPkey, ascription);
//            gtype.setSort(sort);
//            gtypeDao.update(gtype);
//        }
//        if (level == 2)
//        {
//            MktGoodsMain goodsMain = goodsMainDao.get(pkey);
//            updGtypeTwoSort(sort, goodsMain.getSort(), goodsMain.getGtype(), marketPkey, ascription);
//            goodsMain.setSort(sort);
//            goodsMainDao.update(goodsMain);
//        }
//        if (level == 3)
//        {
//            MktGoodsMainThree goodsMainThree = goodsMainThreeDao.get(pkey);
//            updGtypeThreeSort(sort,
//                goodsMainThree.getSort(),
//                goodsMainThree.getGtype(),
//                goodsMainThree.getTwoGtype(),
//                marketPkey,
//                ascription);
//            goodsMainThree.setSort(sort);
//            goodsMainThreeDao.update(goodsMainThree);
//        }
//        return true;
//    }
    
    // 前面一个分类的主键 agoPkey
    public Boolean dragGtypeSortAgo(Integer pkey, Integer agoPkey, Integer afterPkey, int level)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        String marketPkey = CurrentSession.marketPkey();
        if (level == 1)
        {
            MktGtype gtype = gtypeDao.get(pkey);
            Integer sort = null;
            if(agoPkey != null)
            {
                MktGtype mktGtype = gtypeDao.get(agoPkey);
                sort = mktGtype.getSort();
                if(sort > gtype.getSort())
                {
                    // 2移到5前面 2改成4
                    sort = sort - 1;
                }
            }
            if(afterPkey != null)
            {
                MktGtype mktGtype = gtypeDao.get(afterPkey);
                sort = mktGtype.getSort();
                if(gtype.getSort() > sort)
                {
                    // 5移到2后面
                    sort = sort + 1;
                }
            }
            updGtypeSort(sort, gtype.getSort(), marketPkey, ascription);
            gtype.setSort(sort);
            gtypeDao.update(gtype);
            goodListQueryer.updateGtypeSort(gtype.getPkey(), gtype.getSort());
        }
        if (level == 2)
        {
            Integer sort = null;
            if(agoPkey != null)
            {
                MktGoodsMain goodsMain = goodsMainDao.get(agoPkey);
                sort = goodsMain.getSort();
                if(sort > goodsMain.getSort())
                {
                    sort = sort - 1;
                }
            }
            if(afterPkey != null)
            {
                MktGoodsMain goodsMain = goodsMainDao.get(afterPkey);
                sort = goodsMain.getSort();
                if(goodsMain.getSort() > sort)
                {
                    sort = sort + 1;
                }
            }
   
            
            MktGoodsMain goodsMain = goodsMainDao.get(pkey);
            updGtypeTwoSort(sort, goodsMain.getSort(), goodsMain.getGtype(), marketPkey, ascription);
            goodsMain.setSort(sort);
            goodsMainDao.update(goodsMain);
            goodListQueryer.updateGoodsMainSort(goodsMain.getPkey(), goodsMain.getSort());
        }
        if (level == 3)
        {
            Integer sort = null;
            if(agoPkey != null)
            {
                MktGoodsMainThree goodsMainThree = goodsMainThreeDao.get(agoPkey);
                sort = goodsMainThree.getSort();
                if(sort != null && sort > goodsMainThree.getSort())
                {
                    sort = sort - 1;
                }
            }
            if(afterPkey != null)
            {
                MktGoodsMainThree goodsMainThree = goodsMainThreeDao.get(afterPkey);
                sort = goodsMainThree.getSort();
                if(sort != null && goodsMainThree.getSort() > sort)
                {
                    sort = sort + 1;
                }
            }
           
            MktGoodsMainThree goodsMainThree = goodsMainThreeDao.get(pkey);
            updGtypeThreeSort(sort,
                goodsMainThree.getSort(),
                goodsMainThree.getGtype(),
                goodsMainThree.getTwoGtype(),
                marketPkey,
                ascription);
            goodsMainThree.setSort(sort);
            goodsMainThreeDao.update(goodsMainThree);
            goodListQueryer.updateThreeGtypeSort(goodsMainThree.getPkey(), goodsMainThree.getSort());
        }
        // 异步修改 缓存队列
//        appGoodsV4Manager.openThread(CurrentSession.marketPkey(), null);
        return true;
    }
    
    private void updGtypeSort(Integer sort, Integer ySort, String farmer, Integer ascription)
    {
        List<MktGtype> listGeSort;
//            gtypeDao.listGeSort(sort, ySort, farmer, ascription);
        if (ySort == null || sort == null) return;
        if (sort < ySort)
        {
            listGeSort = gtypeDao.listGeSort(sort, ySort, farmer, ascription);
            for (MktGtype g : listGeSort)
            {
                if (g.getSort() != null)
                {
                    g.setSort(g.getSort() + 1);
                }
            }
        }
        else
        {
            listGeSort = gtypeDao.listGeSort(ySort, sort, farmer, ascription);
            for (MktGtype g : listGeSort)
            {
                if (g.getSort() != null)
                {
                    g.setSort(g.getSort() - 1);
                }
            }
        }
        gtypeDao.updateAll(listGeSort);
        for (MktGtype g : listGeSort)
        {
            goodListQueryer.updateGtypeSort(g.getPkey(), g.getSort());
        }
        
    }
    
    private void updGtypeTwoSort(Integer sort, Integer ySort, Integer gtype, String farmer, Integer ascription)
    {
        List<MktGoodsMain> listGeSort = new ArrayList<>();
        if (ySort == null || sort == null) return;
        if (sort < ySort)
        {
            listGeSort = goodsMainDao.listGeSort(sort, gtype, farmer, ascription);
            for (MktGoodsMain g : listGeSort)
            {
                if (g.getSort() != null)
                {
                    g.setSort(g.getSort() + 1);
                }
            }
        }
        else
        {
            listGeSort = goodsMainDao.listGeSort(ySort, gtype, farmer, ascription);
            for (MktGoodsMain g : listGeSort)
            {
                if (g.getSort() != null)
                {
                    g.setSort(g.getSort() - 1);
                }
            }
        }
        goodsMainDao.updateAll(listGeSort);
        for (MktGoodsMain g : listGeSort)
        {
            goodListQueryer.updateGoodsMainSort(g.getPkey(), g.getSort());
        }
    }
    
    private void updGtypeThreeSort(Integer sort, Integer ySort, Integer gtype, Integer gtypeTwo, String farmer,
        Integer ascription)
    {
        List<MktGoodsMainThree> listGeSort = new ArrayList<>();
        if (ySort == null || sort == null) return;
        if (sort < ySort)
        {
            listGeSort = goodsMainThreeDao.listGeSort(sort, gtype, gtypeTwo, farmer, ascription);
            for (MktGoodsMainThree g : listGeSort)
            {
                if (g.getSort() != null)
                {
                    g.setSort(g.getSort() + 1);
                }
            }
        }
        else
        {
            listGeSort = goodsMainThreeDao.listGeSort(ySort, gtype, gtypeTwo, farmer, ascription);
            for (MktGoodsMainThree g : listGeSort)
            {
                if (g.getSort() != null)
                {
                    g.setSort(g.getSort() - 1);
                }
            }
        }
        goodsMainThreeDao.updateAll(listGeSort);
        for (MktGoodsMainThree g : listGeSort)
        {
            goodListQueryer.updateThreeGtypeSort(g.getPkey(), g.getSort());
        }
    }
    
    private void checkGtypeExistence(Integer pkey)
    {
        MktGtype gtype = gtypeDao.getGtype(pkey);
        if (gtype == null) throw TofocusException.of(WsaleErrCode.GTYPE_CORRECT);
    }
    
    private void checkGtypeTwoExistence(Integer pkey, Integer pkeyTwo)
    {
        checkGtypeExistence(pkey);
        MktGoodsMain goodsMain = goodsMainDao.getGoodsMain(pkeyTwo);
        if (goodsMain == null) throw TofocusException.of(WsaleErrCode.GOODSMAIN_CORRECT);
    }
    
    public void oldDataMigrate()
    {
        long k1 = System.currentTimeMillis();
        List<SysAscription> list = sysAscriptionDao.findAll();
        for (SysAscription sa : list)
        {
            log.info("当前ascription是: {}", sa.getPkey());
            assembleGtypeLinshiMap(sa.getPkey());
        }
        long k2 = System.currentTimeMillis();
        System.out.println("耗时: " + (k2 - k1) / 1000);
    }
    
    private void assembleGtypeLinshiMap(Integer ascription)
    {
        List<MktGtype> gtypeList = gtypeDao.select().eq("ascription", ascription).eq("idDel", false).exec();
        log.info("原gtypeList数量: {}", gtypeList.size());
        Map<String, Integer> gtypeMap = new HashMap<>();
        // 修改原分类的市场归属
        for (MktGtype g : gtypeList)
        {
            g.setFarmer(Constant.Operation + g.getAscription());
            gtypeMap.put(g.getName(), g.getPkey());
        }
        gtypeDao.updateAll(gtypeList);
        List<MktGtype> addGtypeList = new ArrayList<>();
        List<SysFarmer> farmerList =
            farmerDao.select().like("pkey", "zy_mkt_").eq("ascription", ascription).eq("idDel", false).exec();
        for (SysFarmer f : farmerList)
        {
            for (MktGtype g : gtypeList)
            {
                MktGtype dto = new MktGtype();
                BeanUtils.copyProperties(g, dto);
                dto.setPkey(null);
                dto.setFarmer(f.getPkey());
                dto.setSort(dto.getMarketSort());
                addGtypeList.add(dto);
            }
        }
        List<MktGtype> addAll = gtypeDao.addAll(addGtypeList);
        log.info("新增addAll数量: {}", addAll.size());
        for (MktGtype add : addAll)
        {
            GtypeCorresponding gc = new GtypeCorresponding();
            gc.setAscription(add.getAscription());
            gc.setGtype(add.getPkey());
            gc.setFarmer(add.getFarmer());
            Integer yGtype = gtypeMap.get(add.getName());
            gc.setYGtype(yGtype);
            // 原先一级分类的主键 加 市场主键
            String key = yGtype + "_" + add.getFarmer();
            gtypeLinshiMap.put(key, gc);
        }
        
        // 二级分类处理
        List<MktGoodsMain> goodsMainList = goodsMainDao.select().eq("ascription", ascription).eq("idDel", false).exec();
        Map<String, MktGoodsMain> goodsMainMap = new HashMap<>();
        List<MktGoodsMain> addGoodsMainList = new ArrayList<>();
        // 修改原分类的市场归属
        for (MktGoodsMain g : goodsMainList)
        {
            g.setFarmer(Constant.Operation + g.getAscription());
            goodsMainMap.put(g.getName(), g);
        }
        goodsMainDao.updateAll(goodsMainList);
        for (SysFarmer f : farmerList)
        {
            for (MktGoodsMain g : goodsMainList)
            {
                MktGoodsMain dto = new MktGoodsMain();
                BeanUtils.copyProperties(g, dto);
                dto.setPkey(null);
                dto.setFarmer(f.getPkey());
                // 原先一级分类的主键 加 市场主键
                String key = g.getGtype() + "_" + f.getPkey();
                if (gtypeLinshiMap.containsKey(key))
                {
                    GtypeCorresponding gc = gtypeLinshiMap.get(key);
                    dto.setGtype(gc.getGtype());
                    // 缓存里新增 原二级分类主键
                    gc.setYGoodsMain(g.getPkey());
                    // 原goodsMain主键 加 市场主键
                    goodsMainLinshiMap.put(g.getPkey() + "_" + f.getPkey(), gc);
                    addGoodsMainList.add(dto);
                }
                else
                    log.info("MktGoodsMain-gtypeLinshiMap不存在的key: " + key);
            }
        }
        List<MktGoodsMain> addGoodsMainAll = goodsMainDao.addAll(addGoodsMainList);
        for (MktGoodsMain g : addGoodsMainAll)
        {
            MktGoodsMain goodsMain = goodsMainMap.get(g.getName());
            // 原goodsMain主键 加 市场主键
            String key = goodsMain.getPkey() + "_" + g.getFarmer();
            if (goodsMainLinshiMap.containsKey(key))
            {
                GtypeCorresponding gc = goodsMainLinshiMap.get(key);
                gc.setGoodsMain(g.getPkey());
                goodsMainLinshiMap.put(key, gc);
            }
            else
                log.info("MktGoodsMain-gtypeLinshiMap不存在的key: " + key);
        }
        
        // 三级分类处理
        List<MktGoodsMainThree> goodsMainThreeList =
            goodsMainThreeDao.select().eq("ascription", ascription).eq("idDel", false).exec();
        List<MktGoodsMainThree> addGoodsMainThreeList = new ArrayList<>();
        Map<String, Integer> goodsMainThreeMap = new HashMap<>();
        // 修改原分类的市场归属
        for (MktGoodsMainThree g : goodsMainThreeList)
        {
            g.setFarmer(Constant.Operation + g.getAscription());
            goodsMainThreeMap.put(g.getName(), g.getPkey());
        }
        goodsMainThreeDao.updateAll(goodsMainThreeList);
        for (SysFarmer f : farmerList)
        {
            for (MktGoodsMainThree g : goodsMainThreeList)
            {
                MktGoodsMainThree dto = new MktGoodsMainThree();
                BeanUtils.copyProperties(g, dto);
                dto.setPkey(null);
                dto.setFarmer(f.getPkey());
                // 原goodsMain主键 加 市场主键
                String key = g.getTwoGtype() + "_" + f.getPkey();
                if (goodsMainLinshiMap.containsKey(key))
                {
                    GtypeCorresponding gc = goodsMainLinshiMap.get(key);
                    dto.setGtype(gc.getGtype());
                    dto.setTwoGtype(gc.getGoodsMain());
                    gc.setYThreeGtype(g.getPkey());
                    // 原goodsMainThree主键 加 市场主键
                    goodsMainThreeLinshiMap.put(g.getPkey() + "_" + f.getPkey(), gc);
                }
                else
                    log.info("goodsMainLinshiMap查key 不存在: {}", key);
                addGoodsMainThreeList.add(dto);
            }
        }
        List<MktGoodsMainThree> addGoodsMainThreeAll = goodsMainThreeDao.addAll(addGoodsMainThreeList);
        for (MktGoodsMainThree g : addGoodsMainThreeAll)
        {
            Integer yThreeGtype = goodsMainThreeMap.get(g.getName());
            // 原goodsMainThree主键 加 市场主键
            String key = yThreeGtype + "_" + g.getFarmer();
            if (goodsMainThreeLinshiMap.containsKey(key))
            {
                GtypeCorresponding gc = goodsMainThreeLinshiMap.get(key);
                gc.setThreeGtype(g.getPkey());
                goodsMainThreeLinshiMap.put(key, gc);
            }
        }
    }
    
    // 商品和商户 切换对应分类
    public void changeGtype()
    {
        long k1 = System.currentTimeMillis();
        // 处理商品
        List<MktGoods> goodsList = goodsDao.select()
            .notEq("mType", MType.INTEGRAL_GOODS)
            .notEq("mType", MType.GIFT_GOODS)
            .notEq("mType", MType.COUPON_GOODS)
            .eq("idDel", false)
            .exec();
        for (MktGoods g : goodsList)
        {
            String threeGtype = g.getThreeGtype() + "_" + g.getFarmer();
            if (goodsMainThreeLinshiMap.containsKey(threeGtype))
            {
                GtypeCorresponding gc = goodsMainThreeLinshiMap.get(threeGtype);
                g.setThreeGtype(gc.getThreeGtype());
                g.setGoodsMain(gc.getGoodsMain());
                g.setGtype(gc.getGtype());
            }
            else
            {
                String goodsMain = g.getGoodsMain() + "_" + g.getFarmer();
                if (goodsMainLinshiMap.containsKey(goodsMain))
                {
                    GtypeCorresponding gc = goodsMainLinshiMap.get(goodsMain);
                    g.setGoodsMain(gc.getGoodsMain());
                    g.setGtype(gc.getGtype());
                }
            }
        }
        goodsDao.updateAll(goodsList);
        
        long k2 = System.currentTimeMillis();
        System.out.println("更新商品耗时: " + (k2 - k1) / 1000);
        
        // 处理商户
        List<MktVendor> vendorList = vendorDao.select().isNotNull("businessScope").eq("idDel", false).exec();
        for (MktVendor v : vendorList)
        {
            String businessScope = v.getBusinessScope();
            StringBuilder builder = new StringBuilder();
            String[] scopes = businessScope.split(",");
            for (int i = 0; i < scopes.length; i++)
            {
                try
                {
                    Integer scopePkey = Integer.parseInt(scopes[i]);
                    String key = scopePkey + "_" + v.getFarmer();
                    if (gtypeLinshiMap.containsKey(key))
                    {
                        if (builder.length() > 0)
                        {
                            builder.append(",");
                        }
                        GtypeCorresponding gc = gtypeLinshiMap.get(key);
                        builder.append(gc.getGtype());
                    }
                }
                // 转换失败处理
                catch (NumberFormatException e)
                {
                    log.info("商户经营范围转换失败");
                }
            }
            v.setBusinessScope(builder.toString());
        }
        vendorDao.updateAll(vendorList);
        long k3 = System.currentTimeMillis();
        System.out.println("更新商户耗时: " + (k3 - k2) / 1000);
    }
    
    // 将所有的排序更新一下
    public void updGtypeAndTwoAndThreeSort(String farmer)
    {
        long k1 = System.currentTimeMillis();
        List<SysFarmer> farmerList =
            farmerDao.select().eq("idDel", false).eq("pkey", farmer).exec();
        for(SysFarmer f : farmerList)
        {
            List<MktGtype> list = gtypeDao.select().eq("idDel", false).eq("farmer", f.getPkey()).sort("sort", false).exec();
            for(int i = 0; i < list.size(); i++)
            {
                MktGtype g = list.get(i);
                g.setSort(i);
            }
            gtypeDao.updateAll(list);
            
            List<MktGoodsMain> twoList = goodsMainDao.select().eq("idDel", false).eq("farmer", f.getPkey()).sort("sort", false).exec();
            Map<Integer, List<MktGoodsMain>> map = new HashMap<>();
            twoList.forEach(e -> {
                if(!map.containsKey(e.getGtype()))
                {
                    List<MktGoodsMain> v = new ArrayList<>();
                    map.put(e.getGtype(), v);
                }
                map.get(e.getGtype()).add(e);
            });
            for(List<MktGoodsMain> vm : map.values())
            {
                for(int i = 0; i < vm.size(); i++)
                {
                    MktGoodsMain gm = vm.get(i);
                    gm.setSort(i);
                }
                goodsMainDao.updateAll(vm);
            }
            
            Map<Integer, List<MktGoodsMainThree>> mapt = new HashMap<>();
            List<MktGoodsMainThree> threeList = goodsMainThreeDao.select().eq("idDel", false).eq("farmer", f.getPkey()).sort("sort", false).exec();
            threeList.forEach(e -> {
                if(!mapt.containsKey(e.getTwoGtype()))
                {
                    List<MktGoodsMainThree> v = new ArrayList<>();
                    mapt.put(e.getTwoGtype(), v);
                }
                mapt.get(e.getTwoGtype()).add(e);
            });
            for(List<MktGoodsMainThree> mt : mapt.values())
            {
                for(int i = 0; i < mt.size(); i++)
                {
                    MktGoodsMainThree gmt = mt.get(i);
                    gmt.setSort(i);
                }
                goodsMainThreeDao.updateAll(mt);
            }
        }
        long k2 = System.currentTimeMillis();
        System.out.println("耗时: " + (k2 - k1) / 1000);
        
     
        
    }
    
    // 导入excel
    public void importExcel(MultipartFile myfile, OutputStream out)
    {
        long k1 = System.currentTimeMillis();
        ExcelReaderBuilder read;
        List<GtypeV4ExportExcel> addList = new ArrayList<>();
        try
        {
            read = EasyExcel.read(myfile.getInputStream());
            read.head(GtypeV4ExportExcel.class);
            read.registerReadListener(new GtypeListener(addList, CurrentSession.ascriptionPkey(), out));
            read.headRowNumber(1);
            read.doReadAll();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        handleImportData(addList);
        long k2 = System.currentTimeMillis();
        System.out.println("更新商品耗时: " + (k2 - k1) / 1000);
    }
    
    private void handleImportData(List<GtypeV4ExportExcel> addList)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        String farmer = CurrentSession.marketPkey();
        
        // k:一级分类名称
        Map<String, MktGtype> gtypeMap = new HashMap<>();
        // k:一级分类名称 + 二级二分名称
        Map<String, MktGoodsMain> gtypeTwoMap = new HashMap<>();
        // k:一级分类名称 + 二级二分名称+三级分类名称
        Map<String, MktGoodsMainThree> gtypeThreeMap = new HashMap<>();
        
        // 一级分类最大的排序号
        Integer gtypeSort = 0;
        // 二级分类最大的排序号 k:一级分类的主键  v:二级分类最大的排序号
        Map<Integer, Integer> gtypeTwoSortMap = new HashMap<>();
        // 三级分类最大的排序后 k:二级分类的主键  v:三级分类最大的排序号
        Map<Integer, Integer> gtypeThreeSortMap = new HashMap<>();
        
        // 数据来源处理
//        assembleData(gtypeMap, gtypeTwoMap, gtypeThreeMap, gtypeSort, gtypeTwoSortMap, gtypeThreeSortMap, farmer, ascription);
        // k:一级分类名称
        gtypeMap = gtypeDao.nameMap(farmer, ascription);
        // k:一级分类名称 + 二级二分名称
        List<MktGoodsMain> listGeSort = goodsMainDao.listGeSort(null, null, farmer, ascription);
        for (MktGoodsMain gm : listGeSort)
        {
            MktGtype mktGtype = gtypeDao.get(gm.getGtype());
            if (mktGtype != null)
            {
                gm.setGtypeName(mktGtype.getName());
                gtypeTwoMap.put(mktGtype.getName() + gm.getName(), gm);
            }
        }
        // k:一级分类名称 + 二级二分名称+三级分类名称
        List<MktGoodsMainThree> threeList = goodsMainThreeDao.listGeSort(null, null, null, farmer, ascription);
        for (MktGoodsMainThree gmt : threeList)
        {
            MktGtype mktGtype = gtypeDao.get(gmt.getGtype());
            MktGoodsMain goodsMain = goodsMainDao.get(gmt.getTwoGtype());
            if (mktGtype != null && goodsMain != null)
            {
                gmt.setGtypeName(mktGtype.getName());
                gtypeThreeMap.put(mktGtype.getName() + goodsMain.getName() + gmt.getName(), gmt);
            }
        }
        // 一级分类最大的排序号
        gtypeSort = gtypeDao.maxSort(farmer, ascription);
        // 二级分类最大的排序号 k:一级分类的主键  v:二级分类最大的排序号
        gtypeTwoSortMap = goodsMainDao.aggMaxSort(farmer, ascription);
        // 三级分类最大的排序后 k:二级分类的主键  v:三级分类最大的排序号
        gtypeThreeSortMap = goodsMainThreeDao.aggMaxSort(farmer, ascription);
        
        
        
        List<MktGoodsMainThree> gmtList = new ArrayList<>();
        for (GtypeV4ExportExcel data : addList)
        {
            String threeKey = data.getGtypeName() + data.getGtypeTwoName() + data.getGtypeThreeName();
            Integer sysTwoGtype = null;
            if(StringUtils.isNotEmpty(data.getSysTwoGtypeName()))
                sysTwoGtype = Integer.valueOf(data.getSysTwoGtypeName());
            if (!gtypeThreeMap.containsKey(threeKey))
            {
                // 三级不存在
                MktGoodsMainThree gt = new MktGoodsMainThree();
                gt.setName(data.getGtypeThreeName());
                gt.setAscription(ascription);
                gt.setFarmer(farmer);
                gt.setIdDel(false);
                gt.setEnabled(true);
                gt.setRowVension(4);
                String kt = data.getGtypeName() + data.getGtypeTwoName();
                if (gtypeTwoMap.containsKey(kt))
                {
                    // 二级存在
                    MktGoodsMain gm = gtypeTwoMap.get(kt);
                    Integer tk = gm.getPkey();
                    gt.setTwoGtype(tk);
                    gt.setGtype(gm.getGtype());
                    if(gm.getSysTwoGtype() == null || !gm.getSysTwoGtype().equals(sysTwoGtype))
                    {
                        gm.setSysTwoGtype(sysTwoGtype);
                        goodsMainDao.update(gm);
                    }
                    if (gtypeThreeSortMap.containsKey(tk))
                    {
                        Integer sort = gtypeThreeSortMap.get(tk) + 1;
                        gt.setSort(sort);
                        gtypeThreeSortMap.put(tk, sort);
                    }
                    else
                    {
                        gt.setSort(1);
                        gtypeThreeSortMap.put(tk, 1);
                    }
                    gmtList.add(gt);
                }
                else
                {
                    gt.setSort(1);
                    //二级不存在
                    MktGoodsMain gm = new MktGoodsMain();
                    gm.setSysTwoGtype(sysTwoGtype);
                    gm.setName(data.getGtypeTwoName());
                    gm.setAscription(ascription);
                    gm.setFarmer(farmer);
                    gm.setIdDel(false);
                    gm.setEnabled(true);
                    gm.setRowVension(4);
                    if (gtypeMap.containsKey(data.getGtypeName()))
                    {
                        // 一级存在
                        MktGtype g = gtypeMap.get(data.getGtypeName());
                        Integer gk = g.getPkey();
                        gt.setGtype(g.getPkey());
                        gm.setGtype(g.getPkey());
                        if (gtypeTwoSortMap.containsKey(gk))
                        {
                            Integer sort = gtypeTwoSortMap.get(gk) + 1;
                            gm.setSort(sort);
                            gtypeTwoSortMap.put(gk, sort);
                        }
                        else
                        {
                            gm.setSort(1);
                            gtypeTwoSortMap.put(gk, 1);
                        }
                    }
                    else
                    {
                        gm.setSort(1);
                        MktGtype g = new MktGtype();
                        g.setName(data.getGtypeName());
                        g.setAscription(ascription);
                        g.setFarmer(farmer);
                        g.setIdDel(false);
                        g.setMarketSort(0);
                        g.setPointSort(0);
                        g.setShowMarket(true);
                        g.setShowPoint(false);
                        g.setEnabled(true);
                        g.setRowVension(4);
                        g.setSort(++gtypeSort);
                        MktGtype add = gtypeDao.add(g);
                        gtypeMap.put(data.getGtypeName(), add);
                        gm.setGtype(add.getPkey());
                        gt.setGtype(add.getPkey());
                    }
                    MktGoodsMain add = goodsMainDao.add(gm);
                    gtypeTwoMap.put(kt, add);
                    gt.setTwoGtype(add.getPkey());
                    gmtList.add(gt);
                }
            }
            else
            {
                String kt = data.getGtypeName() + data.getGtypeTwoName();
                if (gtypeTwoMap.containsKey(kt))
                {
                    MktGoodsMain gm = gtypeTwoMap.get(kt);
                    gm.setSysTwoGtype(sysTwoGtype);
                    goodsMainDao.update(gm);
                }
            }
        }
        goodsMainThreeDao.addAll(gmtList);
    }
    
    private void assembleData(Map<String, MktGtype> gtypeMap, Map<String, MktGoodsMain> gtypeTwoMap,
        Map<String, MktGoodsMainThree> gtypeThreeMap, Integer gtypeSort, Map<Integer, Integer> gtypeTwoSortMap,
        Map<Integer, Integer> gtypeThreeSortMap, String farmer, Integer ascription)
    {
        // k:一级分类名称
        gtypeMap = gtypeDao.nameMap(farmer, ascription);
        // k:一级分类名称 + 二级二分名称
        List<MktGoodsMain> listGeSort = goodsMainDao.listGeSort(null, null, farmer, ascription);
        for (MktGoodsMain gm : listGeSort)
        {
            MktGtype mktGtype = gtypeDao.get(gm.getGtype());
            if (mktGtype != null)
            {
                gm.setGtypeName(mktGtype.getName());
                gtypeTwoMap.put(mktGtype.getName() + gm.getName(), gm);
            }
        }
        // k:一级分类名称 + 二级二分名称+三级分类名称
        List<MktGoodsMainThree> threeList = goodsMainThreeDao.listGeSort(null, null, null, farmer, ascription);
        for (MktGoodsMainThree gmt : threeList)
        {
            MktGtype mktGtype = gtypeDao.get(gmt.getGtype());
            MktGoodsMain goodsMain = goodsMainDao.get(gmt.getTwoGtype());
            if (mktGtype != null && goodsMain != null)
            {
                gmt.setGtypeName(mktGtype.getName());
                gtypeThreeMap.put(mktGtype.getName() + goodsMain.getName() + gmt.getName(), gmt);
            }
        }
        // 一级分类最大的排序号
        gtypeSort = gtypeDao.maxSort(farmer, ascription);
        // 二级分类最大的排序号 k:一级分类的主键  v:二级分类最大的排序号
        gtypeTwoSortMap = goodsMainDao.aggMaxSort(farmer, ascription);
        // 三级分类最大的排序后 k:二级分类的主键  v:三级分类最大的排序号
        gtypeThreeSortMap = goodsMainThreeDao.aggMaxSort(farmer, ascription);
    }
    
    class GtypeListener extends AnalysisEventListener<GtypeV4ExportExcel>
    {
        private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        
        public void validator(GtypeV4ExportExcel v)
        {
            try
            {
                Set<ConstraintViolation<GtypeV4ExportExcel>> set = validator.validate(v);
                if (set != null && !set.isEmpty())
                {
                    for (ConstraintViolation<GtypeV4ExportExcel> cv : set)
                    {
                        Field declaredField = v.getClass().getDeclaredField(cv.getPropertyPath().toString());
                        throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_ERROR,
                            declaredField.getName() + cv.getMessage());
                    }
                }
            }
            catch (NoSuchFieldException | SecurityException e)
            {
                throw TofocusException.of(SysErrCode.UNKNOW_INTER_FAIL, e);
            }
        }
        
        GtypeListener(List<GtypeV4ExportExcel> addList, Integer ascription, OutputStream out)
        {
            this.out = out;
            this.addList = addList;
            this.ascription = ascription;
        }
        
        List<GtypeV4ExportExcel> errList = new ArrayList<>();
        
        List<GtypeV4ExportExcel> addList = new ArrayList<>();
        
        ExcelWriterBuilder errBuilder;
        
        OutputStream out;
        
        Integer ascription;
        
        Map<String, Integer> gtypeThreeMap = new HashMap<>();
        
        @Override
        public void invoke(GtypeV4ExportExcel data, AnalysisContext context)
        {
            try
            {
                validator(data);
                
                String gtn = data.getGtypeName() + data.getGtypeTwoName() + data.getGtypeThreeName();
                if (gtypeThreeMap.containsKey(gtn))
                {
                    data.setErrMsg("分类名字重复");
                    errList.add(data);
                }
                if(StringUtils.isNotBlank(data.getSysTwoGtypeName()))
                {
                    MktGoodsMain byNameSys = goodsMainDao.byNameSys(ascription, data.getSysTwoGtypeName());
                    if(byNameSys == null)
                    {
                        data.setErrMsg("平台商品分类不存在");
                        errList.add(data);
                    }
                    else
                    {
                        data.setSysTwoGtypeName(byNameSys.getPkey() + "");
                    }
                }
                gtypeThreeMap.put(gtn, 0);
                addList.add(data);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                if (e instanceof TofocusException)
                {
                    String errmsg = e.getMessage();
                    data.setErrMsg(errmsg);
                    errList.add(data);
                }
            }
        }
        
        @Override
        public void doAfterAllAnalysed(AnalysisContext context)
        {
            if (!errList.isEmpty())
            {
                errBuilder = EasyExcel.write(out, GtypeV4ExportExcel.class);
                ExcelWriter errWriter = errBuilder.build();
                WriteSheet errSheet = EasyExcel.writerSheet("错误数据").build();
                errWriter.write(errList, errSheet);
                errWriter.finish();
            }
        }
    }
    
    // 云商城第8期上线跑批
    public void runOrderExpressType()
    {
        List<MktOrder> list = orderDao.select()
            .notEq("status", OrderStatus.VOID_ORDER)
            .eq("distributionType", DistributionType.IMMEDIATELY)
            .eq("ascription", 8).exec();
        for(MktOrder o : list)
        {
            if(o.getThirdPartyStatus() != null)
                o.setExpressType(ExpressType.WANLI);
            else
                o.setExpressType(ExpressType.COURIER);
        }
        orderDao.updateAll(list);
    }
}

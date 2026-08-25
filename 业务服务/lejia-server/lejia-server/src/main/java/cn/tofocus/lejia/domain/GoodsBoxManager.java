package cn.tofocus.lejia.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.db.redis.lock.RedisLockTemplate;
import cn.tofocus.lejia.bean.dto.market.MktSupplyDetailInfo;
import cn.tofocus.lejia.bean.dto.market.MktSupplyInfo;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsBox;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.bean.entity.goods.MktSpaceKc;
import cn.tofocus.lejia.bean.entity.market.MktGwc;
import cn.tofocus.lejia.bean.entity.market.MktOrderLine;
import cn.tofocus.lejia.bean.entity.market.MktSupply;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.v5.FarmerType;
import cn.tofocus.lejia.cache.SpaceKcCache;
import cn.tofocus.lejia.dao.goods.MktGoodsBoxDao;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsSpaceDao;
import cn.tofocus.lejia.dao.goods.MktSpaceKcDao;
import cn.tofocus.lejia.dao.market.MktGwcDao;
import cn.tofocus.lejia.dao.market.MktOrderLineDao;
import cn.tofocus.lejia.dao.market.MktSupplyDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.domain.market.SupplyManager;
import cn.tofocus.lejia.domain.market.goods.WareManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GoodsBoxManager
{
    @Autowired
    private MktGoodsBoxDao goodsBoxDao;
    
    @Autowired
    private MktGoodsSpaceDao goodsSpaceDao;
    
    @Autowired
    private SpaceKcCache spaceKcCache;
    
    @Autowired
    private MktSpaceKcDao spaceKcDao;
    
    @Autowired
    private RedisLockTemplate lock;
    
    @Autowired
    private WareManager wareManager;
    
    @Autowired
    private SysFarmerDao farmerDao;
    
    @Autowired
    private MktVendorDao vendorDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private SupplyManager supplyManager;
    
    @Autowired
    private MktSupplyDao mktSupplyDao;
    
    @Autowired
    private MktGwcDao gwcDao;
    
    @Autowired
    private MktOrderLineDao orderLineDao;
    
    // 生成7天的规格
    public void runAddGoodsBoxSpace()
    {
        List<MktGoodsBox> list = goodsBoxDao.findAll();
        for (MktGoodsBox gb : list)
        {
            List<MktGoodsSpace> gsList = goodsSpaceDao.select().eq("goods", gb.getGoods()).sort("boxSd", false).exec();
            if (gsList.isEmpty()) continue;
            MktGoodsSpace space = gsList.get(0);
            List<MktGoodsSpace> gsAddList = new ArrayList<>();
            List<MktGoodsSpace> delList = new ArrayList<>();
            if (gsList.size() > 13)
            {
                MktGoodsSpace gs1 = gsList.get(12);
                MktGoodsSpace gs2 = gsList.get(13);
                
                MktGoodsSpace gsc1 = new MktGoodsSpace();
                BeanUtils.copyProperties(gs1, gsc1, "pkey");
                MktGoodsSpace gsc2 = new MktGoodsSpace();
                BeanUtils.copyProperties(gs2, gsc2, "pkey");
                
                Calendar cal = Calendar.getInstance();
                cal.setTime(gs1.getBoxSd());
                cal.add(Calendar.DATE, 1);
                Date time = cal.getTime();
                String formatDate = DateUtil.formatDate(time, "M月d日");
                gsc1.setSpace(formatDate + " 中午场");
                gsc2.setSpace(formatDate + " 晚上场");
                gsc1.setBoxSd(time);
                gsc1.setBoxEd(new Date(gsc1.getBoxEd().getTime() + (1000 * 60 * 60 * 24)));
                
                gsc2.setBoxSd(new Date(gsc2.getBoxSd().getTime() + (1000 * 60 * 60 * 24)));
                gsc2.setBoxEd(new Date(gsc2.getBoxEd().getTime() + (1000 * 60 * 60 * 24)));
                
                if(gb.getNoonPrice() != null)
                {
                    gsc1.setPrice(gb.getNoonPrice());
                }
                if(gb.getNightPrice() != null)
                {
                    gsc2.setPrice(gb.getNightPrice());
                }
                gsAddList.add(gsc1);
                gsAddList.add(gsc2);
                
                delList.add(gsList.get(0));
                delList.add(gsList.get(1));
                goodsSpaceDao.removeAll(delList);
            }
            else
            {
                LocalDate ld = LocalDate.now();
                int year = ld.getYear();
                int month = ld.getMonthValue();
                int day = ld.getDayOfMonth();
                String m = month + "";
                String d = day + "";
                if (month < 10) m = "0" + m;
                if (day < 10) d = "0" + d;
                delList.addAll(gsList);
                goodsSpaceDao.removeAll(gsList);
                for (int i = 0; i < 14; i++)
                {
                    MktGoodsSpace gs = new MktGoodsSpace();
                    BeanUtils.copyProperties(space, gs, "pkey");
                    
                    gs.setKcNum(1);
                    gs.setXsNum(0);
                    Date startDate;
                    Date endDate;
                    if (Boolean.FALSE.equals(i % 2 == 0))
                    {
                        gs.setSpace(month + "月" + day + "日 晚上场");
                        startDate = DateUtil.formatDateStr(year + m + d + "150000", "yyyyMMddHHmmss");
                        endDate = DateUtil.formatDateStr(year + m + d + "210000", "yyyyMMddHHmmss");
                        ld = ld.plusDays(1);
                        year = ld.getYear();
                        month = ld.getMonthValue();
                        day = ld.getDayOfMonth();
                        m = month + "";
                        if (month < 10) m = "0" + m;
                        d = day + "";
                        if (day < 10) d = "0" + d;
                        if(gb.getNightPrice() != null)
                        {
                            gs.setPrice(gb.getNightPrice());
                        }
                    }
                    else
                    {
                        gs.setSpace(month + "月" + day + "日 中午场");
                        startDate = DateUtil.formatDateStr(year + m + d + "100000", "yyyyMMddHHmmss");
                        endDate = DateUtil.formatDateStr(year + m + d + "150000", "yyyyMMddHHmmss");
                        if(gb.getNoonPrice() != null)
                        {
                            gs.setPrice(gb.getNoonPrice());
                        }
                    }
                    gs.setBoxSd(startDate);
                    gs.setBoxEd(endDate);
                    gsAddList.add(gs);
                }
            }
            List<MktGoodsSpace> addAll = goodsSpaceDao.addAll(gsAddList);
            List<MktSpaceKc> kcList = BeanUtil.beanListFrom(MktSpaceKc.class, addAll);
            spaceKcDao.addAll(kcList);
            kcList.forEach(sk -> {
                spaceKcCache.set(String.valueOf(sk.getPkey()), Long.valueOf(sk.getKcNum()));
            });
            updSupply(gb, addAll, delList);
            delGwc(delList);
        }
    }
    
    public Boolean addGoodsBoxSpace(Integer goods)
    {
        MktGoodsBox gb = goodsBoxDao.selectOne().eq("goods", goods).exec();
        if(gb == null)
            return false;
        List<MktGoodsSpace> gsList = goodsSpaceDao.select().eq("goods", gb.getGoods()).sort("boxSd", false).exec();
        if (gsList.isEmpty()) return false;
        MktGoodsSpace space = gsList.get(0);
        List<MktGoodsSpace> gsAddList = new ArrayList<>();
        List<MktGoodsSpace> delList = new ArrayList<>();
        LocalDate ld = LocalDate.now();
        int year = ld.getYear();
        int month = ld.getMonthValue();
        int day = ld.getDayOfMonth();
        String m = month + "";
        String d = day + "";
        if (month < 10) m = "0" + m;
        if (day < 10) d = "0" + d;
        delList.addAll(gsList);
        goodsSpaceDao.removeAll(gsList);
        for (int i = 0; i < 14; i++)
        {
            MktGoodsSpace gs = new MktGoodsSpace();
            BeanUtils.copyProperties(space, gs, "pkey");
            
            gs.setKcNum(1);
            gs.setXsNum(0);
            Date startDate;
            Date endDate;
            if (Boolean.FALSE.equals(i % 2 == 0))
            {
                gs.setSpace(month + "月" + day + "日 晚上场");
                startDate = DateUtil.formatDateStr(year + m + d + "150000", "yyyyMMddHHmmss");
                endDate = DateUtil.formatDateStr(year + m + d + "210000", "yyyyMMddHHmmss");
                ld = ld.plusDays(1);
                year = ld.getYear();
                month = ld.getMonthValue();
                day = ld.getDayOfMonth();
                m = month + "";
                if (month < 10) m = "0" + m;
                d = day + "";
                if (day < 10) d = "0" + d;
                if(gb.getNightPrice() != null)
                {
                    gs.setPrice(gb.getNightPrice());
                }
            }
            else
            {
                gs.setSpace(month + "月" + day + "日 中午场");
                startDate = DateUtil.formatDateStr(year + m + d + "100000", "yyyyMMddHHmmss");
                endDate = DateUtil.formatDateStr(year + m + d + "150000", "yyyyMMddHHmmss");
                if(gb.getNoonPrice() != null)
                {
                    gs.setPrice(gb.getNoonPrice());
                }
            }
            gs.setBoxSd(startDate);
            gs.setBoxEd(endDate);
            gsAddList.add(gs);
        }
        List<MktGoodsSpace> addAll = goodsSpaceDao.addAll(gsAddList);
        List<MktSpaceKc> kcList = BeanUtil.beanListFrom(MktSpaceKc.class, addAll);
        spaceKcDao.addAll(kcList);
        kcList.forEach(sk -> {
            spaceKcCache.set(String.valueOf(sk.getPkey()), Long.valueOf(sk.getKcNum()));
        });
        updSupply(gb, addAll, delList);
        delGwc(delList);
        return true;
    }
    
    private void updSupply(MktGoodsBox gb, List<MktGoodsSpace> gsAddList, List<MktGoodsSpace> delList)
    {
        SysFarmer sysFarmer = farmerDao.get(gb.getFarmer());
        if (FarmerType.VENDOR_SHOPPING_MALL.equals(sysFarmer.getType()) && gb.getGoods() != null)
        {
            MktGoods goods = goodsDao.get(gb.getGoods());
            if (goods != null)
            {
                MktSupplyInfo supply = new MktSupplyInfo();
                supply.setMarketPkey(goods.getFarmer());
                supply.setGoodsPkey(goods.getPkey());
                supply.setMType(MType.BOX_GOODS);
                MktVendor mktVendor = vendorDao.get(goods.getVendor());
                BigDecimal commissionRate = mktVendor.getCommissionRate();
                if (commissionRate == null) commissionRate = BigDecimal.ZERO;
                supply.setCommissionRate2(commissionRate);
                List<MktSupplyDetailInfo> sdList = new ArrayList<>();
                for (MktGoodsSpace gs : gsAddList)
                {
                    MktSupplyDetailInfo sd = new MktSupplyDetailInfo();
                    sd.setSpace(gs.getPkey());
                    sd.setVendor(mktVendor.getPkey());
                    sd.setCommissionRate2(commissionRate);
                    sd.setPurchasingPrice(gs.getPrice());
                    sd.setSort(1);
                    sd.setEnabled(true);
                    sdList.add(sd);
                }
                supply.setList(sdList);
                supplyManager.insertOrUpdateBoxGoodsRun(supply, gb.getFarmer(), gb.getCompany(), gb.getAscription());
            }
            delSupply(delList);
        }
    }
    
    private void delSupply(List<MktGoodsSpace> delList)
    {
        List<Integer> spaceKeys = new ArrayList<>();
        delList.forEach(e -> spaceKeys.add(e.getPkey()));
        if (spaceKeys.isEmpty()) return;
        List<MktSupply> list = mktSupplyDao.select().in("space", spaceKeys).exec();
        mktSupplyDao.removeAll(list);
    }
    
    private void delGwc(List<MktGoodsSpace> delList)
    {
        List<Integer> spaceKeys = new ArrayList<>();
        delList.forEach(e -> spaceKeys.add(e.getPkey()));
        if (spaceKeys.isEmpty()) return;
        List<MktGwc> list = gwcDao.select().in("space", spaceKeys).exec();
        gwcDao.removeAll(list);
    }
    
    public void reduceKcNum()
    {
        List<MktGoodsSpace> list = goodsSpaceDao.select().ge("kcNum", 1).le("boxEd", new Date()).exec();
        for (MktGoodsSpace gs : list)
        {
            updateKu(gs, 1);
        }
    }
    
    private void updateKu(MktGoodsSpace gd, int num)
    {
        try
        {
            lock.lock("zyysc", "zyysc-server", "goodsSpace" + gd.getPkey());// 业务锁
            log.info("失效规格库存处理开始：{} : {}", gd.getPkey(), num);
            gd.setKcNum(gd.getKcNum() - num);
            spaceKcCache.decrement(String.valueOf(gd.getPkey()), num, null);
            wareManager.insWare(gd.getPkey(), num);
            goodsSpaceDao.update(gd);
            log.info("失效规格库存处理结束");
        }
        finally
        {
            lock.unlock("zyysc", "zyysc-server", "goodsSpace" + gd.getPkey());
        }
    }
    
    public void incrementSpaceKc(List<MktOrderLine> list)
    {
        for (MktOrderLine ol : list)
        {
            MktGoodsSpace space = goodsSpaceDao.get(ol.getSpace().intValue());
            updateKu(ol, ol.getNum(), space);
            ol.setStatus(OrderStatus.REFUNDED_ORDER);
        }
        orderLineDao.updateAll(list);
    }
    
    private void updateKu(MktOrderLine ol, int num, MktGoodsSpace gd)
    {
        try
        {
            lock.lock("zyysc", "zyysc-server", "MktOrderLine" + ol.getPkey());// 业务锁
            log.info("同意退款规格库存处理开始：{} : {}", ol.getPkey(), num);
            gd.setKcNum(gd.getKcNum() + num);
            gd.setXsNum(gd.getXsNum() - num);
            spaceKcCache.increment(String.valueOf(ol.getSpace()), num, null);
            
            MktGoods good = goodsDao.get(gd.getGoods());
            good.setXsNum(good.getXsNum() - num);
            goodsDao.update(good);
            goodsSpaceDao.update(gd);
            log.info("同意退款规格库存处理结束");
        }
        finally
        {
            lock.unlock("zyysc", "zyysc-server", "MktOrderLine" + ol.getPkey());
        }
    }
}

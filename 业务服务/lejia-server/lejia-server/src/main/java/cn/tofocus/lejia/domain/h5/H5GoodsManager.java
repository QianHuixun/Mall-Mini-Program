package cn.tofocus.lejia.domain.h5;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.redis.lock.RedisLockTemplate;
import cn.tofocus.lejia.bean.dto.h5.H5GoodsInfo;
import cn.tofocus.lejia.bean.dto.h5.H5GoodsOnPage;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.bean.entity.h5.H5Goods;
import cn.tofocus.lejia.bean.entity.h5.H5GoodsSpace;
import cn.tofocus.lejia.bean.entity.h5.H5User;
import cn.tofocus.lejia.bean.enums.h5.H5Level;
import cn.tofocus.lejia.cache.SpaceKcCache;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsSpaceDao;
import cn.tofocus.lejia.dao.h5.H5GoodsDao;
import cn.tofocus.lejia.dao.h5.H5GoodsSpaceDao;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class H5GoodsManager
{
    @Autowired
    private H5GoodsDao goodsDao;

    @Autowired
    private H5GoodsSpaceDao goodsSpaceDao;
    
    @Autowired
    private RedisLockTemplate lock;
    
    @Autowired
    private MktGoodsDao mktGoodsDao;

    @Autowired
    private MktGoodsSpaceDao mktGoodsSpaceDao;
    
    @Autowired
    private SpaceKcCache spaceKcCache;
    
    // 新增/编辑 包厢商品
    @Transactional(rollbackOn = Throwable.class)
    public Integer putGoods(H5GoodsInfo info)
    {
        H5Goods g = BeanUtil.beanFrom(H5Goods.class, info);
        g.setFarmer(CurrentSession.marketPkey());
        g.setCompany(CurrentSession.companyPkey());
        g.setAscription(CurrentSession.ascriptionPkey());
        g.setEnabled(true);
        g.setIdDel(false);
        if (g.getXsNum() == null)
        {
            g.setXsNum(0);
        }
        if (g.getSort() == null)
        {
            g.setSort(0);
        }
        if(g.getLevelA() == null)
            g.setLevelA(H5Level.PREDETERMINE);
        if(g.getLevelB() == null)
            g.setLevelB(H5Level.PREDETERMINE);
        if(g.getLevelC() == null)
            g.setLevelC(H5Level.PREDETERMINE);
        H5Goods put = goodsDao.put(g);
        List<H5GoodsSpace> list = goodsSpaceDao.byGoods(put.getPkey());
        if(list == null || list.isEmpty())
        {
            list = new ArrayList<>();
            LocalDate ld = LocalDate.now();
            int year = ld.getYear();
            int month = ld.getMonthValue();
            int day = ld.getDayOfMonth();
            String m = month + "";
            String d = day + "";
            if (month < 10) m = "0" + m;
            if (day < 10) d = "0" + d;
            for (int i = 0; i < 14; i++)
            {
                H5GoodsSpace gs = new H5GoodsSpace();
                gs.setAscription(put.getAscription());
                gs.setKcNum(1);
                gs.setXsNum(0);
                gs.setPriceOld(put.getPriceOld());
                gs.setGoods(put.getPkey());
                
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
                    if(put.getNightPrice() != null)
                    {
                        gs.setPrice(put.getNightPrice());
                    }
                }
                else
                {
                    gs.setSpace(month + "月" + day + "日 中午场");
                    startDate = DateUtil.formatDateStr(year + m + d + "100000", "yyyyMMddHHmmss");
                    endDate = DateUtil.formatDateStr(year + m + d + "150000", "yyyyMMddHHmmss");
                    if(put.getNoonPrice() != null)
                    {
                        gs.setPrice(put.getNoonPrice());
                    }
                }
                gs.setBoxSd(startDate);
                gs.setBoxEd(endDate);
                list.add(gs);
            }
            goodsSpaceDao.addAll(list);
        }
        else
        {
            for (int i = 0; i < list.size(); i++)
            {
                H5GoodsSpace gs = list.get(i);
                gs.setPriceOld(put.getPriceOld());
                if (Boolean.FALSE.equals(i % 2 == 0))
                {
                    gs.setPrice(put.getNightPrice());
                }
                else
                {
                    gs.setPrice(put.getNoonPrice());
                }
            }
            goodsSpaceDao.updateAll(list);
        }
        return put.getPkey();
    }

    // 删除包厢商品
    public Boolean delGoods(Integer pkey)
    {
        H5Goods g = goodsDao.get(pkey);
        if(g == null)
            return false;
        g.setIdDel(true);
        goodsDao.update(g);
        return true;
    }
    
    // 设置包厢各个等级
    public Boolean setBoxLevel(Integer goods, H5Level a, H5Level b, H5Level c)
    {
        H5Goods g = goodsDao.get(goods);
        if (g == null) return false;
        g.setLevelA(a);
        g.setLevelB(b);
        g.setLevelC(c);
        goodsDao.update(g);
        return true;
    }
    
    // 获取商品列表
    public PageResult<H5GoodsOnPage> queryGoodsBox(int page, int pagesize, Boolean flag)
    {
        int level = 3;
        if(Boolean.TRUE.equals(flag))
        {
            H5User user = CurrentSession.getH5User();
            if(user != null && user.getLevel() != null)
                level = user.getLevel();
        }
        PageResult<H5GoodsOnPage> res = goodsDao.query(page, pagesize, level, CurrentSession.marketPkey(), CurrentSession.ascriptionPkey());
        for(H5GoodsOnPage g : res.getContent())
        {
            g.setIsBuy(false);
            if(level == 1 && H5Level.PREDETERMINE.equals(g.getLevelA()))
            {
                g.setIsBuy(true);
            }
            if(level == 2 && H5Level.PREDETERMINE.equals(g.getLevelB()))
            {
                g.setIsBuy(true);
            }
            if(level == 3 && H5Level.PREDETERMINE.equals(g.getLevelC()))
            {
                g.setIsBuy(true);
            }
        }
        return res;
    }
    
    // 规格跑批
    public void runAddGoodsSpace()
    {
        List<H5Goods> list = goodsDao.listAll();
        for(H5Goods g : list)
        {
            List<H5GoodsSpace> gsList = goodsSpaceDao.byGoods(g.getPkey());
            List<H5GoodsSpace> gsAddList = new ArrayList<>();
            List<H5GoodsSpace> delList = new ArrayList<>();
            if (gsList.size() > 13)
            {
                H5GoodsSpace gs1 = gsList.get(12);
                H5GoodsSpace gs2 = gsList.get(13);
                
                H5GoodsSpace gsc1 = new H5GoodsSpace();
                BeanUtils.copyProperties(gs1, gsc1, "pkey");
                H5GoodsSpace gsc2 = new H5GoodsSpace();
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
                
                if(g.getNoonPrice() != null)
                {
                    gsc1.setPrice(g.getNoonPrice());
                }
                if(g.getNightPrice() != null)
                {
                    gsc2.setPrice(g.getNightPrice());
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
                    H5GoodsSpace gs = new H5GoodsSpace();
                    gs.setAscription(g.getAscription());
                    gs.setKcNum(1);
                    gs.setXsNum(0);
                    gs.setPriceOld(g.getPriceOld());
                    gs.setGoods(g.getPkey());
                    
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
                        if(g.getNightPrice() != null)
                        {
                            gs.setPrice(g.getNightPrice());
                        }
                    }
                    else
                    {
                        gs.setSpace(month + "月" + day + "日 中午场");
                        startDate = DateUtil.formatDateStr(year + m + d + "100000", "yyyyMMddHHmmss");
                        endDate = DateUtil.formatDateStr(year + m + d + "150000", "yyyyMMddHHmmss");
                        if(g.getNoonPrice() != null)
                        {
                            gs.setPrice(g.getNoonPrice());
                        }
                    }
                    gs.setBoxSd(startDate);
                    gs.setBoxEd(endDate);
                    gsAddList.add(gs);
                }
            }
            goodsSpaceDao.addAll(gsAddList);
        }
    }
    
    public void reduceKcNum()
    {
        List<H5GoodsSpace> list = goodsSpaceDao.select().ge("kcNum", 1).le("boxEd", new Date()).exec();
        for (H5GoodsSpace gs : list)
        {
            updateKu(gs, 1);
        }
    }
    
    @Transactional
    public void updateKu(H5GoodsSpace gd, int num)
    {
        try
        {
            lock.lock("zyysc", "zyysc-server", "h5GoodsSpace" + gd.getPkey());// 业务锁
            log.info("减规格库存处理开始：{} : {}", gd.getPkey(), num);
            gd.setKcNum(gd.getKcNum() - num);
            goodsSpaceDao.update(gd);
            log.info("减规格库存处理结束");
        }
        finally
        {
            lock.unlock("zyysc", "zyysc-server", "h5GoodsSpace" + gd.getPkey());
        }
    }
    
    /*
     * 更新mkt商品库存
     */
    public void updateGooddsKu(int gdPkey, int num)
    {
        try
        {
            lock.lock("zyysc", "zyysc-server", "goodsSpace" + gdPkey);// 业务锁
            log.info("库存处理开始：{} : {}", gdPkey, num);
            MktGoodsSpace gd = mktGoodsSpaceDao.get(gdPkey);
            gd.setKcNum(gd.getKcNum() - num);
            gd.setXsNum(gd.getXsNum() + num);
            mktGoodsSpaceDao.update(gd);
            MktGoods good = mktGoodsDao.get(gd.getGoods());
            good.setXsNum(good.getXsNum() + num);
            mktGoodsDao.update(good);
            spaceKcCache.decrement(String.valueOf(gdPkey), 1, null);
//            wareManager.insWare(gdPkey, num, orderPkey);
            log.info("库存处理结束");
        }
        finally
        {
            lock.unlock("zyysc", "zyysc-server", "goodsSpace" + gdPkey);
        }
    }
}

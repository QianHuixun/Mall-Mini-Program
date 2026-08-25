package cn.tofocus.lejia.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties.Redis;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.stereotype.Component;

import com.esotericsoftware.minlog.Log;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.lejia.bean.dto.data.IndexYFDTO;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.bean.entity.goods.MktSpaceKc;
import cn.tofocus.lejia.bean.entity.market.MktAccessLog;
import cn.tofocus.lejia.bean.entity.market.MktAdvert;
import cn.tofocus.lejia.bean.entity.market.MktGtype;
import cn.tofocus.lejia.cache.SpaceKcCache;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsSpaceDao;
import cn.tofocus.lejia.dao.goods.MktSpaceKcDao;
import cn.tofocus.lejia.dao.market.MktAccessLogDao;
import cn.tofocus.lejia.dao.market.MktAdvertDao;
import cn.tofocus.lejia.dao.market.MktGtypeDao;
import cn.tofocus.lejia.dao.market.MktMemberDao;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class IterateManager
{
    @Autowired
    private MktGtypeDao gtypeDao;
    
    @Autowired
    private MktAdvertDao advertDao;
    
    @Autowired
    private MktAccessLogDao accessLogDao;
    
    @Autowired
    private MktMemberDao memberDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private MktGoodsSpaceDao goodsSpaceDao;
    
    @Autowired
    private MktSpaceKcDao spaceKcDao;
    
    // 不能删除,初始化 有用到
    public void init3_1_0Gtype(Integer ascription)
    {
        MktGtype couponGtype = gtypeDao.getCouponGtype(ascription);
        if(couponGtype == null)
        {
            couponGtype = new MktGtype();
            couponGtype.setName("优惠券");
            couponGtype.setSort(0);
            couponGtype.setMarketSort(0);
            couponGtype.setPointSort(0);
            couponGtype.setShowMarket(false);
            couponGtype.setShowPoint(true);
            couponGtype.setEnabled(true);
            couponGtype.setIdDel(false);
            couponGtype.setRowVension(3);
            couponGtype.setAscription(ascription);
            gtypeDao.add(couponGtype);
        }
        MktGtype giftGtype = gtypeDao.getGiftGtype(ascription);
        if(giftGtype == null)
        {
            couponGtype = new MktGtype();
            couponGtype.setName("礼券");
            couponGtype.setSort(0);
            couponGtype.setMarketSort(0);
            couponGtype.setPointSort(0);
            couponGtype.setShowMarket(false);
            couponGtype.setShowPoint(true);
            couponGtype.setEnabled(true);
            couponGtype.setIdDel(false);
            couponGtype.setRowVension(3);
            couponGtype.setAscription(ascription);
            gtypeDao.add(couponGtype);
        }
        
    }
    
    public void init3_1_0AccessLog()
    {
        List<MktAccessLog> list = accessLogDao.findAll();
        List<String> openids = new ArrayList<>();
        list.forEach(e -> openids.add(e.getOpenid()));
        Map<String, Integer> openidKeyMap = memberDao.getOpenidKeyMap(openids);
        for(MktAccessLog a : list)
        {
            String openid = a.getOpenid();
            if(openidKeyMap.containsKey(openid))
                a.setMember(openidKeyMap.get(openid));
        }
        accessLogDao.updateAll(list);
        
    }
    
    // 2022/04/29
    public void initKc(Integer ascription)
    {
        long k1 = System.currentTimeMillis();
        Map<Integer, MktSpaceKc> mapKc = spaceKcDao.mapKc(ascription);
        List<MktGoodsSpace> list = goodsSpaceDao.select().eq(MktGoodsSpace.F.ascription, ascription).exec();
        for(MktGoodsSpace gs : list)
        {
            MktSpaceKc kc = mapKc.get(gs.getPkey());
            if(kc != null && kc.getKcNum() != gs.getKcNum())
            {
                goodsSpaceDao.updateKcNum(gs.getPkey(), kc.getKcNum());
            }
        }
        log.info("归属[{}]商品规格库存调整耗时： {}",ascription, System.currentTimeMillis() - k1);
    }
    
    public void updFileUrl()
    {
        List<MktAdvert> advertList = advertDao.findAll();
        for(MktAdvert a : advertList)
        {
            String url = a.getPhoto();
            if(StringUtils.isNotBlank(url))
            {
                try
                {
                    String str = advertDao.updateFileUrlV3(a.getPkey(), url, a.getCompany(), a.getFarmer());
                    if(StringUtils.isNotBlank(str))
                        a.setPhoto(str);
                }
                catch (Exception e)
                {
                    log.info("e: ");
                }
            }
        }
        advertDao.updateAll(advertList);
        List<MktGoods> goodsList = goodsDao.findAll();
        log.info("查询商品");
        List<MktGoodsSpace> list = new ArrayList<>();
        for(MktGoods g : goodsList)
        {
            List<String> photo1 = g.getPhoto1();
            if(photo1 != null && !photo1.isEmpty())
            {
                List<String> photo1new = new ArrayList<>();
                for(String url : photo1)
                {
                    String str = goodsDao.updateFileUrlV3(g.getPkey(), url, g.getCompany(), g.getFarmer());
                    if(StringUtils.isNotBlank(str))
                        photo1new.add(str);
                    else
                        photo1new.add(url);
                }
                g.setPhoto1(photo1new);
            }
            List<String> content = g.getContent();
            if(content != null && !content.isEmpty())
            {
                List<String> contentnew = new ArrayList<>();
                for(String url : content)
                {
                    String str = goodsDao.updateFileUrlV3(g.getPkey(), url, g.getCompany(), g.getFarmer());
                    if(StringUtils.isNotBlank(str))
                        contentnew.add(str);
                    else
                        contentnew.add(url);
                }
                g.setContent(contentnew);
            }
            String photo2 = g.getPhoto2();
            if(StringUtils.isNotBlank(photo2))
            {
                photo2 = goodsDao.updateFileUrlV3(g.getPkey(), photo2, g.getCompany(), g.getFarmer());
                g.setPhoto2(photo2);
            }
            String photo3 = g.getPhoto3();
            if(StringUtils.isNotBlank(photo3))
            {
                photo3 = goodsDao.updateFileUrlV3(g.getPkey(), photo3, g.getCompany(), g.getFarmer());
                g.setPhoto3(photo3);
            }
            List<MktGoodsSpace> gsList = goodsSpaceDao.select().eq("goods", g.getPkey()).isNotNull("photo1").exec();
            if(gsList != null && !gsList.isEmpty())
            {
                for(MktGoodsSpace gs : gsList)
                {
                    String url = goodsSpaceDao.updateFileUrlV3(gs.getPkey(), gs.getPhoto1(), g.getCompany(), g.getFarmer());
                    if(StringUtils.isNotBlank(url))
                        gs.setPhoto1(url);
                }
                list.addAll(gsList);
            }
        }
        if(!list.isEmpty())
        {
            log.info("规格图片处理，合计{}张", list.size());
            goodsSpaceDao.updateAll(list);
        }
        goodsDao.updateAll(goodsList);
    }
    
}

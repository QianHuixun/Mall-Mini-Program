package cn.tofocus.lejia.domain.v2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.tofocus.lejia.bean.dto.config.AscriptionGoodsZoneConfig;
import cn.tofocus.lejia.bean.dto.config.JdGoodsZoneConfig;
import cn.tofocus.lejia.bean.dto.v2.gwc.GwcJdSkuOnList;
import cn.tofocus.lejia.dao.sys.SysDynamicAttributeDao;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.lejia.bean.dto.v2.gwc.GwcDetailsV2OnList;
import cn.tofocus.lejia.bean.dto.v2.gwc.GwcGoodsOnList;
import cn.tofocus.lejia.bean.dto.v2.gwc.GwcV2Info;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.bean.entity.goods.MktSpaceKc;
import cn.tofocus.lejia.bean.entity.market.MktGwc;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerConfig;
import cn.tofocus.lejia.bean.enums.LevelType;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsSpaceDao;
import cn.tofocus.lejia.dao.goods.MktSpaceKcDao;
import cn.tofocus.lejia.dao.market.MktGwcDao;
import cn.tofocus.lejia.dao.sys.SysFarmerConfigDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;

@Component
public class GwcV2Manager
{
    @Autowired
    private MktGwcDao gwcDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private MktGoodsSpaceDao goodsSpaceDao;
    
    @Autowired
    private SysFarmerDao farmerDao;
    
    @Autowired
    private SysFarmerConfigDao farmerConfigDao;
    
    @Autowired
    private MktSpaceKcDao spaceKcDao;

    @Autowired
    private SysDynamicAttributeDao dynamicAttributeDao;
    
    public GwcV2Info queryGwc()
    {
        String farmerPkey = MobileSession.farmerPkey();
        GwcV2Info gwc = new GwcV2Info();
        gwc.setCurrentFarmer(queryGwc(farmerPkey));
        Integer appid = MobileSession.appid();
        gwc.setPoints(queryGwc(Constant.Operation + appid));
        gwc.setJdGoodsList(queryJdGwc(Constant.Operation + appid));
        SysFarmerConfig farmerConfig = farmerConfigDao.get(farmerPkey);
        if (farmerConfig != null)
        {
            gwc.setFreeDelivery(farmerConfig.getFreeDelivery());
            gwc.setIsFree(farmerConfig.getIsFree());
            // 获取市场起步价
            gwc.setStartingPrice(farmerConfig.getStartingPrice());
            gwc.setReachOne(farmerConfig.getReachOne());
            gwc.setReachTwo(farmerConfig.getReachTwo());
            gwc.setReductionDeliveryOne(farmerConfig.getReductionDeliveryOne());
            gwc.setReductionDeliveryTwo(farmerConfig.getReductionDeliveryTwo());
            gwc.setIsReductionOne(farmerConfig.getIsReductionOne());
            gwc.setIsReductionTwo(farmerConfig.getIsReductionTwo());
        }

        AscriptionGoodsZoneConfig ascriptionConfig =
            dynamicAttributeDao.getFarmerAttribute(AscriptionGoodsZoneConfig.class, appid, Constant.Operation + appid);
        if (ascriptionConfig == null)
            ascriptionConfig = new AscriptionGoodsZoneConfig();
        gwc.setIntegralMsdDisplayName(ascriptionConfig.getIntegralMsdDisplayName());
        
        JdGoodsZoneConfig jdGoodsZoneConfig = dynamicAttributeDao.getSysAttribute(JdGoodsZoneConfig.class, appid);
        if (jdGoodsZoneConfig == null)
            jdGoodsZoneConfig = new JdGoodsZoneConfig();
        gwc.setJdGoodsDisplayName(jdGoodsZoneConfig.getJdGoodsName());

//        BigDecimal startingPrice = farmerConfigDao.getStartingPrice(farmerPkey);
//        gwc.setStartingPrice(startingPrice);
        return gwc;
    }
    
    private List<GwcGoodsOnList> queryGwc(String farmerPkey)
    {
        List<GwcGoodsOnList> res = new ArrayList<>();
        Map<Integer, List<GwcDetailsV2OnList>> lineMap = new HashMap<>();
        Integer memberPkey = MobileSession.memberPkey();
        LevelType level = MobileSession.member().getLevel();
        List<MktGwc> list = gwcDao.select()
            .eq("member", memberPkey)
            .eq("farmer", farmerPkey)
            .or()
                .eq("isJd", false)
                .isNull("isJd")
            .close()
            .done()
            .sort("createdTime", true)
            .exec();
        if (list.isEmpty())
            return res;
        List<Integer> spkey = new ArrayList<>();
        List<Integer> gkey = new ArrayList<>();
        list.forEach(e -> {
            gkey.add(e.getGoods());
            spkey.add(e.getSpace());
        });
        List<Integer> gkeys = gkey.stream().distinct().collect(Collectors.toList());
        Map<Integer, MktGoodsSpace> spaceMap = goodsSpaceDao.getSpaceMap(spkey);
        Map<Integer, MktSpaceKc> mapKc = spaceKcDao.mapKc(spkey);
        Map<Integer, MktGoods> goodsMap = goodsDao.getGoodsMap(gkeys);
        SysFarmer farmer = farmerDao.get(farmerPkey);
        for (MktGwc gwc : list)
        {
            GwcGoodsOnList bean = BeanUtil.beanFrom(GwcGoodsOnList.class, gwc);
            if (!lineMap.containsKey(gwc.getGoods()))
            {
                List<GwcDetailsV2OnList> lines = new ArrayList<>();
                if (goodsMap.containsKey(bean.getGoods()))
                {
                    MktGoods goods = goodsMap.get(bean.getGoods());
                    bean.setGoodsTitle(goods.getTitle());
                    bean.setGoodsEnabled(goods.getEnabled());
                    bean.setGoodsPurchaseNum(goods.getPurchaseNum());
                    if (goods.getPhoto1() != null && goods.getPhoto1().size() > 0)
                        bean.setPhoto(goods.getPhoto1().get(0));
                    bean.setMType(goods.getMType());
                }
                if (spaceMap.containsKey(bean.getSpace()))
                {
                    MktGoodsSpace space = spaceMap.get(bean.getSpace());
                    bean.setSpaceName(space.getSpace());
                    bean.setKcNum(space.getKcNum());
                    
                    bean.setPrice(space.getPrice());
                    bean.setPriceOld(space.getPriceOld());
                    bean.setPriceMember(space.getPriceMember());
                }
                bean.setFarmerName(farmer.getName());
                lineMap.put(bean.getGoods(), lines);
                res.add(bean);
            }
            else
            {
                if (goodsMap.containsKey(bean.getGoods()))
                {
                    MktGoods goods = goodsMap.get(bean.getGoods());
                    if (goods.getPhoto1() != null && goods.getPhoto1().size() > 0)
                        bean.setPhoto(goods.getPhoto1().get(0));
                }
            }
            
            if(mapKc.containsKey(bean.getSpace()))
            {
                bean.setKcNum(mapKc.get(bean.getSpace()).getKcNum());
            }
            
            GwcDetailsV2OnList line = BeanUtil.beanFrom(GwcDetailsV2OnList.class, bean);
            if (spaceMap.containsKey(bean.getSpace()))
            {
                MktGoodsSpace space = spaceMap.get(bean.getSpace());
                BeanUtils.copyProperties(space, line, "pkey", "kcNum");
                line.setSpace(space.getPkey());
                line.setSpaceName(space.getSpace());
                line.setNum(gwc.getNum());
                if (StringUtils.isBlank(line.getPhoto1())) line.setPhoto1(bean.getPhoto());
            }
            lineMap.get(bean.getGoods()).add(line);
        }
        
        for (GwcGoodsOnList gwc : res)
        {
            if (lineMap.containsKey(gwc.getGoods()))
            {
                gwc.setLines(lineMap.get(gwc.getGoods()));
                BigDecimal sumPrice = BigDecimal.ZERO;
                if (Boolean.FALSE.equals(gwc.getGoodsEnabled())) continue;
                for (GwcDetailsV2OnList g : gwc.getLines())
                {
                    if (level.equals(LevelType.PAID_MEMBER) 
                        && g.getPriceMember() != null
                        && new BigDecimal(g.getPriceMember()).compareTo(BigDecimal.ZERO) == 1)
                    {
                        sumPrice = sumPrice.add(new BigDecimal(g.getPriceMember()).multiply(BigDecimal.valueOf(g.getNum())));
                    }
                    else if(g.getPrice() != null)
                    {
                        sumPrice = sumPrice.add(new BigDecimal(g.getPrice()).multiply(BigDecimal.valueOf(g.getNum())));
                    }
                }
                gwc.setSumPrice(sumPrice);
            }
        }
        
        return res;
    }
    
    private List<GwcJdSkuOnList> queryJdGwc(String farmerPkey)
    {
        Integer memberPkey = MobileSession.memberPkey();
        return gwcDao.select()
            .eq("isJd", true)
            .eq("member", memberPkey)
            .eq("farmer", farmerPkey)
            .sort("createdTime", true)
            .execDto(GwcJdSkuOnList.class);
    }
    
}

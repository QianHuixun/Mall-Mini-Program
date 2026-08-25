package cn.tofocus.lejia.domain.app.v4;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.common.util.PageUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.app.vendor.v4.AppVendorGoodsOnPage;
import cn.tofocus.lejia.bean.dto.app.vendor.v4.AppVendorGoodsSpaceOnList;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.goods.MktGoods.F;
import cn.tofocus.lejia.bean.entity.market.MktWareLine;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.bean.enums.WareType;
import cn.tofocus.lejia.cache.SpaceKcCache;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.bean.entity.goods.MktSpaceKc;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsSpaceDao;
import cn.tofocus.lejia.dao.goods.MktSpaceKcDao;
import cn.tofocus.lejia.dao.market.MktWareLineDao;
import cn.tofocus.lejia.domain.GoodListQueryer;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.exception.WsaleErrCode;

@Component
public class AppGoodsVendorV4Manager
{
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private MktGoodsSpaceDao goodsSpaceDao;
    
    @Autowired
    private SpaceKcCache spaceKcCache;
    
    @Autowired
    private MktSpaceKcDao spaceKcDao;
    
    @Autowired
    private MktWareLineDao wareLineDao;
    
    @Autowired
    private GoodListQueryer goodListQueryer;
    
    public PageResult<AppVendorGoodsOnPage> queryAppVendorGoods(int page, int pagesize, String title, Integer status)
    {
        // 商品状态, 不填返回所有, 1:在售商品, 2:已下架, 3:已售罄
        Integer vendorPkey = MobileSession.vendorPkey();
        Integer appid = MobileSession.appid();
        
        List<MktGoods> list = goodsDao.select()
            .eq(F.ascription, appid)
            .eq(F.vendor, vendorPkey)
            .like(F.title, title)
            .sort(F.sort, true)
            .sort(F.pkey, true)
            .exec();
        List<Integer> keys = CollectionUtil.keyList(list);
        List<MktGoodsSpace> gsList = goodsSpaceDao.select().in("goods", keys).exec();
        Map<Integer, List<AppVendorGoodsSpaceOnList>> map = new HashMap<>();
        gsList.forEach(e -> {
            if (!map.containsKey(e.getGoods()))
            {
                List<AppVendorGoodsSpaceOnList> spaces = new ArrayList<>();
                map.put(e.getGoods(), spaces);
            }
            map.get(e.getGoods()).add(BeanUtil.beanFrom(AppVendorGoodsSpaceOnList.class, e));
        });
        List<AppVendorGoodsOnPage> content = new ArrayList<>();
        for (MktGoods g : list)
        {
            AppVendorGoodsOnPage dto = BeanUtil.beanFrom(AppVendorGoodsOnPage.class, g);
            if (map.containsKey(dto.getPkey()))
            {
                dto.setSpaces(map.get(dto.getPkey()));
                // 最高价格
                setGoodsMaxPrice(dto, dto.getSpaces());
                Boolean flag = false;
                for (AppVendorGoodsSpaceOnList e : dto.getSpaces())
                {
                    if (e.getKcNum() > 0) flag = true;
                }
                if (status != null)
                {
                    
                    switch (status)
                    {
                        case 1:
                            if (Boolean.TRUE.equals(dto.getEnabled()) && Boolean.TRUE.equals(flag))
                            {
                                dto.setStatusName("在售商品");
                                content.add(dto);
                            }
                            break;
                        case 2:
                            if (Boolean.FALSE.equals(dto.getEnabled()) && Boolean.TRUE.equals(flag))
                            {
                                dto.setStatusName("已下架");
                                content.add(dto);
                            }
                            break;
                        case 3:
                            if (Boolean.FALSE.equals(flag))
                            {
                                dto.setStatusName("已售馨");
                                content.add(dto);
                            }
                            break;
                    }
                }
                else
                {
                    if (Boolean.TRUE.equals(dto.getEnabled()))
                    {
                        if(Boolean.TRUE.equals(flag))
                            dto.setStatusName("在售商品");
                        else
                            dto.setStatusName("已售馨");
                    }
                    if (Boolean.FALSE.equals(dto.getEnabled()))
                    {
                        if(Boolean.TRUE.equals(flag))
                            dto.setStatusName("已下架");
                        else
                            dto.setStatusName("已售馨");
                    }
                    content.add(dto);
                }
            }
        }
        return PageUtil.page(content, PageParameter.of(page, pagesize));
    }
    
    private void setGoodsMaxPrice(AppVendorGoodsOnPage goods, List<AppVendorGoodsSpaceOnList> spaces)
    {
        if (spaces.size() > 1)
        {
            Collections.sort(spaces, new Comparator<AppVendorGoodsSpaceOnList>()
            {
                
                @Override
                public int compare(AppVendorGoodsSpaceOnList o1, AppVendorGoodsSpaceOnList o2)
                {
                    BigDecimal i = o1.getPrice().subtract(o2.getPrice());
                    return BigDecimal.ZERO.compareTo(i);
                }
            });
        }
        goods.setMaxPrice(spaces.get(0).getPrice());
        
    }
    
    public Boolean updAppVendorGoods(List<AppVendorGoodsSpaceOnList> spaces)
    {
        List<MktGoodsSpace> updList = new ArrayList<>();
        List<MktWareLine> addWareLineAll = new ArrayList<>();
        MktGoods mktGoods = goodsDao.get(spaces.get(0).getGoods());
        if(mktGoods == null)
            return false;
        for(AppVendorGoodsSpaceOnList dto : spaces)
        {
            MktGoodsSpace space = goodsSpaceDao.get(dto.getPkey());
            if(space == null)
                continue;
            if(!dto.getKcNum().equals(space.getKcNum()))
            {
                MktWareLine add = new MktWareLine();
                add.setWareType(WareType.INVENTORY);
                add.setGoods(mktGoods.getPkey());
                add.setGoodsName(mktGoods.getTitle());
                add.setSpace(space.getPkey());
                add.setSpaceName(space.getSpace());
                add.setNum(dto.getKcNum() - space.getKcNum());
                add.setActualNum(space.getKcNum());
                add.setAscription(MobileSession.appid());
                addWareLineAll.add(add);
                space.setKcNum(dto.getKcNum());
            }
            space.setPriceOld(dto.getPriceOld());
            space.setPrice(dto.getPrice());
            updList.add(space);
        }
        wareLineDao.addAll(addWareLineAll);
        goodsSpaceDao.updateAll(updList);
        List<MktSpaceKc> kcList = BeanUtil.beanListFrom(MktSpaceKc.class, updList);
        spaceKcDao.putAll(kcList);
        kcList.forEach(sk -> spaceKcCache.set(String.valueOf(sk.getPkey()), Long.valueOf(sk.getKcNum())));
        setGoodsMixPrice(mktGoods, updList);
        // 修改三级分类
        goodListQueryer.resetThreeGtype(mktGoods.getThreeGtype(), mktGoods.getFarmer());
        goodListQueryer.resetThreeGtype(mktGoods);
        return true;
    }
    
    private void setGoodsMixPrice(MktGoods goods, List<MktGoodsSpace> addAll)
    {
        if (addAll.size() > 1)
        {
            Collections.sort(addAll, new Comparator<MktGoodsSpace>()
            {
                
                @Override
                public int compare(MktGoodsSpace o1, MktGoodsSpace o2)
                {
                    BigDecimal i = o1.getPrice().subtract(o2.getPrice());
                    return i.compareTo(BigDecimal.ZERO);
                }
            });
        }
        if (!addAll.isEmpty())
        {
            goods.setPrice(addAll.get(0).getPrice());
        }
        goodsDao.update(goods);
    }
    
    
    public Boolean enabledGoods(Integer pkey, Boolean flag)
    {
        MktGoods mktGoods = goodsDao.getGoods(pkey);
        // 商品校验
        if (Objects.isNull(mktGoods))
        {
            throw TofocusException.of(LejiaErrCode.GOODS_INEXISTENCE);
        }
        if (flag)
        {
            Date startDate = mktGoods.getStartDate();
            if (startDate != null && new Date().getTime() < startDate.getTime())
                throw TofocusException.of(WsaleErrCode.GOODS_NOT_AVAILABLE);
            if(mktGoods.getEndDate() != null)
            {
                Date date = mktGoods.getEndDate();
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.DATE, 1);
                if (new Date().getTime() > cal.getTime().getTime())
                    throw TofocusException.of(WsaleErrCode.GOODS_NOT_ENDAVAILABLE);
            }
        }
        else
        {
            // 商品下架，同时关闭“猜我喜欢”
            mktGoods.setGuessLike(false);
        }
        mktGoods.setEnabled(flag);
        MktGoods update = goodsDao.update(mktGoods);
        if (update == null) return false;
        goodListQueryer.resetThreeGtype(update);
        return true;
    }
}

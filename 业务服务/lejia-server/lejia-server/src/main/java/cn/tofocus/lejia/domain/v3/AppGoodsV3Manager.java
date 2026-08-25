package cn.tofocus.lejia.domain.v3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.common.util.PageUtil;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.app.market.AppGoodsAppOnList;
import cn.tofocus.lejia.bean.dto.market.MktGoodsSpaceOnList;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.enums.v3.GoodsSpaceKcV3Dto;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsMainDao;
import cn.tofocus.lejia.dao.goods.MktGoodsSpaceDao;
import cn.tofocus.lejia.dao.market.MktGtypeDao;
import cn.tofocus.lejia.dao.market.MktGwcDao;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.domain.app.AppCollectionManager;

@Component
public class AppGoodsV3Manager
{
    @Autowired
    private MktGtypeDao gtypeDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private MktGwcDao gwcDao;
    
    @Autowired
    private MktGoodsSpaceDao goodsSpaceDao;
    
    @Autowired
    private MktGoodsMainDao goodsMainDao;
    
    @Autowired
    private AppCollectionManager collectionManager;
    
    @Autowired
    private MktOrderDao orderDao;
    
    @Value("${tofocus.file.baseUrl}")
    private String fileStart;
    
    public PageResult<AppGoodsAppOnList> queryAppGoodsV3(Integer page, Integer pagesize, Integer gtype,
        Integer goodsMain)
    {
        long k1 = System.currentTimeMillis();
        String farmerPkey = MobileSession.farmerPkey();
        List<MktGoods> goodsList = goodsDao.select()
            .eq("gtype", gtype)
            .eq("goodsMain", goodsMain)
            .eq("farmer", farmerPkey)
            .eq("idDel", false)
            .exec();
        if (goodsList.isEmpty()) return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
        List<Integer> keys = CollectionUtil.keyList(goodsList);
        List<GoodsSpaceKcV3Dto> kcNumSort = goodsSpaceDao.listKcNumSort(page, pagesize, keys);
        keys.clear();
        kcNumSort.forEach(e -> keys.add(e.getGoods()));
        List<MktGoods> list = goodsDao.select().in("pkey", keys).exec();
        PageResult<AppGoodsAppOnList> res = assembleGoodsDTO(list, page, pagesize);
        System.out.println("zzz: " + (System.currentTimeMillis() - k1));
        assembleGwcNum(res);
        return res;
    }
    
    private PageResult<AppGoodsAppOnList> assembleGoodsDTO(List<MktGoods> list, int page, int pagesize)
    {
        List<AppGoodsAppOnList> content = BeanUtil.beanListFrom(AppGoodsAppOnList.class, list);
        Integer memberPkey = MobileSession.memberPkey();
        PageResult<AppGoodsAppOnList> result = assembleName(content, memberPkey, page, pagesize);
        for (AppGoodsAppOnList appGoodsAppOnList : result.getContent())
        {
            if (memberPkey != null)
            {
                Integer collectionPkey = collectionManager.chkCollection(1, appGoodsAppOnList.getPkey());
                if (collectionPkey != 0)
                {
                    appGoodsAppOnList.setCollection(true);
                    appGoodsAppOnList.setCollectionPkey(collectionPkey);
                }
            }
        }
        return result;
    }
    
    private void assembleGwcNum(PageResult<AppGoodsAppOnList> result)
    {
        Integer memberPkey = MobileSession.memberPkey();
        if (memberPkey == null) return;
        Map<String, Number> map = gwcDao.aggregation().eq("member", memberPkey).execGroupBySum("goods", "num");
        for (AppGoodsAppOnList agal : result.getContent())
        {
            String pkey = agal.getPkey().toString();
            if (map.containsKey(pkey))
            {
                Number number = map.get(pkey);
                if (number != null) agal.setGwcNum(number.intValue());
            }
        }
        
    }
    
    private PageResult<AppGoodsAppOnList> assembleName(List<AppGoodsAppOnList> list, Integer memberPkey, int page,
        int pagesize)
    {
        List<Integer> keys = new ArrayList<>();
        list.forEach(e -> keys.add(e.getPkey()));
        if (keys.isEmpty()) return PageUtil.page(list, PageParameter.of(page, pagesize));
        List<MktGoodsSpaceOnList> sList =
            goodsSpaceDao.select().in("goods", keys).execDto(MktGoodsSpaceOnList.class);
        Map<Integer, List<MktGoodsSpaceOnList>> map = new HashMap<>();
        sList.forEach(e -> {
            if (!map.containsKey(e.getGoods()))
            {
                List<MktGoodsSpaceOnList> v = new ArrayList<>();
                map.put(e.getGoods(), v);
            }
            map.get(e.getGoods()).add(e);
        });
        for (AppGoodsAppOnList bean : list)
        {
            List<MktGoodsSpaceOnList> spaceList = map.get(bean.getPkey());
            int kcNum = 0;
            bean.setKcNum(kcNum);
            for (MktGoodsSpaceOnList space : spaceList)
            {
                kcNum += space.getKcNum();
                space.setStatus(0);
            }
            bean.setSpaces(spaceList);
            bean.setKcNum(kcNum);
        }
        PageResult<AppGoodsAppOnList> res = PageUtil.page(list, PageParameter.of(page, pagesize));
        for (AppGoodsAppOnList bean : res.getContent())
        {
            if (StringUtils.isBlank(bean.getPhoto3()) || StringUtils.isBlank(bean.getPhoto3().replace(fileStart, "")))
            {
                if (bean.getPhoto1() != null && !bean.getPhoto1().isEmpty())
                    bean.setWrapperPhoto(bean.getPhoto1().get(0));
            }
            else
                bean.setWrapperPhoto(bean.getPhoto3());
            bean.setGtypeName(gtypeDao.get(bean.getGtype()).getName());
            bean.setName(goodsMainDao.get(bean.getGoodsMain()).getName());
            
            if (bean.getMType().getIndex() == 5)
            {
                // 如果是砍价商品 增加已经砍价成功的人数
                Integer orderCount = orderDao.getOrderCount(bean.getPkey());
                bean.setCutMemberNum(orderCount);
                Integer judgOrderCut = orderDao.judgOrderCut(bean.getPkey(), memberPkey);
                if (judgOrderCut == null)
                    bean.setIsCut(false);
                else
                    bean.setIsCut(true);
            }
        }
        return res;
    }
    
}

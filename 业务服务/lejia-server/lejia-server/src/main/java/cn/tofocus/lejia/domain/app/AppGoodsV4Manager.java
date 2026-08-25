package cn.tofocus.lejia.domain.app;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.common.util.PageUtil;
import cn.tofocus.core.page.GroupResult;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectBuilder;
import cn.tofocus.db.SelectGroupBuilder;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.lejia.bean.dto.app.AppVendorGtypeInfo;
import cn.tofocus.lejia.bean.dto.app.goods.AppGoodsV4OnList;
import cn.tofocus.lejia.bean.dto.goods.GoodsListItem;
import cn.tofocus.lejia.bean.dto.goods.GoodsListItemIndex;
import cn.tofocus.lejia.bean.dto.market.MktGoodsSpaceOnList;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsMain;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsMainThree;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.bean.entity.goods.ThreeGtypeSortEntity;
import cn.tofocus.lejia.bean.entity.market.MktGtype;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.enums.GoodsSortType;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsMainDao;
import cn.tofocus.lejia.dao.goods.MktGoodsMainThreeDao;
import cn.tofocus.lejia.dao.goods.MktGoodsSpaceDao;
import cn.tofocus.lejia.dao.goods.ThreeGtypeSortDao;
import cn.tofocus.lejia.dao.market.MktGtypeDao;
import cn.tofocus.lejia.dao.market.MktGwcDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.dao.vendor.MktVendorFileDao;
import cn.tofocus.lejia.domain.GoodListQueryer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppGoodsV4Manager
{
    @Autowired
    private MktGtypeDao gtypeDao;
    
    @Autowired
    private MktGoodsMainThreeDao goodsMainThreeDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private MktGwcDao gwcDao;
    
    @Autowired
    private MktGoodsSpaceDao goodsSpaceDao;
    
    @Autowired
    private MktGoodsMainDao goodsMainDao;
    
    @Autowired
    private MktVendorDao vendorDao;
    
    @Autowired
    private MktVendorFileDao vendorFileDao;
    
    @Autowired
    private GoodListQueryer goodListQueryer;
    
    @Autowired
    private ThreeGtypeSortDao threeGtypeSortDao;
    
    @Autowired
    private SysFarmerDao farmerDao;
    
    @Value("${tofocus.file.baseUrl}")
    private String fileStart;
    
    public PageResult<AppGoodsV4OnList> queryAppGoodsV4(Integer page, Integer pagesize, Integer gtype,
        Integer goodsMain)
    {
        MktMember member = MobileSession.member();
        Integer tjv = null;
        if (member != null)
        {
            tjv = member.getTjv();
        }
        Map<String, Number> map = goodsDao.aggregation()
            .eq("ascription", MobileSession.appid())
            .eq("idDel", false)
            .eq("mType", MType.MARKET_GOODS)
            .eq("enabled", true)
            .eq("gtype", gtype)
            .eq("goodsMain", goodsMain)
            .eq("farmer", MobileSession.farmerPkey())
            .execGroupByMin("threeGtype", "price");
        
        List<AppGoodsV4OnList> content = new ArrayList<>();
        Map<Integer, AppGoodsV4OnList> goodsMap = new HashMap<>();
        for (String key : map.keySet())
        {
            MktGoods goods = null;
            if (checkTjv())
            {
                goods = goodsDao.selectOne()
                    .eq("threeGtype", key)
                    .eq("enabled", true)
                    .eq("idDel", false)
                    .eq("vendor", tjv)
                    .exec();
            }
            
            if (!checkTjv() || goods == null)
            {
                goods = goodsDao.selectOne()
                    .eq("threeGtype", key)
                    .eq("enabled", true)
                    .eq("idDel", false)
                    .eq("price", map.get(key))
                    .exec();
            }
            AppGoodsV4OnList dto = BeanUtil.beanFrom(AppGoodsV4OnList.class, goods);
            if (goods.getVendor() != null)
            {
                MktVendor mktVendor = vendorDao.get(goods.getVendor());
                if (mktVendor != null) dto.setVendorName(mktVendor.getDisplayName());
            }
            goodsMap.put(dto.getThreeGtype(), dto);
        }
        List<MktGoodsMainThree> listSortFalse = goodsMainThreeDao.listSortFalse(MobileSession.appid());
        for (MktGoodsMainThree gmt : listSortFalse)
        {
            if (goodsMap.containsKey(gmt.getPkey())) content.add(goodsMap.get(gmt.getPkey()));
        }
        PageResult<AppGoodsV4OnList> res = PageUtil.page(content, PageParameter.of(page, pagesize));
        assembleGwcNum(res.getContent());
        assembleName(res.getContent());
        return res;
    }
    
    // 获取一个三级分类下所有的 商品
    public PageResult<AppGoodsV4OnList> queryThreeGtypeAppGoodsV4(Integer page, Integer pagesize, Integer threeGtype)
    {
        String farmerPkey = MobileSession.farmerPkey();
        MktMember member = MobileSession.member();
        Integer tjv = null;
        if (member != null)
        {
            tjv = member.getTjv();
        }
        PageResult<AppGoodsV4OnList> res;
        if (checkTjv())
        {
            List<AppGoodsV4OnList> content = goodsDao.select()
                .eq("vendor", tjv)
                .eq("farmer", farmerPkey)
                .eq("mType", MType.MARKET_GOODS)
                .eq("threeGtype", threeGtype)
                .eq("enabled", true)
                .eq("idDel", false)
                .sort("price", false)
                .sort("sort", false)
                .execDto(AppGoodsV4OnList.class);
            List<AppGoodsV4OnList> list = goodsDao.select()
                .notEq("vendor", tjv)
                .eq("farmer", farmerPkey)
                .eq("mType", MType.MARKET_GOODS)
                .eq("threeGtype", threeGtype)
                .eq("enabled", true)
                .eq("idDel", false)
                .sort("price", false)
                .sort("sort", false)
                .execDto(AppGoodsV4OnList.class);
            content.addAll(list);
            res = PageUtil.page(content, PageParameter.of(page, pagesize));
        }
        else
        {
            res = goodsDao.selectPage()
                .page(page)
                .pagesize(pagesize)
                .eq("mType", MType.MARKET_GOODS)
                .eq("farmer", farmerPkey)
                .eq("threeGtype", threeGtype)
                .eq("enabled", true)
                .eq("idDel", false)
                .sort("price", false)
                .sort("sort", false)
                .execDto(AppGoodsV4OnList.class);
        }
        for (AppGoodsV4OnList g : res.getContent())
        {
            if (g.getVendor() != null)
            {
                MktVendor mktVendor = vendorDao.get(g.getVendor());
                if (mktVendor != null) g.setVendorName(mktVendor.getDisplayName());
            }
        }
        assembleGwcNum(res.getContent());
        assembleName(res.getContent());
        return res;
    }
    
    public PageResult<AppGoodsV4OnList> queryAppVendorGoods(Integer page, Integer pagesize, Integer vendor, String name,
        Integer goodsMain, Boolean priceSort, Boolean xsNumSort)
    {
        SelectPageBuilder<Integer, MktGoods> builder = goodsDao.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq("ascription", MobileSession.appid())
            .eq("idDel", false)
            .like("title", name)
            .eq("enabled", true)
            .eq("vendor", vendor)
            .eq("goodsMain", goodsMain)
            .eq("farmer", MobileSession.farmerPkey());
        if (xsNumSort != null) builder.sort("xsNum", xsNumSort);
        PageResult<AppGoodsV4OnList> res =
            builder.sort("price", priceSort).sort("sort", false).sort("pkey", false).execDto(AppGoodsV4OnList.class);
        assembleGwcNum(res.getContent());
        assembleName(res.getContent());
        return res;
    }
    
    private void assembleGwcNum(List<AppGoodsV4OnList> result)
    {
        Integer memberPkey = MobileSession.memberPkey();
        if (memberPkey == null) return;
        Map<String, Number> map = gwcDao.aggregation().eq("member", memberPkey).execGroupBySum("goods", "num");
        for (AppGoodsV4OnList agal : result)
        {
            if (agal != null && agal.getPkey() != null)
            {
                String pkey = agal.getPkey().toString();
                if (map.containsKey(pkey))
                {
                    Number number = map.get(pkey);
                    if (number != null) agal.setGwcNum(number.intValue());
                }
                Integer kcNum = goodsSpaceDao.getKcNum(agal.getPkey());
                agal.setKcNum(kcNum);
            }
        }
        
    }
    private void assembleItemGwcNum(List<GoodsListItem> result)
    {
        Integer memberPkey = MobileSession.memberPkey();
        if (memberPkey == null) return;
        Map<String, Number> map = gwcDao.aggregation().eq("member", memberPkey).execGroupBySum("goods", "num");
        for (GoodsListItem agal : result)
        {
            if (agal != null && agal.getPkey() != null)
            {
                String pkey = agal.getPkey().toString();
                if (map.containsKey(pkey))
                {
                    Number number = map.get(pkey);
                    if (number != null) agal.setGwcNum(number.intValue());
                }
                Integer kcNum = goodsSpaceDao.getKcNum(agal.getPkey());
                agal.setKcNum(kcNum);
            }
        }
    }
    
    // 查询购物车各个商品数量
    private List<AppGoodsV4OnList> assembleName(List<AppGoodsV4OnList> res)
    {
        //        List<AppGoodsV4OnList> list = res;
        List<Integer> keys = new ArrayList<>();
        res.forEach(e -> keys.add(e.getPkey()));
        if (keys.isEmpty()) return res;
        SelectBuilder<Integer, MktGoodsSpace> builder = goodsSpaceDao.select().in("goods", keys).sort("price", false);
        List<MktGoodsSpaceOnList> sList = builder.execDto(MktGoodsSpaceOnList.class);
        Map<Integer, List<MktGoodsSpaceOnList>> map = new HashMap<>();
        sList.forEach(e -> {
            if (!map.containsKey(e.getGoods()))
            {
                List<MktGoodsSpaceOnList> v = new ArrayList<>();
                map.put(e.getGoods(), v);
            }
            map.get(e.getGoods()).add(e);
        });
        for (AppGoodsV4OnList bean : res)
        {
            List<MktGoodsSpaceOnList> spaceList = map.get(bean.getPkey());
            int kcNum = 0;
            bean.setKcNum(kcNum);
            if(spaceList != null && !spaceList.isEmpty())
            {
                for (MktGoodsSpaceOnList space : spaceList)
                {
                    kcNum += space.getKcNum();
                    space.setStatus(0);
                }
            }
            bean.setSpaces(spaceList);
            bean.setKcNum(kcNum);
        }
        for (AppGoodsV4OnList bean : res)
        {
            if (StringUtils.isBlank(bean.getPhoto3()) || StringUtils.isBlank(bean.getPhoto3().replace(fileStart, "")))
            {
                if (bean.getPhoto1() != null && bean.getPhoto1().size() > 0)
                    bean.setWrapperPhoto(bean.getPhoto1().get(0));
            }
            else
                bean.setWrapperPhoto(bean.getPhoto3());
            bean.setGtypeName(gtypeDao.get(bean.getGtype()).getName());
            bean.setName(goodsMainDao.get(bean.getGoodsMain()).getName());
        }
        return res;
    }
    
    public PageResult<AppVendorGtypeInfo> queryGtypeVendor(Integer page, Integer pagesize, Integer gtype, String name)
    {
        List<AppVendorGtypeInfo> content = assembleGtypeVendorList(gtype, name);
        PageResult<AppVendorGtypeInfo> res = PageUtil.page(content, PageParameter.of(page, pagesize));
        return res;
    }
    
    private List<AppVendorGtypeInfo> assembleGtypeVendorList(Integer gtype, String name)
    {
        List<AppVendorGtypeInfo> content = new ArrayList<>();
        List<MktVendor> list = vendorDao.select()
            .like("displayName", name)
            .eq("enabled", true)
            .eq("farmer", MobileSession.farmerPkey())
            .exec();
        if (gtype != null)
        {
            for (MktVendor v : list)
            {
                if (StringUtils.isNotBlank(v.getBusinessScope()))
                {
                    String[] split = v.getBusinessScope().split(",");
                    List<String> asList = Arrays.asList(split);
                    if (asList.contains(gtype.toString()))
                    {
                        AppVendorGtypeInfo dto = new AppVendorGtypeInfo();
                        dto.setPkey(v.getPkey());
                        dto.setName(v.getDisplayName());
                        dto.setBooth(v.getBooth());
                        dto.setXsNum(0);
                        dto.setCreatedTime(v.getCreatedTime());
                        dto.setGtype(gtype);
                        MktGtype mktGtype = gtypeDao.get(gtype);
                        if (mktGtype != null) dto.setGtypeName(mktGtype.getName());
                        content.add(dto);
                    }
                }
            }
        }
        else
        {
            for (MktVendor v : list)
            {
                String[] split = v.getBusinessScope().split(",");
                if (split.length < 1) continue;
                for (String s : split)
                {
                    AppVendorGtypeInfo dto = new AppVendorGtypeInfo();
                    dto.setPkey(v.getPkey());
                    dto.setName(v.getDisplayName());
                    dto.setBooth(v.getBooth());
                    dto.setXsNum(0);
                    dto.setCreatedTime(v.getCreatedTime());
                    dto.setGtype(Integer.valueOf(s));
                    MktGtype mktGtype = gtypeDao.get(Integer.valueOf(s));
                    if (mktGtype != null) dto.setGtypeName(mktGtype.getName());
                    content.add(dto);
                }
            }
        }
        for (AppVendorGtypeInfo vg : content)
        {
            // 计算销量
            Integer xsNum = goodsDao.getGoodsVendorXsNum(vg.getPkey());
            //            Integer xsNum = vendorOrderDao.getVendorXsNum(vg.getPkey());
            vg.setXsNum(xsNum);
            // 获取头像
            String headIcon = vendorFileDao.getHeadIcon(vg.getPkey());
            vg.setHeadIcon(headIcon);
        }
        // 根据销量排序 倒序 高到低 
        Collections.sort(content, new Comparator<AppVendorGtypeInfo>()
        {
            @Override
            public int compare(AppVendorGtypeInfo o1, AppVendorGtypeInfo o2)
            {
                // 销量一样 根据时间排序
                if (o2.getXsNum() - o1.getXsNum() == 0) return o1.getCreatedTime().compareTo(o2.getCreatedTime());
                return o2.getXsNum() - o1.getXsNum();
            }
        });
        if (checkTjv())
        {
            MktMember member = MobileSession.member();
            Integer tjv = null;
            if (member != null)
            {
                tjv = member.getTjv();
            }
            AppVendorGtypeInfo ovg = null;
            for (AppVendorGtypeInfo vg : content)
            {
                if (tjv.equals(vg.getPkey()))
                {
                    ovg = vg;
                    content.remove(vg);
                    break;
                }
            }
            if (ovg != null) content.add(0, ovg);
        }
        return content;
    }
    
    private List<AppVendorGtypeInfo> assembleGtypeVendorListV2(String name)
    {
        Map<Integer, List<AppVendorGtypeInfo>> map = new HashMap<>();
        List<MktVendor> list = vendorDao.listVendor(MobileSession.farmerPkey());
        
        for (MktVendor e : list)
        {
            if (StringUtils.isNotBlank(e.getBusinessScope()))
            {
                String[] split = e.getBusinessScope().split(",");
                if (split.length >= 1)
                {
                    for (String s : split)
                    {
                        Integer key = Integer.valueOf(s);
                        if (!map.containsKey(key)) map.put(key, new ArrayList<>());
                        MktGtype mktGtype = gtypeDao.get(key);
                        AppVendorGtypeInfo dto = new AppVendorGtypeInfo();
                        dto.setPkey(e.getPkey());
                        dto.setName(e.getDisplayName());
                        dto.setBooth(e.getBooth());
                        dto.setXsNum(0);
                        if (mktGtype != null) dto.setGtypeName(mktGtype.getName());
                        dto.setCreatedTime(e.getCreatedTime());
                        dto.setGtype(Integer.valueOf(s));
                        map.get(key).add(dto);
                    }
                }
            }
        }
        List<MktGtype> listMarketGtype = gtypeDao.listMarketGtype(MobileSession.farmerPkey(), MobileSession.appid());
        Map<Integer, List<AppVendorGtypeInfo>> linkedMap = new LinkedHashMap<>();
        for(MktGtype g : listMarketGtype)
        {
            if(map.containsKey(g.getPkey()))
            {
                linkedMap.put(g.getPkey(), map.get(g.getPkey()));
            }
        }
        for (List<AppVendorGtypeInfo> vgs : linkedMap.values())
        {
            for (AppVendorGtypeInfo vg : vgs)
            {
                // 计算销量
                Integer xsNum = goodsDao.getGoodsVendorXsNum(vg.getPkey());
                vg.setXsNum(xsNum);
                // 获取头像
                String headIcon = vendorFileDao.getHeadIcon(vg.getPkey());
                vg.setHeadIcon(headIcon);
            }
        }
        for (List<AppVendorGtypeInfo> vgs : linkedMap.values())
        {
            // 根据销量排序 倒序 高到低 
            Collections.sort(vgs, new Comparator<AppVendorGtypeInfo>()
            {
                @Override
                public int compare(AppVendorGtypeInfo o1, AppVendorGtypeInfo o2)
                {
                    // 销量一样 根据时间排序
                    if (o2.getXsNum() - o1.getXsNum() == 0) return o1.getCreatedTime().compareTo(o2.getCreatedTime());
                    return o2.getXsNum() - o1.getXsNum();
                }
            });
            if (Boolean.TRUE.equals(checkTjv()))
            {
                MktMember member = MobileSession.member();
                Integer tjv = null;
                if (member != null)
                {
                    tjv = member.getTjv();
                }
                AppVendorGtypeInfo ovg = null;
                for (AppVendorGtypeInfo vg : vgs)
                {
                    if (tjv.equals(vg.getPkey()))
                    {
                        ovg = vg;
                        vgs.remove(vg);
                        break;
                    }
                }
                if (ovg != null) vgs.add(0, ovg);
            }
        }
        List<AppVendorGtypeInfo> res = new ArrayList<>();
        linkedMap.values().forEach(e -> res.addAll(e));
        return res;
    }
    
    private Boolean checkTjv()
    {
        MktMember member = MobileSession.member();
        Integer tjv = null;
        Date tjvTime = null;
        if (member != null)
        {
            tjv = member.getTjv();
            tjvTime = member.getTjvTime();
        }
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_WEEK, -7);
        return tjv != null && tjvTime != null && cal.getTime().compareTo(tjvTime) < 0;
    }
    
    
    // 组合商品数据 用于存到缓存
    public List<GoodsListItem> addGoodsListItemCache(String market, Boolean enabled, Boolean xsNum)
    {
        SysFarmer sysFarmer = farmerDao.get(market);
        int ascription = sysFarmer.getAscription();
        // 获取市场下所有数据
        Map<Integer, List<GoodsListItem>> goodsMap;
        if(Boolean.TRUE.equals(xsNum))
        {
            goodsMap = assembleMapXsNum(market, enabled, ascription);
        }
        else
            goodsMap = assembleMap(market, enabled, ascription);
        // 按一级分类排序
        Map<Integer, List<GoodsListItem>> goodsLinkedMap = assembleGtypeMap(goodsMap, enabled, market, ascription);
        // 按二级和三级分类排序
        Map<Integer, List<GoodsListItem>> goodsLinkedV2Map = assembleTwoAndThreeMap(goodsLinkedMap, enabled, market, ascription);
        
        List<GoodsListItem> res = new ArrayList<>();
        for (Map.Entry<Integer, List<GoodsListItem>> entry : goodsLinkedV2Map.entrySet())
        {
            res.addAll(entry.getValue());
        }
        return res;
    }
    
    // 获取市场下所有商品数据
    private Map<Integer, List<GoodsListItem>> assembleMap(String market, Boolean enabled, int ascription)
    {
        Map<String, Number> map = goodsDao.aggregation()
            .eq("idDel", false)
            .eq("farmer", market)
            .eq("ascription", ascription)
            .in("mType", MType.MARKET_GOODS, MType.BOX_GOODS)
            .eq("enabled", enabled)
            .isNotNull("threeGtype")
            .execGroupByMin("threeGtype", "price");
        Map<Integer, List<GoodsListItem>> goodsMap = new HashMap<>();
        for (Entry<String, Number> entry : map.entrySet())
        {
            GoodsListItem dto = goodsDao.selectOne()
                .eq("threeGtype", entry.getKey())
                .eq("enabled", enabled)
                .eq("farmer", market)
                .eq("ascription", ascription)
                .eq("idDel", false)
                .eq("price", entry.getValue())
                .execDto(GoodsListItem.class);
            if (StringUtils.isBlank(dto.getPhoto3()) || StringUtils.isBlank(dto.getPhoto3().replace(fileStart, "")))
            {
                if (dto.getPhoto1() != null && !dto.getPhoto1().isEmpty()) dto.setWrapperPhoto(dto.getPhoto1().get(0));
            }
            else
                dto.setWrapperPhoto(dto.getPhoto3());
            if (!goodsMap.containsKey(dto.getGtype()))
            {
                goodsMap.put(dto.getGtype(), new ArrayList<>());
            }
            goodsMap.get(dto.getGtype()).add(dto);
        }
        return goodsMap;
    }
    
    // 获取市场下所有商品数据
    private Map<Integer, List<GoodsListItem>> assembleMapXsNum(String market, Boolean enabled, int ascription)
    {
        Map<String, Number> map = goodsDao.aggregation()
            .eq("idDel", false)
            .eq("farmer", market)
            .eq("ascription", ascription)
            .in("mType", MType.MARKET_GOODS, MType.BOX_GOODS)
            .eq("enabled", enabled)
            .isNotNull("threeGtype")
            .execGroupByMax("threeGtype", "xsNum");
        Map<Integer, List<GoodsListItem>> goodsMap = new HashMap<>();
        for (Entry<String, Number> entry : map.entrySet())
        {
            GoodsListItem dto = goodsDao.selectOne()
                .eq("threeGtype", entry.getKey())
                .eq("enabled", enabled)
                .eq("farmer", market)
                .eq("ascription", ascription)
                .eq("idDel", false)
                .eq("xsNum", entry.getValue())
                .execDto(GoodsListItem.class);
            if (StringUtils.isBlank(dto.getPhoto3()) || StringUtils.isBlank(dto.getPhoto3().replace(fileStart, "")))
            {
                if (dto.getPhoto1() != null && !dto.getPhoto1().isEmpty()) dto.setWrapperPhoto(dto.getPhoto1().get(0));
            }
            else
                dto.setWrapperPhoto(dto.getPhoto3());
            if (!goodsMap.containsKey(dto.getGtype()))
            {
                goodsMap.put(dto.getGtype(), new ArrayList<>());
            }
            goodsMap.get(dto.getGtype()).add(dto);
        }
        return goodsMap;
    }
    
    // 按一级分类排序
    private Map<Integer, List<GoodsListItem>> assembleGtypeMap(Map<Integer, List<GoodsListItem>> goodsMap,
        Boolean enabled, String farmer, int ascription)
    {
        List<MktGtype> gtypeList = gtypeDao.select()
            .eq("farmer", farmer)
            .eq("enabled", enabled)
            .eq("idDel", false)
            .eq("ascription", ascription)
//            .sort("marketSort", false)
            .sort("sort", false)
            .sort("pkey", true)
            .exec();
        
        Map<Integer, List<GoodsListItem>> goodsLinkedMap = new LinkedHashMap<>();
        for (MktGtype g : gtypeList)
        {
            Integer gtypeKey = g.getPkey();
            if (goodsMap.containsKey(gtypeKey))
            {
                if (!goodsLinkedMap.containsKey(gtypeKey))
                {
                    List<GoodsListItem> value = new ArrayList<>();
                    goodsLinkedMap.put(gtypeKey, value);
                }
                List<GoodsListItem> goodsListItem = goodsMap.get(gtypeKey);
                for (GoodsListItem glt : goodsListItem)
                {
                    glt.setGtypeName(g.getName());
                    glt.setGtypeEnable(g.getEnabled());
                    glt.setGtypeSort(g.getSort());
                }
                goodsLinkedMap.get(gtypeKey).addAll(goodsListItem);
            }
        }
        return goodsLinkedMap;
    }
    
    // 按二级和三级分类排序
    private Map<Integer, List<GoodsListItem>> assembleTwoAndThreeMap(Map<Integer, List<GoodsListItem>> goodsLinkedMap,
        Boolean enabled, String farmer, int ascription)
    {
        Map<Integer, List<GoodsListItem>> goodsLinkedV2Map = new LinkedHashMap<>();
        for (Map.Entry<Integer, List<GoodsListItem>> entry : goodsLinkedMap.entrySet())
        {
            Map<Integer, List<GoodsListItem>> goodsMainMap = new HashMap<>();
            entry.getValue().forEach(e -> {
                if (!goodsMainMap.containsKey(e.getGoodsMain()))
                {
                    List<GoodsListItem> value = new ArrayList<>();
                    goodsMainMap.put(e.getGoodsMain(), value);
                }
                goodsMainMap.get(e.getGoodsMain()).add(e);
            });
            List<MktGoodsMain> goodsMainList = goodsMainDao.listSortFalse(entry.getKey(), enabled, farmer, ascription);
            List<GoodsListItem> value = new ArrayList<>();
            Map<Integer, List<GoodsListItem>> goodsGMLinkedMap = new LinkedHashMap<>();
            // 按二级分类排序
            for (MktGoodsMain gm : goodsMainList)
            {
                if (goodsMainMap.containsKey(gm.getPkey()))
                {
                    List<GoodsListItem> goodsListItem = goodsMainMap.get(gm.getPkey());
                    goodsListItem.forEach(e -> {
                        e.setName(gm.getName());
                        e.setGoodsMainEnable(gm.getEnabled());
                        e.setGoodsMainSort(gm.getSort());
                    });
                    if (!goodsGMLinkedMap.containsKey(gm.getPkey()))
                    {
                        List<GoodsListItem> gmValue = new ArrayList<>();
                        goodsGMLinkedMap.put(gm.getPkey(), gmValue);
                    }
                    goodsGMLinkedMap.get(gm.getPkey()).addAll(goodsListItem);
                }
            }
            // 按三级分类排序
            for (Map.Entry<Integer, List<GoodsListItem>> entryGM : goodsGMLinkedMap.entrySet())
            {
                Map<Integer, GoodsListItem> tMap = new HashMap<>();
                entryGM.getValue().forEach(e -> tMap.put(e.getThreeGtype(), e));
                List<MktGoodsMainThree> threeList =
                    goodsMainThreeDao.listDto(entryGM.getKey(), enabled, ascription, MktGoodsMainThree.class);
                for (MktGoodsMainThree gmt : threeList)
                {
                    if (tMap.containsKey(gmt.getPkey()))
                    {
                        GoodsListItem goodsListItem = tMap.get(gmt.getPkey());
                        goodsListItem.setThreeGtypeEnable(gmt.getEnabled());
                        goodsListItem.setThreeGtypeSort(gmt.getSort());
                        value.add(goodsListItem);
                    }
                }
            }
            goodsLinkedV2Map.put(entry.getKey(), value);
        }
        return goodsLinkedV2Map;
    }
    
//    public GroupResult<String, GoodsListItem> queryAppGtypeGoods(int from, int limit, Integer gtype)
//    {
//        GroupResult<String, GoodsListItem> res =
//            goodListQueryer.rangeCat1(MobileSession.farmerPkey(), gtype, from, limit);
//        List<GroupList<String,GoodsListItem>> groups = res.getGroups();
//        for(GroupList<String,GoodsListItem> group : groups)
//        {
//            List<GoodsListItem> data = group.getData();
//            assembleItemGwcNum(data);
//        }
//        return res;
//    }
//    
//    public GroupResult<String, GoodsListItem> queryAppGoodsMainGoods(int from, int limit, Integer goodsMain)
//    {
//        MktGoodsMain mktGoodsMain = goodsMainDao.get(goodsMain);
//        GroupResult<String, GoodsListItem> res =
//            goodListQueryer.rangeCat2(MobileSession.farmerPkey(), mktGoodsMain.getGtype(), goodsMain, from, limit);
//        List<GroupList<String,GoodsListItem>> groups = res.getGroups();
//        for(GroupList<String,GoodsListItem> group : groups)
//        {
//            List<GoodsListItem> data = group.getData();
//            for(GoodsListItem agal : data)
//            {
//                Integer kcNum = goodsSpaceDao.getKcNum(agal.getPkey());
//                agal.setKcNum(kcNum);
//            }
//        }
//        return res;
//    }
    
    // 分类页下面 商户
    public GroupResult<String, AppVendorGtypeInfo> queryAppGtypeVendor(int from, int limit, Integer gtype, String name)
    {
        List<AppVendorGtypeInfo> content = assembleGtypeVendorListV2(name);
        
        // 计算一二级类别的开始位置
        Map<String, GoodsListItemIndex> indexs = new HashMap<>();
        for (int i = 0; i < content.size(); i++)
        {
            AppVendorGtypeInfo item = content.get(i);
            String typeKey = item.getGtype() + ":-1";
            
            int start = i;
            GoodsListItemIndex typeIndex =
                indexs.computeIfAbsent(typeKey, k -> new GoodsListItemIndex(item.getGtype(), -1, start));
            typeIndex.setSize(typeIndex.getSize() + 1);
        }
        GoodsListItemIndex idx = null;
        if (gtype != null)
        {
            idx = indexs.get(gtype + ":-1");
            if (idx == null)
            {
                GroupResult<String, AppVendorGtypeInfo> r = new GroupResult<>();
                r.setStart(from);
                r.setLimit(limit);
                r.setNextStart(from + limit);
                return r;
            }
            else
                from = from + idx.getStart();
        }
        SelectGroupBuilder<?, HasPkey<String>, String, AppVendorGtypeInfo> s =
            new SelectGroupBuilder<>(null, null, String.class, AppVendorGtypeInfo.class);
        GroupResult<String, AppVendorGtypeInfo> r =
            s.groupBy("gtype").groupValue("gtypeName").from(from).limit(limit).exec((t1, t2) -> {
                
                if ((t1 + t2) > content.size())
                {
                    if (t1 > content.size())
                    {
                        return new ArrayList<>();
                    }
                    return content.subList(t1, content.size());
                }
                return content.subList(t1, t1 + t2);
            });
        if (gtype != null)
        {
            r.setStart(r.getStart() - idx.getStart());
            r.setNextStart(r.getNextStart() - idx.getStart());
        }
        return r;
    }
    
    // 商户页下面所有的商品
    public GroupResult<String, GoodsListItem> queryAppGoodsMainVendorGoods(int from, int limit, Integer vendor,
        String name, Integer goodsMain, Boolean priceSort, Boolean xsNumSort)
    {
        List<AppGoodsV4OnList> list = goodsDao.select()
            .eq("ascription", MobileSession.appid())
            .eq("idDel", false)
            .like("title", name)
            .eq("enabled", true)
            .eq("vendor", vendor)
            .eq("farmer", MobileSession.farmerPkey())
            .sort("sort", false)
            .sort("pkey", false)
            .execDto(AppGoodsV4OnList.class);
        assembleGwcNum(list);
        assembleName(list);
        Map<Integer, List<GoodsListItem>> yMap = new HashMap<>();
        for (AppGoodsV4OnList ag : list)
        {
            if (!yMap.containsKey(ag.getGoodsMain()))
            {
                yMap.put(ag.getGoodsMain(), new ArrayList<>());
            }
            yMap.get(ag.getGoodsMain()).add(BeanUtil.beanFrom(GoodsListItem.class, ag));
        }
        
        List<MktGoodsMain> goodsMainList = goodsMainDao.listSortFalse(null, true, MobileSession.farmerPkey(), MobileSession.appid());
        Map<Integer, List<GoodsListItem>> map = new LinkedHashMap<>();
        for (MktGoodsMain gm : goodsMainList)
        {
            if (!map.containsKey(gm.getPkey()))
            {
                map.put(gm.getPkey(), new ArrayList<>());
            }
            if (yMap.containsKey(gm.getPkey()))
            {
                map.get(gm.getPkey()).addAll(yMap.get(gm.getPkey()));
            }
        }
        
        if (goodsMain != null)
        {
            if (priceSort != null)
            {
                List<GoodsListItem> priceList = map.get(goodsMain);
                Collections.sort(priceList, new Comparator<GoodsListItem>()
                {
                    @Override
                    public int compare(GoodsListItem o1, GoodsListItem o2)
                    {
                        if (Boolean.TRUE.equals(priceSort))
                            return o2.getPrice().compareTo(o1.getPrice());
                        else
                            return o1.getPrice().compareTo(o2.getPrice());
                    }
                });
            }
            if (xsNumSort != null)
            {
                List<GoodsListItem> priceList = map.get(goodsMain);
                Collections.sort(priceList, new Comparator<GoodsListItem>()
                {
                    @Override
                    public int compare(GoodsListItem o1, GoodsListItem o2)
                    {
                        if (Boolean.TRUE.equals(xsNumSort))
                            return o2.getXsNum() - o1.getXsNum();
                        else
                            return o1.getXsNum() - o2.getXsNum();
                    }
                });
            }
            
        }
        List<GoodsListItem> cacheList = new ArrayList<>();
        map.values().forEach(e -> cacheList.addAll(e));
        // 计算一二级类别的开始位置
        Map<String, GoodsListItemIndex> indexs = new HashMap<>();
        for (int i = 0; i < cacheList.size(); i++)
        {
            GoodsListItem item = cacheList.get(i);
            String key = item.getGtype() + ":" + item.getGoodsMain();
            
            int start = i;
            GoodsListItemIndex subIndex =
                indexs.computeIfAbsent(key, k -> new GoodsListItemIndex(item.getGtype(), item.getGoodsMain(), start));
            subIndex.setSize(subIndex.getSize() + 1);
        }
        GoodsListItemIndex idx = null;
        if (goodsMain != null)
        {
            MktGoodsMain mktGoodsMain = goodsMainDao.get(goodsMain);
            idx = indexs.get(mktGoodsMain.getGtype() + ":" + goodsMain);
            if (idx == null)
            {
                GroupResult<String, GoodsListItem> r = new GroupResult<>();
                r.setStart(from);
                r.setLimit(limit);
                r.setNextStart(from + limit);
                return r;
            }
            else
                from = from + idx.getStart();
        }
        SelectGroupBuilder<?, HasPkey<Integer>, String, GoodsListItem> s =
            new SelectGroupBuilder<>(null, null, String.class, GoodsListItem.class);
        GroupResult<String, GoodsListItem> r =
            s.groupBy("goodsMain").groupValue("name").from(from).limit(limit).exec((t1, t2) -> {
                if ((t1 + t2) > cacheList.size())
                {
                    if (t1 > cacheList.size())
                    {
                        return new ArrayList<>();
                    }
                    return cacheList.subList(t1, cacheList.size());
                }
                return cacheList.subList(t1, t1 + t2);
            });
        if (goodsMain != null)
        {
            r.setStart(r.getStart() - idx.getStart());
            r.setNextStart(r.getNextStart() - idx.getStart());
        }
        return r;
    }
    
    
    public void putThreeGtypeSort(String farmer, GoodsSortType sortType)
    {
        List<String> farmerKeys = new ArrayList<>();
        if(StringUtils.isBlank(farmer))
        {
            List<SysFarmer> exec = farmerDao.select().notLike("pkey", Constant.Operation).eq("idDel", false).exec();
            farmerKeys.addAll(CollectionUtil.keyList(exec));
        }
        else
            farmerKeys.add(farmer);
       
        // 价格排序
        if(sortType == null || GoodsSortType.PRICE.equals(sortType))
        {
            for(String f : farmerKeys)
            {
                List<GoodsListItem> priceList = addGoodsListItemCache(f, true, false);
                long del = threeGtypeSortDao.select()
                .eq("sortType", GoodsSortType.PRICE)
                .eq("farmer", f)
                .strict(true)
                .del();
                log.info("删除threeGtypeSort表PRICE,市场主键:{} 结果: {}", f, del);
                List<ThreeGtypeSortEntity> tgPriceList = new ArrayList<>();
                for(GoodsListItem g : priceList)
                {
                    if(g.getSpaces() == null || g.getSpaces().isEmpty())
                        continue;
                    ThreeGtypeSortEntity tgs = BeanUtil.beanFrom(ThreeGtypeSortEntity.class, g);
                    tgs.setSortType(GoodsSortType.PRICE);
                    tgs.setSortValue(g.getPrice());
                    tgs.setGoods(g.getPkey());
                    tgs.setSpace(g.getSpaces().get(0).getPkey());
                    tgs.setFarmer(f);
                    tgPriceList.add(tgs);
                }
                threeGtypeSortDao.addAll(tgPriceList);
            }
        }
        // 销量
        if(sortType == null || GoodsSortType.SALED.equals(sortType))
        {
            for(String f : farmerKeys)
            {
                List<GoodsListItem> xsNumList = addGoodsListItemCache(f, true, true);
                long del = threeGtypeSortDao.select()
                    .eq("sortType", GoodsSortType.SALED)
                    .eq("farmer", f)
                    .strict(true)
                    .del();
                    log.info("删除threeGtypeSort表SALED,市场主键:{} 结果: {}", f, del);
                List<ThreeGtypeSortEntity> tgXsNumList = new ArrayList<>();
                for(GoodsListItem g : xsNumList)
                {
                    if(g.getSpaces() == null || g.getSpaces().isEmpty())
                        continue;
                    ThreeGtypeSortEntity tgs = BeanUtil.beanFrom(ThreeGtypeSortEntity.class, g);
                    tgs.setSortType(GoodsSortType.SALED);
                    tgs.setSortValue(BigDecimal.valueOf(g.getXsNum()));
                    tgs.setGoods(g.getPkey());
                    // 取销量最大的规格
                    Optional<MktGoodsSpaceOnList> optional = g.getSpaces().stream().max(Comparator.comparing(MktGoodsSpaceOnList::getXsNum));
                    MktGoodsSpaceOnList orElse = optional.orElse(new MktGoodsSpaceOnList());
                    tgs.setSpace(orElse.getPkey());
                    tgs.setFarmer(f);
                    tgXsNumList.add(tgs);
                }
                threeGtypeSortDao.addAll(tgXsNumList);
            }
        }
    }
    
}

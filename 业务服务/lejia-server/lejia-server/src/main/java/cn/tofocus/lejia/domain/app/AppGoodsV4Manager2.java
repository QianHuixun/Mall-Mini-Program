package cn.tofocus.lejia.domain.app;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.common.util.PageUtil;
import cn.tofocus.core.data.KeyValue;
import cn.tofocus.core.page.GroupList;
import cn.tofocus.core.page.GroupResult;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectBuilder;
import cn.tofocus.db.SelectGroupBuilder;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.bean.dto.app.AppVendorGtypeInfo;
import cn.tofocus.lejia.bean.dto.app.goods.AppGoodsV4OnList;
import cn.tofocus.lejia.bean.dto.goods.GoodsListItem;
import cn.tofocus.lejia.bean.dto.goods.GoodsListItemIndex;
import cn.tofocus.lejia.bean.dto.goods.GoodsListItemV2;
import cn.tofocus.lejia.bean.dto.goods.GoodsProcessOnInfo;
import cn.tofocus.lejia.bean.dto.market.MktGoodsSpaceOnList;
import cn.tofocus.lejia.bean.entity.goods.*;
import cn.tofocus.lejia.bean.entity.market.MktGtype;
import cn.tofocus.lejia.bean.entity.market.MktSupplier;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.enums.GoodsRecommendZone;
import cn.tofocus.lejia.bean.enums.GoodsSortType;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.goods.*;
import cn.tofocus.lejia.dao.market.MktGtypeDao;
import cn.tofocus.lejia.dao.market.MktGwcDao;
import cn.tofocus.lejia.dao.market.MktSupplierDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.dao.vendor.MktVendorFileDao;
import cn.tofocus.lejia.domain.GoodListQueryer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppGoodsV4Manager2
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
    private MktSupplierDao supplierDao;
    
    @Autowired
    private MktVendorFileDao vendorFileDao;
    
    @Autowired
    private GoodListQueryer goodListQueryer;
    
    @Autowired
    private ThreeGtypeSortDao threeGtypeSortDao;
    
    @Autowired
    private SysFarmerDao farmerDao;
    
    @Autowired
    private MktVendorDao mktVendorDao;
    
    @Autowired
    private MktGoodsProcessDao goodsProcessDao;

    @Autowired
    private MktGoodsSellingPointDao mktGoodsSellingPointDao;

    @Autowired
    private MktGoodsRecommendDao mktGoodsRecommendDao;
    
    @Value("${tofocus.file.baseUrl}")
    private String fileStart;

    //@Value("${gtype.query.mysql.left.join:false}")
    //private Boolean gtypeLeft;
    
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
    public PageResult<AppGoodsV4OnList> queryThreeGtypeAppGoodsV4(Integer page, Integer pagesize, Integer threeGtype, GoodsSortType goodsSortType, Boolean sortDesc)
    {
        String farmerPkey = MobileSession.farmerPkey();
        MktMember member = MobileSession.member();
        Integer tjv = null;
        if (member != null)
        {
            tjv = member.getTjv();
        }
//        boolean sortDesc = GoodsSortType.PRICE == goodsSortType ? false : true;
        if(GoodsSortType.SALED == goodsSortType)
            sortDesc = true;
        PageResult<AppGoodsV4OnList> res;
        
        List<String> farmerKeyList = new ArrayList<>();
        List<MType> mtypeList = new ArrayList<>();
        farmerKeyList.add(farmerPkey);
        farmerKeyList.add(Constant.Operation + MobileSession.appid());
        mtypeList.add(MType.INTEGRAL_GOODS);
        mtypeList.add(MType.INTEGRAL_BNYP_GOODS);
        mtypeList.add(MType.MARKET_GOODS);
        mtypeList.add(MType.SPECIAL_GOODS);
        mtypeList.add(MType.BOX_GOODS);
        System.out.println("tjv: " + tjv);
        if (checkTjv())
        {
            SelectBuilder<Integer, MktGoods> vbuilder = goodsDao.select()
            .eq("vendor", tjv)
            .in("farmer", farmerKeyList)
            .in("mType", mtypeList)
            .eq("threeGtype", threeGtype)
            .eq("enabled", true)
            .eq("idDel", false);
            SelectBuilder<Integer, MktGoods> nvbuilder = goodsDao.select()
             
                .in("farmer", farmerKeyList)
                .in("mType", mtypeList)
                .eq("threeGtype", threeGtype)
                .eq("enabled", true)
                .eq("idDel", false);
            if(GoodsSortType.SALED == goodsSortType)
            {
                vbuilder.sort("xsNum");
                nvbuilder.sort("xsNum");
            }
            else
            {
                vbuilder.sort("price", sortDesc);
                nvbuilder.sort("price", sortDesc);
            }
            List<AppGoodsV4OnList> content = vbuilder.sort("sort", false).execDto(AppGoodsV4OnList.class);
            List<AppGoodsV4OnList> list = nvbuilder
                .and()
                .or()
                    .notEq("vendor", tjv)
                    .isNull("vendor")
                .close()
                .close()
                .done()
                .sort("sort", false).execDto(AppGoodsV4OnList.class);
            content.addAll(list);
            res = PageUtil.page(content, PageParameter.of(page, pagesize));
        }
        else
        {
            SelectPageBuilder<Integer,MktGoods> builder = goodsDao.selectPage()
            .page(page)
            .pagesize(pagesize)
            .in("mType", mtypeList)
            .in("farmer", farmerKeyList)
            .eq("threeGtype", threeGtype)
            .eq("enabled", true)
            .eq("idDel", false);
            if(GoodsSortType.SALED == goodsSortType)
            {
                builder.sort("xsNum");
            }
            else
            {
                builder.sort("price", sortDesc);
            }
            res = builder.sort("sort", false).execDto(AppGoodsV4OnList.class);
        }
        for (AppGoodsV4OnList g : res.getContent())
        {
            if (g.getVendor() != null)
            {
                MktVendor mktVendor = vendorDao.get(g.getVendor());
                if (mktVendor != null) g.setVendorName(mktVendor.getDisplayName());
            }
            if (g.getSupplier() != null)
            {
                MktSupplier mktSupplier = supplierDao.get(g.getSupplier());
                if (mktSupplier != null)
                {
                    g.setSupplierName(mktSupplier.getName());
                    if(StringUtils.isBlank(g.getVendorName()))
                        g.setVendorName(g.getSupplierName());
                }
            }
            
            List<GoodsProcessOnInfo> processLines = new ArrayList<>();
            if(Boolean.TRUE.equals(g.getIsProcess()))
            {
                List<Integer> listProcess = goodsProcessDao.listProcess(g.getPkey());
                if(!listProcess.isEmpty())
                {
                    List<MktGoodsSpace> gsList = goodsSpaceDao.select()
                        .in("pkey", listProcess)
                        .exec();
                    gsList.forEach(e -> {
                        MktGoods mktGoods = goodsDao.selectOne().eq("pkey", e.getGoods())
                            .eq("mType", MType.PROCESS_GOODS)
                            .eq("idDel", false)
                            .eq("enabled", true)
                            .exec();
                        if(mktGoods != null)
                        {
                            GoodsProcessOnInfo p = new GoodsProcessOnInfo();
                            p.setProcess(e.getPkey());
                            p.setProcessName(mktGoods.getTitle());
                            if(StringUtils.isNotBlank(e.getPhoto1()))
                                e.setPhoto1(e.getPhoto1());
                            else if(mktGoods.getPhoto1() != null && !mktGoods.getPhoto1().isEmpty())
                                p.setPhoto(mktGoods.getPhoto1().get(0));
                            p.setPrice(e.getPrice());
                            processLines.add(p);
                        }
                    });
                }
            }
            g.setProcessLines(processLines);
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
        Map<String, Number> map = gwcDao.aggregation()
            .isNotNull("goods")
            .eq("member", memberPkey)
            .execGroupBySum("goods", "num");
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
            }
        }
        
    }
    
    private void assembleItemGwcNum(List<GoodsListItemV2> result)
    {
        Integer memberPkey = MobileSession.memberPkey();
        if (memberPkey == null) return;
        Map<String, Number> map = gwcDao.aggregation().eq("member", memberPkey).execGroupBySum("goods", "num");
        for (GoodsListItemV2 agal : result)
        {
            if (agal != null && agal.getGoods() != null)
            {
                String pkey = agal.getGoods().toString();
                if (map.containsKey(pkey))
                {
                    Number number = map.get(pkey);
                    if (number != null) agal.setGwcNum(number.intValue());
                }
                List<MktGoodsSpaceOnList> spaces = goodsSpaceDao.
                    select().eq("goods", agal.getGoods())
                    .sort("price", false).execDto(MktGoodsSpaceOnList.class);
                agal.setSpaces(spaces);
                List<GoodsProcessOnInfo> processLines = new ArrayList<>();
                if(Boolean.TRUE.equals(agal.getIsProcess()))
                {
                    List<Integer> listProcess = goodsProcessDao.listProcess(agal.getGoods());
                    if(!listProcess.isEmpty())
                    {
                        List<MktGoodsSpace> gsList = goodsSpaceDao.select()
                            .in("pkey", listProcess)
                            .exec();
                        
                        gsList.forEach(e -> {
                            MktGoods mktGoods = goodsDao.selectOne().eq("pkey", e.getGoods())
                                .eq("mType", MType.PROCESS_GOODS)
                                .eq("idDel", false)
                                .eq("enabled", true)
                                .exec();
                            if(mktGoods != null)
                            {
                                GoodsProcessOnInfo p = new GoodsProcessOnInfo();
                                p.setProcess(e.getPkey());
                                p.setProcessName(mktGoods.getTitle());
                                if(StringUtils.isNotBlank(e.getPhoto1()))
                                    e.setPhoto1(e.getPhoto1());
                                else if(mktGoods.getPhoto1() != null && !mktGoods.getPhoto1().isEmpty())
                                    p.setPhoto(mktGoods.getPhoto1().get(0));
                                p.setPrice(e.getPrice());
                                processLines.add(p);
                            }
                        });
                    }
                }
                agal.setProcessLines(processLines);
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
            if (spaceList != null && !spaceList.isEmpty())
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
                        if (mktGtype != null)
                        {
                            dto.setGtypeName(mktGtype.getName());
                            dto.setGtypeSort(mktGtype.getSort());
                        }
                        dto.setCreatedTime(e.getCreatedTime());
                        dto.setGtype(Integer.valueOf(s));
                        map.get(key).add(dto);
                    }
                }
            }
        }
        List<MktGtype> listMarketGtype = gtypeDao.listMarketGtype(MobileSession.farmerPkey(), MobileSession.appid());
        Map<Integer, List<AppVendorGtypeInfo>> linkedMap = new LinkedHashMap<>();
        for (MktGtype g : listMarketGtype)
        {
            if (map.containsKey(g.getPkey()))
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
        Collections.sort(res, new Comparator<AppVendorGtypeInfo>()
        {
            @Override
            public int compare(AppVendorGtypeInfo o1, AppVendorGtypeInfo o2)
            {
                return o1.getGtypeSort() - o2.getGtypeSort();
            }
        });
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
    
    public void openThread(String market, GoodsSortType sortType)
    {
        // 异步修改 缓存队列
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                goodListQueryer.resetAll(market, sortType);
            }
        }).start();
    }
    
    // 组合商品数据 用于存到缓存
    public List<GoodsListItem> addGoodsListItemCache(String market, Boolean enabled, Boolean xsNum)
    {
        SysFarmer sysFarmer = farmerDao.get(market);
        int ascription = sysFarmer.getAscription();
        // 获取市场下所有数据
        Map<Integer, List<GoodsListItem>> goodsMap;
        if (Boolean.TRUE.equals(xsNum))
        {
            goodsMap = assembleMapXsNum(market, enabled, ascription);
        }
        else
            goodsMap = assembleMap(market, enabled, ascription);
        // 按一级分类排序
        Map<Integer, List<GoodsListItem>> goodsLinkedMap = assembleGtypeMap(goodsMap, enabled, ascription);
        // 按二级和三级分类排序
        Map<Integer, List<GoodsListItem>> goodsLinkedV2Map =
            assembleTwoAndThreeMap(goodsLinkedMap, enabled, sysFarmer.getPkey(), ascription);
        
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
            .eq("mType", MType.MARKET_GOODS)
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
            .eq("mType", MType.MARKET_GOODS)
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
        Boolean enabled, int ascription)
    {
        List<MktGtype> gtypeList = gtypeDao.select()
            .eq("showMarket", true)
            .eq("enabled", enabled)
            .eq("idDel", false)
            .eq("ascription", ascription)
            .sort("marketSort", false)
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
                        value.add(goodsListItem);
                    }
                }
            }
            goodsLinkedV2Map.put(entry.getKey(), value);
        }
        return goodsLinkedV2Map;
    }
    
    public GroupResult<String, GoodsListItemV2> queryAppGtypeGoods(int from, int limit, Integer gtype,
        GoodsSortType sortType, Boolean sortDesc)
    {
        Integer topVendor = null;
        if (checkTjv())
        {
            MktMember member = MobileSession.member();
            topVendor = member.getTjv();
        }
        //if(Boolean.TRUE.equals(gtypeLeft))
        //{
        //    GroupResult<String, GoodsListItemV2> res = queryAppGtypeGoodsSQL(from, limit, gtype, sortType, sortDesc, MobileSession.farmerPkey(), topVendor);
        //    List<GroupList<String, GoodsListItemV2>> groups = res.getGroups();
        //    for (GroupList<String, GoodsListItemV2> group : groups)
        //    {
        //        List<GoodsListItemV2> data = group.getData();
        //        assembleItemGwcNum(data);
        //    }
        //    return res;
        //}
        MktGtype mktGtype = gtypeDao.get(gtype);
        GroupResult<String, GoodsListItemV2> res = goodListQueryer
            .rangeCat1(sortType, MobileSession.farmerPkey(), gtype, mktGtype.getSort(), from, limit, topVendor, sortDesc);
        List<GroupList<String, GoodsListItemV2>> groups = res.getGroups();
        for (GroupList<String, GoodsListItemV2> group : groups)
        {
            List<GoodsListItemV2> data = group.getData();
            assembleItemGwcNum(data);
        }
        return res;
    }
    
    public GroupResult<String, GoodsListItemV2> queryAppGtypeGoodsSQL(int from, int limit, Integer gtype,
        GoodsSortType sortType, Boolean sortDesc, String farmer, Integer topVendor)
    {
        MktGtype mktGtype = gtypeDao.get(gtype);
        List<GoodsListItemV2> l;
        if(topVendor != null)
        {
            l = topVendor(mktGtype, sortType, sortDesc, farmer, topVendor);
        }
        else
        {
            l = listGoodsListItem(mktGtype, sortType, sortDesc, farmer, topVendor);
        }
        SelectGroupBuilder<?, HasPkey<String>, String, GoodsListItemV2> s =
            new SelectGroupBuilder<>(null, null, String.class, GoodsListItemV2.class);
        GroupResult<String, GoodsListItemV2> r = s.groupBy("gtype")
            .groupValue("gtypeName").from(from).limit(limit).exec((t1, t2) -> {
            if ((t1 + t2) > l.size())
            {
                if (t1 > l.size())
                {
                    return new ArrayList<>();
                }
                return l.subList(t1, l.size());
            }
            return l.subList(t1, t1 + t2);
        });
       
        for(GroupList<String, GoodsListItemV2> g: r.getGroups())
        {
            MktGtype mg = gtypeDao.get(Integer.valueOf(g.getGroup().getKey()));
            g.getGroup().setValue(mg.getName());
            for(GoodsListItemV2 gli : g.getData())
            {
                MktGoods goods = goodsDao.selectOne()
                .eq("gtype", gli.getGtype())
                .eq("goodsMain", gli.getGoodsMain())
                .eq("threeGtype", gli.getThreeGtype())
                .iF(gli.getPrice() != null && gli.getPrice2() == null)
                    .eq("price", gli.getPrice())
                .endIf()
                .iF(gli.getPrice2() != null)
                    .eq("price", gli.getPrice2())
                .endIf()
                .iF(gli.getXsNum() != null && gli.getXsNum2() == null)
                    .ge("xsNum", gli.getXsNum())
                .endIf()
                .iF(gli.getXsNum2() != null)
                    .ge("xsNum", gli.getXsNum2())
                .endIf()
                .eq("enabled", true)
                .eq("idDel", false)
                .eq("farmer", farmer)
                .in("mType", MType.MARKET_GOODS, MType.SPECIAL_GOODS, MType.BOX_GOODS)
                .exec();
                gli.setGtypeName(mg.getName());
                MktGoodsMain mgm = goodsMainDao.get(gli.getGoodsMain());
                if(mgm != null)
                    gli.setName(mgm.getName());
                if(goods == null)
                {
                    continue;
                }
                BeanUtils.copyProperties(goods, gli);
                gli.setGoods(goods.getPkey());
                if (gli.getVendor() != null)
                {
                    MktVendor mktVendor = vendorDao.get(gli.getVendor());
                    if (mktVendor != null) gli.setVendorName(mktVendor.getDisplayName());
                }
                if (gli.getSupplier() != null)
                {
                    MktSupplier mktSupplier = supplierDao.get(gli.getSupplier());
                    if (mktSupplier != null) gli.setSupplierName(mktSupplier.getName());
                }
                List<MktGoodsSpaceOnList> spaces = goodsSpaceDao.listByGoodsSortByPrice(Arrays.asList(goods.getPkey()), false, MktGoodsSpaceOnList.class);
                gli.setSpaces(spaces);
                List<String> sellingPoints =
                    mktGoodsSellingPointDao.listContentByGoods(gli.getGoods(), goods.getAscription());
                gli.setSellingPoints(sellingPoints);
                
                // 加工商品
                List<GoodsProcessOnInfo> processLines = new ArrayList<>();
                if(Boolean.TRUE.equals(gli.getIsProcess()))
                {
                    List<Integer> listProcess = goodsProcessDao.listProcess(gli.getGoods());
                    if(!listProcess.isEmpty())
                    {
                        List<MktGoodsSpace> gsList = goodsSpaceDao.select()
                            .in("pkey", listProcess)
                            .exec();
                        gsList.forEach(e -> {
                            MktGoods mktGoods = goodsDao.selectOne().eq("pkey", e.getGoods())
                                .eq("mType", MType.PROCESS_GOODS)
                                .eq("idDel", false)
                                .eq("enabled", true)
                                .exec();
                            if(mktGoods != null)
                            {
                                GoodsProcessOnInfo p = new GoodsProcessOnInfo();
                                p.setProcess(e.getPkey());
                                p.setProcessName(mktGoods.getTitle());
                                if(StringUtils.isNotBlank(e.getPhoto1()))
                                    e.setPhoto1(e.getPhoto1());
                                else if(mktGoods.getPhoto1() != null && !mktGoods.getPhoto1().isEmpty())
                                    p.setPhoto(mktGoods.getPhoto1().get(0));
                                p.setPrice(e.getPrice());
                                processLines.add(p);
                            }
                        });
                    }
                }
                gli.setProcessLines(processLines);
            }
        }
        return r;
    }
    
    private List<GoodsListItemV2> listGoodsListItem(MktGtype mktGtype, GoodsSortType sortType, Boolean sortDesc, String farmer, Integer topVendor)
    {
        List<GoodsListItemV2> l = new ArrayList<>();
        if(GoodsSortType.PRICE.equals(sortType))
        {
            l = goodsDao
                .joinSelect()
                .and()
                .eq("farmer", farmer)
                .eq("idDel", false)
                .eq("enabled", true)
//                .ge("gtype", mktGtype.getSort())
                .or()
                .eq("mType", MType.MARKET_GOODS)
                .eq("mType", MType.BOX_GOODS)
                .eq("mType", MType.SPECIAL_GOODS)
                .close()
                .done()
                .groupby("gtype")
                .groupby("goodsMain")
                .groupby("threeGtype")
                .min("price", "price")
                .join(MktGoodsMainThree.class, "threeGtype", "pkey")
                    .eq("enabled", true)
                .join(MktGoodsMain.class, "goodsMain", "pkey")
                    .eq("enabled", true)
                .join(MktGtype.class, "gtype", "pkey")
                    .eq("enabled", true)
                    .ge("sort", mktGtype.getSort())
                .endJoin()
                .sort(2, "sort", false)
                .sort(2, "pkey", false)
                .sort(1, "sort", false)
                .sort(1, "pkey", false)
                .sort("price", sortDesc)
                .sort(0, "sort", false)
                .sort(0, "pkey", false)
                .exec(GoodsListItemV2.class);
        }
        else
        {
            l = goodsDao
                .joinSelect()
                .and()
                .eq("farmer", farmer)
                .eq("idDel", false)
                .eq("enabled", true)
                .ge("gtype", mktGtype.getSort())
                .or()
                .eq("mType", MType.MARKET_GOODS)
                .eq("mType", MType.BOX_GOODS)
                .eq("mType", MType.SPECIAL_GOODS)
                .close()
                .done()
                .groupby("gtype")
                .groupby("goodsMain")
                .groupby("threeGtype")
                .max("xsNum", "xsNum")
                .join(MktGoodsMainThree.class, "threeGtype", "pkey")
                    .eq("enabled", true)
                .join(MktGoodsMain.class, "goodsMain", "pkey")
                    .eq("enabled", true)
                .join(MktGtype.class, "gtype", "pkey")
                    .eq("enabled", true)
                    .ge("sort", mktGtype.getSort())
                .endJoin()
                .sort(2, "sort", false)
                .sort(2, "pkey", false)
                .sort(1, "sort", false)
                .sort(1, "pkey", false)
                .sort("xsNum")
                .sort(0, "sort", false)
                .sort(0, "pkey", false)
                .exec(GoodsListItemV2.class);
        }
        return l;
    }
    
    private List<GoodsListItemV2> topVendor(MktGtype mktGtype, GoodsSortType sortType, Boolean sortDesc, String farmer, Integer topVendor)
    {
        if(GoodsSortType.PRICE.equals(sortType))
        {
            return goodsDao
                .joinSelect()
                  .and()
                    .eq("farmer", farmer)
                    .eq("idDel", false)
                    .eq("enabled", true)
                    .or()
                      .eq("mType", MType.MARKET_GOODS)
                      .eq("mType", MType.BOX_GOODS)
                      .eq("mType", MType.SPECIAL_GOODS)
                    .close()
                  .done()
                  .groupby("gtype")
                  .groupby("goodsMain")
                  .groupby("threeGtype")
                  .min("price", "price")
                .join(MktGoodsMainThree.class, "threeGtype", "pkey")
                  .eq("enabled", true)
                .join(MktGoodsMain.class, "goodsMain", "pkey")
                  .eq("enabled", true)
                .join(MktGtype.class, "gtype", "pkey")
                  .eq("enabled", true)
                  .ge("sort", mktGtype.getSort())
                .join(MktGoods.class, "pkey", "pkey")
                  .on()  //在 MktGoods 的 On 条件里增加额外筛选条件
                    .and()
                      .eq("vendor", topVendor)
                      .eq("idDel", false)
                      .eq("enabled", true)
                      .or()
                        .eq("mType", MType.MARKET_GOODS)
                        .eq("mType", MType.BOX_GOODS)
                        .eq("mType", MType.SPECIAL_GOODS)
                      .close()
                    .done()
                  .endOn()
                  .min("price", "price2")
                .endJoin()
                .sort(2, "sort", false)
                .sort(2, "pkey", false)
                .sort(1, "sort", false)
                .sort(1, "pkey", false)
                .sort(3, "price2", sortDesc)
                .sort("price", sortDesc)
                .sort("threeGtype")
                .sort(0, "sort", false)
                .sort(0, "pkey", false)
                .exec(GoodsListItemV2.class);
        }
        else
        {
            return goodsDao
                .joinSelect()
                  .and()
                    .eq("farmer", farmer)
                    .eq("idDel", false)
                    .eq("enabled", true)
                    .or()
                      .eq("mType", MType.MARKET_GOODS)
                      .eq("mType", MType.BOX_GOODS)
                      .eq("mType", MType.SPECIAL_GOODS)
                    .close()
                  .done()
                  .groupby("gtype")
                  .groupby("goodsMain")
                  .groupby("threeGtype")
                  .max("xsNum", "xsNum")
                .join(MktGoodsMainThree.class, "threeGtype", "pkey")
                  .eq("enabled", true)
                .join(MktGoodsMain.class, "goodsMain", "pkey")
                  .eq("enabled", true)
                .join(MktGtype.class, "gtype", "pkey")
                  .eq("enabled", true)
                  .ge("sort", mktGtype.getSort())
                .join(MktGoods.class, "pkey", "pkey")
                  .on()  //在 MktGoods 的 On 条件里增加额外筛选条件
                    .and()
                      .eq("vendor", topVendor)
                      .eq("idDel", false)
                      .eq("enabled", true)
                      .or()
                        .eq("mType", MType.MARKET_GOODS)
                        .eq("mType", MType.BOX_GOODS)
                        .eq("mType", MType.SPECIAL_GOODS)
                      .close()
                    .done()
                  .endOn()
                  .max("xsNum", "xsNum2")
                .endJoin()
                .sort(2, "sort", false)
                .sort(2, "pkey", false)
                .sort(1, "sort", false)
                .sort(1, "pkey", false)
                .sort(3, "xsNum2", sortDesc)
                .sort("xsNum", sortDesc)
                .sort(0, "sort", false)
                .sort(0, "pkey", false)
                .exec(GoodsListItemV2.class);
        }
        
    }
    
    public void assembleGoodsListItem(GroupResult<String, GoodsListItemV2> r)
    {
        for (GroupList<String, GoodsListItemV2> group : r.getGroups())
        {
            List<GoodsListItemV2> data = group.getData();
            for (GoodsListItemV2 gli : data)
            {
                MktGtype mktGtype = gtypeDao.get(gli.getGtype());
                gli.setGtypeName(mktGtype.getName());
                MktGoodsMain goodsMain = goodsMainDao.getGoodsMain(gli.getGoodsMain());
                gli.setName(goodsMain.getName());
                if (gli.getVendor() != null)
                {
                    MktVendor mktVendor = vendorDao.get(gli.getVendor());
                    if (mktVendor != null) gli.setVendorName(mktVendor.getDisplayName());
                }
                if (gli.getSupplier() != null)
                {
                    MktSupplier mktSupplier = supplierDao.get(gli.getSupplier());
                    if (mktSupplier != null) gli.setSupplierName(mktSupplier.getName());
                }
                MktGoods mktGoods = goodsDao.get(gli.getGoods());
                BeanUtils.copyProperties(mktGoods, gli);
                List<String> sellingPoints =
                    mktGoodsSellingPointDao.listContentByGoods(gli.getGoods(), mktGoods.getAscription());
                gli.setSellingPoints(sellingPoints);
            }
        }
    }
    
    private GroupResult<String, GoodsListItemV2> queryAppGoodsMainGoodsRecommend(int from, int limit,
        GoodsSortType sortType, Boolean sortDesc)
    {
        List<GoodsListItemV2> list = mktGoodsRecommendDao.joinSelect()
            .limit(from, limit)
            .isNull(MktGoodsRecommend.F.sourceGoods)
            .eq(MktGoodsRecommend.F.ascription, MobileSession.appid())
            .join(MktGoodsRecommendZone.class, MktGoodsRecommend.F.pkey, MktGoodsRecommendZone.F.goodsRecommend)
            .eq(MktGoodsRecommendZone.F.zone, GoodsRecommendZone.CATEGORY)
            .join(MktGoods.class, MktGoodsRecommend.F.goods, MktGoods.F.pkey)
            .as(MktGoods.F.pkey)
            .as(MktGoods.F.pkey, "goods")
            .as(MktGoods.F.gtype)
            .as(MktGoods.F.goodsMain)
            .as(MktGoods.F.vendor)
            .as(MktGoods.F.supplier)
            .eq(MktGoods.F.enabled, true)
            .eq(MktGoods.F.idDel, false)
            .endJoin()
            // 先按销量/价格排序，其次按推荐商品排序，再其次根据商品排序
            .sort(1, GoodsSortType.PRICE.equals(sortType) ? "price" : "xsNum", sortDesc)
            .sort("sort", false)
            .sort(1, "sort", false)
            .sort(1, "pkey", false)
            .exec(GoodsListItemV2.class);
        
        GroupList<String, GoodsListItemV2> group = new GroupList<>();
        group.setGroup(new KeyValue<>(Constant.GoodsMainRecommend.pkey.toString(), Constant.GoodsMainRecommend.name));
        group.setData(list);
        List<GroupList<String, GoodsListItemV2>> groups = new ArrayList<>();
        groups.add(group);
        GroupResult<String, GoodsListItemV2> res = new GroupResult<>();
        res.setGroups(groups);
        res.setStart(from);
        res.setLimit(limit);
        res.setNextStart(from + limit);

        assembleGoodsListItem(res);
        return res;
    }
    
    public GroupResult<String, GoodsListItemV2> queryAppGoodsMainGoods(int from, int limit, Integer goodsMain,
        GoodsSortType sortType, Boolean sortDesc, Boolean limitGoodsMain)
    {
        long k1 = System.currentTimeMillis();
        // 分类页商品推荐
        if (Constant.GoodsMainRecommend.pkey.equals(goodsMain))
        {
            return queryAppGoodsMainGoodsRecommend(from, limit, sortType, sortDesc);
        }
        Integer topVendor = null;
        if (checkTjv())
        {
            MktMember member = MobileSession.member();
            topVendor = member.getTjv();
        }
        MktGoodsMain mktGoodsMain = goodsMainDao.get(goodsMain);
        GroupResult<String, GoodsListItemV2> res;
        //if(Boolean.TRUE.equals(gtypeLeft))
        //{
        //    res = queryAppGoodsMainGoodsSQL(from, limit, sortType, sortDesc, mktGoodsMain, topVendor, limitGoodsMain);
        //}
        //else
        //{
            res = goodListQueryer.rangeCat2(sortType,
                MobileSession.farmerPkey(),
                mktGoodsMain.getGtype(),
                goodsMain,
                mktGoodsMain.getSort(),
                from,
                limit,
                topVendor,
                sortDesc,
                limitGoodsMain);
        //}
        
        for (GroupList<String, GoodsListItemV2> group : res.getGroups())
        {
            List<GoodsListItemV2> data = group.getData();
            for (GoodsListItemV2 gli : data)
            {
                if (gli.getVendor() != null)
                {
                    MktVendor mktVendor = mktVendorDao.get(gli.getVendor());
                    gli.setVendorName(mktVendor.getDisplayName());
                }
                List<GoodsProcessOnInfo> processLines = new ArrayList<>();
                if(Boolean.TRUE.equals(gli.getIsProcess()))
                {
                    List<Integer> listProcess = goodsProcessDao.listProcess(gli.getGoods());
                    if(!listProcess.isEmpty())
                    {
                        List<MktGoodsSpace> gsList = goodsSpaceDao.select()
                            .in("pkey", listProcess)
                            .exec();
//                        List<GoodsProcessOnInfo> processLines = new ArrayList<>();
                        gsList.forEach(e -> {
                            MktGoods mktGoods = goodsDao.selectOne().eq("pkey", e.getGoods())
                                .eq("mType", MType.PROCESS_GOODS)
                                .eq("idDel", false)
                                .eq("enabled", true)
                                .exec();
                            if(mktGoods != null)
                            {
                                GoodsProcessOnInfo p = new GoodsProcessOnInfo();
                                p.setProcess(e.getPkey());
                                p.setProcessName(mktGoods.getTitle());
                                if(StringUtils.isNotBlank(e.getPhoto1()))
                                    e.setPhoto1(e.getPhoto1());
                                else if(mktGoods.getPhoto1() != null && !mktGoods.getPhoto1().isEmpty())
                                    p.setPhoto(mktGoods.getPhoto1().get(0));
                                p.setPrice(e.getPrice());
                                processLines.add(p);
                            }
                        });
                    }
                }
                gli.setProcessLines(processLines);
            }
        }
        System.out.println("提前存耗时: " + (System.currentTimeMillis() - k1));
        return res;
    }
    
    public GroupResult<String, GoodsListItemV2> queryAppGoodsMainGoodsSQL(int from, int limit, 
        Integer goodsMain, GoodsSortType sortType,
        Boolean sortDesc, Boolean limitGoodsMain, int deliveryType)
    {
        long k1 = System.currentTimeMillis();
        String farmer = MobileSession.farmerPkey();
        List<GoodsListItemV2> l;
        
        // 分类页商品推荐
        if (Constant.GoodsMainRecommend.pkey.equals(goodsMain))
        {
            return queryAppGoodsMainGoodsRecommend(from, limit, sortType, sortDesc);
        }
        Integer topVendor = null;
        if (checkTjv())
        {
            MktMember member = MobileSession.member();
            topVendor = member.getTjv();
        }
        
        MktGoodsMain mktGoodsMain = goodsMainDao.get(goodsMain);
        MktGoodsMain gm2 = null;
        if(mktGoodsMain.getSysTwoGtype() != null)
        {
            gm2 = goodsMainDao.get(mktGoodsMain.getSysTwoGtype());
            if(gm2.getEnabled().equals(false) || gm2.getIdDel().equals(true))
                gm2 = null;
        }
        l = gmListItemV2Assembly(mktGoodsMain, gm2, sortType, sortDesc, limitGoodsMain, topVendor);

        SelectGroupBuilder<?, HasPkey<String>, String, GoodsListItemV2> s =
            new SelectGroupBuilder<>(null, null, String.class, GoodsListItemV2.class);
        GroupResult<String, GoodsListItemV2> r = s.groupBy("goodsMain")
            .groupValue("name").from(from).limit(limit).exec((t1, t2) -> {
            if ((t1 + t2) > l.size())
            {
                if (t1 > l.size())
                {
                    return new ArrayList<>();
                }
                return l.subList(t1, l.size());
            }
            return l.subList(t1, t1 + t2);
        });
       
        for(GroupList<String, GoodsListItemV2> g: r.getGroups())
        {
            MktGoodsMain mgm = goodsMainDao.get(Integer.valueOf(g.getGroup().getKey()));
            g.getGroup().setValue(mgm.getName());
            MktGtype mg = gtypeDao.get(mgm.getGtype());
            
            Iterator<GoodsListItemV2> iterator = g.getData().iterator();
            while(iterator.hasNext())
            {
                GoodsListItemV2 gli = iterator.next();
                List<String> farmerKeyList = new ArrayList<>();
                List<MType> mtypeList = new ArrayList<>();
                List<Integer> gmList = new ArrayList<>();
                if(deliveryType == 0 || deliveryType == 2)
                {
                    farmerKeyList.add(farmer);
                    mtypeList.add(MType.MARKET_GOODS);
                    mtypeList.add(MType.SPECIAL_GOODS);
                    mtypeList.add(MType.BOX_GOODS);
                    gmList.add(gli.getGoodsMain());
                }
                if(deliveryType == 0 || deliveryType == 1)
                {
                    farmerKeyList.add(Constant.Operation + mg.getAscription());
                    mtypeList.add(MType.INTEGRAL_GOODS);
                    mtypeList.add(MType.INTEGRAL_BNYP_GOODS);
                    if(mgm.getSysTwoGtype() != null)
                    {
                        gmList.add(mgm.getSysTwoGtype());
                    }
                }
              
                MktGoods goods = goodsDao.selectOne()
                .eq("threeGtype", gli.getThreeGtype())
                .in("goodsMain", gmList)
                .iF(gli.getPrice() != null && gli.getPrice2() == null)
                    .eq("price", gli.getPrice())
                .endIf()
                .iF(gli.getPrice2() != null)
                    .eq("price", gli.getPrice2())
                .endIf()
                .iF(gli.getXsNum() != null && gli.getXsNum2() == null)
                    .ge("xsNum", gli.getXsNum())
                .endIf()
                .iF(gli.getXsNum2() != null)
                    .ge("xsNum", gli.getXsNum2())
                .endIf()
                .eq("enabled", true)
                .eq("idDel", false)
                .in("farmer", farmerKeyList)
                .in("mType", mtypeList)
                .exec();
                gli.setGtypeName(mg.getName());
                gli.setName(mgm.getName());
                
                if(goods == null)
                {
                    log.info("该分类下没有商品");
                    iterator.remove();
                    continue;
                }
                BeanUtils.copyProperties(goods, gli);
                gli.setGoods(goods.getPkey());
                List<String> sellingPoints =
                    mktGoodsSellingPointDao.listContentByGoods(gli.getGoods(), goods.getAscription());
                gli.setSellingPoints(sellingPoints);
                List<MktGoodsSpaceOnList> spaces = goodsSpaceDao.listByGoodsSortByPrice(Arrays.asList(goods.getPkey()), false, MktGoodsSpaceOnList.class);
                gli.setSpaces(spaces);
                
                if (gli.getVendor() != null)
                {
                    MktVendor mktVendor = vendorDao.get(gli.getVendor());
                    if (mktVendor != null) gli.setVendorName(mktVendor.getDisplayName());
                }
                if (gli.getSupplier() != null)
                {
                    MktSupplier mktSupplier = supplierDao.get(gli.getSupplier());
                    if (mktSupplier != null) gli.setSupplierName(mktSupplier.getName());
                }
                // 加工商品
                List<GoodsProcessOnInfo> processLines = new ArrayList<>();
                if(Boolean.TRUE.equals(gli.getIsProcess()))
                {
                    List<Integer> listProcess = goodsProcessDao.listProcess(gli.getGoods());
                    if(!listProcess.isEmpty())
                    {
                        List<MktGoodsSpace> gsList = goodsSpaceDao.select()
                            .in("pkey", listProcess)
                            .exec();
                        gsList.forEach(e -> {
                            MktGoods mktGoods = goodsDao.selectOne().eq("pkey", e.getGoods())
                                .eq("mType", MType.PROCESS_GOODS)
                                .eq("idDel", false)
                                .eq("enabled", true)
                                .exec();
                            if(mktGoods != null)
                            {
                                GoodsProcessOnInfo p = new GoodsProcessOnInfo();
                                p.setProcess(e.getPkey());
                                p.setProcessName(mktGoods.getTitle());
                                if(StringUtils.isNotBlank(e.getPhoto1()))
                                    e.setPhoto1(e.getPhoto1());
                                else if(mktGoods.getPhoto1() != null && !mktGoods.getPhoto1().isEmpty())
                                    p.setPhoto(mktGoods.getPhoto1().get(0));
                                p.setPrice(e.getPrice());
                                processLines.add(p);
                            }
                        });
                    }
                }
                gli.setProcessLines(processLines);
            }
        }
        System.out.println("sql直接查耗时: " + (System.currentTimeMillis() - k1));
        return r;
    }
    
    public Boolean correlationGoodsMain(Integer goodsMain)
    {
        MktGoodsMain mktGoodsMain = goodsMainDao.get(goodsMain);
        if(mktGoodsMain.getSysTwoGtype() == null)
            return false;
        MktGoodsMain gm2 = goodsMainDao.get(mktGoodsMain.getSysTwoGtype());
        if(gm2.getEnabled().equals(false) || gm2.getIdDel().equals(true))
            return false;
        long count = goodsDao.aggregation().eq("goodsMain", mktGoodsMain.getSysTwoGtype())
        .eq("enabled", true).eq("idDel", false)
        .execCount();
        return count > 0;
    }
    
    public List<GoodsListItemV2> topGoodsMainVendor(MktGoodsMain mktGoodsMain, GoodsSortType sortType, 
        Boolean sortDesc, Integer topVendor, Boolean limitGoodsMain)
    {
        List<GoodsListItemV2> l;
        String farmer = mktGoodsMain.getFarmer();
        if(GoodsSortType.PRICE.equals(sortType))
        {
            l = goodsDao
                .joinSelect()
                .and()
                .eq("farmer", farmer)
                .eq("idDel", false)
                .eq("enabled", true)
                .eq("gtype", mktGoodsMain.getGtype())
                .iF(Boolean.TRUE.equals(limitGoodsMain))
                    .eq("goodsMain", mktGoodsMain.getPkey())
                .endIf()
                .in("mType", MType.MARKET_GOODS, MType.BOX_GOODS, MType.SPECIAL_GOODS)
                .done()
                .groupby("goodsMain")
                .groupby("threeGtype")
                .min("price", "price")
                .join(MktGoodsMainThree.class, "threeGtype", "pkey")
                    .eq("enabled", true)
                .join(MktGoodsMain.class, "goodsMain", "pkey")
                    .eq("enabled", true)
                    .ge("sort", mktGoodsMain.getSort())
                .join(MktGtype.class, "gtype", "pkey")
                    .eq("enabled", true)
                .join(MktGoods.class, "pkey", "pkey")
                    .on()  //在 MktGoods 的 On 条件里增加额外筛选条件
                      .and()
                        .eq("vendor", topVendor)
                        .eq("idDel", false)
                        .eq("enabled", true)
                        .in("mType", MType.MARKET_GOODS, MType.BOX_GOODS, MType.SPECIAL_GOODS)
                      .done()
                    .endOn()
                    .min("price", "price2")
                .endJoin()
                .sort(2, "sort", false)
                .sort(2, "pkey", false)
                .sort(1, "sort", false)
                .sort(1, "pkey", false)
                .sort(3, "price2", sortDesc)
                .sort("price", sortDesc)
                .sort(0, "sort", false)
                .sort(0, "pkey", false)
                .exec(GoodsListItemV2.class);
        }
        else
        {
            l = goodsDao
                .joinSelect()
                .and()
                .eq("farmer", farmer)
                .eq("idDel", false)
                .eq("enabled", true)
                .eq("gtype", mktGoodsMain.getGtype())
                .iF(Boolean.TRUE.equals(limitGoodsMain))
                    .eq("goodsMain", mktGoodsMain.getPkey())
                .endIf()
                .in("mType", MType.MARKET_GOODS, MType.BOX_GOODS, MType.SPECIAL_GOODS)
                .done()
                .groupby("goodsMain")
                .groupby("threeGtype")
                .max("xsNum", "xsNum")
                .join(MktGoodsMainThree.class, "threeGtype", "pkey")
                    .eq("enabled", true)
                .join(MktGoodsMain.class, "goodsMain", "pkey")
                    .eq("enabled", true)
                    .ge("sort", mktGoodsMain.getSort())
                .join(MktGtype.class, "gtype", "pkey")
                    .eq("enabled", true)
                .join(MktGoods.class, "pkey", "pkey")
                .on()  //在 MktGoods 的 On 条件里增加额外筛选条件
                  .and()
                    .eq("vendor", topVendor)
                    .eq("idDel", false)
                    .eq("enabled", true)
                    .in("mType", MType.MARKET_GOODS, MType.BOX_GOODS, MType.SPECIAL_GOODS)
                  .done()
                .endOn()
                .max("xsNum", "xsNum2")
                .endJoin()
                .sort(2, "sort", false)
                .sort(2, "pkey", false)
                .sort(1, "sort", false)
                .sort(1, "pkey", false)
                .sort(3, "xsNum2", sortDesc)
                .sort("xsNum", sortDesc)
                .sort(0, "sort", false)
                .sort(0, "pkey", false)
                .exec(GoodsListItemV2.class);
        }
        List<MktGoods> list = goodsDao.select()
            .eq("goodsMain", mktGoodsMain.getPkey())
            .eq("vendor", topVendor)
            .eq("idDel", false)
            .eq("enabled", true)
            .in("mType", MType.MARKET_GOODS, MType.BOX_GOODS, MType.SPECIAL_GOODS)
            .exec();
        for(GoodsListItemV2 g : l)
        {
            for(MktGoods mg : list)
            {
                if(g.getGoodsMain().equals(mg.getGoodsMain()) 
                    && g.getThreeGtype().equals(mg.getThreeGtype())
                    && mg.getVendor().equals(topVendor))
                    g.setVendor(topVendor);
            }
        }
        return l;
    }
    
    public List<GoodsListItemV2> gmListItemV2Assembly(MktGoodsMain gm1, MktGoodsMain gm2, GoodsSortType sortType, 
        Boolean sortDesc, Boolean limitGoodsMain, Integer topVendor)
    {
        List<GoodsListItemV2> l1 = null;
        if(topVendor != null)
        {
            l1 = topGoodsMainVendor(gm1, sortType, sortDesc, topVendor, limitGoodsMain);
        }
        else
            l1 = goodsMainListItemV2(gm1, sortType, sortDesc, limitGoodsMain);
        List<GoodsListItemV2> l2 = null;
        if(gm2 != null)
            l2 = goodsMainListItemV2Sys(gm2, sortType, sortDesc, limitGoodsMain);
        List<GoodsListItemV2> res = new ArrayList<>();
        List<GoodsListItemV2> sortList = new ArrayList<>();
        
        for(GoodsListItemV2 g : l1)
        {
            if(g.getVendor() != null && g.getVendor().equals(topVendor))
            {
                res.add(g);
            }
            else
                sortList.add(g);
        }
        if(l2 != null)
        {
            for(GoodsListItemV2 g: l2)
            {
//                g.setGtype(gm1.getGtype());
                g.setGoodsMain(gm1.getPkey());
            }
            sortList.addAll(l2);
        }
        Collections.sort(sortList, new Comparator<GoodsListItemV2>() 
        {
            @Override
            public int compare(GoodsListItemV2 o1, GoodsListItemV2 o2)
            {
                if(GoodsSortType.PRICE.equals(sortType))
                {
                    if(Boolean.TRUE.equals(sortDesc))
                        return o2.getPrice().compareTo(o1.getPrice());
                    return o1.getPrice().compareTo(o2.getPrice());
                }
                return o2.getXsNum() - o1.getXsNum();
            }
        });
        res.addAll(sortList);
        return res;
    }
    
    public List<GoodsListItemV2> goodsMainListItemV2(MktGoodsMain mktGoodsMain, GoodsSortType sortType, 
        Boolean sortDesc, Boolean limitGoodsMain)
    {
        List<GoodsListItemV2> l;
        String farmer = mktGoodsMain.getFarmer();
        if(GoodsSortType.PRICE.equals(sortType))
        {
            l = goodsDao
                .joinSelect()
                .and()
                .eq("farmer", farmer)
                .eq("idDel", false)
                .eq("enabled", true)
                .eq("gtype", mktGoodsMain.getGtype())
                .iF(Boolean.TRUE.equals(limitGoodsMain))
                    .eq("goodsMain", mktGoodsMain.getPkey())
                .endIf()
                .in("mType", MType.MARKET_GOODS, MType.BOX_GOODS, MType.SPECIAL_GOODS)
//                .or()
//                    .eq("mType", MType.MARKET_GOODS)
//                    .eq("mType", MType.BOX_GOODS)
//                    .eq("mType", MType.SPECIAL_GOODS)
//                .close()
                .done()
                .groupby("goodsMain")
                .groupby("threeGtype")
                .min("price", "price")
                .join(MktGoodsMainThree.class, "threeGtype", "pkey")
                    .eq("enabled", true)
                .join(MktGoodsMain.class, "goodsMain", "pkey")
                    .eq("enabled", true)
                    .ge("sort", mktGoodsMain.getSort())
                .join(MktGtype.class, "gtype", "pkey")
                    .eq("enabled", true)
                .endJoin()
                .sort(2, "sort", false)
                .sort(2, "pkey", false)
                .sort(1, "sort", false)
                .sort(1, "pkey", false)
                .sort("price", sortDesc)
                .sort(0, "sort", false)
                .sort(0, "pkey", false)
                .exec(GoodsListItemV2.class);
        }
        else
        {
            l = goodsDao
                .joinSelect()
                .and()
                .eq("farmer", farmer)
                .eq("idDel", false)
                .eq("enabled", true)
                .eq("gtype", mktGoodsMain.getGtype())
                .iF(Boolean.TRUE.equals(limitGoodsMain))
                    .eq("goodsMain", mktGoodsMain.getPkey())
                .endIf()
                .in("mType", MType.MARKET_GOODS, MType.BOX_GOODS, MType.SPECIAL_GOODS)
//                .or()
//                    .eq("mType", MType.MARKET_GOODS)
//                    .eq("mType", MType.BOX_GOODS)
//                    .eq("mType", MType.SPECIAL_GOODS)
//                .close()
                .done()
                .groupby("goodsMain")
                .groupby("threeGtype")
                .max("xsNum", "xsNum")
                .join(MktGoodsMainThree.class, "threeGtype", "pkey")
                    .eq("enabled", true)
                .join(MktGoodsMain.class, "goodsMain", "pkey")
                    .eq("enabled", true)
                    .ge("sort", mktGoodsMain.getSort())
                .join(MktGtype.class, "gtype", "pkey")
                    .eq("enabled", true)
                .endJoin()
                .sort(2, "sort", false)
                .sort(2, "pkey", false)
                .sort(1, "sort", false)
                .sort(1, "pkey", false)
                .sort("xsNum")
                .sort(0, "sort", false)
                .sort(0, "pkey", false)
                .exec(GoodsListItemV2.class);
        }
        return l;
    }
    
    public List<GoodsListItemV2> goodsMainListItemV2Sys(MktGoodsMain mktGoodsMain, GoodsSortType sortType, 
        Boolean sortDesc, Boolean limitGoodsMain)
    {
        List<GoodsListItemV2> l;
        String farmer = mktGoodsMain.getFarmer();
        if(GoodsSortType.PRICE.equals(sortType))
        {
            l = goodsDao
                .joinSelect()
                .and()
                .eq("farmer", farmer)
                .eq("idDel", false)
                .eq("enabled", true)
                .eq("gtype", mktGoodsMain.getGtype())
                .iF(Boolean.TRUE.equals(limitGoodsMain))
                .eq("goodsMain", mktGoodsMain.getPkey())
                .endIf()
                .or()
                .eq("mType", MType.INTEGRAL_GOODS)
                .eq("mType", MType.INTEGRAL_BNYP_GOODS)
                .close()
                .done()
                .groupby("goodsMain")
                .groupby("threeGtype")
                .min("price", "price")
                .join(MktGoodsMainThree.class, "threeGtype", "pkey")
                .eq("enabled", true)
                .join(MktGoodsMain.class, "goodsMain", "pkey")
                .eq("enabled", true)
                .ge("sort", mktGoodsMain.getSort())
                .join(MktGtype.class, "gtype", "pkey")
                .eq("enabled", true)
                .endJoin()
                .sort(2, "sort", false)
                .sort(2, "pkey", false)
                .sort(1, "sort", false)
                .sort(1, "pkey", false)
                .sort("price", sortDesc)
                .sort(0, "sort", false)
                .sort(0, "pkey", false)
                .exec(GoodsListItemV2.class);
        }
        else
        {
            l = goodsDao
                .joinSelect()
                .and()
                .eq("farmer", farmer)
                .eq("idDel", false)
                .eq("enabled", true)
                .eq("gtype", mktGoodsMain.getGtype())
                .iF(Boolean.TRUE.equals(limitGoodsMain))
                .eq("goodsMain", mktGoodsMain.getPkey())
                .endIf()
                .or()
                .eq("mType", MType.INTEGRAL_GOODS)
                .eq("mType", MType.INTEGRAL_BNYP_GOODS)
                .close()
                .done()
                .groupby("goodsMain")
                .groupby("threeGtype")
                .max("xsNum", "xsNum")
                .join(MktGoodsMainThree.class, "threeGtype", "pkey")
                .eq("enabled", true)
                .join(MktGoodsMain.class, "goodsMain", "pkey")
                .eq("enabled", true)
                .ge("sort", mktGoodsMain.getSort())
                .join(MktGtype.class, "gtype", "pkey")
                .eq("enabled", true)
                .endJoin()
                .sort(2, "sort", false)
                .sort(2, "pkey", false)
                .sort(1, "sort", false)
                .sort(1, "pkey", false)
                .sort("xsNum")
                .sort(0, "sort", false)
                .sort(0, "pkey", false)
                .exec(GoodsListItemV2.class);
        }
        return l;
    }
    
    
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
        
        List<MktGoodsMain> goodsMainList =
            goodsMainDao.listSortFalse(null, true, MobileSession.farmerPkey(), MobileSession.appid());
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
        
        for(Integer gm : map.keySet())
        {
            if (priceSort != null)
            {
                List<GoodsListItem> priceList = map.get(gm);
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
                List<GoodsListItem> priceList = map.get(gm);
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
        if (StringUtils.isBlank(farmer))
        {
            List<SysFarmer> exec = farmerDao.select().notLike("pkey", Constant.Operation).exec();
            farmerKeys.addAll(CollectionUtil.keyList(exec));
        }
        else
            farmerKeys.add(farmer);
        
        // 价格排序
        if (sortType == null || GoodsSortType.PRICE.equals(sortType))
        {
            for (String f : farmerKeys)
            {
                List<GoodsListItem> priceList = addGoodsListItemCache(f, null, false);
                List<ThreeGtypeSortEntity> tgPriceList = new ArrayList<>();
                for (GoodsListItem g : priceList)
                {
                    if (g.getSpaces() == null || g.getSpaces().isEmpty()) continue;
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
        if (sortType == null || GoodsSortType.SALED.equals(sortType))
        {
            for (String f : farmerKeys)
            {
                List<GoodsListItem> xsNumList = addGoodsListItemCache(f, null, true);
                List<ThreeGtypeSortEntity> tgXsNumList = new ArrayList<>();
                for (GoodsListItem g : xsNumList)
                {
                    if (g.getSpaces() == null || g.getSpaces().isEmpty()) continue;
                    ThreeGtypeSortEntity tgs = BeanUtil.beanFrom(ThreeGtypeSortEntity.class, g);
                    tgs.setSortType(GoodsSortType.SALED);
                    tgs.setSortValue(BigDecimal.valueOf(g.getXsNum()));
                    tgs.setGoods(g.getPkey());
                    // 取销量最大的规格
                    Optional<MktGoodsSpaceOnList> optional =
                        g.getSpaces().stream().max(Comparator.comparing(MktGoodsSpaceOnList::getXsNum));
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

package cn.tofocus.lejia.domain.app;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.app.market.MktGwcDetailsDTO;
import cn.tofocus.lejia.bean.dto.app.market.MktGwcOnList;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsBox;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.bean.entity.jd.JdGoods;
import cn.tofocus.lejia.bean.entity.market.MktCookfdLine;
import cn.tofocus.lejia.bean.entity.market.MktGwc;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerConfig;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.cache.SpaceKcCache;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.goods.MktGoodsBoxDao;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsSpaceDao;
import cn.tofocus.lejia.dao.jd.JdGoodsDao;
import cn.tofocus.lejia.dao.market.MktCookfdLineDao;
import cn.tofocus.lejia.dao.market.MktGwcDao;
import cn.tofocus.lejia.dao.sys.SysFarmerConfigDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.exception.WsaleErrCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppMemberGwcManager
{
    @Autowired
    private MktGwcDao gwcDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private MktGoodsSpaceDao goodsSpaceDao;
    
    @Autowired
    private JdGoodsDao jdGoodsDao;
    
    @Autowired
    private SysFarmerDao farmerDao;
    
    @Autowired
    private MktCookfdLineDao cookFdLineDao;
    
    @Autowired
    private SpaceKcCache spaceKcCache;
    
    @Autowired
    private SysFarmerConfigDao farmerConfigDao;
    
    @Autowired
    private MktGoodsBoxDao goodsBoxDao;
    
    @Value("${tofocus.file.baseUrl}")
    private String fileStart;
    
    @Transactional(rollbackFor = Exception.class)
    public Boolean insGwcCp(int pkey)
    {
        List<MktCookfdLine> list = cookFdLineDao.select().eq("cookfd", pkey).exec();
        for (MktCookfdLine line : list)
        {
            insGwc(line.getGoods(), line.getSpace(), line.getNum(), null);
        }
        return true;
    }
    
    public Boolean insGwc(int goodsPkey, int space, int goodsNum, Integer association)
    {
        Integer memberPkey = MobileSession.memberPkey();
        String farmerPkey = MobileSession.farmerPkey();
        Integer ascription = MobileSession.appid();
        SysFarmer farmer = farmerDao.get(farmerPkey);
        if (farmer == null) throw TofocusException.of(WsaleErrCode.UNKOWN_MARKET);
        Integer total = gwcDao.countAll(memberPkey, farmerPkey, ascription);
        if (total >= 50) throw TofocusException.of(WsaleErrCode.GWC_FULL);
//        		orderManager.getBuyGoodsNum(goodsPkey, goodsNum);
        MktGoodsSpace gsp = goodsSpaceDao.get(space);
        MktGoods gd = goodsDao.get(gsp.getGoods());
        if (gd == null) throw TofocusException.of(LejiaErrCode.GOODS_ERROR);
        if (gd.getMType().equals(MType.CUT_GOODS) || gd.getMType().equals(MType.COLLAGE_GOODS)
            || gd.getMType().equals(MType.PRESALE_GOODS) || gd.getMType().equals(MType.GIFT_GOODS))
            throw TofocusException.of(LejiaErrCode.WRONG_GWCTYPE, gd.getMType().getName() + "商品不能加入购物车!");
        // 不能增加不同包厢到购物车,暂时注释 后续看需求
//        if(gd.getMType().equals(MType.BOX_GOODS))
//        {
//            checkBoxGwc(memberPkey, farmerPkey, ascription, gsp.getGoods());
//        }
        MktGwc bean = gwcDao.selectOne().eq("member", memberPkey).eq("space", space).exec();

        int num = bean == null ? 0 : bean.getNum();
        Long kc = spaceKcCache.getLong(String.valueOf(gsp.getPkey()));
        if(kc == null)
            kc = 0l;
        if ((num + goodsNum) > kc)
            throw TofocusException.of(LejiaErrCode.GOODS_NONUM);
        if (gd.getPurchaseNum() != null && gd.getPurchaseNum() > 0)
        {
            // 检查限购
            int gwcGoodsNum = gwcDao.sumNum(gsp.getGoods(), memberPkey, farmerPkey, ascription);
            int maxAllowedNum = gd.getPurchaseNum() - gwcGoodsNum;
            // 前端有用到判断该异常的code，请勿修改报错
            if (goodsNum > maxAllowedNum)
                throw TofocusException.of(LejiaErrCode.GOODS_NUM_PURCHASENUM, "每人每天限购" + gd.getPurchaseNum() + "件");
        }
        // 处理
        if (bean == null)
        {
            MktGwc add = new MktGwc();
            add.setMember(memberPkey);
            add.setGoods(gsp.getGoods());
            add.setCompany(gd.getCompany());
            add.setFarmer(gd.getFarmer());
            add.setSpace(space);
            add.setNum(goodsNum);
            if(association != null)
            {
                MktGoodsSpace mktGoodsSpace = goodsSpaceDao.get(association);
                if(mktGoodsSpace != null)
                {
                    MktGoods mktGoods = goodsDao.get(mktGoodsSpace.getGoods());
                    if(mktGoods != null)
                    {
                        add.setAssociation(association);
                        add.setAssociationName(mktGoods.getTitle());
                        MktGwc gwcAss = gwcDao.selectOne().eq("member", memberPkey).eq("space", association).exec();
                        if(gwcAss == null)
                        {
                            gwcAss = new MktGwc();
                            gwcAss.setMember(memberPkey);
                            gwcAss.setGoods(mktGoodsSpace.getGoods());
                            gwcAss.setCompany(mktGoods.getCompany());
                            gwcAss.setFarmer(mktGoods.getFarmer());
                            gwcAss.setSpace(association);
                            gwcAss.setNum(1);
                            gwcAss.setAssociation(space);
                            gwcAss.setAssociationName(gd.getTitle());
                            gwcDao.add(gwcAss);
                        }
                        else
                        {
                            gwcAss.setNum(gwcAss.getNum() + 1);
                            gwcDao.update(gwcAss);
                        }
                    }
                }
            }
            add.setAscription(ascription);
            gwcDao.add(add);
        }
        else
        {
            System.out.println(space + "购物车：" + gsp.getKcNum() + ":" + bean.getNum());
            bean.setNum(bean.getNum() + goodsNum);
            if(association != null)
            {
                MktGoodsSpace mktGoodsSpace = goodsSpaceDao.get(association);
                if(mktGoodsSpace != null)
                {
                    MktGoods mktGoods = goodsDao.get(mktGoodsSpace.getGoods());
                    if(mktGoods != null)
                    {
                        bean.setAssociation(association);
                        bean.setAssociationName(mktGoods.getTitle());
                        MktGwc gwcAss = gwcDao.selectOne().eq("member", memberPkey).eq("space", association).exec();
                        if(gwcAss == null)
                        {
                            gwcAss = new MktGwc();
                            gwcAss.setMember(memberPkey);
                            gwcAss.setGoods(mktGoodsSpace.getGoods());
                            gwcAss.setCompany(mktGoods.getCompany());
                            gwcAss.setFarmer(mktGoods.getFarmer());
                            gwcAss.setSpace(association);
                            gwcAss.setNum(1);
                            gwcAss.setAssociation(space);
                            gwcAss.setAssociationName(gd.getTitle());
                            gwcDao.add(gwcAss);
                        }
                        else
                        {
                            gwcAss.setNum(gwcAss.getNum() + 1);
                            gwcDao.update(gwcAss);
                        }
                    }
                }
            }
            gwcDao.update(bean);
        }
        return true;
    }
    
    private void checkBoxGwc(Integer member, String farmer, Integer ascription, Integer goods)
    {
        List<MktGwc> list = gwcDao.select().eq("member", member)
        .eq("farmer", farmer)
        .eq("ascription", ascription)
        .exec();
        if(list.isEmpty())
            return;
        List<Integer> goodsKey = new ArrayList<>();
        list.forEach(e -> goodsKey.add(e.getGoods()));
        
        List<MktGoodsBox> goodsBoxList = goodsBoxDao.select().eq("farmer", farmer)
        .eq("ascription", ascription)
        .exec();
        if(!goodsBoxList.isEmpty())
        {
            MktGoodsBox goodsBox = goodsBoxDao.selectOne()
            .eq("goods", goods)
            .eq("farmer", farmer)
            .eq("ascription", ascription)
            .exec();
            if(goodsBox != null && !goodsBox.getDesktop().equals(goodsBoxList.get(0).getDesktop()))
                throw TofocusException.of(LejiaErrCode.GOODS_BOX_GWC_ONLY_ERROR);
        }
    }
    
    public MktGwcOnList queryGwc()
    {
        String farmerPkey = MobileSession.farmerPkey();
        MktGwcOnList mktGwcOnList = new MktGwcOnList();
        mktGwcOnList.setCurrentFarmer(queryGwc(farmerPkey));
        mktGwcOnList.setPointsMall(queryGwc(Constant.Operation + MobileSession.appid()));
        SysFarmerConfig farmerConfig = farmerConfigDao.get(farmerPkey);
        if (farmerConfig != null)
        {
            mktGwcOnList.setFreeDelivery(farmerConfig.getFreeDelivery());
            mktGwcOnList.setIsFree(farmerConfig.getIsFree());
        }
        // 获取市场起步价
        BigDecimal startingPrice = farmerConfigDao.getStartingPrice(farmerPkey);
        mktGwcOnList.setStartingPrice(startingPrice);
        return mktGwcOnList;
    }
    
    private List<MktGwcDetailsDTO> queryGwc(String farmerPkey)
    {
        Integer memberPkey = MobileSession.memberPkey();
        List<MktGwc> listResult =
            gwcDao.select().eq("member", memberPkey).eq("farmer", farmerPkey).sort("createdTime", true).exec();
        List<MktGwcDetailsDTO> result = BeanUtil.beanListFrom(MktGwcDetailsDTO.class, listResult);
        SysFarmer farmer = farmerDao.get(farmerPkey);
        List<Integer> spaceList = new ArrayList<>();
        for (MktGwcDetailsDTO bean : result)
        {
            MktGoods goods = goodsDao.get(bean.getGoods());
            bean.setGoodsTitle(goods.getTitle());
            bean.setGoodsEnabled(goods.getEnabled());
            if (goods.getPhoto1() != null && goods.getPhoto1().size() > 0) bean.setPhoto(goods.getPhoto1().get(0));
            MktGoodsSpace space = goodsSpaceDao.get(bean.getSpace());
            if (space == null)
            {
                spaceList.add(bean.getSpace());
                bean.setSapceEnabled(false);
            }
            else
            {
                bean.setSpaceName(space.getSpace());
                bean.setKcNum(space.getKcNum());
                bean.setPrice(space.getPrice());
                bean.setPriceOld(space.getPriceOld());
                bean.setPriceMember(space.getPriceMember());
            }
            bean.setFarmerName(farmer.getName());
            bean.setMType(goods.getMType());
        }
        if (spaceList.size() > 0)
        {
            List<MktGwc> exec = gwcDao.select()
                .eq("member", memberPkey)
                .eq("farmer", farmerPkey)
                .in("space", spaceList.toArray())
                .exec();
            gwcDao.removeAll(exec);
            listResult =
                gwcDao.select().eq("member", memberPkey).eq("farmer", farmerPkey).sort("createdTime", true).exec();
            result = BeanUtil.beanListFrom(MktGwcDetailsDTO.class, listResult);
            for (MktGwcDetailsDTO bean : result)
            {
                MktGoods goods = goodsDao.get(bean.getGoods());
                bean.setGoodsTitle(goods.getTitle());
                bean.setGoodsEnabled(goods.getEnabled());
                if (goods.getPhoto1() != null && goods.getPhoto1().size() > 0) bean.setPhoto(goods.getPhoto1().get(0));
                MktGoodsSpace space = goodsSpaceDao.get(bean.getSpace());
                if (space == null)
                {
                    bean.setSapceEnabled(false);
                }
                else
                {
                    bean.setSpaceName(space.getSpace());
                    bean.setKcNum(space.getKcNum());
                    bean.setPrice(space.getPrice());
                    bean.setPriceOld(space.getPriceOld());
                    bean.setPriceMember(space.getPriceMember());
                }
                bean.setFarmerName(farmer.getName());
                bean.setMType(goods.getMType());
            }
        }
        return result;
    }
    
    public Boolean modifyGwcNum(int pkey, Boolean flag, Integer association)
    {
        log.info("modifyGwcNum-pkey: {}", pkey);
        MktGwc gwc = gwcDao.get(pkey);
        if (gwc == null) throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
        Long bignum = spaceKcCache.getLong(String.valueOf(gwc.getSpace()));
        System.out.println(gwc.getSpace() + "库存：" + bignum);
        if (flag)
        {
            //		    orderManager.getBuyGoodsNum(gwc.getGoods(), 1);
            gwc.setNum(gwc.getNum() + 1);
            System.out.println(gwc.getSpace() + "购物车：" + gwc.getNum());
            if (gwc.getNum() > bignum) throw TofocusException.of(LejiaErrCode.GOODS_NONUM);
            // 检查限购
            MktGoods goods = goodsDao.getGoods(gwc.getGoods());
            if (goods == null) throw TofocusException.of(LejiaErrCode.GOODS_ERROR);
            // 前端有用到判断该异常的code，请勿修改报错
            if (goods.getPurchaseNum() != null && goods.getPurchaseNum() > 0 && gwc.getNum() > goods.getPurchaseNum())
                throw TofocusException.of(LejiaErrCode.GOODS_NUM_PURCHASENUM, "每人每天限购" + goods.getPurchaseNum() + "件");
        }
        else
        {
            if (gwc.getNum() > 1) gwc.setNum(gwc.getNum() - 1);
        }
        if(association != null)
        {
            Integer memberPkey = MobileSession.memberPkey();
            
            MktGoodsSpace mktGoodsSpace = goodsSpaceDao.get(association);
            if(mktGoodsSpace != null)
            {
                MktGoods mktGoods = goodsDao.get(mktGoodsSpace.getGoods());
                if(mktGoods != null)
                {
                    gwc.setAssociation(association);
                    gwc.setAssociationName(mktGoods.getTitle());
                    MktGwc gwcAss = gwcDao.selectOne().eq("member", memberPkey).eq("space", association).exec();
                    MktGoods goods = goodsDao.getGoods(gwc.getGoods());
                    if(gwcAss == null)
                    {
                        gwcAss = new MktGwc();
                        gwcAss.setMember(memberPkey);
                        gwcAss.setGoods(mktGoodsSpace.getGoods());
                        gwcAss.setCompany(mktGoods.getCompany());
                        gwcAss.setFarmer(mktGoods.getFarmer());
                        gwcAss.setSpace(association);
                        gwcAss.setNum(1);
                        gwcAss.setAssociation(gwc.getSpace());
                        gwcAss.setAssociationName(goods.getTitle());
                        gwcDao.add(gwcAss);
                    }
                    else
                    {
                        gwcAss.setNum(gwcAss.getNum() + 1);
                        gwcDao.update(gwcAss);
                    }
                }
            }
        }
        gwcDao.update(gwc);
        return true;
    }
    
    public Boolean delGwc(int pkey)
    {
        MktGwc mktGwc = gwcDao.get(pkey);
        if(mktGwc.getAssociation() != null)
        {
//            MktGoodsSpace mktGoodsSpace = goodsSpaceDao.get(mktGwc.getAssociation());
//            if(mktGoodsSpace != null)
//            {
//                MktGoods mktGoods = goodsDao.get(mktGoodsSpace.getGoods());
//                if(mktGoods != null && MType.PROCESS_GOODS.equals(mktGoods.getMType()))
//                {
//                    List<MktGwc> list = gwcDao.select().eq("member", mktGwc.getMember()).eq("space", mktGwc.getAssociation()).exec();
//                    gwcDao.removeAll(list);
//                }
//            }
            
            MktGoods mktGoods = goodsDao.get(mktGwc.getGoods());
            if(mktGoods != null && !MType.PROCESS_GOODS.equals(mktGoods.getMType()))
            {
                List<MktGwc> list = gwcDao.select().eq("member", mktGwc.getMember()).eq("association", mktGwc.getSpace()).exec();
                gwcDao.removeAll(list);
            }
        }
        return gwcDao.removeById(pkey);
    }
    
    public Boolean delByPkeys(List<Integer> pkeys)
    {
        List<MktGwc> gwcList = gwcDao.select().in("pkey", pkeys).exec();
        for(MktGwc mktGwc : gwcList)
        {
            if(mktGwc.getAssociation() != null)
            {
//                MktGoodsSpace mktGoodsSpace = goodsSpaceDao.get(mktGwc.getAssociation());
//                if(mktGoodsSpace != null)
//                {
//                    MktGoods mktGoods = goodsDao.get(mktGoodsSpace.getGoods());
//                    if(mktGoods != null && MType.PROCESS_GOODS.equals(mktGoods.getMType()))
//                    {
//                        List<MktGwc> list = gwcDao.select().eq("member", mktGwc.getMember()).eq("space", mktGwc.getAssociation()).exec();
//                        gwcDao.removeAll(list);
//                    }
//                }
                MktGoods mktGoods = goodsDao.get(mktGwc.getGoods());
                if(mktGoods != null && !MType.PROCESS_GOODS.equals(mktGoods.getMType()))
                {
                    List<MktGwc> list = gwcDao.select().eq("member", mktGwc.getMember()).eq("association", mktGwc.getSpace()).exec();
                    gwcDao.removeAll(list);
                }
            }
        }
        return gwcDao.removeAllById(pkeys);
    }
    
    @SuppressWarnings("unchecked")
    public PageResult<Map<String, Object>> freeDeliveryGoods(int page, int pagesize)
    {
        log.info("fileStart: {}", fileStart);
        PageParameter pageParamter = new PageParameter(page, pagesize);
        List<Map<String, Object>> content = new ArrayList<>();
        String farmerPkey = MobileSession.farmerPkey();
        SysFarmer sysFarmer = farmerDao.get(farmerPkey);
        BigDecimal freeDelivery = sysFarmer.getConfig().getFreeDelivery();
        if (freeDelivery == null || !sysFarmer.getConfig().getIsFree())
            return new PageResult<>(content, pageParamter, 0);
        List<MktGwcDetailsDTO> queryGwc = queryGwc(farmerPkey);
        BigDecimal sum = new BigDecimal("0");
        for (MktGwcDetailsDTO dto : queryGwc)
        {
            BigDecimal price = dto.getPrice();
            BigDecimal num = new BigDecimal(dto.getNum());
            BigDecimal multiply = price.multiply(num);
            sum = sum.add(multiply);
        }
        int size = 0;
        BigDecimal subtract = freeDelivery.subtract(sum);
        log.info("sum: {}", sum);
        log.info("subtract: {}", subtract);
        if (subtract.signum() > 0)
        {
            List<List<Object>> list = goodsDao.getGoodsPool(farmerPkey, subtract, page, pagesize);
            for (List<Object> o : list)
            {
                Map<String, Object> map = new HashMap<>();
                map.put("pkey", o.get(0));
                map.put("goodsTitle", o.get(1));
                if (o.get(2) != null)
                {
                    List<String> list2 = JsonUtil.getBean(o.get(2).toString(), List.class);
                    if (list2.size() > 0)
                    {
                        String string = list2.get(0);
                        if (!string.startsWith(fileStart)) string = fileStart + string;
                        map.put("photo", string);
                    }
                }
                else
                    map.put("photo", "");
                map.put("price", o.get(3));
                map.put("priceOld", o.get(4));
                map.put("space", o.get(5));
                content.add(map);
            }
            
            size = goodsDao.getGoodsPool(farmerPkey, subtract, 0, 10000).size();
        }
        
        PageResult<Map<String, Object>> result = new PageResult<>(content, pageParamter, size);
        return result;
    }
    
    public Integer getGwcGoodsNum()
    {
        String farmerPkey = MobileSession.farmerPkey();
        Integer ascription = MobileSession.appid();
        if (farmerPkey == null) farmerPkey = Constant.Operation + ascription;
        Integer memberPkey = MobileSession.memberPkey();
        List<MktGwc> list =
            gwcDao.select().eq("member", memberPkey).in("farmer", farmerPkey, Constant.Operation + ascription).sort("createdTime", true).exec();
        
        // 商品pkey -> enabled是否启用
//        Map<Integer, Boolean> goodsMap =
//            goodsDao.select().exec().stream().collect(Collectors.toMap(MktGoods::getPkey, MktGoods::getEnabled));
        
        // 规格pkey -> kcNum库存数量
//        Map<Integer, Integer> spaceMap = goodsSpaceDao.select()
//            .exec()
//            .stream()
//            .collect(Collectors.toMap(MktGoodsSpace::getPkey, MktGoodsSpace::getKcNum));
        // 初始化结果
        Integer sum = 0;
        for (MktGwc g : list)
        {
            if(g.getGoods() != null)
            {
                MktGoods mktGoods = goodsDao.get(g.getGoods());
                if(mktGoods != null && mktGoods.getEnabled())
                {
                    MktGoodsSpace goodsSpace = goodsSpaceDao.get(g.getSpace());
                    if(goodsSpace != null && goodsSpace.getKcNum() > 0)
                    {
                        sum += g.getNum();
                    }
                }
            }
            else
            {
                JdGoods jdGoods = jdGoodsDao.get(g.getSkuId());
                if(jdGoods != null && jdGoods.getEnabled() && jdGoods.getSkuState() == 1)
                    sum += g.getNum();
            }
//            if (goodsMap.containsKey(g.getGoods()) && spaceMap.containsKey(g.getSpace()))
//            {
//                // 商品是否启用
//                Boolean enabled = goodsMap.get(g.getGoods());
//                // 库存数量
//                Integer spaceKcNum = spaceMap.get(g.getSpace());
//                if (enabled && spaceKcNum > 0)
//                {
//                    sum += g.getNum();
//                }
//            }
        }
        return sum;
    }
    public BigDecimal getGwcGoodsPrice()
    {
        String farmerPkey = MobileSession.farmerPkey();
        Integer ascription = MobileSession.appid();
        if (farmerPkey == null) farmerPkey = Constant.Operation + ascription;
        Integer memberPkey = MobileSession.memberPkey();
        List<MktGwc> list =
            gwcDao.select().eq("member", memberPkey).in("farmer", farmerPkey, Constant.Operation + ascription).sort("createdTime", true).exec();
        if(list.isEmpty())
            return BigDecimal.ZERO;
        // 商品pkey -> enabled是否启用
        List<Integer> goodsKeys = new ArrayList<>();
        list.forEach(e -> 
        {
            if(e.getGoods() != null)
                goodsKeys.add(e.getGoods());
        });
        Map<Integer, Boolean> goodsMap =
            goodsDao.select().in("pkey", goodsKeys).eq("farmer", farmerPkey).eq("enabled", true).exec()
            .stream().collect(Collectors.toMap(MktGoods::getPkey, MktGoods::getEnabled));
        // 规格pkey -> kcNum库存数量
        Map<Integer, BigDecimal> spaceMap = goodsSpaceDao.select()
            .in("goods", goodsMap.keySet())
            .exec()
            .stream()
            .collect(Collectors.toMap(MktGoodsSpace::getPkey, MktGoodsSpace::getPrice));
        // 初始化结果
        BigDecimal sum = BigDecimal.ZERO;
        for (MktGwc g : list)
        {
            if (goodsMap.containsKey(g.getGoods()) && spaceMap.containsKey(g.getSpace()))
            {
                // 商品是否启用
                Boolean enabled = goodsMap.get(g.getGoods());
                // 库存数量
                BigDecimal spaceKcNum = spaceMap.get(g.getSpace());
                if (enabled)
                {
                    sum = sum.add(spaceKcNum.multiply(new BigDecimal(g.getNum())));
                }
            }
        }
        return sum;
    }
    
    public Boolean addOrLessGwcNum(int goodsPkey, int space, int goodsNum, boolean addOrLess, Integer association)
    {
        Integer memberPkey = MobileSession.memberPkey();
        String farmerPkey = MobileSession.farmerPkey();
        Integer ascription = MobileSession.appid();
        SysFarmer farmer = farmerDao.get(farmerPkey);
        if (farmer == null) throw TofocusException.of(WsaleErrCode.UNKOWN_MARKET);
        if (addOrLess)
        {
            Integer total = gwcDao.countAll(memberPkey, farmerPkey, ascription);
            if (total >= 50) throw TofocusException.of(WsaleErrCode.GWC_FULL);
        }
        MktGoodsSpace gsp = goodsSpaceDao.get(space);
        MktGoods gd = goodsDao.get(gsp.getGoods());
        if (gd == null) throw TofocusException.of(LejiaErrCode.GOODS_ERROR);
        if (gd.getMType().equals(MType.CUT_GOODS) || gd.getMType().equals(MType.COLLAGE_GOODS)
            || gd.getMType().equals(MType.PRESALE_GOODS) || gd.getMType().equals(MType.GIFT_GOODS))
            throw TofocusException.of(LejiaErrCode.WRONG_GWCTYPE, gd.getMType().getName() + "商品不能加入购物车!");
        MktGwc bean = gwcDao.selectOne().eq("member", memberPkey).eq("space", space).exec();
        if (addOrLess)
        {
            int num = bean == null ? 0 : bean.getNum();
            if ((num + goodsNum) > spaceKcCache.getLong(String.valueOf(gsp.getPkey())))
                throw TofocusException.of(LejiaErrCode.GOODS_NONUM);
            if (gd.getPurchaseNum() != null && gd.getPurchaseNum() > 0)
            {
                // 检查限购
                int gwcGoodsNum = gwcDao.sumNum(gsp.getGoods(), memberPkey, farmerPkey, ascription);
                // 前端有用到判断该异常的code，请勿修改报错
                if (gwcGoodsNum + goodsNum > gd.getPurchaseNum())
                    throw TofocusException.of(LejiaErrCode.GOODS_NUM_PURCHASENUM,
                        "每人每天限购" + gd.getPurchaseNum() + "件");
            }
            // 处理
            if (bean == null)
            {
                MktGwc add = new MktGwc();
                add.setMember(memberPkey);
                add.setGoods(gsp.getGoods());
                add.setCompany(gd.getCompany());
                add.setFarmer(gd.getFarmer());
                add.setSpace(space);
                add.setNum(goodsNum);
                
                
                if(association != null)
                {
                    MktGoodsSpace mktGoodsSpace = goodsSpaceDao.get(association);
                    if(mktGoodsSpace != null)
                    {
                        MktGoods mktGoods = goodsDao.get(mktGoodsSpace.getGoods());
                        if(mktGoods != null)
                        {
                            add.setAssociation(association);
                            add.setAssociationName(mktGoods.getTitle());
                            MktGwc gwcAss = gwcDao.selectOne().eq("member", memberPkey).eq("space", association).exec();
                            if(gwcAss == null)
                            {
                                gwcAss = new MktGwc();
                                gwcAss.setMember(memberPkey);
                                gwcAss.setGoods(mktGoodsSpace.getGoods());
                                gwcAss.setCompany(mktGoods.getCompany());
                                gwcAss.setFarmer(mktGoods.getFarmer());
                                gwcAss.setSpace(association);
                                gwcAss.setNum(1);
                                gwcAss.setAssociation(space);
                                gwcAss.setAssociationName(gd.getTitle());
                                gwcDao.add(gwcAss);
                            }
                            else
                            {
                                gwcAss.setNum(gwcAss.getNum() + 1);
                                gwcDao.update(gwcAss);
                            }
                        }
                    }
                }
                
                
                
                add.setAscription(ascription);
                gwcDao.add(add);
            }
            else
            {
                bean.setNum(bean.getNum() + goodsNum);
                if(association != null)
                {
                    MktGoodsSpace mktGoodsSpace = goodsSpaceDao.get(association);
                    if(mktGoodsSpace != null)
                    {
                        MktGoods mktGoods = goodsDao.get(mktGoodsSpace.getGoods());
                        if(mktGoods != null)
                        {
                            bean.setAssociation(association);
                            bean.setAssociationName(mktGoods.getTitle());
                            MktGwc gwcAss = gwcDao.selectOne().eq("member", memberPkey).eq("space", association).exec();
                            if(gwcAss == null)
                            {
                                gwcAss = new MktGwc();
                                gwcAss.setMember(memberPkey);
                                gwcAss.setGoods(mktGoodsSpace.getGoods());
                                gwcAss.setCompany(mktGoods.getCompany());
                                gwcAss.setFarmer(mktGoods.getFarmer());
                                gwcAss.setSpace(association);
                                gwcAss.setNum(1);
                                gwcAss.setAssociation(space);
                                gwcAss.setAssociationName(gd.getTitle());
                                gwcDao.add(gwcAss);
                            }
                            else
                            {
                                gwcAss.setNum(gwcAss.getNum() + 1);
                                gwcDao.update(gwcAss);
                            }
                        }
                    }
                }
                gwcDao.update(bean);
            }
        }
        else
        {
            if (bean != null)
            {
                int i = bean.getNum() - goodsNum;
                if (i <= 0)
                {
                    gwcDao.remove(bean);
                    if(bean.getAssociation() != null)
                    {
//                        MktGoodsSpace mktGoodsSpace = goodsSpaceDao.get(bean.getAssociation());
//                        if(mktGoodsSpace != null)
//                        {
//                            MktGoods mktGoods = goodsDao.get(mktGoodsSpace.getGoods());
//                            if(mktGoods != null && MType.PROCESS_GOODS.equals(mktGoods.getMType()))
//                            {
//                                List<MktGwc> list = gwcDao.select().eq("member", bean.getMember()).eq("space", bean.getAssociation()).exec();
//                                gwcDao.removeAll(list);
//                            }
//                        }
                        MktGoods mktGoods = goodsDao.get(bean.getGoods());
                        if(mktGoods != null && !MType.PROCESS_GOODS.equals(mktGoods.getMType()))
                        {
                            List<MktGwc> list = gwcDao.select().eq("member", bean.getMember()).eq("association", bean.getSpace()).exec();
                            gwcDao.removeAll(list);
                        }
                    }
                }
                else
                {
                    bean.setNum(i);
                    gwcDao.update(bean);
                }
            }
        }
        
        return true;
    }
    
}

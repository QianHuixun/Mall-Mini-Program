package cn.tofocus.lejia.domain.v3;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.MktGtypeOnList;
import cn.tofocus.lejia.bean.dto.market.v3.MktVendorGoodsOnInfo;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.bean.entity.goods.MktSpaceKc;
import cn.tofocus.lejia.bean.entity.market.MktGtype;
import cn.tofocus.lejia.bean.entity.market.MktWareLine;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.bean.enums.WareType;
import cn.tofocus.lejia.cache.SpaceKcCache;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsMainDao;
import cn.tofocus.lejia.dao.goods.MktGoodsSpaceDao;
import cn.tofocus.lejia.dao.goods.MktSpaceKcDao;
import cn.tofocus.lejia.dao.market.MktGtypeDao;
import cn.tofocus.lejia.dao.market.MktWareLineDao;
import cn.tofocus.lejia.domain.market.goods.GtypeManager;
import cn.tofocus.lejia.domain.market.goods.WareManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.exception.WsaleErrCode;

@Component
public class AppGoodsVendorManager
{
    
    @Autowired
    private GtypeManager gtypeManager;
    
    @Autowired
    private MktGtypeDao gtypeDao;
    
    @Autowired
    private MktGoodsSpaceDao goodsSpaceDao;
    
    @Autowired
    private MktGoodsMainDao goodsMainDao;
    
    @Autowired
    private WareManager wareManager;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private SpaceKcCache spaceKcCache;
    
    @Autowired
    private MktSpaceKcDao spaceKcDao;
    
    @Autowired
    private MktWareLineDao wareLineDao;
    
    public PageResult<MktVendorGoodsOnInfo> queryGoods(int page, int pagesize, String title,
        Integer gtype, Boolean enabled, Integer status)
    {
        MktVendor vendor = MobileSession.vendor();
        String marketPkey = vendor.getFarmer();
        String company = vendor.getCompany();
        Integer appid = MobileSession.appid();
        
        PageResult<MktVendorGoodsOnInfo> res =
            goodsDao.queryAppVendorGoods(page, pagesize, enabled, status, gtype, title, marketPkey, company, appid);
        for (MktVendorGoodsOnInfo bean : res.getContent())
        {
            bean.setGtypeName(gtypeDao.get(bean.getGtype()).getName());
            // 通过mkt_goods_main设置商品名称
            bean.setName(goodsMainDao.get(bean.getGoodsMain()).getName());
            List<MktGoodsSpace> list = goodsSpaceDao.select().eq("goods", bean.getPkey()).exec();
            bean.setPrice(list.get(0).getPrice());
            bean.setPriceOld(list.get(0).getPriceOld());
            bean.setKcNum(list.get(0).getKcNum());
            bean.setSpacePkey(list.get(0).getPkey());
        }
        return res;
    }
    
    public List<MktGtypeOnList> queryGtype()
    {
        List<MktGtype> list = gtypeDao.listEnabledGtype(MobileSession.appid());
        List<MktGtypeOnList> res = BeanUtil.beanListFrom(MktGtypeOnList.class, list);
        gtypeManager.assembleGoodsName(res);
        return res;
    }
    
    public Integer insGoods(MktVendorGoodsOnInfo entity)
    {
        MktGoods mktGoods = BeanUtil.beanFrom(MktGoods.class, entity);
        MktVendor vendor = MobileSession.vendor();
        String marketPkey = vendor.getFarmer();
        String company = vendor.getCompany();
        
        List<MktGoods> exec = goodsDao.select()
            .eq("mType", MType.MARKET_GOODS)
            .eq("farmer", marketPkey)
            .eq("title", entity.getTitle())
            .eq("idDel", false)
            .eq("ascription", MobileSession.appid())
            .exec();
        if (!exec.isEmpty())
        {
            throw TofocusException.of(LejiaErrCode.GOODS_NAMEREPEAT);
        }
        mktGoods.setFarmer(marketPkey);
        mktGoods.setCompany(company);
        mktGoods.setAscription(MobileSession.appid());
        mktGoods.setRowVension(1);
        mktGoods.setIdDel(false);
        mktGoods.setViewCount(0);
        mktGoods.setEnabled(false);
        mktGoods.setCreatedBy(MobileSession.vendorPkey());
        mktGoods.setXsNum(0);
        if (entity.getEndDate() == null)
        {
            Calendar cal = Calendar.getInstance();
            cal.set(2030, 1, 1);
            mktGoods.setEndDate(cal.getTime());
        }
        if (entity.getSort() == null)
        {
            mktGoods.setSort(0);
        }
        mktGoods.setPurchaseNum(0);
        mktGoods.setGuessLike(false);
        mktGoods.setGuessSort(0);
        mktGoods.setMType(MType.MARKET_GOODS);
        mktGoods.setIsPostage(false);
        if (goodsMainDao.checkGtype(mktGoods.getGtype(), entity.getGoodsMain()))
            throw TofocusException.of(LejiaErrCode.GTYPE_NOT_GOODSMAIN);
        MktGoods add = goodsDao.add(mktGoods);
        Integer goodsPkey = add.getPkey();
        MktGoodsSpace space = new MktGoodsSpace();
        space.setWeight(BigDecimal.ZERO);
        space.setPrice(entity.getPrice());
        if (space.getPriceOld() == null)
        {
            space.setPriceOld(space.getPrice());
        }
        space.setSpace(entity.getTitle());
        space.setPriceMember(BigDecimal.ZERO);
        space.setGoods(goodsPkey);
        space.setXsNum(0);
        space.setPoint(0);
        space.setAscription(MobileSession.appid());
        space.setComm(BigDecimal.ZERO);
        space.setKcNum(entity.getKcNum());
        MktGoodsSpace addSpace = goodsSpaceDao.add(space);
        MktSpaceKc skc = BeanUtil.beanFrom(MktSpaceKc.class, addSpace);
        spaceKcDao.add(skc);
        spaceKcCache.set(String.valueOf(skc.getPkey()), Long.valueOf(skc.getKcNum()));
        add.setPrice(addSpace.getPrice());
        wareManager.insWare(add, Arrays.asList(addSpace));
        return null;
    }
    
    public Integer updGoods(MktVendorGoodsOnInfo entity)
    {
        MktVendor vendor = MobileSession.vendor();
        String marketPkey = vendor.getFarmer();
        Integer goodsPkey = entity.getPkey();
        MktGoods mktGoods = goodsDao.get(goodsPkey);
        if (mktGoods == null) throw TofocusException.of(WsaleErrCode.NOT_GOODS);
        if (mktGoods.getEnabled()) throw TofocusException.of(WsaleErrCode.GOODS_CANNOT_EDIT);
        List<MktGoods> exec = goodsDao.select()
            .eq("mType", MType.MARKET_GOODS)
            .eq("farmer", marketPkey)
            .notEq("pkey", goodsPkey)
            .eq("title", entity.getTitle())
            .eq("idDel", false)
            .eq("ascription", MobileSession.appid())
            .exec();
        if (!exec.isEmpty())
        {
            throw TofocusException.of(LejiaErrCode.GOODS_NAMEREPEAT);
        }
        BeanUtils.copyProperties(entity, mktGoods, "viewCount", "enabled");
        if(mktGoods.getSort() == null)
            mktGoods.setSort(0);
        goodsDao.update(mktGoods);
        
        // 更新规格
        MktGoodsSpace updaSpace = goodsSpaceDao.get(entity.getSpacePkey());
        updaSpace.setSpace(entity.getTitle());
        updaSpace.setPriceOld(entity.getPriceOld());
        updaSpace.setPrice(entity.getPrice());
        updaSpace.setKcNum(entity.getKcNum());
        goodsSpaceDao.update(updaSpace);
        // 更新库存
        if (updaSpace.getKcNum().intValue() != entity.getKcNum().intValue())
        {
            MktWareLine add = new MktWareLine();
            add.setWareType(WareType.INVENTORY);
            add.setGoods(mktGoods.getPkey());
            add.setGoodsName(mktGoods.getTitle());
            add.setSpace(entity.getSpacePkey());
            add.setSpaceName(entity.getTitle());
            add.setNum(entity.getKcNum() - updaSpace.getKcNum());
            add.setActualNum(entity.getKcNum());
            add.setAscription(MobileSession.appid());
            wareLineDao.add(add);
        }
        MktSpaceKc skc = BeanUtil.beanFrom(MktSpaceKc.class, updaSpace);
        spaceKcDao.add(skc);
        spaceKcCache.set(String.valueOf(skc.getPkey()), Long.valueOf(skc.getKcNum()));
        
        return goodsPkey;
    }
    
}

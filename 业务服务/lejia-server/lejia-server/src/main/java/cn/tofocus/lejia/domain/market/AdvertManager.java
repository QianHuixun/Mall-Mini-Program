package cn.tofocus.lejia.domain.market;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.base.Objects;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.PageUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.core.data.KeyValue;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.MktAdvertOnList;
import cn.tofocus.lejia.bean.dto.market.MktFunMenuConfigInfo;
import cn.tofocus.lejia.bean.dto.market.MktFunMenuConfigOnList;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsMain;
import cn.tofocus.lejia.bean.entity.market.MktActivity;
import cn.tofocus.lejia.bean.entity.market.MktAdvert;
import cn.tofocus.lejia.bean.entity.market.MktFunMenuConfig;
import cn.tofocus.lejia.bean.entity.market.MktGtype;
import cn.tofocus.lejia.bean.entity.market.MktSpecialAdvert;
import cn.tofocus.lejia.bean.entity.member.MktTag;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.enums.AdvertPosition;
import cn.tofocus.lejia.bean.enums.AdvertType;
import cn.tofocus.lejia.bean.enums.LinkType;
import cn.tofocus.lejia.bean.enums.LocationType;
import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsMainDao;
import cn.tofocus.lejia.dao.market.MktActivityDao;
import cn.tofocus.lejia.dao.market.MktAdvertDao;
import cn.tofocus.lejia.dao.market.MktFunMenuConfigDao;
import cn.tofocus.lejia.dao.market.MktGtypeDao;
import cn.tofocus.lejia.dao.market.MktSpecialAdvertDao;
import cn.tofocus.lejia.dao.market.MktTagDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.exception.WsaleErrCode;

@Component
public class AdvertManager
{
    @Autowired
    private MktAdvertDao mktAdvertDao;
    
    @Autowired
    private MktSpecialAdvertDao mktSpecialAdvertDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private MktGtypeDao gtypeDao;
    
    @Autowired
    private MktGoodsMainDao goodsMainDao;
    
    @Autowired
    private SysFarmerDao farmerDao;
    
    @Autowired
    private MktActivityDao activityDao;
    
    @Autowired
    private MktFunMenuConfigDao mktFunMenuConfigDao;
    @Autowired
    private MktVendorDao vendorDao;
    
    @Autowired
    private MktTagDao mktTagDao;
    
    public MktAdvertOnList insAdvert(MktAdvertOnList entity)
    {
        String currentFarmer = CurrentSession.marketPkey();
        String realPositionObj = null;
        if (entity.getPosition() == AdvertPosition.ADVERT_POSITION_GOODS_MAIN
            || entity.getPosition() == AdvertPosition.ADVERT_POSITION_MSD_GOODS_MAIN)
        {
            if (StringUtil.isBlank(entity.getPositionObj()))
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "请选择展示分类");
            realPositionObj = entity.getPositionObj();
            if (realPositionObj.contains(","))
            {
                String[] split = realPositionObj.split(",");
                realPositionObj = split[1];
            }
            if (!StringUtil.isNumeric(realPositionObj))
                throw TofocusException.of(WsaleErrCode.GOODSMAIN_CORRECT);
            MktGoodsMain goodsMain = goodsMainDao.getGoodsMain(Integer.valueOf(realPositionObj));
            if (goodsMain == null || !Objects.equal(goodsMain.getFarmer(), currentFarmer))
                throw TofocusException.of(WsaleErrCode.GOODSMAIN_CORRECT, "找不到分类");
        }
        MktAdvert advert = BeanUtil.beanFrom(MktAdvert.class, entity);
        advert.setPositionObj(realPositionObj);
        advert.setRowVension(1);
        advert.setEnabled(true);
        SysFarmer farmer = farmerDao.get(currentFarmer);
        if (farmer == null)
            throw TofocusException.of(WsaleErrCode.UNKOWN_MARKET);
        advert.setCompany(farmer.getOrg());
        advert.setFarmer(farmer.getPkey());
        advert.setAscription(CurrentSession.ascriptionPkey());
        advert.setType(AdvertType.OWN);
        if (entity.getSort() == null)
            advert.setSort(0);
        switch (entity.getUrlType())
        {
            case NOT_URL:
                advert.setObjKey(null);
                break;
            case LINK:
                advert.setObjKey(entity.getUrlType().getValue() + entity.getObjKey());
                break;
            case GOODS:
            {
                if (StringUtil.isBlank(entity.getObjKey()))
                    throw TofocusException.of(WsaleErrCode.GOODS_PKEY_CORRECT);
                if (!StringUtil.isNumeric(entity.getObjKey()))
                    throw TofocusException.of(WsaleErrCode.GOODS_PKEY_CORRECT);
                MktGoods goods = goodsDao.getGoods(Integer.valueOf(entity.getObjKey()));
                if (goods == null)
                    throw TofocusException.of(WsaleErrCode.GOODS_PKEY_CORRECT);
                advert.setObjKey(entity.getObjKey());
                break;
            }
            case GTYPE:
                if (StringUtil.isBlank(entity.getObjKey()))
                    throw TofocusException.of(WsaleErrCode.GOODS_PKEY_CORRECT);
                checkGtypeAndMain(entity.getObjKey());
                advert.setObjKey(entity.getObjKey());
                break;
            case ACTIVITY:
            {
                if (StringUtil.isBlank(entity.getObjKey()))
                    throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "请选择卡券活动");
                if (!StringUtil.isNumeric(entity.getObjKey()))
                    throw TofocusException.of(LejiaErrCode.ACTIVITY_NOT_FOUND);
                MktActivity activity = activityDao.get(Integer.valueOf(entity.getObjKey()));
                if (activity == null)
                    throw TofocusException.of(LejiaErrCode.ACTIVITY_NOT_FOUND);
                advert.setObjKey(entity.getObjKey());
                break;
            }
            case VENDOR:
            {
                advert.setObjKey(entity.getObjKey());
            }
            break;
            default:
                advert.setObjKey(entity.getUrlType().getValue());
        }
        MktAdvert add = mktAdvertDao.add(advert);
        return BeanUtil.beanFrom(MktAdvertOnList.class, add);
    }
    
    private void checkGtypeAndMain(String objKey)
    {
        if (objKey.contains(","))
        {
            String[] split = objKey.split(",");
            Pattern pattern = Pattern.compile("[0-9]*");
            Matcher isNum = pattern.matcher(split[0]);
            if (!isNum.matches())
                throw TofocusException.of(WsaleErrCode.GTYPE_CORRECT);
            MktGtype gtype = gtypeDao.getGtype(Integer.valueOf(split[0]));
            if (gtype == null)
                throw TofocusException.of(WsaleErrCode.GTYPE_CORRECT);
            isNum = pattern.matcher(split[1]);
            if (!isNum.matches())
                throw TofocusException.of(WsaleErrCode.GOODSMAIN_CORRECT);
            MktGoodsMain goodsMain = goodsMainDao.getGoodsMain(Integer.valueOf(split[1]));
            if (goodsMain == null)
                throw TofocusException.of(WsaleErrCode.GOODSMAIN_CORRECT);
        }
        else
        {
            Pattern pattern = Pattern.compile("[0-9]*");
            Matcher isNum = pattern.matcher(objKey);
            if (!isNum.matches())
                throw TofocusException.of(WsaleErrCode.GTYPE_CORRECT);
            MktGtype gtype = gtypeDao.getGtype(Integer.valueOf(objKey));
            if (gtype == null)
                throw TofocusException.of(WsaleErrCode.GTYPE_CORRECT);
        }
    }
    
    // 新增专区广告
    public MktAdvertOnList insSpecialAdvert(MktAdvertOnList entity)
    {
        MktAdvert advert = BeanUtil.beanFrom(MktAdvert.class, entity);
        advert.setRowVension(1);
        advert.setEnabled(true);
        String marketPkey = CurrentSession.marketPkey();
        Integer ascription = CurrentSession.ascriptionPkey();
        if (!(Constant.Operation + ascription).equals(marketPkey))
            throw TofocusException.of(LejiaErrCode.NOT_RIGHT);
        advert.setCompany(Constant.Operation + ascription);
        advert.setFarmer(Constant.Operation + ascription);
        advert.setAscription(ascription);
        advert.setType(AdvertType.SPECIAL_AREA);
        if (entity.getSort() == null)
            advert.setSort(0);
        switch (entity.getUrlType())
        {
            case NOT_URL:
                advert.setObjKey(null);
                break;
            case LINK:
                advert.setObjKey(entity.getUrlType().getValue() + entity.getObjKey());
                break;
            case GOODS:
            {
                if (StringUtil.isBlank(entity.getObjKey()))
                    throw TofocusException.of(WsaleErrCode.GOODS_PKEY_CORRECT);
                if (!StringUtil.isNumeric(entity.getObjKey()))
                    throw TofocusException.of(WsaleErrCode.GOODS_PKEY_CORRECT);
                MktGoods goods = goodsDao.getGoods(Integer.valueOf(entity.getObjKey()));
                if (goods == null)
                    throw TofocusException.of(WsaleErrCode.GOODS_PKEY_CORRECT);
                advert.setObjKey(entity.getObjKey());
                break;
            }
            case GTYPE:
                if (StringUtil.isBlank(entity.getObjKey()))
                    throw TofocusException.of(WsaleErrCode.GOODS_PKEY_CORRECT);
                checkGtypeAndMain(entity.getObjKey());
                advert.setObjKey(entity.getObjKey());
                break;
            case ACTIVITY:
            {
                if (StringUtil.isBlank(entity.getObjKey()))
                    throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "请选择卡券活动");
                if (!StringUtil.isNumeric(entity.getObjKey()))
                    throw TofocusException.of(LejiaErrCode.ACTIVITY_NOT_FOUND);
                MktActivity activity = activityDao.get(Integer.valueOf(entity.getObjKey()));
                if (activity == null)
                    throw TofocusException.of(LejiaErrCode.ACTIVITY_NOT_FOUND);
                advert.setObjKey(entity.getObjKey());
              
            }
            break;
            case VENDOR:
            {
                advert.setObjKey(entity.getObjKey());
            }
            break;
            default:
                advert.setObjKey(entity.getUrlType().getValue());
        }
        
        MktAdvert add = mktAdvertDao.add(advert);
        addSpecialAdvert(add.getPkey(), add.getPosition(), entity.getFarmers(), ascription);
        return BeanUtil.beanFrom(MktAdvertOnList.class, add);
    }
    
    private void addSpecialAdvert(Integer advertKey, AdvertPosition position, List<String> farmers, Integer ascription)
    {
        List<MktSpecialAdvert> adds = new ArrayList<>();
        int i = 1;
        for (String key : farmers)
        {
            MktSpecialAdvert e = new MktSpecialAdvert();
            e.setId(i);
            e.setAdvertKey(advertKey);
            e.setFarmer(key);
            e.setPosition(position);
            e.setEnabled(true);
            e.setAscription(ascription);
            adds.add(e);
            i++;
        }
        mktSpecialAdvertDao.addAll(adds);
    }
    
    public MktAdvertOnList getAdvert(Integer pkey)
    {
        MktAdvert advert = mktAdvertDao.selectOne().eq("pkey", pkey).eq("enabled", true).exec();
        MktAdvertOnList dto = BeanUtil.beanFrom(MktAdvertOnList.class, advert);
        if (dto.getPosition() == AdvertPosition.ADVERT_POSITION_GOODS_MAIN
            && StringUtil.isNotBlank(dto.getPositionObj()))
        {
            MktGoodsMain goodsMain = goodsMainDao.getGoodsMain(Integer.valueOf(dto.getPositionObj()));
            if (goodsMain != null)
            {
                StringBuilder sb = new StringBuilder();
                MktGtype gtype = gtypeDao.getGtype(goodsMain.getGtype());
                if (gtype != null)
                    sb.append(gtype.getName()).append("/");
                sb.append(goodsMain.getName());
                dto.setPositionObjName(sb.toString());
                dto.setPositionObj(goodsMain.getGtype() + "," + dto.getPositionObj());
            }
        }
        switch (dto.getUrlType())
        {
            case LINK:
            {
                String objKey = dto.getObjKey();
                String value = dto.getUrlType().getValue();
                if (StringUtils.isNotBlank(objKey) && objKey.contains(value))
                    objKey = objKey.replace(value, "");
                dto.setObjKey(objKey);
                break;
            }
            case GOODS:
            {
                MktGoods goods = goodsDao.get(Integer.valueOf(dto.getObjKey()));
                if (goods != null)
                    dto.setGoodsName(goods.getTitle());
                break;
            }
            case GTYPE:
            {
                String objKey = dto.getObjKey();
                if (objKey.contains(","))
                {
                    String[] split = objKey.split(",");
                    MktGoodsMain goodsMain = goodsMainDao.get(Integer.valueOf(split[1]));
                    if (goodsMain != null)
                        dto.setObjKeyName(goodsMain.getName());
                }
                else
                {
                    MktGtype gtype = gtypeDao.get(Integer.valueOf(objKey));
                    if (gtype != null)
                        dto.setObjKeyName(gtype.getName());
                }
                break;
            }
            case ACTIVITY:
            {
                MktActivity activity = activityDao.get(Integer.valueOf(dto.getObjKey()));
                if (activity != null)
                    dto.setActivityName(activity.getName());
                break;
            }
            case VENDOR:
            {
                if (StringUtil.isNotBlank(dto.getObjKey()))
                {
                    MktVendor vendor = vendorDao.get(Integer.valueOf(dto.getObjKey()));
                    if(vendor != null)
                        dto.setObjKeyName(vendor.getDisplayName());
                }
            }
            break;
            default:
        }
        if(dto.getTargerKeys()!=null&&dto.getVisibleRange()!=null&&dto.getVisibleRange().equals(MemberVisibleRange.TAG)&&!dto.getTargerKeys().isEmpty())
        {
            
            List<MktTag> list=  mktTagDao.get(dto.getTargerKeys());
            if(!list.isEmpty())
            {
                List<String>liststr=list.stream().map(MktTag::getName).collect(Collectors.toList());
                dto.setVisibleRangeName(String.join(",", liststr)); 
            }
        }
        
        return dto;
    }
    
    public PageResult<MktAdvertOnList> queryAdvert(int page, int pagesize, AdvertPosition position)
    {
        String marketPkey = CurrentSession.marketPkey();
        PageResult<MktAdvertOnList> result = BeanUtil.beanPageFrom(MktAdvertOnList.class,
            mktAdvertDao.queryAdvert(page, pagesize, position, marketPkey, AdvertType.OWN));
        setGoodsName(result.getContent());
        return result;
    }
    
    public PageResult<MktAdvertOnList> querySpecialAdvert(int page, int pagesize, AdvertPosition position,
        List<String> farmers)
    {
        List<Integer> keys = new ArrayList<>();
        if (farmers != null && farmers.size() > 0)
        {
            List<MktSpecialAdvert> exec =
                mktSpecialAdvertDao.select().eq("position", position).in("farmer", farmers).exec();
            if (exec.size() == 0)
                return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
            for (MktSpecialAdvert a : exec)
            {
                keys.add(a.getAdvertKey());
            }
        }
        PageResult<MktAdvertOnList> pageResult = mktAdvertDao.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq("type", AdvertType.SPECIAL_AREA)
            .eq("ascription", CurrentSession.ascriptionPkey())
            .sort("sort", false)
            .eq("position", position)
            .in("pkey", keys)
            .execDto(MktAdvertOnList.class);
        setGoodsName(pageResult.getContent());
        Map<String, String> map = farmerDao.findNameMap(CurrentSession.ascriptionPkey());
        for (MktAdvertOnList d : pageResult.getContent())
        {
            List<MktSpecialAdvert> exec = mktSpecialAdvertDao.select().eq("advertKey", d.getPkey()).exec();
            if (!exec.isEmpty())
            {
                List<String> farmerKeys = new ArrayList<>();
                List<String> farmersName = new ArrayList<>();
                for (MktSpecialAdvert sa : exec)
                {
                    farmerKeys.add(sa.getFarmer());
                    if (map.containsKey(sa.getFarmer()))
                    {
                        farmersName.add(map.get(sa.getFarmer()));
                    }
                }
                d.setFarmers(farmerKeys);
                d.setFarmersName(farmersName);
            }
            if(d.getTargerKeys()!=null&&d.getVisibleRange()!=null&&d.getVisibleRange().equals(MemberVisibleRange.TAG)&&!d.getTargerKeys().isEmpty())
            {
                
                List<MktTag> list=  mktTagDao.get(d.getTargerKeys());
                if(!list.isEmpty())
                {
                    List<String>liststr=list.stream().map(MktTag::getName).collect(Collectors.toList());
                    d.setVisibleRangeName(String.join(",", liststr)); 
                }
            }
        }
        return pageResult;
    }
    
    // 将链接是商品的 objKey的pkey 改成name 返回给前段
    private void setGoodsName(List<MktAdvertOnList> list)
    {
        for (MktAdvertOnList ad : list)
        {
            if ((ad.getPosition() == AdvertPosition.ADVERT_POSITION_GOODS_MAIN
                || ad.getPosition() == AdvertPosition.ADVERT_POSITION_MSD_GOODS_MAIN)
                && StringUtil.isNotBlank(ad.getPositionObj()))
            {
                MktGoodsMain goodsMain = goodsMainDao.getGoodsMain(Integer.valueOf(ad.getPositionObj()));
                if (goodsMain != null)
                {
                    StringBuilder sb = new StringBuilder();
                    MktGtype gtype = gtypeDao.getGtype(goodsMain.getGtype());
                    if (gtype != null)
                        sb.append(gtype.getName()).append("/");
                    sb.append(goodsMain.getName());
                    ad.setPositionObjName(sb.toString());
                    ad.setPositionObj(goodsMain.getGtype() + "," + ad.getPositionObj());
                }
            }
            switch (ad.getUrlType())
            {
                case LINK:
                {
                    String objKey = ad.getObjKey();
                    String value = ad.getUrlType().getValue();
                    if (StringUtils.isNotBlank(objKey) && objKey.contains(value))
                        objKey = objKey.replace(value, "");
                    ad.setObjKey(objKey);
                    break;
                }
                case GOODS:
                {
                    if (StringUtils.isNotBlank(ad.getObjKey()))
                    {
                        MktGoods goods = goodsDao.get(Integer.valueOf(ad.getObjKey()));
                        if (goods != null)
                            ad.setGoodsName(goods.getTitle());
                    }
                    break;
                }
                case GTYPE:
                {
                    String objKey = ad.getObjKey();
                    if (StringUtils.isNotBlank(objKey))
                    {
                        if (objKey.contains(","))
                        {
                            String[] split = objKey.split(",");
                            MktGoodsMain goodsMain = goodsMainDao.get(Integer.valueOf(split[1]));
                            if (goodsMain != null)
                                ad.setObjKeyName(goodsMain.getName());
                        }
                        else
                        {
                            MktGtype gtype = gtypeDao.get(Integer.valueOf(objKey));
                            if (gtype != null)
                                ad.setObjKeyName(gtype.getName());
                        }
                    }
                    break;
                }
                case ACTIVITY:
                {
                    if (StringUtil.isNotBlank(ad.getObjKey()))
                    {
                        MktActivity activity = activityDao.get(Integer.valueOf(ad.getObjKey()));
                        if (activity != null)
                            ad.setActivityName(activity.getName());
                    }
                    break;
                }
                case VENDOR:
                {
                    if (StringUtil.isNotBlank(ad.getObjKey()))
                    {
                        MktVendor vendor = vendorDao.get(Integer.valueOf(ad.getObjKey()));
                        if(vendor != null)
                            ad.setObjKeyName(vendor.getDisplayName());
                    }
                }
                break;
                default:
            }
            if(ad.getVisibleRange()!=null&&ad.getVisibleRange().equals(MemberVisibleRange.TAG)&&!ad.getTargerKeys().isEmpty())
            {
                List<MktTag> rlist=  mktTagDao.get(ad.getTargerKeys());
                if(!list.isEmpty())
                {
                    List<String>liststr=rlist.stream().map(MktTag::getName).collect(Collectors.toList());
                    ad.setVisibleRangeName(String.join(",", liststr)); 
                }

            }
        }
        
        
    }
    
    @Transactional
    public MktAdvertOnList updAdvert(Integer pkey, String name, AdvertPosition position, String positionObj,
        String photo, LinkType urlType, String objKey, Integer sort, List<String> farmers,
        LocationType locationType,
        List<Integer> targerKeys, MemberVisibleRange visibleRange)
    {
        String currentFarmer = CurrentSession.marketPkey();
        String realPositionObj = null;
        if (position == AdvertPosition.ADVERT_POSITION_GOODS_MAIN)
        {
            if (StringUtil.isBlank(positionObj))
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "请选择展示分类");
            realPositionObj = positionObj;
            if (realPositionObj.contains(","))
            {
                String[] split = realPositionObj.split(",");
                realPositionObj = split[1];
            }
            if (!StringUtil.isNumeric(realPositionObj))
                throw TofocusException.of(WsaleErrCode.GOODSMAIN_CORRECT);
            MktGoodsMain goodsMain = goodsMainDao.getGoodsMain(Integer.valueOf(realPositionObj));
            if (goodsMain == null || !Objects.equal(goodsMain.getFarmer(), currentFarmer))
                throw TofocusException.of(WsaleErrCode.GOODSMAIN_CORRECT, "找不到分类");
        }
        MktAdvert advert = mktAdvertDao.get(pkey);
        if (StringUtils.isNotBlank(name))
            advert.setName(name);
        if (position != null)
            advert.setPosition(position);
        if (StringUtil.isNotBlank(realPositionObj))
            advert.setPositionObj(realPositionObj);
        if (photo != null)
            advert.setPhoto(photo);
        if(locationType!=null)
            advert.setLocationType(locationType);
        if(visibleRange!=null&&visibleRange.equals(MemberVisibleRange.TAG)&&targerKeys!=null&&!targerKeys.isEmpty())
        {
            advert.setVisibleRange(visibleRange);
            advert.setTargerKeys(targerKeys);
        }
        else
            advert.setVisibleRange(visibleRange);
            
            
        if (urlType != null)
        {
            switch (urlType)
            {
                case NOT_URL:
                    advert.setObjKey(null);
                    break;
                case LINK:
                    advert.setObjKey(urlType.getValue() + objKey);
                    break;
                case GOODS:
                {
                    if (StringUtil.isBlank(objKey))
                        throw TofocusException.of(WsaleErrCode.GOODS_PKEY_CORRECT);
                    if (!StringUtil.isNumeric(objKey))
                        throw TofocusException.of(WsaleErrCode.GOODS_PKEY_CORRECT);
                    MktGoods goods = goodsDao.getGoods(Integer.valueOf(objKey));
                    if (goods == null)
                        throw TofocusException.of(WsaleErrCode.GOODS_PKEY_CORRECT);
                    advert.setObjKey(objKey);
                    break;
                }
                case GTYPE:
                {
                    if (StringUtil.isBlank(objKey))
                        throw TofocusException.of(WsaleErrCode.GOODS_PKEY_CORRECT);
                    checkGtypeAndMain(objKey);
                    advert.setObjKey(objKey);
                    break;
                }
                case ACTIVITY:
                {
                    if (StringUtil.isBlank(objKey))
                        throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "请选择卡券活动");
                    if (!StringUtil.isNumeric(objKey))
                        throw TofocusException.of(LejiaErrCode.ACTIVITY_NOT_FOUND);
                    MktActivity activity = activityDao.get(Integer.valueOf(objKey));
                    if (activity == null)
                        throw TofocusException.of(LejiaErrCode.ACTIVITY_NOT_FOUND);
                    advert.setObjKey(objKey);
                    break;
                }

                default:
                    advert.setObjKey(objKey);
            }
            advert.setUrlType(urlType);
        }
        AdvertType type = advert.getType();
        if (type != null && type.getIndex() == AdvertType.SPECIAL_AREA.getIndex())
        {
            if (farmers == null || farmers.isEmpty())
                throw TofocusException.of(LejiaErrCode.DATA_NOT_EMPTY);
            List<MktSpecialAdvert> exec = mktSpecialAdvertDao.select().eq("advertKey", pkey).exec();
            mktSpecialAdvertDao.removeAll(exec);
            addSpecialAdvert(advert.getPkey(), advert.getPosition(), farmers, CurrentSession.ascriptionPkey());
        }
        if (sort != null)
            advert.setSort(sort);
        MktAdvert update = mktAdvertDao.update(advert);
        return BeanUtil.beanFrom(MktAdvertOnList.class, update);
    }
    
    public Boolean delAdvert(Integer pkey)
    {
        //		MktAdvert advert = mktAdvertDao.get(pkey);
        //		if(advert.getEnabled())
        //			throw TofocusException.of(WsaleErrCode.NOT_DELETED);
        return mktAdvertDao.removeById(pkey);
    }
    
    public Boolean enabledAdvert(Integer pkey, Boolean flag)
    {
        MktAdvert advert = mktAdvertDao.get(pkey);
        advert.setEnabled(flag);
        MktAdvert update = mktAdvertDao.update(advert);
        if (update.getEnabled() == flag)
            return true;
        return false;
    }
    
    public Boolean enableFunMenu(Integer pkey, Boolean enabled)
    {
        
        MktFunMenuConfig config = mktFunMenuConfigDao.get(pkey);
        config.setEnabled(enabled);
        mktFunMenuConfigDao.update(config);
        return true;
    }
    
    public PageResult<MktFunMenuConfigOnList> queryFunMenuConfig(int page, int pagesize, List<String> farmers)
    {
        
        PageResult<MktFunMenuConfigOnList> rpg =
            mktFunMenuConfigDao.queryMktFunMenuConfig(page, pagesize, farmers, MktFunMenuConfigOnList.class);
        for (MktFunMenuConfigOnList ad : rpg.getContent())
        {
            processType(ad);
        }
        
        return rpg;
    }
    
    public MktFunMenuConfigInfo getFunMenuConfig(Integer pkey)
    {
        
        MktFunMenuConfigInfo info = mktFunMenuConfigDao.get(pkey, MktFunMenuConfigInfo.class);
        
        switch (info.getUrlType())
        {
            case LINK:
            {
                String objKey = info.getObjKey();
                String value = info.getUrlType().getValue();
                if (StringUtils.isNotBlank(objKey) && objKey.contains(value))
                    objKey = objKey.replace(value, "");
                info.setObjKey(objKey);
                break;
            }
            case GOODS:
            {
                if (StringUtils.isNotBlank(info.getObjKey()))
                {
                    MktGoods goods = goodsDao.get(Integer.valueOf(info.getObjKey()));
                    if (goods != null)
                        info.setGoodsName(goods.getTitle());
                }
                break;
            }
            case GTYPE:
            {
                String objKey = info.getObjKey();
                if (StringUtils.isNotBlank(objKey))
                {
                    if (objKey.contains(","))
                    {
                        String[] split = objKey.split(",");
                        MktGoodsMain goodsMain = goodsMainDao.get(Integer.valueOf(split[1]));
                        if (goodsMain != null)
                            info.setObjKeyName(goodsMain.getName());
                    }
                    else
                    {
                        MktGtype gtype = gtypeDao.get(Integer.valueOf(objKey));
                        if (gtype != null)
                            info.setObjKeyName(gtype.getName());
                    }
                }
                break;
            }
            case ACTIVITY:
            {
                if (StringUtil.isNotBlank(info.getObjKey()))
                {
                    MktActivity activity = activityDao.get(Integer.valueOf(info.getObjKey()));
                    if (activity != null)
                        info.setActivityName(activity.getName());
                }
                break;
            }
            case VENDOR:
            {
                if (StringUtil.isNotBlank(info.getObjKey()))
                {
                    MktVendor vendor = vendorDao.get(Integer.valueOf(info.getObjKey()));
                    if(vendor != null)
                        info.setObjKeyName(vendor.getDisplayName());
                }
            }
            break;
            default:
        }
        
        return info;
    }
    
    public Boolean updFunMenuConfig(MktFunMenuConfigInfo info, String farmer)
    {
        MktFunMenuConfig config = BeanUtil.beanFrom(MktFunMenuConfig.class, info);
        config.setFarmer(farmer);
        
        if (info.getUrlType() != null)
        {
            switch (info.getUrlType())
            {
                case NOT_URL:
                    config.setObjKey(null);
                    break;
                case LINK:
                    config.setObjKey(info.getUrlType().getValue() + info.getObjKey());
                    break;
                case GOODS:
                {
                    if (StringUtil.isBlank(info.getObjKey()))
                        throw TofocusException.of(WsaleErrCode.GOODS_PKEY_CORRECT);
                    if (!StringUtil.isNumeric(info.getObjKey()))
                        throw TofocusException.of(WsaleErrCode.GOODS_PKEY_CORRECT);
                    MktGoods goods = goodsDao.getGoods(Integer.valueOf(info.getObjKey()));
                    if (goods == null)
                        throw TofocusException.of(WsaleErrCode.GOODS_PKEY_CORRECT);
                    config.setObjKey(info.getObjKey());
                    break;
                }
                case GTYPE:
                {
                    if (StringUtil.isBlank(info.getObjKey()))
                        throw TofocusException.of(WsaleErrCode.GOODS_PKEY_CORRECT);
                    checkGtypeAndMain(info.getObjKey());
                    config.setObjKey(info.getObjKey());
                    break;
                }
                case ACTIVITY:
                {
                    if (StringUtil.isBlank(info.getObjKey()))
                        throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "请选择卡券活动");
                    if (!StringUtil.isNumeric(info.getObjKey()))
                        throw TofocusException.of(LejiaErrCode.ACTIVITY_NOT_FOUND);
                    MktActivity activity = activityDao.get(Integer.valueOf(info.getObjKey()));
                    if (activity == null)
                        throw TofocusException.of(LejiaErrCode.ACTIVITY_NOT_FOUND);
                    config.setObjKey(info.getObjKey());
                    break;
                }
                case VENDOR:
                {
                    if (StringUtil.isNotBlank(info.getObjKey()))
                    {
 
                        MktVendor vendor = vendorDao.get(Integer.valueOf(info.getObjKey()));
                        if(vendor != null)
                        {
                            info.setObjKeyName(vendor.getDisplayName());
                            config.setObjKey(info.getObjKey());
                        }
                    }
                }
                break;
                case WEIXIN_MINI_PROGRAM:
                {
                    config.setObjKey(info.getObjKey());
                    break;
                }
                default:
                    config.setObjKey(info.getUrlType().getValue());
            }
            config.setUrlType(info.getUrlType());
        }
        
        mktFunMenuConfigDao.put(config);
        return true;
    }
    
    public Boolean delFunMenuConfig(Integer pkey)
    {
        
        mktFunMenuConfigDao.removeById(pkey);
        return true;
    }
    
    private void processType(MktFunMenuConfigOnList ad)
    {
        
        switch (ad.getUrlType())
        {
            case LINK:
            {
                String objKey = ad.getObjKey();
                String value = ad.getUrlType().getValue();
                if (StringUtils.isNotBlank(objKey) && objKey.contains(value))
                    objKey = objKey.replace(value, "");
                ad.setObjKey(objKey);
                break;
            }
            case GOODS:
            {
                if (StringUtils.isNotBlank(ad.getObjKey()))
                {
                    MktGoods goods = goodsDao.get(Integer.valueOf(ad.getObjKey()));
                    if (goods != null)
                        ad.setGoodsName(goods.getTitle());
                }
                break;
            }
            case GTYPE:
            {
                String objKey = ad.getObjKey();
                if (StringUtils.isNotBlank(objKey))
                {
                    if (objKey.contains(","))
                    {
                        String[] split = objKey.split(",");
                        MktGoodsMain goodsMain = goodsMainDao.get(Integer.valueOf(split[1]));
                        if (goodsMain != null)
                            ad.setObjKeyName(goodsMain.getName());
                    }
                    else
                    {
                        MktGtype gtype = gtypeDao.get(Integer.valueOf(objKey));
                        if (gtype != null)
                            ad.setObjKeyName(gtype.getName());
                    }
                }
                break;
            }
            case ACTIVITY:
            {
                if (StringUtil.isNotBlank(ad.getObjKey()))
                {
                    MktActivity activity = activityDao.get(Integer.valueOf(ad.getObjKey()));
                    if (activity != null)
                        ad.setActivityName(activity.getName());
                }
                break;
            }
            case VENDOR:
            {
                if (StringUtil.isNotBlank(ad.getObjKey()))
                {
                    MktVendor vendor = vendorDao.get(Integer.valueOf(ad.getObjKey()));
                    if(vendor != null)
                        ad.setObjKeyName(vendor.getDisplayName());
                }
            }
            break;
            default:
        }
        
        if(ad.getVisibleRange()!=null&&ad.getVisibleRange().equals(MemberVisibleRange.TAG)&&!ad.getTargerKeys().isEmpty())
        {
            List<MktTag> list=  mktTagDao.get(ad.getTargerKeys());
            if(!list.isEmpty())
            {
                List<String>liststr=list.stream().map(MktTag::getName).collect(Collectors.toList());
                ad.setVisibleRangeName(String.join(",", liststr)); 
            }

        }
       

    }
    
}

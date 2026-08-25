package cn.tofocus.lejia.domain;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.MktIndexAdvertOnList;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.market.MktActivity;
import cn.tofocus.lejia.bean.entity.market.MktIndexAdvert;
import cn.tofocus.lejia.bean.enums.LinkType;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.market.MktActivityDao;
import cn.tofocus.lejia.dao.market.MktIndexAdvertDao;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.exception.WsaleErrCode;

@Component
public class IndexAdvertManager
{
    
    @Autowired
    private MktIndexAdvertDao indexAdvertDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private MktActivityDao activityDao;
    
    public Integer insIndexAdvert(MktIndexAdvertOnList entity)
    {
        MktIndexAdvert advert = BeanUtil.beanFrom(MktIndexAdvert.class, entity);
        advert.setRowVension(1);
        advert.setCompany(CurrentSession.companyPkey());
        if(StringUtils.isBlank(entity.getFarmer()))
            advert.setFarmer(CurrentSession.marketPkey());
        advert.setAscription(CurrentSession.ascriptionPkey());
        advert.setSort(0);
        
        switch (entity.getUrlType())
        {
            case NOT_URL:
                advert.setObjKey(null);
                break;
            case LINK:
                advert.setObjKey(entity.getObjKey());
                break;
            case GOODS:
            {
                if (StringUtil.isBlank(entity.getObjKey())) throw TofocusException.of(WsaleErrCode.GOODS_PKEY_CORRECT);
                if (!StringUtil.isNumeric(entity.getObjKey()))
                    throw TofocusException.of(WsaleErrCode.GOODS_PKEY_CORRECT);
                MktGoods goods = goodsDao.getGoods(Integer.valueOf(entity.getObjKey()));
                if (goods == null) throw TofocusException.of(WsaleErrCode.GOODS_PKEY_CORRECT);
                advert.setObjKey(entity.getObjKey());
                break;
            }
            case ACTIVITY:
            {
                if (StringUtil.isBlank(entity.getObjKey()))
                    throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "请选择卡券活动");
                if (!StringUtil.isNumeric(entity.getObjKey()))
                    throw TofocusException.of(LejiaErrCode.ACTIVITY_NOT_FOUND);
                MktActivity activity = activityDao.get(Integer.valueOf(entity.getObjKey()));
                if (activity == null) throw TofocusException.of(LejiaErrCode.ACTIVITY_NOT_FOUND);
                advert.setObjKey(entity.getObjKey());
                break;
            }
            default:
                advert.setObjKey(entity.getUrlType().getValue());
        }
        
        MktIndexAdvert add = indexAdvertDao.add(advert);
        return add.getPkey();
    }
    
    public PageResult<MktIndexAdvertOnList> queryIndexAdvert(int page, int pagesize)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        PageResult<MktIndexAdvertOnList> result = indexAdvertDao.queryIndexAdvert(page, pagesize, ascription);
        for (MktIndexAdvertOnList ad : result.getContent())
        {
            switch (ad.getUrlType())
            {
                case GOODS:
                {
                    if (StringUtil.isNotBlank(ad.getObjKey()))
                    {
                        MktGoods goods = goodsDao.get(Integer.valueOf(ad.getObjKey()));
                        if (goods != null) ad.setGoodsName(goods.getTitle());
                    }
                    break;
                }
                case ACTIVITY:
                {
                    if (StringUtil.isNotBlank(ad.getObjKey()))
                    {
                        MktActivity activity = activityDao.get(Integer.valueOf(ad.getObjKey()));
                        if (activity != null) ad.setActivityName(activity.getName());
                    }
                    break;
                }
            }
            ad.setSubjectName(ad.getSubject().getName());
        }
        return result;
    }
    
    public Integer updIndexAdvert(MktIndexAdvertOnList entity)
    {
        MktIndexAdvert advert = indexAdvertDao.get(entity.getPkey());
        if (advert == null) throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
        BeanUtils.copyProperties(entity, advert, "createdTime");
        LinkType urlType = entity.getUrlType();
        String objKey = entity.getObjKey();
        if (urlType != null)
        {
            switch (entity.getUrlType())
            {
                case NOT_URL:
                    advert.setObjKey(null);
                    break;
                case LINK:
                    advert.setObjKey(objKey);
                    break;
                case GOODS:
                {
                    if (StringUtil.isBlank(objKey)) throw TofocusException.of(WsaleErrCode.GOODS_PKEY_CORRECT);
                    if (!StringUtil.isNumeric(objKey)) throw TofocusException.of(WsaleErrCode.GOODS_PKEY_CORRECT);
                    MktGoods goods = goodsDao.getGoods(Integer.valueOf(objKey));
                    if (goods == null) throw TofocusException.of(WsaleErrCode.GOODS_PKEY_CORRECT);
                    advert.setObjKey(objKey);
                    break;
                }
                case ACTIVITY:
                {
                    if (StringUtil.isBlank(objKey)) throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "请选择卡券活动");
                    if (!StringUtil.isNumeric(objKey)) throw TofocusException.of(LejiaErrCode.ACTIVITY_NOT_FOUND);
                    MktActivity activity = activityDao.get(Integer.valueOf(objKey));
                    if (activity == null) throw TofocusException.of(LejiaErrCode.ACTIVITY_NOT_FOUND);
                    advert.setObjKey(objKey);
                    break;
                }
                default:
                    advert.setObjKey(urlType.getValue());
            }
            advert.setUrlType(urlType);
        }
        
        MktIndexAdvert update = indexAdvertDao.update(advert);
        return update.getPkey();
    }
    
    public Boolean delIndexAdvert(Integer pkey)
    {
        return indexAdvertDao.removeById(pkey);
    }
}

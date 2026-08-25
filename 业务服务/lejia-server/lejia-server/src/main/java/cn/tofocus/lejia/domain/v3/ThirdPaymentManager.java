package cn.tofocus.lejia.domain.v3;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.XaszAssociationOnInfo;
import cn.tofocus.lejia.bean.entity.applet.XaszAssociationEntity;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.applet.XaszAssociationDao;
import cn.tofocus.lejia.domain.app.SaasTokenPublicManager;

@Component
public class ThirdPaymentManager
{
    @Autowired
    private XaszAssociationDao xaDao;
    
    @Autowired
    private SaasTokenPublicManager saasTokenPublicManager;
    
    public Boolean ins(XaszAssociationOnInfo dto)
    {
        XaszAssociationEntity entity = BeanUtil.beanFrom(XaszAssociationEntity.class, dto);
        long count = xaDao.aggregation().eq("farmer", dto.getFarmer()).eq("market", dto.getMarket()).execCount();
        if (count > 0) return false;
        entity.setAscription(CurrentSession.ascriptionPkey());
        xaDao.add(entity);
        return true;
    }
    
    public Boolean upd(XaszAssociationOnInfo dto)
    {
        XaszAssociationEntity entity = BeanUtil.beanFrom(XaszAssociationEntity.class, dto);
        long count = xaDao.aggregation()
            .eq("farmer", dto.getFarmer())
            .eq("market", dto.getMarket())
            .notEq("pkey", dto.getPkey())
            .execCount();
        if (count > 0) return false;
        entity.setAscription(CurrentSession.ascriptionPkey());
        xaDao.put(entity);
        return true;
    }
    
    public Boolean del(Integer pkey)
    {
        return xaDao.removeById(pkey);
    }
    
    public PageResult<XaszAssociationOnInfo> query(int page, int pagesize)
    {
        PageResult<XaszAssociationOnInfo> pageResult =
            xaDao.selectPage().page(page).pagesize(pagesize).eq("ascription", CurrentSession.ascriptionPkey()).execDto(XaszAssociationOnInfo.class);
        
        Map<Integer, String> map = saasTokenPublicManager.listMarketName();
        if (map == null) return pageResult;
        for (XaszAssociationOnInfo xa : pageResult.getContent())
        {
            if (map.containsKey(xa.getMarket())) xa.setMarketName(map.get(xa.getMarket()));
        }
        return pageResult;
    }
    
    public Map<Integer,String> listMarketName()
    {
        return saasTokenPublicManager.listMarketName();
    }
    
}

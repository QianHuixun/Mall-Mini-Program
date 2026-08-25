package cn.tofocus.lejia.domain.market;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.MktCourierOnList;
import cn.tofocus.lejia.bean.entity.market.MktCourier;
import cn.tofocus.lejia.bean.entity.market.MktMarketCourier;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerConfig;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.market.MktCourierDao;
import cn.tofocus.lejia.dao.market.MktMarketCourierDao;
import cn.tofocus.lejia.dao.sys.SysFarmerConfigDao;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.exception.WsaleErrCode;

@Component
public class LejiaCourierManager
{
    @Autowired
    private MktCourierDao courierDao;
    
    @Autowired
    private SysFarmerConfigDao sysFarmerConfigDao;
    
    @Autowired
    private MktMarketCourierDao marketCourierDao;
    
    @Transactional
    public MktCourierOnList insCourier(String name, String mobile)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        List<MktCourier> exec = courierDao.select()
            .eq("ascription", ascription)
            .eq("mobile", mobile).exec();
        if (!exec.isEmpty())
        {
            MktCourier courier = exec.get(0);
            if (exec.get(0).getIdDel())
            {
                courier.setIdDel(false);
                courier.setName(name);
                MktCourier update = courierDao.update(courier);
                BeanUtil.beanFrom(MktCourierOnList.class, update);
            }
            else
                throw TofocusException.of(WsaleErrCode.MOBILE_REPEAT);
        }
        
        MktCourier add =
            courierDao.addMktCourier(name, mobile, CurrentSession.marketPkey(), CurrentSession.companyPkey(), ascription);
        return BeanUtil.beanFrom(MktCourierOnList.class, add);
    }
    
    public MktCourierOnList getCourier(Integer pkey)
    {
        MktCourier courier = courierDao.getOne(pkey);
        return BeanUtil.beanFrom(MktCourierOnList.class, courier);
    }
    
    public PageResult<MktCourierOnList> queryCourier(int page, int pagesize, String courierName, String courierMobile,
        Boolean enabled)
    {
        PageResult<MktCourier> pageResult =
            courierDao.queryCourier(page, pagesize, courierName, courierMobile, enabled, CurrentSession.marketPkey(), CurrentSession.ascriptionPkey());
        return BeanUtil.beanPageFrom(MktCourierOnList.class, pageResult);
    }
    
    public MktCourierOnList updCourier(Integer pkey, String name, String mobile, String remark)
    {
        MktCourier courier = courierDao.getOne(pkey);
        if (courier == null) throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
        if (StringUtils.isNotBlank(name)) courier.setName(name);
        if (StringUtils.isNotBlank(mobile))
        {
            List<MktCourier> exec = courierDao.select().eq("ascription", CurrentSession.ascriptionPkey()).eq("mobile", mobile).exec();
            if (exec.size() > 0 && !mobile.equals(courier.getMobile()))
                throw TofocusException.of(WsaleErrCode.MOBILE_REPEAT);
            courier.setMobile(mobile);
        }
        courier.setRemark(remark);
        MktCourier update = courierDao.update(courier);
        return BeanUtil.beanFrom(MktCourierOnList.class, update);
    }
    
    public Boolean delCourier(Integer pkey)
    {
        MktCourier courier = courierDao.getOne(pkey);
        if (courier == null) throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
        if (courier.getEnabled()) throw TofocusException.of(WsaleErrCode.NOT_DELETED);
        
        String PrefixM = "0000";
        String mobile = courier.getMobile();
        List<MktCourier> delExec = courierDao.select().eq("ascription", CurrentSession.ascriptionPkey()).like("mobile", mobile).eq("idDel", true).exec();
        if (delExec != null && delExec.size() > 0)
        {
            List<Integer> prefixMList = new ArrayList<>();
            for (MktCourier c : delExec)
                prefixMList.add(Integer.valueOf(c.getMobile().substring(0, 4)));
            Collections.sort(prefixMList);
            PrefixM = String.format("%04d", prefixMList.get(prefixMList.size() - 1) + 1);
        }
        courier.setMobile(PrefixM + mobile);
        courier.setIdDel(true);
        MktCourier update = courierDao.update(courier);
        return update != null;
    }
    
    public Boolean enabledCourier(Integer pkey, Boolean flag)
    {
        MktCourier courier = courierDao.getOne(pkey);
        if (courier == null) throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
        if (!flag)
        {
            String marketPkey = CurrentSession.marketPkey();
            if (StringUtils.isNotBlank(marketPkey))
            {
                SysFarmerConfig config = sysFarmerConfigDao.get(marketPkey);
                if (config != null && config.getAutomaticCourier() != null && config.getAutomaticCourier())
                {
                    MktMarketCourier c =
                        marketCourierDao.selectOne().eq("market", marketPkey).eq("courierKey", pkey).exec();
                    if (c != null) throw TofocusException.of(WsaleErrCode.COURIER_DISPATCH_ENABLED);
                }
            }
        }
        courier.setEnabled(flag);
        MktCourier update = courierDao.update(courier);
        return update.getEnabled().equals(flag);
    }
    
    public Boolean bindCourier(String openid1, String openid2)
    {
        MktCourier courier = courierDao.selectOne().eq("openid1", openid1).exec();
        if(courier == null)
            throw TofocusException.of(LejiaErrCode.COURIER_INEXISTENCE);
        courier.setOpenid2(openid2);
        courierDao.update(courier);
        return true;
    }
}

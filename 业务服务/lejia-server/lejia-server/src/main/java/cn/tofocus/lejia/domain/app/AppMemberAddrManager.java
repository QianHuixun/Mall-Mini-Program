package cn.tofocus.lejia.domain.app;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.lejia.bean.dto.app.market.AppMemberAddrFourArea;
import cn.tofocus.lejia.bean.dto.app.market.JdAddressOption;
import cn.tofocus.lejia.bean.entity.jd.JdAddress;
import cn.tofocus.lejia.dao.jd.JdAddressDao;
import cn.tofocus.lejia.domain.jdvop.JdVOPAddrManager;
import com.google.common.collect.Sets;
import com.jd.open.api.sdk.domain.vopdz.ConvertAddressOpenProvider.response.convertFourAreaByLatLng.QueryAreaFourIdOpenResp;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.app.market.AppMktAddrOnList;
import cn.tofocus.lejia.bean.entity.market.MktAddr;
import cn.tofocus.lejia.bean.enums.AddrType;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.market.MktAddrDao;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.exception.WsaleErrCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppMemberAddrManager
{
    @Autowired
    private MktAddrDao addrDao;

    @Autowired
    private JdAddressDao jdAddressDao;

    @Autowired
    private JdVOPAddrManager jdVOPAddrManager;
    
    @Transactional
    public AppMktAddrOnList insAddr(AppMktAddrOnList entity)
    {
        Integer memberPkey = MobileSession.memberPkey();
        Integer ascription = MobileSession.appid();
        if (entity.getType() == null) entity.setType(AddrType.getDefault());
        if (entity.getType() == AddrType.DELIVERY) validDeliveryAddr(entity);
        MktAddr addr = BeanUtil.beanFrom(MktAddr.class, entity);
        addr.setMember(memberPkey);
        addr.setAscription(ascription);
        boolean exist = addrDao.existByMember(memberPkey, entity.getType());
        if (!exist) addr.setDefaultAddr(true);
        MktAddr add = addrDao.add(addr);
        if (exist && Boolean.TRUE.equals(add.getDefaultAddr())) modifyDefaultAddr(memberPkey, add.getPkey(), add.getType());
        return BeanUtil.beanFrom(AppMktAddrOnList.class, add);
    }
    
    public AppMktAddrOnList getAddr(Integer pkey)
    {
        MktAddr addr = addrDao.get(pkey);
        return BeanUtil.beanFrom(AppMktAddrOnList.class, addr);
    }
    
    public PageResult<AppMktAddrOnList> queryAddr(int page, int pagesize, AddrType type)
    {
        Integer memberPkey = MobileSession.memberPkey();
        Integer ascription = MobileSession.appid();
        PageResult<AppMktAddrOnList> result =
            addrDao.query(page, pagesize, ascription, memberPkey, type, AppMktAddrOnList.class);
        for (AppMktAddrOnList bean : result)
        {
            if (bean.getAddr() == null) bean.setAddr("");
            bean.setHideMobile(StringUtil.mask(bean.getMobile(), 3, 7));
        }
        return result;
    }
    
    @Transactional
    public AppMktAddrOnList updAddr(AppMktAddrOnList entity)
    {
        Integer memberPkey = MobileSession.memberPkey();
        if (entity.getType() == AddrType.DELIVERY) validDeliveryAddr(entity);
        MktAddr addr = addrDao.get(entity.getPkey());
        if (addr == null) throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
        // 类型不能变
        BeanUtils.copyProperties(entity, addr, "type");
        log.info("updGoods-mktGoods: {}", addr);
        MktAddr update = addrDao.update(addr);
        if (Boolean.TRUE.equals(entity.getDefaultAddr()))
            modifyDefaultAddr(memberPkey, entity.getPkey(), entity.getType());
        return BeanUtil.beanFrom(AppMktAddrOnList.class, update);
    }
    
    private void validDeliveryAddr(AppMktAddrOnList entity)
    {
        if (StringUtil.isBlank(entity.getPro()) || StringUtil.isBlank(entity.getCity())
            || StringUtil.isBlank(entity.getArea())) throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "请选择所在地区");
        if (StringUtil.isBlank(entity.getAddr())) throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "请输入详细地址");
    }
    
    public Boolean delAddr(Integer pkey)
    {
        return addrDao.removeById(pkey);
    }
    
    @Transactional
    public Boolean defaultAddr(Integer pkey)
    {
        Integer memberPkey = MobileSession.memberPkey();
        MktAddr addr = addrDao.get(pkey);
        if (addr == null) throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
        addr.setDefaultAddr(true);
        addrDao.update(addr);
        modifyDefaultAddr(memberPkey, pkey, addr.getType());
        return true;
    }
    
    // 修改默认地址
    private void modifyDefaultAddr(Integer memberPkey, Integer pkey, AddrType type)
    {
        List<MktAddr> exec = addrDao.select().eq("member", memberPkey).eq("type", type).notEq("pkey", pkey).exec();
        for (MktAddr bean : exec)
        {
            bean.setDefaultAddr(false);
        }
        addrDao.updateAll(exec);
    }
    
    public List<JdAddressOption> listTown(String pro, String city, String area)
    {
        // 先按区查，分别按areaName或clientName查
        List<JdAddress> areaList = jdAddressDao.listByName(area);
        if (CollectionUtil.isEmpty(areaList))
            throw TofocusException.of(LejiaErrCode.DATA_INEXISTENCE, "匹配不到地区");
        for (JdAddress areaBean : areaList)
        {
            if (areaBean.getParent() != null)
            {
                JdAddress parent = jdAddressDao.get(areaBean.getParent());
                if (parent != null && (city.equals(parent.getAreaName()) || city.equals(parent.getClientName())))
                {
                    if (parent.getParent() != null)
                    {
                        JdAddress grandParent = jdAddressDao.get(parent.getParent());
                        if (grandParent != null
                            && (pro.equals(grandParent.getAreaName()) || pro.equals(grandParent.getClientName())))
                        {
                            return jdAddressDao.listByParent(areaBean.getAreaId(), JdAddressOption.class);
                        }
                    }
                    else
                    {
                        return jdAddressDao.listByParent(areaBean.getAreaId(), JdAddressOption.class);
                    }
                }
            }
        }
        throw TofocusException.of(LejiaErrCode.DATA_INEXISTENCE, "匹配不到地区");
    }
    
    private static final Set<Long> THREE_LEVEL_PRO = Sets.newHashSet(1L, 2L, 3L, 4L);
    
    public AppMemberAddrFourArea convertFourAreaByLatLng(BigDecimal longitude, BigDecimal latitude)
    {
        QueryAreaFourIdOpenResp resp =
            jdVOPAddrManager.convertFourAreaByLatLng(longitude.doubleValue(), latitude.doubleValue());
        if (resp == null)
            return null;
        AppMemberAddrFourArea addr = new AppMemberAddrFourArea();
        JdAddress pro = jdAddressDao.get(resp.getProvinceId());
        if (pro != null)
        {
            addr.setPro(StringUtil.isNotBlank(pro.getClientName()) ? pro.getClientName() : pro.getAreaName());
        }
        else
        {
            addr.setPro(resp.getProvinceName());
        }
        if (THREE_LEVEL_PRO.contains(resp.getProvinceId()))
        {
            addr.setCity(addr.getPro());
            setArea(addr, resp.getCityId(), resp.getCityName());
            setTown(addr, resp.getCountyId(), resp.getCountyName());
        }
        else
        {
            JdAddress city = jdAddressDao.get(resp.getCityId());
            if (city != null)
            {
                addr.setCity(StringUtil.isNotBlank(city.getClientName()) ? city.getClientName() : city.getAreaName());
            }
            else
            {
                addr.setCity(resp.getCityName());
            }
            setArea(addr, resp.getCountyId(), resp.getCountyName());
            setTown(addr, resp.getTownId(), resp.getTownName());
        }
        return addr;
    }
    
    private void setArea(AppMemberAddrFourArea addr, Long jdAddressId, String jdAddressName)
    {
        JdAddress area = jdAddressDao.get(jdAddressId);
        if (area != null)
        {
            addr.setArea(StringUtil.isNotBlank(area.getClientName()) ? area.getClientName() : area.getAreaName());
        }
        else
        {
            addr.setArea(jdAddressName);
        }
    }
    
    private void setTown(AppMemberAddrFourArea addr, Long jdAddressId, String jdAddressName)
    {
        JdAddress town = jdAddressDao.get(jdAddressId);
        if (town != null)
        {
            addr.setTown(StringUtil.isNotBlank(town.getClientName()) ? town.getClientName() : town.getAreaName());
        }
        else
        {
            addr.setTown(jdAddressName);
        }
    }
}

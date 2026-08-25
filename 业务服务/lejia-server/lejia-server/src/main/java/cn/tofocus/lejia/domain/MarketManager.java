package cn.tofocus.lejia.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.lejia.bean.dto.market.*;
import cn.tofocus.lejia.bean.entity.sys.*;
import cn.tofocus.lejia.bean.entity.zx.ZxUserInfo;
import cn.tofocus.lejia.bean.enums.ZxUserType;
import cn.tofocus.lejia.bean.enums.v2.ZxCardStatus;
import cn.tofocus.lejia.dao.sys.*;
import cn.tofocus.lejia.dao.zx.ZxUserInfoDao;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import cn.tofocus.account.api.v4.AdminApiV4;
import cn.tofocus.account.api.v4.UserInDeptApiV4;
import cn.tofocus.account.dto.user.SysUserInfo;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.common.util.Util;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.entity.market.MktCard;
import cn.tofocus.lejia.bean.entity.market.MktCourier;
import cn.tofocus.lejia.bean.entity.market.MktMarketCourier;
import cn.tofocus.lejia.bean.entity.market.MktPostageConfig;
import cn.tofocus.lejia.bean.entity.market.MktSupply;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.enums.CommissionType;
import cn.tofocus.lejia.bean.enums.ConfigGoodsType;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.bean.enums.SettlementMethodType;
import cn.tofocus.lejia.bean.enums.v4.DeliveryDate;
import cn.tofocus.lejia.bean.enums.v5.FarmerType;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.Constant.Role;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.market.MktCardDao;
import cn.tofocus.lejia.dao.market.MktCourierDao;
import cn.tofocus.lejia.dao.market.MktMarketCourierDao;
import cn.tofocus.lejia.dao.market.MktPostageConfigDao;
import cn.tofocus.lejia.dao.market.MktSupplyDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.exception.WsaleErrCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MarketManager
{
    @Autowired
    private SysFarmerDao sysFarmerDao;
    
    @Autowired
    private SysAscriptionDao sysAscriptionDao;
    
    @Autowired
    private SysCompanyDao sysCompanyDao;

    @Autowired
    private SysFarmerExtendDao sysFarmerExtendDao;
    
    @Autowired
    private SysFarmerConfigDao sysFarmerConfigDao;
    
    @Autowired
    private UserManager userManager;
    
    @Autowired
    private MktPostageConfigDao postageConfigDao;
    
    @Autowired
    private SysFarmerMtypeDao farmerMtypeDao;
    
    @Autowired
    private SysUserDao sysUserDao;
    
    @Autowired
    private MktMarketCourierDao marketCourierDao;
    
    @Autowired
    private MktCourierDao courierDao;
    
    @Autowired
    private MktSupplyDao mktSupplyDao;
    
    /**
     * mkt_vendor 商户
     */
    @Resource
    private MktVendorDao mktVendorDao;
    
    @Autowired
    private SysFarmerStationDao sysFarmerStationDao;
    
    @Autowired
    private MktCardDao cardDao;
    
    @Autowired
    private SysFarmerTimeDao sysFarmerTimeDao;

    @Autowired
    private SysFarmerPickupLocationDao farmerPickupLocationDao;

    @Autowired
    private ZxUserInfoDao zxUserInfoDao;

    @Autowired
    private AdminApiV4 adminApi;

    @Autowired
    private UserInDeptApiV4 userInDeptApiV4;
    
    @Value("${tofocus.pay.options:false}")
    private Boolean payFlag;

    @Value("${zx.qingfen.ascription:13}")
    private Integer qfAscription;
    
    @Transactional
    public SysFarmerInfo insMarket(SysFarmerInfo farmer)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        if (sysFarmerDao.checkRepeatName(null, farmer.getName(), ascription))
            throw TofocusException.of(WsaleErrCode.NAME_REPEAT);
        
        SysAscription sysAscription = sysAscriptionDao.get(ascription);
        if(sysAscription != null && sysAscription.getMarketNum() != null) 
        {
            int marketNum = sysFarmerDao.countMarketNum(ascription);
            if(marketNum >= sysAscription.getMarketNum() + 1)
                throw TofocusException.of(WsaleErrCode.MARKET_NUM_ERROR);
        }
        if(qfAscription.intValue() == ascription.intValue() 
            && Boolean.TRUE.equals(farmer.getConfig().getIsEnterprise()))
        {
            if(CommissionType.BLOC.equals(farmer.getConfig().getCommissionType()))
            {
                BigDecimal cr = farmer.getConfig().getCommissionRate();
                BigDecimal tcr = Constant.ZxConfig.TJ_COMMISSION_RATE.multiply(new BigDecimal(100));
                if(cr == null || cr.compareTo(tcr) < 0)
                    throw TofocusException.of(LejiaErrCode.COMMISSIONRATE_ERROR); 
            }
        }
        
        String companyPkey = farmer.getComPkey();
        // 初始化市场主键
        SysFarmer farmerEntity = new SysFarmer();
        farmerEntity.setAscription(ascription);
        sysFarmerDao.generateID(farmerEntity);
        String farmerPkey = farmerEntity.getPkey();
        // 准备负责人账号
        SysUserInfo manager = null;
        if (farmer.getManager() != null || farmer.getMobile() != null)
        {
            SysUser sysUser = new SysUser();
            sysUser.setCompany(companyPkey);
            sysUser.setFarmer(farmerPkey);
            sysUser.setMobile(farmer.getMobile());
            sysUser.setNickname(farmer.getManager());
            sysUser.setRoleKey(Role.MARKET_MANAGER);
            sysUser.setRowVension(1);
            sysUser.setAscription(ascription);
            manager = userManager.insUser(sysUser);
        }
        
        // 创建市场
        adminApi.saveDepartment(farmerPkey, companyPkey, farmer.getName());
        //保存市场
        farmerEntity.setOrg(companyPkey);
        SysFarmer market = convertFarmerEntity(farmerEntity, farmer, manager);
        SysFarmer result = sysFarmerDao.add(market);
        //给负责人账号增加市场负责人角色
        if (manager != null)
        {
            log.info("Long: " + manager.getPkey());
            userInDeptApiV4.addUserRole(manager.getPkey(), Role.MARKET_MANAGER, farmerPkey);
        }
        farmer.setPkey(result.getPkey());
        // 清分的运营商，要新增账户数据
        if (qfAscription.equals(ascription))
        {
            ZxUserInfo zxUserInfo = new ZxUserInfo();
          
            if (farmer.getConfig().getIsEnterprise())
            {
                zxUserInfo.setType(ZxUserType.MARKET);
                zxUserInfo.setMarketAuto(Boolean.FALSE);
                zxUserInfo.setComms(BigDecimal.ZERO);
            }
            else
            {
                zxUserInfo.setType(ZxUserType.SELF_MARKET);
                zxUserInfo.setMarketAuto(null);
            }
            zxUserInfo.setValue(farmerPkey);
            zxUserInfo.setName(farmer.getName());
            zxUserInfo.setVendorAuto(Boolean.FALSE);
            zxUserInfo.setCardStatus(ZxCardStatus.NOT_BINDING);
            zxUserInfo.setDelFlag(Boolean.FALSE);
            zxUserInfo.setAscription(ascription);
            zxUserInfoDao.add(zxUserInfo);
        }
        return farmer;
    }
    
    private void bulidSysFarmerSation(SysFarmer market, SysFarmerOnList farmer)
    {
        SysFarmerStation station = sysFarmerStationDao.selectOne().eq("market", market.getPkey()).exec();
        if (station == null)
        {
            station = new SysFarmerStation();
            station.setAddress(farmer.getConfig().getAddr());
            station.setPhour(farmer.getPickupHour());
            station.setPminute(farmer.getPickupMinute());
            station.setMarket(market.getPkey());
            station.setPkey(null);
            station.setYytb(market.getConfig().getYytb());
            station.setYyte(market.getConfig().getYyte());
            station.setLatitude(market.getConfig().getLatitude());
            station.setLongitude(market.getConfig().getLongitude());
            station.setAscription(market.getAscription());
            station.setDeliveryDate(farmer.getPickupDeliveryDate());
            if(station.getDeliveryDate() == null)
                station.setDeliveryDate(DeliveryDate.TOMORROW);
            station = sysFarmerStationDao.add(station);
            log.info("BulidSysFarmerSation: " + station.toString());
        }
        else
        {
            station.setLatitude(market.getConfig().getLatitude());
            station.setLongitude(market.getConfig().getLongitude());
            station.setYytb(market.getConfig().getYytb());
            station.setYyte(market.getConfig().getYyte());
            station.setMarket(market.getPkey());
            station.setPhour(farmer.getPickupHour());
            station.setPminute(farmer.getPickupMinute());
            station.setAddress(market.getConfig().getAddr());
            station.setDeliveryDate(farmer.getPickupDeliveryDate());
            if(station.getDeliveryDate() == null)
                station.setDeliveryDate(DeliveryDate.TOMORROW);
            station = sysFarmerStationDao.update(station);
            log.info("BulidSysFarmerSation: update " + station.toString());
        }
    }
    
    public void bulidSysFarmerTime(String pkey, Integer ascription, List<SysFarmerTime> times)
    {
        List<SysFarmerTime> listTime = sysFarmerTimeDao.listTime(pkey, ascription);
        sysFarmerTimeDao.removeAll(listTime);
        if(times != null && !times.isEmpty())
        {
            List<SysFarmerTime> newList = new ArrayList<>();
            for(SysFarmerTime t : times)
            {
                SysFarmerTime newt = new SysFarmerTime();
                newt.setStartHour(t.getStartHour());
                newt.setStartMinute(t.getStartMinute());
                newt.setEndHour(t.getEndHour());
                newt.setEndMinute(t.getEndMinute());
                newt.setFarmer(pkey);
                newt.setAscription(ascription);
                newList.add(newt);
            }
            for(int i = 0; i < newList.size(); i++)
            {
                SysFarmerTime sysFarmerTime = newList.get(i);
                for(int j = 0; j < newList.size(); j++)
                {
                    SysFarmerTime timej = newList.get(j);
                    if(i != j)
                    {
                        Integer sh = sysFarmerTime.getStartHour();
                        Integer sm = sysFarmerTime.getStartMinute();
                        Integer eh = sysFarmerTime.getEndHour();
                        Integer em = sysFarmerTime.getEndMinute();
                        
                        Integer shj = timej.getStartHour();
                        Integer smj = timej.getStartMinute();
                        Integer ehj = timej.getEndHour();
                        Integer emj = timej.getEndMinute();
                        if ((sh * 60 + sm) >= (shj * 60 + smj) && (sh * 60 + sm) <= (ehj * 60 + emj))
                            throw TofocusException.of(WsaleErrCode.FARMER_TIME_ERROR);
                        if ((eh * 60 + em) >= (shj * 60 + smj) && (eh * 60 + em) <= (ehj * 60 + emj))
                            throw TofocusException.of(WsaleErrCode.FARMER_TIME_ERROR);
                    }
                }
            }
            sysFarmerTimeDao.putAll(newList);
        }
    }
    
    private SysFarmer convertFarmerEntity(SysFarmer farmerEntity, SysFarmerOnList farmer, SysUserInfo manager)
    {
        SysFarmer market = BeanUtil.beanFrom(SysFarmer.class, farmer);
        market.setPkey(farmerEntity.getPkey());
        market.setManagerUser(manager.getPkey().intValue());
        market.setOrg(farmerEntity.getOrg());
        market.setRowVension(1);
        market.setType(farmer.getType());
        if(market.getType() == null)
            market.setType(FarmerType.MARKET_SHOPPING_MALL);
        market.setAscription(farmerEntity.getAscription());
        market.setDept(farmerEntity.getPkey());
        market.setIdDel(false);
        market.setEnabled(true);
        
        SysFarmerConfig config = new SysFarmerConfig();
        if (payFlag)
        {
            config.setLatitude(new BigDecimal("29.320139"));
            config.setLongitude(new BigDecimal("120.092338"));
        }
        else
        {
            config.setLatitude(new BigDecimal(0));
            config.setLongitude(new BigDecimal(0));
        }
        config.setPkey(market.getPkey());
        config.setYStatus(false);
        config.setYytb("");
        config.setYyte("");
        config.setYjTime("");
        config.setYjPos(5);
        config.setDeliveryRange(new BigDecimal("5"));
        config.setIsFree(false);
        config.setFreeDelivery(BigDecimal.ZERO);
        config.setPsTime(new ArrayList<String>());
        config.setAutomaticCourier(false);
        if(FarmerType.VENDOR_SHOPPING_MALL.equals(market.getType()))
            config.setAutomaticPurchase(true);
        else
            config.setAutomaticPurchase(false);
        config.setDistributionConfig(true);
        config.setDeliveryDate(DeliveryDate.TOMORROW);
        config.setAscription(farmerEntity.getAscription());
        config.setSettlementMethod(farmer.getConfig().getSettlementMethod());
        config.setIsEnterprise(farmer.getConfig().getIsEnterprise());
        config.setCommissionType(farmer.getConfig().getCommissionType());
        config.setCommissionRate(farmer.getConfig().getCommissionRate());
        config.setMemberCommissionRate(farmer.getConfig().getMemberCommissionRate());
        market.setConfig(config);
        List<MktPostageConfig> entitys = new ArrayList<>();
        for (int i = 0; i < 4; i++)
        {
            MktPostageConfig bean = new MktPostageConfig();
            bean.setPostage(new BigDecimal(0));
            bean.setWeight(new BigDecimal(0));
            bean.setCompany(farmer.getComPkey());
            bean.setFarmer(farmerEntity.getPkey());
            bean.setRowVension(1);
            entitys.add(bean);
        }
        postageConfigDao.addAll(entitys);
        List<SysFarmerMtype> mtypeAdd = new ArrayList<>();
        List<MType> types = new ArrayList<>();
        types.add(MType.MARKET_GOODS);
        types.add(MType.CUT_GOODS);
        types.add(MType.COLLAGE_GOODS);
        types.add(MType.PRESALE_GOODS);
        for (MType m : types)
        {
            SysFarmerMtype mt = new SysFarmerMtype();
            mt.setFarmer(farmerEntity.getPkey());
            mt.setMType(m);
            mt.setDelivery(true);
            mt.setPickup(true);
            mt.setAscription(farmerEntity.getAscription());
            mtypeAdd.add(mt);
        }
        farmerMtypeDao.addAll(mtypeAdd);
        return market;
    }
    
    public SysFarmerInfo getMarket(String pkey)
    {
        if (StringUtil.isEmpty(pkey)) pkey = CurrentSession.marketPkey();
        System.out.println("CurrentSession.marketPkey();" + CurrentSession.marketPkey());
        SysFarmer farmer = sysFarmerDao.get(pkey);
        if (farmer == null || farmer.getIdDel()) throw TofocusException.of(WsaleErrCode.UNKOWN_MARKET);
        SysFarmerInfo beanFrom = BeanUtil.beanFrom(SysFarmerInfo.class, farmer);
        beanFrom.setComPkey(farmer.getOrg());
        if (beanFrom.getConfig() == null) beanFrom.setConfig(new SysFarmerConfig());
        SysFarmerStation station = sysFarmerStationDao.selectOne().eq("market", CurrentSession.marketPkey()).exec();
        if (station != null)
        {
            beanFrom.setPickupMinute(station.getPminute());
            beanFrom.setPickupHour(station.getPhour());
            beanFrom.setPickupDeliveryDate(station.getDeliveryDate());
        }
        else
        {
            beanFrom.setPickupDeliveryDate(DeliveryDate.TOMORROW);
        }
        List<SysFarmerTime> listTime = sysFarmerTimeDao.listTime(pkey, farmer.getAscription());
        beanFrom.setTimes(listTime);
        // 自提点
        List<SysFarmerInfo.PickupLocation> pickupLocations =
            farmerPickupLocationDao.findByFarmer(pkey, farmer.getAscription(), SysFarmerInfo.PickupLocation.class);
        beanFrom.setPickupLocations(pickupLocations);
        return beanFrom;
    }
    
    public PageResult<SysFarmerOnList> queryMarket(int page, int pagesize, String marketName)
    {
        String marketPkey = CurrentSession.marketPkey();
        String companyPkey = CurrentSession.companyPkey();
        Integer ascription = CurrentSession.ascriptionPkey();
        if ((Constant.Operation + ascription).equals(marketPkey)) marketPkey = null;
        if ((Constant.Operation + ascription).equals(companyPkey)) companyPkey = null;
        PageResult<SysFarmer> pageResult =
            sysFarmerDao.queryMarket(page, pagesize, marketName, marketPkey, companyPkey, ascription);
        PageResult<SysFarmerOnList> result = BeanUtil.beanPageFrom(SysFarmerOnList.class, pageResult);
        if (pageResult.getTotalElements() <= 0) return result;
        for (SysFarmerOnList bean : result.getContent())
        {
            for (SysFarmer f : pageResult.getContent())
            {
                if (bean.getPkey().equals(f.getPkey())) bean.setComPkey(f.getOrg());
            }
        }
        return result;
    }
    
    @Transactional
    public SysFarmerInfo updMarket(SysFarmerInfo entity)
    {
        //查原记录
        SysFarmer old = sysFarmerDao.get(entity.getPkey());
        if (old == null)
            throw TofocusException.of(WsaleErrCode.UNKOWN_MARKET);
        else
        {
            boolean updName = false;
            if (sysFarmerDao.checkRepeatName(entity.getPkey(), entity.getName(), CurrentSession.ascriptionPkey()))
                throw TofocusException.of(WsaleErrCode.NAME_REPEAT);
            //名称变更
            if (StringUtils.isNotBlank(entity.getName()) && !Util.equal(old.getName(), entity.getName()))
            {
                adminApi.saveDepartment(entity.getPkey(), old.getOrg(), entity.getName());
                old.setName(entity.getName());
                updName = true;
            }
            //保存市场
            String marketPkey = CurrentSession.marketPkey();
            Integer ascription = CurrentSession.ascriptionPkey();
            if (StringUtils.isNotBlank(marketPkey) && (Constant.Operation + ascription).equals(marketPkey))
            {
                assembleFramerSys(entity, old);
                sysFarmerDao.update(old);
            }
            else
            {
                assembleFramer(entity, old);
                SysFarmer update = sysFarmerDao.update(old);
                bulidSysFarmerSation(update, entity);
                List<SysFarmerTime> times = entity.getTimes();
                if(times != null && !times.isEmpty())
                    bulidSysFarmerTime(update.getPkey(), update.getAscription(), times);
                else
                    throw TofocusException.of(WsaleErrCode.FARMER_TIME_ONE_ERROR);
            }
            
            // 修改市场下的商户的采购方式
            if (entity.getConfig().getSettlementMethod() != null && !payFlag)
            {
                List<MktVendor> validVendor = mktVendorDao.getValidVendor(old.getPkey(), old.getOrg());
                validVendor.forEach(vendor -> vendor.setSettlementMethod(entity.getConfig().getSettlementMethod()));
                mktVendorDao.updateAll(validVendor);
            }

            // 处理自提点
            List<SysFarmerPickupLocation> oldPickupLocations =
                farmerPickupLocationDao.findByFarmer(entity.getPkey(), ascription);
            if(entity.getPickupLocations() != null)
            {
                List<SysFarmerPickupLocation> pickupLocations =
                    Lists.newArrayListWithCapacity(entity.getPickupLocations().size());
                for (SysFarmerInfo.PickupLocation location : entity.getPickupLocations())
                {
                    SysFarmerPickupLocation locationBean = BeanUtil.beanFrom(SysFarmerPickupLocation.class, location);
                    locationBean.setFarmer(entity.getPkey());
                    locationBean.setAscription(ascription);
                    pickupLocations.add(locationBean);
                }
                farmerPickupLocationDao.removeAndPutAll(oldPickupLocations, pickupLocations);
                
            }
            // 清分的运营商，如果修改了名称，同步更新
            if (qfAscription.equals(old.getAscription()) && updName)
            {
                ZxUserInfo zxUserInfo = zxUserInfoDao.getByFarmer(old.getPkey());
                if (zxUserInfo != null)
                {
                    zxUserInfo.setName(old.getName());
                    zxUserInfoDao.put(zxUserInfo);
                }
            }
            return BeanUtil.beanFrom(SysFarmerInfo.class, old);
        }
    }
    
    private void assembleFramerSys(SysFarmerOnList entity, SysFarmer old)
    {
        SysFarmerConfig oldConfig = old.getConfig();
        if (entity.getConfig() != null)
        {
            SysFarmerConfig newConfig = entity.getConfig();
            if (StringUtils.isNotBlank(entity.getCode())) old.setCode(entity.getCode());
            
            if(qfAscription.intValue() == oldConfig.getAscription().intValue())
            {
                BigDecimal tcr = Constant.ZxConfig.TJ_COMMISSION_RATE.multiply(new BigDecimal(100));
                if(Boolean.TRUE.equals(newConfig.getIsEnterprise()))
                {
                    if(CommissionType.BLOC.equals(newConfig.getCommissionType()))
                    {
                        BigDecimal cr = newConfig.getCommissionRate();
                        if(cr == null || cr.compareTo(tcr) < 0)
                            throw TofocusException.of(LejiaErrCode.COMMISSIONRATE_ERROR);
                        Boolean checkCommissionRate = mktVendorDao.checkCommissionRate(cr, oldConfig.getPkey(), oldConfig.getAscription());
                        if(checkCommissionRate)
                            throw TofocusException.of(LejiaErrCode.COMMISSIONRATE_ERROR);
                    }
                    if(CommissionType.MARKET.equals(newConfig.getCommissionType()))
                    {
                        BigDecimal cr = newConfig.getCommissionRate();
                        if(cr == null)
                            cr = BigDecimal.ZERO;
                        Boolean checkCommissionRate = mktVendorDao.checkCommissionRate(tcr.add(cr), oldConfig.getPkey(), oldConfig.getAscription());
                        if(checkCommissionRate)
                            throw TofocusException.of(LejiaErrCode.COMMISSIONRATE_ERROR);
                    }
                    if(CommissionType.MERCHANT.equals(newConfig.getCommissionType()))
                    {
                        BigDecimal cr = newConfig.getCommissionRate();
                        if(cr == null)
                            cr = BigDecimal.ZERO;
                        Boolean checkCommissionRate = mktVendorDao.checkCommissionRate(cr, oldConfig.getPkey(), oldConfig.getAscription());
                        if(checkCommissionRate)
                            throw TofocusException.of(LejiaErrCode.COMMISSIONRATE_ERROR);
                    }
                }
                else
                {
                    if(CommissionType.BLOC.equals(newConfig.getCommissionType()))
                    {
                        Boolean checkCommissionRate = mktVendorDao.checkCommissionRate(tcr, oldConfig.getPkey(), oldConfig.getAscription());
                        if(checkCommissionRate)
                            throw TofocusException.of(LejiaErrCode.COMMISSIONRATE_ERROR);
                    }
                }
            }
            
            
            // 商户结算方式
            SettlementMethodType smType = newConfig.getSettlementMethod();
            if(smType != null && oldConfig.getSettlementMethod() != null 
                && !oldConfig.getSettlementMethod().equals(smType)
                && FarmerType.MARKET_SHOPPING_MALL.equals(old.getType()))
            {
                List<MktSupply> supplys = mktSupplyDao.select().eq("farmer", entity.getPkey()).exec();
                for(MktSupply s : supplys)
                {
                    s.setSettlementMethod(smType);
                }
                mktSupplyDao.updateAll(supplys);
                
                List<MktVendor> vendors = mktVendorDao.select().eq("farmer", entity.getPkey()).exec();
                for(MktVendor v : vendors)
                {
                    v.setSettlementMethod(smType);
                }
                mktVendorDao.updateAll(vendors);
            }
            if(smType != null)
                oldConfig.setSettlementMethod(smType);
            if (newConfig.getIsEnterprise() != null)
                oldConfig.setIsEnterprise(newConfig.getIsEnterprise());
            if (newConfig.getCommissionType() != null)
                oldConfig.setCommissionType(newConfig.getCommissionType());
            if (newConfig.getCommissionRate() != null)
                oldConfig.setCommissionRate(newConfig.getCommissionRate());
            if (newConfig.getMemberCommissionRate() != null)
                oldConfig.setMemberCommissionRate(newConfig.getMemberCommissionRate());
            oldConfig.setCustomerServiceId(newConfig.getCustomerServiceId());
            oldConfig.setCustomerServiceLink(newConfig.getCustomerServiceLink());
        }
    }
    
    private void assembleFramer(SysFarmerOnList entity, SysFarmer old)
    {
        if (StringUtils.isNotBlank(entity.getCode())) old.setCode(entity.getCode());
        if (StringUtils.isNotBlank(entity.getContent())) old.setContent(entity.getContent());
        if (StringUtils.isNotBlank(entity.getTel())) old.setTel(entity.getTel());
        old.setLogo(entity.getLogo());
        old.setPhoto1(entity.getPhoto1());
        old.setPhoto2(entity.getPhoto2());
        old.setPhoto3(entity.getPhoto3());
        old.setTel(entity.getTel());
        //		old.setEnabled(entity.getEnabled());
        SysFarmerConfig oldConfig = old.getConfig();
        if (entity.getConfig() != null)
        {
            SysFarmerConfig newConfig = entity.getConfig();
            BeanUtils.copyProperties(entity.getConfig(), oldConfig, 
                "deliveryRange", "freeDelivery", "yStatus", "longitude", "latitude", "isFree", "deliveryDate", 
                "psTime", "ascription", "settlementMethod", "storeId", "shopId", "startingPrice", "fee", "distributionConfig",
                "automaticCourier", "automaticPurchase", "isPackingCharge", "isEnterprise", 
                "commissionType", "commissionRate", "memberCommissionRate");
            if (newConfig.getLongitude() != null) oldConfig.setLongitude(newConfig.getLongitude());
            if (newConfig.getLatitude() != null) oldConfig.setLatitude(newConfig.getLatitude());
            if (StringUtils.isNotBlank(newConfig.getYytb())) oldConfig.setYytb(newConfig.getYytb());
            if (StringUtils.isNotBlank(newConfig.getYyte())) oldConfig.setYyte(newConfig.getYyte());
            if (newConfig.getDeliveryRange() != null) oldConfig.setDeliveryRange(newConfig.getDeliveryRange());
            if (newConfig.getFreeDelivery() != null) oldConfig.setFreeDelivery(newConfig.getFreeDelivery());
            if (newConfig.getYStatus() != null) oldConfig.setYStatus(newConfig.getYStatus());
            if (newConfig.getIsFree() != null) oldConfig.setIsFree(newConfig.getIsFree());
            if (newConfig.getDeliveryDate() != null) oldConfig.setDeliveryDate(newConfig.getDeliveryDate());
            if (newConfig.getGoodsType() == null) oldConfig.setGoodsType(ConfigGoodsType.VENDOR_RECOMMEND);
            if (newConfig.getGoodsType() != null) oldConfig.setGoodsType(newConfig.getGoodsType());
//            if (newConfig.getIsEnterprise() != null)
//                oldConfig.setIsEnterprise(newConfig.getIsEnterprise());
//            if (newConfig.getCommissionType() != null)
//                oldConfig.setCommissionType(newConfig.getCommissionType());
//            if (newConfig.getCommissionRate() != null)
//                oldConfig.setCommissionRate(newConfig.getCommissionRate());
//            if (newConfig.getMemberCommissionRate() != null)
//                oldConfig.setMemberCommissionRate(newConfig.getMemberCommissionRate());
            
            oldConfig.setMemberPhoto(newConfig.getMemberPhoto());
            oldConfig.setAddr(newConfig.getAddr());
            oldConfig.setAbnormalNum(newConfig.getAbnormalNum());
            
            oldConfig.setCustomerServiceId(newConfig.getCustomerServiceId());
            oldConfig.setCustomerServiceLink(newConfig.getCustomerServiceLink());
            
//            oldConfig.setReachOne(newConfig.getReachOne());
//            oldConfig.setReachTwo(newConfig.getReachTwo());
//            oldConfig.setReductionDeliveryOne(newConfig.getReductionDeliveryOne());
//            oldConfig.setReductionDeliveryTwo(newConfig.getReductionDeliveryTwo());
//            oldConfig.setIsReductionOne(newConfig.getIsReductionOne());
//            oldConfig.setIsReductionTwo(newConfig.getIsReductionTwo());
        }
        oldConfig.setPkey(entity.getPkey());
        old.setConfig(oldConfig);
        List<SysFarmerMtype> types = entity.getTypes();
        if (types != null && !types.isEmpty())
        {
            List<SysFarmerMtype> updType = new ArrayList<>();
            for (SysFarmerMtype type : types)
            {
                if (type.getDelivery() != null && type.getPickup() != null)
                {
                    if (!type.getDelivery() && !type.getPickup())
                        throw TofocusException.of(LejiaErrCode.DELIVERY_PICKUP_ERROR);
                    updType.add(type);
                }
            }
            old.setTypes(updType);
        }
    }
    
    @Transactional
    public Boolean delMarket(String pkey)
    {
        SysFarmer sysFarmer = sysFarmerDao.get(pkey);
        if (sysFarmer.getEnabled()) throw TofocusException.of(WsaleErrCode.NOT_DELETED);
        
        String mobile = sysFarmer.getMobile();
        String PrefixM = "0000";
        List<SysFarmer> delExec = sysFarmerDao.select().like("mobile", mobile).eq("idDel", true).exec();
        if (delExec != null && delExec.size() > 0)
        {
            List<Integer> prefixMList = new ArrayList<>();
            for (SysFarmer c : delExec)
                prefixMList.add(Integer.valueOf(c.getMobile().substring(0, 4)));
            Collections.sort(prefixMList);
            PrefixM = String.format("%04d", prefixMList.get(prefixMList.size() - 1) + 1);
        }
        sysFarmer.setMobile(PrefixM + sysFarmer.getMobile());
        sysFarmer.setIdDel(true);
        SysFarmer farmer = sysFarmerDao.update(sysFarmer);
        SysUser sysUser = sysUserDao.selectOne().eq("mobile", mobile).eq("farmer", pkey).exec();
        if (sysUser != null) userManager.delUser(sysUser.getPkey());
        if (farmer == null) return false;
        List<MktCard> exec = cardDao.select().eq("farmer", pkey).exec();
        if (!exec.isEmpty())
        {
            for (MktCard c : exec)
            {
                c.setEnabled(false);
            }
            cardDao.updateAll(exec);
        }
        // 清分的运营商，要逻辑删除账户数据
        if (qfAscription.equals(sysFarmer.getAscription()))
        {
            ZxUserInfo zxUserInfo = zxUserInfoDao.getByFarmer(pkey);
            if (zxUserInfo != null)
            {
                zxUserInfo.setDelFlag(Boolean.TRUE);
                zxUserInfoDao.put(zxUserInfo);
            }
        }
        return true;
    }
    
    public Boolean enableMarket(String pkey, Boolean enable)
    {
        SysFarmer sysFarmer = sysFarmerDao.get(pkey);
        sysFarmer.setEnabled(enable);
        SysFarmer farmer = sysFarmerDao.update(sysFarmer);
        if (farmer == null) return false;
        return true;
    }
    
    public String marketMobile(String pkey)
    {
        if (StringUtils.isBlank(pkey)) return null;
        SysFarmer farmer = sysFarmerDao.get(pkey);
        if (farmer == null) return null;
        return farmer.getMobile();
    }
    
    public void updCompanyNameAndMobile(String pkey, String name, String mobile)
    {
        SysFarmer farmer = sysFarmerDao.get(pkey);
        if (StringUtils.isNotBlank(name)) farmer.setManager(name);
        if (StringUtils.isNotBlank(mobile)) farmer.setMobile(mobile);
        sysFarmerDao.update(farmer);
    }
    
    @Transactional
    public Boolean updCourierDispatch(List<MktMarketCourierOnList> infos)
    {
        // 骑手是否可以重复添加
        String market = CurrentSession.marketPkey();
        List<MktMarketCourier> exec = marketCourierDao.select().eq("market", market).exec();
        if (!exec.isEmpty()) marketCourierDao.removeAll(exec);
        if (infos == null || infos.isEmpty())
        {
            return true;
        }
        List<MktMarketCourier> couriers = new ArrayList<>();
        int i = 1;
        Map<Integer, Integer> map = new HashMap<>();
        for (MktMarketCourierOnList c : infos)
        {
            if (map.containsKey(c.getPkey())) throw TofocusException.of(LejiaErrCode.COURIER_NOT_REPEAT);
            MktMarketCourier e = new MktMarketCourier();
            e.setMarket(market);
            e.setSort(i);
            e.setFlag(false);
            e.setCourierKey(c.getPkey());
            e.setNum(0);
            e.setNowDate(new Date());
            e.setId(i);
            e.setAscription(CurrentSession.ascriptionPkey());
            if (i == 1) e.setFlag(true);
            couriers.add(e);
            i++;
            map.put(c.getPkey(), 1);
        }
        marketCourierDao.addAll(couriers);
        return true;
    }
    
    public List<MktMarketCourierOnList> listCourierDispatch()
    {
        String market = CurrentSession.marketPkey();
        List<MktMarketCourier> exec = marketCourierDao.select().eq("market", market).sort("id", false).exec();
        List<MktCourier> cList = courierDao.select().eq("farmer", market).eq("enabled", true).eq("idDel", false).exec();
        Map<Integer, MktMarketCourier> map = new HashMap<>();
        exec.forEach(e -> {
            map.put(e.getCourierKey(), e);
        });
        List<MktMarketCourierOnList> res = new ArrayList<>();
        cList.forEach(e -> {
            MktMarketCourierOnList c = new MktMarketCourierOnList();
            c.setPkey(e.getPkey());
            c.setName(e.getName());
            c.setMobile(e.getMobile());
            c.setValue(e.getName() + " " + e.getMobile());
            c.setSelected(false);
            if (map.containsKey(e.getPkey())) c.setSelected(true);
            res.add(c);
        });
        
        return res;
    }
    
    public Boolean updDispatch(Boolean automaticCourier, Boolean automaticPurchase)
    {
        String market = CurrentSession.marketPkey();
        if (StringUtils.isBlank(market)) return false;
        SysFarmerConfig config = sysFarmerConfigDao.get(market);
        config.setAutomaticCourier(automaticCourier);
        config.setAutomaticPurchase(automaticPurchase);
        sysFarmerConfigDao.update(config);
        return true;
    }
    
    public void runDispatchCourier()
    {
        Date date = new Date();
        List<MktMarketCourier> exec = marketCourierDao.select().lt("nowDate", date).exec();
        log.info("骑手自动派单重置数量 :{}", exec.size());
        for (MktMarketCourier c : exec)
        {
            if (c.getId() == 1)
            {
                c.setFlag(true);
            }
            else
            {
                c.setFlag(false);
            }
            c.setNowDate(new Date());
        }
        marketCourierDao.updateAll(exec);
    }
    
    public List<DropStringDown> listDropName()
    {
        String companyPkey = CurrentSession.companyPkey();
        Integer ascription = CurrentSession.ascriptionPkey();
        if ((Constant.Operation + ascription).equals(companyPkey)) companyPkey = null;
        List<SysCompany> exec = sysCompanyDao.select().eq("pkey", companyPkey).eq("ascription", ascription).eq("idDel", false).exec();
        if (exec.isEmpty()) return new ArrayList<>();
        List<String> keys = CollectionUtil.keyList(exec);
        return sysFarmerDao.select()
            .notEq("pkey", (Constant.Operation + ascription))
            .eq("ascription", ascription)
            .eq("idDel", false)
            .in("org", keys)
            .execDto(DropStringDown.class);
    }
    
    public List<DropStringDown> listDropSupplyName()
    {
        String companyPkey = CurrentSession.companyPkey();
        Integer ascription = CurrentSession.ascriptionPkey();
        if ((Constant.Operation + ascription).equals(companyPkey)) companyPkey = null;
        List<SysCompany> exec = sysCompanyDao.select().eq("pkey", companyPkey).eq("ascription", ascription).eq("idDel", false).exec();
        if (exec.isEmpty()) return new ArrayList<>();
        List<String> keys = CollectionUtil.keyList(exec);
        return sysFarmerDao.select()
            .notEq("pkey", (Constant.Operation + ascription))
            .eq("ascription", ascription)
            .eq("type", FarmerType.MARKET_SHOPPING_MALL)
            .eq("idDel", false)
            .in("org", keys)
            .execDto(DropStringDown.class);
    }
    
    public boolean updPrintCode(String code)
    {
        SysFarmerExtend fe = sysFarmerExtendDao.get(CurrentSession.marketPkey());
        if (fe == null)
        {
            fe = new SysFarmerExtend();
            fe.setPkey(CurrentSession.marketPkey());
        }
        fe.setPrintCode(code);
        sysFarmerExtendDao.put(fe);
        return true;
    }
    
    public String getPrintCode()
    {
        String res = "";
        SysFarmerExtend fe = sysFarmerExtendDao.get(CurrentSession.marketPkey());
        if (fe != null)
            res = fe.getPrintCode();
        return res;
    }
    
    public MarketTechConfig getTechConfig()
    {
        String marketPkey = CurrentSession.marketPkey();
        
        MarketTechConfig config = new MarketTechConfig();
        SysFarmerExtend fe = sysFarmerExtendDao.get(marketPkey);
        if (fe != null)
        {
            config.setPrintCode(fe.getPrintCode());
            config.setContent(fe.getContent());
            config.setPhoto1(fe.getPhoto1());
            config.setPhoto1Text(fe.getPhoto1Text());
            config.setPhoto2(fe.getPhoto2());
            config.setPhoto2Text(fe.getPhoto2Text());
        }
        
        SysFarmerConfig fc = sysFarmerConfigDao.get(marketPkey);
        if (fc != null)
        {
            config.setWanliAppId(fc.getWanliAppId());
            config.setWanliSecret(fc.getWanliSecret());
            config.setStoreId(fc.getStoreId());
            config.setShopId(fc.getShopId());
        }
        
        return config;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public boolean updTechConfig(MarketTechConfig config)
    {
        String marketPkey = CurrentSession.marketPkey();
        
        SysFarmerExtend fe = sysFarmerExtendDao.get(marketPkey);
        if (fe == null)
        {
            fe = new SysFarmerExtend();
            fe.setPkey(CurrentSession.marketPkey());
        }
        fe.setPrintCode(config.getPrintCode());
        fe.setContent(config.getContent());
        fe.setPhoto1(config.getPhoto1());
        fe.setPhoto1Text(config.getPhoto1Text());
        fe.setPhoto2(config.getPhoto2());
        fe.setPhoto2Text(config.getPhoto2Text());
        sysFarmerExtendDao.put(fe);
        
        SysFarmerConfig fc = sysFarmerConfigDao.get(marketPkey);
        fc.setWanliAppId(config.getWanliAppId());
        fc.setWanliSecret(config.getWanliSecret());
        fc.setStoreId(config.getStoreId());
        fc.setShopId(config.getShopId());
        sysFarmerConfigDao.put(fc);
        return true;
    }
}

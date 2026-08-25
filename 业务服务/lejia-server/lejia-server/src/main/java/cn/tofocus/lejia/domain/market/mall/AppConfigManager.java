package cn.tofocus.lejia.domain.market.mall;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.lejia.bean.dto.sys.AppConfig;
import cn.tofocus.lejia.bean.entity.applet.MktProblemTypeEntity;
import cn.tofocus.lejia.bean.entity.market.MktAppConfig;
import cn.tofocus.lejia.bean.entity.market.MktDrawConf;
import cn.tofocus.lejia.bean.entity.market.MktPostageConfig;
import cn.tofocus.lejia.bean.entity.sys.SysCompany;
import cn.tofocus.lejia.bean.entity.sys.SysConfigEntity;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerConfig;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerMtype;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerTime;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.applet.MktProblemTypeDao;
import cn.tofocus.lejia.dao.market.MktAppConfigDao;
import cn.tofocus.lejia.dao.market.MktDrawConfDao;
import cn.tofocus.lejia.dao.market.MktPostageConfigDao;
import cn.tofocus.lejia.dao.sys.SysCompanyDao;
import cn.tofocus.lejia.dao.sys.SysConfigDao;
import cn.tofocus.lejia.dao.sys.SysFarmerConfigDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.dao.sys.SysFarmerMtypeDao;
import cn.tofocus.lejia.dao.sys.SysFarmerTimeDao;
import cn.tofocus.lejia.domain.IterateManager;
import cn.tofocus.lejia.domain.MarketManager;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AppConfigManager
{
    @Autowired
    private MktAppConfigDao appConfigDao;
    
    @Autowired
    private SysConfigDao sysConfigDao;
    
    @Autowired
    private MktDrawConfDao drawConfDao;
    
    @Autowired
    private SysFarmerDao sysFarmerDao;
    
    @Autowired
    private SysFarmerConfigDao sysFarmerConfigDao;
    
    @Autowired
    private SysCompanyDao companyDao;
    
    @Autowired
    private SysFarmerMtypeDao farmerMtypeDao;
    
    @Autowired
    private MktPostageConfigDao postageConfigDao;
    
    @Autowired
    private IterateManager iterateManager;
    
    @Autowired
    private MktProblemTypeDao problemTypeDao;
    
    @Autowired
    private SysFarmerTimeDao sysFarmerTimeDao;
    
    @Autowired
    private MarketManager marketManager;
    
    public MktAppConfig getAppConfig()
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        if (ascription == null)
            ascription = MobileSession.appid();
        System.out.println("/getAppConfig: " + ascription);
        String mobile = "13800000000";
        if (ascription == null)
        {
            System.out.println("归属主键不存在");
            return new MktAppConfig();
        }
        MktAppConfig appConfig = appConfigDao.selectOne().eq("ascription", ascription).exec();
        if (appConfig == null)
        {
            appConfig = new MktAppConfig();
            appConfig.setPointsRate(1);
            appConfig.setMoneyRate(1);
            appConfig.setPointsQd(1);
            appConfig.setPointsQdDz(1);
            appConfig.setPointsQdSx(3);
            appConfig.setPointsCjUser(10);
            appConfig.setPointsCjXz(3);
            appConfig.setMemberPrice(new BigDecimal(100));
            appConfig.setMemberPriceN(new BigDecimal(100));
            appConfig.setMemberPoints(0);
            appConfig.setMemberGetPoints(1);
            appConfig.setTel(mobile);
            appConfig.setAddr(mobile);
            appConfig.setUpdateTime(new Date());
            appConfig.setRowVension(1);
            appConfig.setAscription(ascription);
            appConfigDao.add(appConfig);
        }
        return appConfig;
    }
    
    public AppConfig getConfig()
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        System.out.println("getConfig: " + ascription);
        String mobile = "13800000000";
        if (ascription == null)
        {
            System.out.println("归属主键不存在");
            return new AppConfig();
        }
        MktAppConfig appConfig = appConfigDao.selectOne().eq("ascription", ascription).exec();
        if (appConfig == null)
        {
            appConfig = new MktAppConfig();
            appConfig.setPointsRate(1);
            appConfig.setMoneyRate(1);
            appConfig.setPointsQd(1);
            appConfig.setPointsQdDz(1);
            appConfig.setPointsQdSx(3);
            appConfig.setPointsCjUser(10);
            appConfig.setPointsCjXz(3);
            appConfig.setMemberPrice(new BigDecimal(100));
            appConfig.setMemberPriceN(new BigDecimal(100));
            appConfig.setMemberPoints(0);
            appConfig.setMemberGetPoints(1);
            appConfig.setTel(mobile);
            appConfig.setAddr(mobile);
            appConfig.setUpdateTime(new Date());
            appConfig.setRowVension(1);
            appConfig.setAscription(ascription);
            appConfigDao.add(appConfig);
        }
        AppConfig result = BeanUtil.beanFrom(AppConfig.class, appConfig);
        
        String pkey = Constant.Operation + ascription;
        SysFarmerConfig config = sysFarmerConfigDao.get(pkey);
        if (config != null)
        {
            //客服链接
            result.setCustomerServiceId(config.getCustomerServiceId());
            result.setCustomerServiceLink(config.getCustomerServiceLink());
        }
        
        //营业时间
        List<SysFarmerTime> listTime = sysFarmerTimeDao.listTime(pkey, ascription);
        result.setTimes(listTime);
        return result;
    }
    
    public MktAppConfig getAppConfig(Integer ascription)
    {
        String mobile = "13800000000";
        MktAppConfig appConfig = appConfigDao.selectOne().eq("ascription", ascription).exec();
        if (appConfig == null)
        {
            appConfig = new MktAppConfig();
            appConfig.setPointsRate(1);
            appConfig.setMoneyRate(1);
            appConfig.setPointsQd(1);
            appConfig.setPointsQdDz(1);
            appConfig.setPointsQdSx(3);
            appConfig.setPointsCjUser(10);
            appConfig.setPointsCjXz(3);
            appConfig.setMemberPrice(new BigDecimal(100));
            appConfig.setMemberPriceN(new BigDecimal(100));
            appConfig.setMemberPoints(0);
            appConfig.setMemberGetPoints(1);
            appConfig.setTel(mobile);
            appConfig.setAddr(mobile);
            appConfig.setUpdateTime(new Date());
            appConfig.setRowVension(1);
            appConfig.setAscription(ascription);
            appConfigDao.add(appConfig);
        }
        return appConfig;
    }
    
    public void updAppConfig(AppConfig config)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        if (ascription == null)
            ascription = MobileSession.appid();
        
        MktAppConfig appConfig = BeanUtil.beanFrom(MktAppConfig.class, config);
        MktDrawConf drawConf = drawConfDao.selectOne().eq("ascription", ascription).exec();
        if (drawConf != null)
        {
            drawConf.setPoint(config.getPointsCjUser());
            drawConfDao.update(drawConf);
        }
        appConfig.setPkey(ascription);
        appConfig.setRowVension(1);
        appConfigDao.update(appConfig);
        
        String pkey = Constant.Operation + ascription;
        SysFarmerConfig farmerConfig = sysFarmerConfigDao.get(pkey);
        if (farmerConfig != null)
        {
            //客服链接
            farmerConfig.setCustomerServiceId(config.getCustomerServiceId());
            farmerConfig.setCustomerServiceLink(config.getCustomerServiceLink());
            sysFarmerConfigDao.update(farmerConfig);
        }
        
        //营业时间
        marketManager.bulidSysFarmerTime(pkey, ascription, config.getTimes());
    }
    
    @Transactional
    public void initConfig(Long pkey, String name, Integer ascription, String mobile)
    {
        StringBuilder sb = new StringBuilder(mobile);
        sb.replace(1, 2, "2");
        String comMob = sb.toString();
        sb.replace(1, 2, "3");
        String farMob = sb.toString();
        getAppConfig(ascription);
        addConfig(ascription);
        SysCompany companyEntity = new SysCompany();
        companyEntity.setPkey(Constant.Operation + ascription);
        companyEntity.setName("默认公司");
        companyEntity.setManagerUser(pkey);
        companyEntity.setManager(name);
        companyEntity.setMobile(comMob);
        companyEntity.setEnabled(true);
        companyEntity.setIdDel(false);
        companyEntity.setRowVension(1);
        companyEntity.setAscription(ascription);
        companyDao.add(companyEntity);
        SysFarmer farmer = new SysFarmer();
        farmer.setPkey(Constant.Operation + ascription);
        farmer.setName("积分商城");
        farmer.setManagerUser(pkey.intValue());
        farmer.setManager(name);
        farmer.setMobile(farMob);
        farmer.setEnabled(true);
        farmer.setIdDel(false);
        farmer.setDept(Constant.Operation + ascription);
        farmer.setOrg(Constant.Operation + ascription);
        farmer.setAscription(ascription);
        farmer.setRowVension(1);
        sysFarmerDao.add(farmer);
        
        SysFarmerConfig config = new SysFarmerConfig();
        config.setPkey(Constant.Operation + ascription);
        config.setLatitude(new BigDecimal(0));
        config.setLongitude(new BigDecimal(0));
        config.setYStatus(false);
        config.setYytb("");
        config.setYyte("");
        config.setYjTime("");
        config.setYjPos(5);
        config.setDeliveryRange(new BigDecimal("5"));
        config.setIsFree(false);
        config.setFreeDelivery(BigDecimal.ZERO);
        config.setPsTime(new ArrayList<>());
        config.setAutomaticCourier(false);
        config.setAutomaticPurchase(false);
        config.setAscription(ascription);
        sysFarmerConfigDao.add(config);
        List<MktPostageConfig> entitys = new ArrayList<>();
        for (int i = 0; i < 4; i++)
        {
            MktPostageConfig bean = new MktPostageConfig();
            bean.setPostage(new BigDecimal(0));
            bean.setWeight(new BigDecimal(0));
            bean.setCompany(Constant.Operation + ascription);
            bean.setFarmer(Constant.Operation + ascription);
            bean.setRowVension(1);
            bean.setAscription(ascription);
            entitys.add(bean);
        }
        postageConfigDao.addAll(entitys);
        addFarmerMtype(ascription);
        iterateManager.init3_1_0Gtype(ascription);
        addProblemType(ascription);
    }
    
    public void addConfig(Integer ascription)
    {
        List<SysConfigEntity> adds = new ArrayList<>();
        SysConfigEntity c1 = new SysConfigEntity();
        c1.setPkey(Constant.SysConfig.GOODS_SUPPLY_DEPLOY + "_" + ascription);
        c1.setName("商品供应库配置");
        c1.setValue("1");
        c1.setAscription(ascription);
        adds.add(c1);
        
        SysConfigEntity c2 = new SysConfigEntity();
        c2.setPkey(Constant.SysConfig.ADVERTISE_MANAGER_DEPLOY + "_" + ascription);
        c2.setName("广告配置");
        c2.setValue("1");
        c2.setAscription(ascription);
        adds.add(c2);
        
        SysConfigEntity c3 = new SysConfigEntity();
        c3.setPkey(Constant.SysConfig.VENDOR_MANAGER_DEPLOY + "_" + ascription);
        c3.setName("合作商户配置");
        c3.setValue("1");
        c3.setAscription(ascription);
        adds.add(c3);
        
        SysConfigEntity c4 = new SysConfigEntity();
        c4.setPkey(Constant.SysConfig.GOODS_PURCHASE_DEPLOY + "_" + ascription);
        c4.setName("是否自动采购配置");
        c4.setValue("1");
        c4.setAscription(ascription);
        adds.add(c4);
        
        sysConfigDao.addAll(adds);
    }
    
    public void addFarmerMtype(Integer ascription)
    {
        List<MType> types = new ArrayList<>();
        types.add(MType.MARKET_GOODS);
        types.add(MType.CUT_GOODS);
        types.add(MType.COLLAGE_GOODS);
        types.add(MType.PRESALE_GOODS);
        List<SysFarmerMtype> mtypeAdd = new ArrayList<>();
        for (MType m : types)
        {
            SysFarmerMtype mt = new SysFarmerMtype();
            mt.setFarmer(Constant.Operation + ascription);
            mt.setMType(m);
            mt.setDelivery(true);
            mt.setPickup(true);
            mt.setAscription(ascription);
            mtypeAdd.add(mt);
        }
        farmerMtypeDao.addAll(mtypeAdd);
    }
    
    public void addProblemType(Integer ascription)
    {
        List<MktProblemTypeEntity> ptList = new ArrayList<>();
        MktProblemTypeEntity pt1 = new MktProblemTypeEntity();
        pt1.setName("订单问题");
        pt1.setSort(0);
        pt1.setAscription(ascription);
        MktProblemTypeEntity pt2 = new MktProblemTypeEntity();
        pt2.setName("退款问题");
        pt2.setSort(1);
        pt2.setAscription(ascription);
        MktProblemTypeEntity pt3 = new MktProblemTypeEntity();
        pt3.setName("联系我们");
        pt3.setSort(2);
        pt3.setAscription(ascription);
        ptList.add(pt1);
        ptList.add(pt2);
        ptList.add(pt3);
        problemTypeDao.addAll(ptList);
    }
}

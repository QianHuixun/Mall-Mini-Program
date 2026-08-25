package cn.tofocus.lejia.domain.app;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import cn.tofocus.lejia.bean.dto.config.JdGoodsZoneConfig;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectBuilder;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.bean.dto.app.AppAscriptionConfigDTO;
import cn.tofocus.lejia.bean.dto.app.goods.AppGtypeDTO;
import cn.tofocus.lejia.bean.dto.app.market.*;
import cn.tofocus.lejia.bean.dto.config.AscriptionConfig;
import cn.tofocus.lejia.bean.dto.config.AscriptionGoodsZoneConfig;
import cn.tofocus.lejia.bean.dto.config.FarmerGoodsZoneConfig;
import cn.tofocus.lejia.bean.dto.market.MktDeliveryTimeConfig;
import cn.tofocus.lejia.bean.entity.sys.AccountEntity;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerConfig;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerTime;
import cn.tofocus.lejia.bean.enums.AccountType;
import cn.tofocus.lejia.bean.enums.ConfigGoodsType;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.cache.MemberDistanceCache;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.market.MktDeliveryTimeConfigDao;
import cn.tofocus.lejia.dao.market.MktGtypeDao;
import cn.tofocus.lejia.dao.sys.*;
import cn.tofocus.lejia.domain.market.mall.AppOrderManager;
import cn.tofocus.lejia.domain.v2.AppOrderV2Expand;
import cn.tofocus.lejia.util.LocationUtils;
import cn.tofocus.lejia.utils.DateUtil;

@Component
public class AppIndexManager
{
    @Value("${lejia.goods.recommend.max:20}")
    private Integer maxRecommend;
    
    @Autowired
    private SysFarmerDao sysFarmerDao;
    
    @Autowired
    private SysFarmerConfigDao sysFarmerConfigDao;
    
    @Autowired
    private SysFarmerTimeDao sysFarmerTimeDao;
    
    @Autowired
    private AccountDao accountDao;
    
    @Autowired
    private AppGoodsManager appGoodsManager;
    
    @Autowired
    private MemberDistanceCache memberDistanceCache;
    
    @Autowired
    private MktDeliveryTimeConfigDao deliveryTimeConfigDao;
    
    @Autowired
    private SysDynamicAttributeDao dynamicAttributeDao;
    
    @Autowired
    private AppOrderV2Expand appOrderV2Expand;
    
    @Autowired
    private AppOrderManager appOrderManager;
    
    @Autowired
    private MktGtypeDao gtypeDao;
    
    public AppAscriptionConfigDTO getAscriptionConfig()
    {
        Integer ascription = MobileSession.appid();
        AscriptionConfig ascriptionConfig =
            dynamicAttributeDao.getFarmerAttribute(AscriptionConfig.class, ascription, Constant.Operation + ascription);
        if (ascriptionConfig == null)
            ascriptionConfig = new AscriptionConfig();
        AppAscriptionConfigDTO res = new AppAscriptionConfigDTO();
        res.setEnableComment(ascriptionConfig.getEnableComment());
        return res;
    }
    
    public PageResult<SysFarmerAppOnList> queryMarket(Integer page, Integer pagesize, BigDecimal longitude,
        BigDecimal latitude, String area, String name, String version, AccountType accountType)
    {
        Integer ascription = MobileSession.appid();
        AccountEntity account = null;
        List<String> allowedFarmers = null;
        if (accountType != null)
        {
            account = accountDao.get(ascription, accountType);
            if (account != null && StringUtil.isNotBlank(account.getShieldAllowedPkey()))
            {
                String[] farmerArr = account.getShieldAllowedPkey().split(",");
                allowedFarmers = Lists.newArrayList(farmerArr);
            }
        }
        if (longitude != null && latitude != null)
        {
            // 获取所有市场
            List<SysFarmer> list = null;
            // 限制版本号一致，则限制显示市场
            if (account != null && StringUtil.isNotBlank(version) && version.equals(account.getShieldVersion()))
            {
                list = sysFarmerDao.queryAppMarketAll(area, name, ascription, allowedFarmers, null);
            }
            // 否则过滤限制显示市场
            else
            {
                list = sysFarmerDao.queryAppMarketAll(area, name, ascription, null, allowedFarmers);
            }
            // 转为DTO列表
            List<SysFarmerAppOnList> appList0 = BeanUtil.beanListFrom(SysFarmerAppOnList.class, list);
            
            // 设置dto的配置、距离数据
            for (SysFarmerAppOnList sysFarmer : appList0)
            {
                perfectConfig(sysFarmer, longitude, latitude);
            }
            // 列表排序
            appList0.sort(new SysFarmerAppOnListComparator());
            // 设置分页内容
            List<SysFarmerAppOnList> content = new ArrayList<>();
            for (int i = page * pagesize; i < (Math.min(appList0.size(), (page + 1) * pagesize)); i++)
            {
                appList0.get(i).setPstime("不在配送范围");
                content.add(appList0.get(i));
            }
            
            content.forEach(c -> {
                c.setInRange(false);
                if (Objects.nonNull(c.getConfig()))
                {
                    SysFarmerConfig config = c.getConfig();
                    c.setGoodsType(c.getConfig().getGoodsType());
                    if(c.getConfig().getGoodsType() == null)
                        c.setGoodsType(ConfigGoodsType.VENDOR_RECOMMEND);
                    boolean distanceFlag =
                        config.getLatitude() != null && config.getLongitude() != null && c.getDistance() != null;
                    if (distanceFlag)
                    {
                        // 配送距离（公里）
                        BigDecimal deliveryRange = config.getDeliveryRange();
                        BigDecimal deliveryRangeMeter = deliveryRange.multiply(new BigDecimal("1000"));
                        BigDecimal distance = c.getDistance();
                        if (distance.compareTo(deliveryRangeMeter) < 0)
                        {
                            c.setInRange(true);
                            
                            MktAppAddrDTO addr = new MktAppAddrDTO();
                            addr.setDistance(distance);
                            MktDeliveryTimeConfig deliveryTimeConfig =
                                deliveryTimeConfigDao.getDeliveryTimeConfigByDistance(config.getPkey(), addr);
                            c.setPstime(appOrderV2Expand.getImPsTime(config, deliveryTimeConfig));
                            if (StringUtils.isBlank(c.getPstime()))
                            {
                                String pstime = appOrderManager.getPsTime(false, c.getPkey());
                                pstime = pstime.substring(5, pstime.length());
                                c.setPstime("预计" + pstime);
                            }
                            else
                            {
                                String pstime = c.getPstime();
                                pstime = pstime.substring(11, 16);
                                c.setPstime("预计" + pstime + "可送达");
                            }
                        }
                    }
                    
                    String weekTime = assembleWeekTime(config);
                    String dayTime = "";
                    List<SysFarmerTime> listTime = sysFarmerTimeDao.listTime(config.getPkey(), config.getAscription());
                    for (int i = 0; i < listTime.size(); i++)
                    {
                        SysFarmerTime ft = listTime.get(i);
                        String sh = getTimeFormat(ft.getStartHour());
                        String sm = getTimeFormat(ft.getStartMinute());
                        String eh = getTimeFormat(ft.getEndHour());
                        String em = getTimeFormat(ft.getEndMinute());
                        if (i == listTime.size() - 1)
                        {
                            dayTime = dayTime + sh + ":" + sm + " ~ " + eh + ":" + em;
                        }
                        else
                            dayTime = dayTime + sh + ":" + sm + " ~ " + eh + ":" + em + ", ";
                    }
                    c.setWeekTime(weekTime);
                    c.setDayTime(dayTime);
                    
                }
            });
            
            return new PageResult<>(content, new PageParameter(page, pagesize), appList0.size());
        }
        else
        {
            PageResult<SysFarmer> pageResult = null;
            // 限制版本号一致，则限制显示市场
            if (account != null && StringUtil.isNotBlank(version) && version.equals(account.getShieldVersion()))
            {
                pageResult =
                    sysFarmerDao.queryAppMarketByArea(page, pagesize, area, name, ascription, allowedFarmers, null);
            }
            // 否则过滤限制显示市场
            else
            {
                pageResult =
                    sysFarmerDao.queryAppMarketByArea(page, pagesize, area, name, ascription, null, allowedFarmers);
            }
            PageResult<SysFarmerAppOnList> result = BeanUtil.beanPageFrom(SysFarmerAppOnList.class, pageResult);
            // 没传经纬度，都不在配送范围内
            result.getContent().forEach(c -> {
                c.setInRange(false);
                
                c.setGoodsType(c.getConfig().getGoodsType());
                if(c.getConfig().getGoodsType() == null)
                    c.setGoodsType(ConfigGoodsType.VENDOR_RECOMMEND);
                
                String weekTime = assembleWeekTime(c.getConfig());
                String dayTime = "";
                List<SysFarmerTime> listTime = sysFarmerTimeDao.listTime(c.getPkey(), c.getConfig().getAscription());
                for (int i = 0; i < listTime.size(); i++)
                {
                    SysFarmerTime ft = listTime.get(i);
                    String sh = getTimeFormat(ft.getStartHour());
                    String sm = getTimeFormat(ft.getStartMinute());
                    String eh = getTimeFormat(ft.getEndHour());
                    String em = getTimeFormat(ft.getEndMinute());
                    if (i == listTime.size() - 1)
                    {
                        dayTime = dayTime + sh + ":" + sm + " ~ " + eh + ":" + em;
                    }
                    else
                        dayTime = dayTime + sh + ":" + sm + " ~ " + eh + ":" + em + ", ";
                }
                c.setWeekTime(weekTime);
                c.setDayTime(dayTime);
            });
            return result;
        }
    }
    
    public AppCheckFarmerRangInfo checkFarmerInRange(BigDecimal longitude, BigDecimal latitude, String farmer,
        Boolean addrBoolean)
    {
        AppCheckFarmerRangInfo f = new AppCheckFarmerRangInfo();
        f.setInRange(false);
        f.setDistance(BigDecimal.ZERO);
        SysFarmer sysFarmer = sysFarmerDao.get(farmer);
        f.setName(sysFarmer.getName());
        f.setAddr(sysFarmer.getConfig().getAddr());
        // 判断今天是否跳过弹窗
        String openid = MobileSession.openid();
        if (StringUtils.isBlank(openid))
        {
            return f;
        }
        Date date = memberDistanceCache.get(openid);
        Date nowStart = DateUtil.atStartOfDay(new Date());
        if (date != null && !Boolean.FALSE.equals(addrBoolean))
        {
            Date atStartOfDay = DateUtil.atStartOfDay(date);
            if (atStartOfDay.getTime() == nowStart.getTime())
            {
                f.setInRange(true);
                return f;
            }
        }
        SysFarmerConfig config = sysFarmer.getConfig();
        BigDecimal farmerDistance = null;
        BigDecimal latitude2 = config.getLatitude();
        BigDecimal longitude2 = config.getLongitude();
        boolean flag = longitude != null && latitude != null && longitude2 != null && latitude2 != null
            && longitude2.compareTo(new BigDecimal("0")) > 0 && latitude2.compareTo(new BigDecimal("0")) > 0;
        if (flag)
        {
            // 计算距离
            double distance = LocationUtils.getDistance(config.getLatitude().doubleValue(),
                config.getLongitude().doubleValue(),
                latitude.doubleValue(),
                longitude.doubleValue());
            farmerDistance = new BigDecimal(distance).setScale(2, RoundingMode.HALF_UP);
            f.setDistance(farmerDistance);
        }
        
        boolean distanceFlag = farmerDistance != null;
        if (distanceFlag)
        {
            // 配送距离（公里）
            BigDecimal deliveryRange = config.getDeliveryRange();
            BigDecimal deliveryRangeMeter = deliveryRange.multiply(new BigDecimal("1000"));
            if (farmerDistance.compareTo(deliveryRangeMeter) < 0)
            {
                f.setInRange(true);
            }
        }
        if (!Boolean.FALSE.equals(addrBoolean))
            memberDistanceCache.put(openid, nowStart);
        return f;
    }
    
    /**
     * 按照distance升序
     */
    class SysFarmerAppOnListComparator implements Comparator<SysFarmerAppOnList>
    {
        @Override
        public int compare(SysFarmerAppOnList o1, SysFarmerAppOnList o2)
        {
            BigDecimal sort1 = o1.getDistance();
            BigDecimal sort2 = o2.getDistance();
            // 前一项为空，移到后面
            if (Objects.isNull(sort1))
            {
                return 1;
            }
            if (Objects.isNull(sort2))
            {
                return -1;
            }
            
            return sort1.compareTo(sort2);
        }
    }
    
    private void perfectConfig(SysFarmerAppOnList sysFarmer, BigDecimal longitude, BigDecimal latitude)
    {
        // 获取市场扩展表
        SysFarmerConfig sysFarmerConfig = sysFarmerConfigDao.selectOne().eq("pkey", sysFarmer.getPkey()).exec();
        if (sysFarmerConfig != null)
        {
            // 设置扩展信息
            sysFarmer.setConfig(sysFarmerConfig);
            // 不设置经纬度，数据库记录的时0.000000
            BigDecimal latitude2 = sysFarmerConfig.getLatitude();
            BigDecimal longitude2 = sysFarmerConfig.getLongitude();
            boolean flag = longitude != null && latitude != null && longitude2 != null && latitude2 != null
                && longitude2.compareTo(new BigDecimal("0")) > 0 && latitude2.compareTo(new BigDecimal("0")) > 0;
            if (flag)
            {
                // 计算距离
                double distance = LocationUtils.getDistance(sysFarmerConfig.getLatitude().doubleValue(),
                    sysFarmerConfig.getLongitude().doubleValue(),
                    latitude.doubleValue(),
                    longitude.doubleValue());
                sysFarmer.setDistance(new BigDecimal(distance).setScale(2, RoundingMode.HALF_UP));
            }
        }
    }
    
    //    public static void main(String[] args)
    //    {
    //        double a = 39.01623919199611;
    //        double b = 117.66388818650788;
    ////        double a = 27.966927;
    ////        double b = 120.667993;
    //        // 学校大街
    //        double distance = LocationUtils.getDistance(39.017122,
    //            117.669978,
    //            a,
    //            b);
    //        System.out.println("学校大街市场: " + distance);
    //        
    //        distance = LocationUtils.getDistance(39.024122,
    //            117.66496,
    //            a,
    //            b);
    //        System.out.println("沈阳道市场: " + distance);
    //        
    //        distance = LocationUtils.getDistance(39.008051,
    //            117.712639,
    //            a,
    //            b);
    //        System.out.println("濒开里市场: " + distance);
    //        
    ////        double a = 27.966927;
    ////        double b = 120.667993;
    ////        
    ////        // 数安大
    ////        double distance = LocationUtils.getDistance(27.963902,
    ////            120.672255,
    ////            a,
    ////            b);
    ////        System.out.println("distance: " + distance);
    ////        // 瓯海中学
    ////        distance = LocationUtils.getDistance(27.968834,
    ////            120.670409,
    ////            a,
    ////            b);
    ////        System.out.println("distance2: " + distance);
    //    }
    
    public SysFarmerAppOnList getMarket(BigDecimal longitude, BigDecimal latitude, String version,
        AccountType accountType)
    {
        Integer ascription = MobileSession.appid();
        
        AccountEntity account = null;
        List<String> allowedFarmers = null;
        if (accountType != null)
        {
            account = accountDao.get(ascription, accountType);
            if (account != null && StringUtil.isNotBlank(account.getShieldAllowedPkey()))
            {
                String[] farmerArr = account.getShieldAllowedPkey().split(",");
                allowedFarmers = Lists.newArrayList(farmerArr);
            }
        }
        String pkey = MobileSession.farmerPkey();
        SysFarmerAppOnList bean = new SysFarmerAppOnList();
       
//        if (StringUtils.isNotBlank(pkey))
//        {
//            sysFarmer = sysFarmerDao.selectOne().eq("idDel", false).eq("enabled", true).eq("pkey", pkey).exec();
//        }
//        if (sysFarmer != null)
//        {
//            bean = BeanUtil.beanFrom(SysFarmerAppOnList.class, sysFarmer);
//            perfectConfig(bean, longitude, latitude);
//        }
//        else if (longitude != null && latitude != null)
//        {
//            PageResult<SysFarmerAppOnList> list = queryMarket(0, 1, longitude, latitude, "", "", version, accountType);
//            if (!list.getContent().isEmpty())
//                bean = list.getContent().get(0);
//            System.out.println("longitude != null && latitude != null");
//        }
//        else
//        {
//            SelectBuilder<String, SysFarmer> builder = sysFarmerDao.select()
//                .eq("ascription", MobileSession.appid())
//                .notEq("pkey", 1)
//                .eq("idDel", false)
//                .eq("enabled", true);
//            // 限制版本号一致，则限制显示市场
//            if (account != null && StringUtil.isNotBlank(version) && version.equals(account.getShieldVersion()))
//                builder.in("pkey", allowedFarmers);
//            // 否则过滤限制显示市场
//            else
//                builder.notIn("pkey", allowedFarmers);
//            List<SysFarmer> sysFarmers = builder.sort("pkey", true).exec();
//            bean = BeanUtil.beanFrom(SysFarmerAppOnList.class, sysFarmers.get(0));
//        }
        
        if (longitude != null && latitude != null)
        {
            PageResult<SysFarmerAppOnList> list = queryMarket(0, 1, longitude, latitude, "", "", version, accountType);
            if (!list.getContent().isEmpty())
                bean = list.getContent().get(0);
            System.out.println("longitude != null && latitude != null");
        }
        else
        {
        	SysFarmer sysFarmer = null;
            if (StringUtils.isNotBlank(pkey))
            {
                sysFarmer = sysFarmerDao.selectOne().eq("idDel", false).eq("enabled", true).eq("pkey", pkey).exec();
            }
            if (sysFarmer != null)
            {
                bean = BeanUtil.beanFrom(SysFarmerAppOnList.class, sysFarmer);
                perfectConfig(bean, longitude, latitude);
            }
            else
            {
                SelectBuilder<String, SysFarmer> builder = sysFarmerDao.select()
                    .eq("ascription", MobileSession.appid())
                    .notEq("pkey", 1)
                    .eq("idDel", false)
                    .eq("enabled", true);
                // 限制版本号一致，则限制显示市场
                if (account != null && StringUtil.isNotBlank(version) && version.equals(account.getShieldVersion()))
                    builder.in("pkey", allowedFarmers);
                // 否则过滤限制显示市场
                else
                    builder.notIn("pkey", allowedFarmers);
                List<SysFarmer> sysFarmers = builder.sort("pkey", true).exec();
                bean = BeanUtil.beanFrom(SysFarmerAppOnList.class, sysFarmers.get(0));
            }
        }
        SysFarmerConfig config = bean.getConfig();
        List<SysFarmerTime> listTime = sysFarmerTimeDao.listTime(bean.getPkey(), config.getAscription());
        String weekTime = assembleWeekTime(config);
        String dayTime = "";
        
        for (int i = 0; i < listTime.size(); i++)
        {
            SysFarmerTime ft = listTime.get(i);
            String sh = getTimeFormat(ft.getStartHour());
            String sm = getTimeFormat(ft.getStartMinute());
            String eh = getTimeFormat(ft.getEndHour());
            String em = getTimeFormat(ft.getEndMinute());
            if (i == listTime.size() - 1)
            {
                dayTime = dayTime + sh + ":" + sm + " ~ " + eh + ":" + em;
            }
            else
                dayTime = dayTime + sh + ":" + sm + " ~ " + eh + ":" + em + ", ";
        }
        bean.setWeekTime(weekTime);
        bean.setDayTime(dayTime);
        bean.setGoodsType(config.getGoodsType());
        if(config.getGoodsType() == null)
            bean.setGoodsType(ConfigGoodsType.VENDOR_RECOMMEND);
        System.out.println("当前最近市场返回bean: " + JsonUtil.toString(bean, true));
        return bean;
    }
    
    private String getTimeFormat(Integer time)
    {
        String res = "";
        if (time > 9)
            res = time + "";
        else
            res = "0" + time;
        return res;
    }
    
    public String assembleWeekTime(SysFarmerConfig config)
    {
        List<Boolean> list = new ArrayList<>();
        list.add(config.getMonday());
        list.add(config.getTuesday());
        list.add(config.getWednesday());
        list.add(config.getThursday());
        list.add(config.getFriday());
        list.add(config.getSaturday());
        list.add(config.getSunday());
        int lastIndexOf = list.lastIndexOf(true);
        return assembleWeekList(list, lastIndexOf, 0, 0, "");
    }
    
    private String assembleWeekList(List<Boolean> list, Integer lastIndexOf, int i, int j, String weekTime)
    {
        for (; i < list.size(); i++)
        {
            Boolean b1 = list.get(i);
            if (Boolean.TRUE.equals(b1))
            {
                break;
            }
        }
        for (int h = i; h < list.size(); h++)
        {
            Boolean h1 = list.get(h);
            if (Boolean.FALSE.equals(h1) || h1 == null)
            {
                j = h - 1;
                break;
            }
            if (h == lastIndexOf && Boolean.TRUE.equals(h1))
            {
                j = h;
                break;
            }
        }
        if (i == j)
        {
            String week = getWeek(i);
            if (weekTime.length() > 0)
            {
                weekTime = weekTime + "," + week;
            }
            else
                weekTime = week;
        }
        else
        {
            String week = getWeek(i);
            String weekj = getWeek(j);
            if (weekTime.length() > 0)
            {
                weekTime = weekTime + "," + week + "至" + weekj;
            }
            else
                weekTime = week + "至" + weekj;
        }
        if (j < lastIndexOf)
            weekTime = assembleWeekList(list, lastIndexOf, ++j, j, weekTime);
        return weekTime;
    }
    
    private String getWeek(int key)
    {
        String res = "周一";
        switch (key)
        {
            case 0:
                res = "周一";
                break;
            case 1:
                res = "周二";
                break;
            case 2:
                res = "周三";
                break;
            case 3:
                res = "周四";
                break;
            case 4:
                res = "周五";
                break;
            case 5:
                res = "周六";
                break;
            case 6:
                res = "周日";
                break;
            
            default:
                break;
        }
        return res;
    }
    
    public AppIndexZoneConfig getZoneConfig()
    {
        Integer ascription = MobileSession.appid();
        String farmerPkey = MobileSession.farmerPkey();
        AscriptionGoodsZoneConfig ascriptionConfig = dynamicAttributeDao
            .getFarmerAttribute(AscriptionGoodsZoneConfig.class, ascription, Constant.Operation + ascription);
        FarmerGoodsZoneConfig farmerConfig =
            dynamicAttributeDao.getFarmerAttribute(FarmerGoodsZoneConfig.class, ascription, farmerPkey);
        AppIndexZoneConfig config = new AppIndexZoneConfig();
        if (ascriptionConfig == null)
            ascriptionConfig = new AscriptionGoodsZoneConfig();
        if (farmerConfig == null)
            farmerConfig = new FarmerGoodsZoneConfig();
        config.setIntegralDisplayName(ascriptionConfig.getIntegralDisplayName());
        config.setIntegralPresaleDisplayName(ascriptionConfig.getIntegralPresaleDisplayName());
        config.setIntegralBNYPDisplayName(ascriptionConfig.getIntegralBNYPDisplayName());
        config.setIntegralMsdDisplayName(ascriptionConfig.getIntegralMsdDisplayName());
        config.setSpecialDisplayName(farmerConfig.getSpecialDisplayName());

        JdGoodsZoneConfig jdGoodsZoneConfig = dynamicAttributeDao.getSysAttribute(JdGoodsZoneConfig.class, ascription);
        if (jdGoodsZoneConfig == null) jdGoodsZoneConfig = new JdGoodsZoneConfig();
        config.setJdGoodsDisplayName(jdGoodsZoneConfig.getJdGoodsName());

        return config;
    }
    
    public AppIndexZoneGoodsList listZoneGoods()
    {
        PageResult<AppGoodsAppOnList> specialPageRes =
            appGoodsManager.queryAppGoods(0, maxRecommend, MType.SPECIAL_GOODS, false, true);
        if (!specialPageRes.hasContent())
        {
            specialPageRes = appGoodsManager.queryAppGoods(0, maxRecommend, MType.SPECIAL_GOODS, false, null);
        }
        
        PageResult<AppGoodsAppOnList> integralPageRes =
            appGoodsManager.queryAppGoods(0, maxRecommend, MType.INTEGRAL_GOODS, false, true);
        if (!integralPageRes.hasContent())
        {
            integralPageRes = appGoodsManager.queryAppGoods(0, maxRecommend, MType.INTEGRAL_GOODS, false, null);
        }
        
        PageResult<AppGoodsAppOnList> integralPresalePageRes =
            appGoodsManager.queryAppGoods(0, maxRecommend, MType.INTEGRAL_PRESALE_GOODS, true, true);
        if (!integralPresalePageRes.hasContent())
        {
            integralPresalePageRes =
                appGoodsManager.queryAppGoods(0, maxRecommend, MType.INTEGRAL_PRESALE_GOODS, true, null);
        }
        
        PageResult<AppGoodsAppOnList> integralBNYPPageRes =
            appGoodsManager.queryAppGoods(0, maxRecommend, MType.INTEGRAL_BNYP_GOODS, false, true);
        if (!integralBNYPPageRes.hasContent())
        {
            integralBNYPPageRes =
                appGoodsManager.queryAppGoods(0, maxRecommend, MType.INTEGRAL_BNYP_GOODS, false, null);
        }

        PageResult<AppGoodsAppOnList> integralMsdPageRes =
            appGoodsManager.queryAppGoods(0, maxRecommend, MType.INTEGRAL_MSD_GOODS, false, true);
        if (!integralMsdPageRes.hasContent())
        {
            integralMsdPageRes =
                appGoodsManager.queryAppGoods(0, maxRecommend, MType.INTEGRAL_MSD_GOODS, false, null);
        }
        
        AppIndexZoneGoodsList res = new AppIndexZoneGoodsList();
        res.setSpecialList(specialPageRes.getContent());
        res.setIntegralList(integralPageRes.getContent());
        res.setIntegralPresaleList(integralPresalePageRes.getContent());
        res.setIntegralBNYPList(integralBNYPPageRes.getContent());
        res.setIntegralMsdList(integralMsdPageRes.getContent());
        return res;
    }
    
    //    public static void main(String[] args)
    //    {
    //        List<Boolean> list = new ArrayList<>();
    //        list.add(true);
    //        list.add(true);
    //        list.add(true);
    //        list.add(true);
    //        list.add(true);
    //        list.add(true);
    //        list.add(true);
    //        int lastIndexOf = list.lastIndexOf(true);
    //        System.out.println("list: " + JsonUtil.toString(list, true));
    //        System.out.println("lastIndexOf: " + lastIndexOf);
    //        AppIndexManager aim = new AppIndexManager();
    //        String weekTime = aim.assembleWeekList(list, lastIndexOf, 0, 0, "");
    //        System.out.println("weekTime: " + weekTime);
    //    }
    
    public List<AppGtypeDTO> listGtype()
    {
        PageResult<AppGtypeDTO> pageResult = gtypeDao.selectPage()
        .page(0).pagesize(10)
        .eq("farmer", MobileSession.farmerPkey())
        .eq("idDel", false).eq("enabled", true)
        .sort("sort", false).execDto(AppGtypeDTO.class);
        return pageResult.getContent();
    }
}

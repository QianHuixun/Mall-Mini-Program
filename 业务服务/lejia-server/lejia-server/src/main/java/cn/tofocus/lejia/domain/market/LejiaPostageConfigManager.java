package cn.tofocus.lejia.domain.market;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.lejia.bean.dto.market.MktDeliveryTimeConfig;
import cn.tofocus.lejia.bean.dto.market.MktPostageConfigOnList;
import cn.tofocus.lejia.bean.dto.market.PostageExpressConfigDTO;
import cn.tofocus.lejia.bean.entity.market.MktDeliveryTimeConfigEntity;
import cn.tofocus.lejia.bean.entity.market.MktPostageConfig;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerConfig;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerStation;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.market.MktDeliveryTimeConfigDao;
import cn.tofocus.lejia.dao.market.MktPostageConfigDao;
import cn.tofocus.lejia.dao.sys.SysFarmerConfigDao;
import cn.tofocus.lejia.dao.sys.SysFarmerStationDao;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LejiaPostageConfigManager
{
    @Autowired
    private MktPostageConfigDao postageConfigDao;
    
    @Autowired
    private SysFarmerConfigDao farmerConfigDao;
    
    @Autowired
    private MktDeliveryTimeConfigDao deliveryTimeConfigDao;
    
    public List<MktDeliveryTimeConfig> queryDeliveryTimeConfig()
    {
        List<MktDeliveryTimeConfig> l =
            deliveryTimeConfigDao.listByMarket(MktDeliveryTimeConfig.class, CurrentSession.marketPkey());
        int size = l.size();
        if (size < 4)
        {
            for (int i = 0; i < 4 - size; i++)
            {
                l.add(new MktDeliveryTimeConfig());
            }
        }
        return l;
    }
    
    public List<MktPostageConfigOnList> queryPostageConfig()
    {
        List<MktPostageConfig> exec =
            postageConfigDao.queryPostageConfig(CurrentSession.marketPkey(), CurrentSession.companyPkey());
        if (exec.isEmpty())
        {
            List<MktPostageConfigOnList> entitys = new ArrayList<>();
            for (int i = 0; i < 4; i++)
            {
                MktPostageConfigOnList dto = new MktPostageConfigOnList();
                dto.setPostage(new BigDecimal(0));
                dto.setWeight(new BigDecimal(0));
                entitys.add(dto);
            }
            List<MktPostageConfig> result = updMktPostageConfig(entitys);
            return BeanUtil.beanListFrom(MktPostageConfigOnList.class, result);
        }
        return BeanUtil.beanListFrom(MktPostageConfigOnList.class, exec);
    }
    
    public List<MktPostageConfigOnList> updPostageConfig(List<MktPostageConfigOnList> entitys)
    {
        List<MktPostageConfig> result = updMktPostageConfig(entitys);
        return BeanUtil.beanListFrom(MktPostageConfigOnList.class, result);
    }
    
    @Autowired
    private SysFarmerStationDao sysFarmerStationDao;
    
    @Transactional
    public Boolean updPostageConfig(PostageExpressConfigDTO entity)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        String marketPkey = CurrentSession.marketPkey();
        if (!(Constant.Operation + ascription).equals(marketPkey)) updDeliveryTime(entity.getDeliveryTimes());
        updMktPostageConfig(entity.getPcList());
        log.info("marketPkey: {}", marketPkey);
        SysFarmerConfig farmerConfig = farmerConfigDao.get(marketPkey);
        BeanUtils.copyProperties(entity, farmerConfig);
        farmerConfigDao.update(farmerConfig);
        
        //修改配置 z
        SysFarmerStation station = sysFarmerStationDao.selectOne().eq("market", marketPkey).exec();
        if (station == null)
        {
            station = new SysFarmerStation();
            station.setMarket(marketPkey);
            station.setAscription(CurrentSession.ascriptionPkey());
        }
        station.setPhour(entity.getPickupHour());
        station.setPminute(entity.getPickupMinute());
        station.setDeliveryDate(entity.getPickupDeliveryDate());
        station = sysFarmerStationDao.put(station);
        return true;
    }
    
    private void updDeliveryTime(List<MktDeliveryTimeConfig> deliveryTimes)
    {
        String companyPkey = CurrentSession.companyPkey();
        String marketPkey = CurrentSession.marketPkey();
        List<MktDeliveryTimeConfigEntity> entitys = new ArrayList<>();
        for (int i = 0; i < deliveryTimes.size(); i++)
        {
            MktDeliveryTimeConfig c = deliveryTimes.get(i);
            MktDeliveryTimeConfigEntity entity = new MktDeliveryTimeConfigEntity();
            entity.setCompany(companyPkey);
            entity.setFarmer(marketPkey);
            entity.setAscription(CurrentSession.ascriptionPkey());
            if (i == 0)
                entity.setDistance(BigDecimal.ZERO);
            else
                entity.setDistance(c.getDistance());
            entity.setHour(c.getHour());
            entity.setMinute(c.getMinute());
            entity.setPkey(marketPkey + "_" + i);
            entitys.add(entity);
        }
        deliveryTimeConfigDao.putAll(entitys);
    }
    
    private List<MktPostageConfig> updMktPostageConfig(List<MktPostageConfigOnList> entitys)
    {
        List<MktPostageConfig> exec = postageConfigDao.select()
            .eq("farmer", CurrentSession.marketPkey())
            .eq("company", CurrentSession.companyPkey())
            .sort("pkey", false)
            .exec();
        List<MktPostageConfig> result = new ArrayList<>();
        List<MktPostageConfig> addList = new ArrayList<>();
        String companyPkey = CurrentSession.companyPkey();
        String marketPkey = CurrentSession.marketPkey();
        log.info("entitys: {}", JsonUtil.toString(entitys, true));
        for (MktPostageConfigOnList entity : entitys)
        {
            if (entity.getPkey() == null)
            {
                MktPostageConfig postageConfig = BeanUtil.beanFrom(MktPostageConfig.class, entity);
                postageConfig.setCompany(companyPkey);
                postageConfig.setFarmer(marketPkey);
                postageConfig.setAscription(CurrentSession.ascriptionPkey());
                postageConfig.setRowVension(1);
                addList.add(postageConfig);
            }
            else
            {
                for (MktPostageConfig pc : exec)
                {
                    if (entity.getPkey().intValue() == pc.getPkey().intValue())
                    {
                        pc.setWeight(entity.getWeight());
                        pc.setPostage(entity.getPostage());
                    }
                }
            }
        }
        result = postageConfigDao.updateAll(exec);
        List<MktPostageConfig> addAll = postageConfigDao.addAll(addList);
        result.addAll(addAll);
        if (exec.isEmpty())
        {
            exec.addAll(addAll);
        }
        MktPostageConfig config = exec.get(exec.size() - 1);
        MktPostageConfig config2 = exec.get(exec.size() - 2);
        config.setWeight(config2.getWeight().add(new BigDecimal("0.01")));
        return result;
    }
    
}

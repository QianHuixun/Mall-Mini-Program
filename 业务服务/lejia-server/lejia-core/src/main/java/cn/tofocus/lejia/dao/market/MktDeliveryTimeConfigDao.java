package cn.tofocus.lejia.dao.market;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.dto.app.market.MktAppAddrDTO;
import cn.tofocus.lejia.bean.dto.market.MktDeliveryTimeConfig;
import cn.tofocus.lejia.bean.entity.market.MktDeliveryTimeConfigEntity;
import cn.tofocus.lejia.bean.entity.market.MktDeliveryTimeConfigEntity.F;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MktDeliveryTimeConfigDao extends JpaSpecificationDelegate<String, MktDeliveryTimeConfigEntity>
{
    public <T> List<T> listByMarket(Class<T> clazz, String market)
    {
        return this.select().eq(F.farmer, market).sort(F.pkey, false).execDto(clazz);
    }
    
    public MktDeliveryTimeConfig getDeliveryTimeConfigByDistance(String market, BigDecimal distance)
    {
        if(market == null)
            throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_NULL, "市场不能为空");
        MktDeliveryTimeConfig cfg = null;
        if (distance != null)
        {
            double d = 0;
            double d1 = distance.doubleValue();
            if (d1 > 0)
                d = d1 / 1000;
            log.warn("[配送距离] {} 公里", d);
            cfg = this.selectOne()
                .eq(F.farmer, market)
                .le(F.distance, d)
                .sort(F.distance)
                .execDto(MktDeliveryTimeConfig.class);
        }
        else
        {
            cfg = this.selectOne().eq(F.farmer, market).sort(F.distance, false).execDto(MktDeliveryTimeConfig.class);
        }
        if (cfg == null)
        {
            log.warn("[配送距离]{} 市场未配置，按1小时计算", market);
            cfg = new MktDeliveryTimeConfig();
            cfg.setHour(1);
            cfg.setMinute(0);
        }
        else
        {
            log.warn("[配送距离] 预计送达时间：{} 小时 {} 分钟", cfg.getHour(), cfg.getMinute());
        }
        if(cfg.getMinute() == null)
            cfg.setMinute(0);
        if (cfg.getHour() == null)
        {
            log.warn("[配送距离]{} 市场，{} 距离，未配置时间，按1小时计算", market, cfg.getDistance());
            cfg.setHour(1);
        }
        return cfg;
    }
    
    public MktDeliveryTimeConfig getDeliveryTimeConfigByDistance(String market, MktAppAddrDTO addr)
    {
        if(market == null)
            throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_NULL, "市场不能为空");
        MktDeliveryTimeConfig cfg = null;
        BigDecimal distance = null;
        if (addr != null)
        {
            if (addr.getDistance() != null)
            {
                distance = addr.getDistance();
            }
            else
            {
                log.warn("[配送距离]为 null, 按最小距离计算, {}", addr.getAddr());
            }
        }
        else
        {
            log.warn("[配送距离]为 null, 按最小距离计算");
        }
        
        if (distance != null)
        {
            double d = 0;
            double d1 = distance.doubleValue();
            if (d1 > 0)
                d = d1 / 1000;
            log.warn("[配送距离] {} 公里", d);
            cfg = this.selectOne()
                .eq(F.farmer, market)
                .le(F.distance, d)
                .sort(F.distance)
                .execDto(MktDeliveryTimeConfig.class);
        }
        else
        {
            cfg = this.selectOne().eq(F.farmer, market).sort(F.distance, false).execDto(MktDeliveryTimeConfig.class);
        }
        if (cfg == null)
        {
            log.warn("[配送距离]{} 市场未配置，按1小时计算", market);
            cfg = new MktDeliveryTimeConfig();
            cfg.setHour(1);
            cfg.setMinute(0);
        }
        else
        {
            log.warn("[配送距离] 预计送达时间：{} 小时 {} 分钟", cfg.getHour(), cfg.getMinute());
        }
        if(cfg.getMinute() == null)
            cfg.setMinute(0);
        if (cfg.getHour() == null)
        {
            log.warn("[配送距离]{} 市场，{} 距离，未配置时间，按1小时计算", market, cfg.getDistance());
            cfg.setHour(1);
        }
        return cfg;
    }
}

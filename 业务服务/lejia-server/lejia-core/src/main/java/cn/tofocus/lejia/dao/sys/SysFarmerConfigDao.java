package cn.tofocus.lejia.dao.sys;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerConfig;
import cn.tofocus.lejia.bean.enums.SettlementMethodType;

@Component
public class SysFarmerConfigDao extends JpaSpecificationDelegate<String, SysFarmerConfig>
{
    //	@Autowired
    //	private SysFarmerConfigRepository repository;
    //
    //	@Override
    //	protected JpaRepository<SysFarmerConfig, String> getRepository() {
    //		return repository;
    //	}
    
    /**
     * 获取市场的结算方式
     * @return  市场的结算方式
     */
    public SettlementMethodType getFarmerSettle(String farmerPkey)
    {
        SysFarmerConfig sysFarmerConfig = this.get(farmerPkey);
        if (Objects.nonNull(sysFarmerConfig))
        {
            return sysFarmerConfig.getSettlementMethod();
        }
        return null;
    }
    
    /**
     * 获取市场的订单起步价
     * @return  市场的订单起步价
     */
    public BigDecimal getStartingPrice(String farmerPkey)
    {
        SysFarmerConfig sysFarmerConfig = this.get(farmerPkey);
        if (Objects.nonNull(sysFarmerConfig))
        {
            return sysFarmerConfig.getStartingPrice();
        }
        return null;
    }
    
    public Map<String,Boolean> mapIsEnterprise(Integer ascription)
    {
        Map<String,Boolean> map = new HashMap<>();
        List<SysFarmerConfig> list = this.select().eq("ascription", ascription).exec();
        list.forEach(e -> map.put(e.getPkey(), e.getIsEnterprise()));
        return map;
    }
}
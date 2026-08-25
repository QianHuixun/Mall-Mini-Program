package cn.tofocus.lejia.dao.market;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.market.MktOrderExpressRoute;
import cn.tofocus.lejia.bean.entity.market.MktOrderExpressRoute.F;

@Component
public class MktOrderExpressRouteDao extends JpaSpecificationDelegate<Long, MktOrderExpressRoute>
{
    public MktOrderExpressRoute getByOrderExpressAndThirdId(Long orderExpress, String thirdId)
    {
        return this.selectOne().eq(F.orderExpress, orderExpress).eq(F.thirdId, thirdId).exec();
    }
    
    public <T> List<T> listByOrderPkey(Integer orderPkey, Class<T> clazz)
    {
        return this.select().eq(F.orderPkey, orderPkey).sort(F.time).sort(F.thirdId).execDto(clazz);
    }
    
    /**
     * 预留，以后可能会有多个包裹，需要分组
     */
    public MultiValueMap<String, MktOrderExpressRoute> listMailNoGroupByOrderPkey(Integer orderPkey)
    {
        List<MktOrderExpressRoute> list = this.select().eq(F.orderPkey, orderPkey).sort(F.time).sort(F.thirdId).exec();
        MultiValueMap<String, MktOrderExpressRoute> map = new LinkedMultiValueMap<>();
        for (MktOrderExpressRoute route : list)
        {
            map.add(route.getMailNo(), route);
        }
        return map;
    }
}

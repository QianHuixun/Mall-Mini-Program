package cn.tofocus.lejia.dao.market;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.market.MktOrderExpress;
import cn.tofocus.lejia.bean.entity.market.MktOrderExpress.F;
import cn.tofocus.lejia.bean.enums.express.ExpressCompany;
import cn.tofocus.lejia.bean.enums.express.OrderExpressStatus;

@Component
public class MktOrderExpressDao extends JpaSpecificationDelegate<Long, MktOrderExpress>
{
    // 默认一个订单就一个进行中的物流单
    public <T> T getNotCanceledByOrderPkey(Integer orderPkey, Class<T> clazz)
    {
        return this.selectOne().eq(F.orderPkey, orderPkey).notEq(F.status, OrderExpressStatus.CANCELED).execDto(clazz);
    }
    
    public MktOrderExpress getByExpressNo(ExpressCompany expressCompany, String expressNo)
    {
        return this.selectOne().eq(F.expressCompany, expressCompany).eq(F.expressNo, expressNo).exec();
    }
    
    public MktOrderExpress byOrder(Integer orderPkey)
    {
        return this.selectOne().eq(F.orderPkey, orderPkey).exec();
    }
}

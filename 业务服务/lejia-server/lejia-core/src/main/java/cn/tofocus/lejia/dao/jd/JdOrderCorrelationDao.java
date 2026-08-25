package cn.tofocus.lejia.dao.jd;

import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.jd.JdOrderCorrelation;
import cn.tofocus.lejia.bean.entity.jd.JdOrderCorrelation.F;
import cn.tofocus.lejia.bean.enums.jd.OrderCorrelationStatus;

@Component
public class JdOrderCorrelationDao extends JpaSpecificationDelegate<Integer, JdOrderCorrelation>
{
    public JdOrderCorrelation getByJdCode(Long jdCode)
    {
        return this.selectOne().eq(F.jdCode, jdCode).exec();
    }

    public JdOrderCorrelation getByCode(String orderCode)
    {
        return this.selectOne().eq(F.orderCode, orderCode).exec();
    }
    
    public List<JdOrderCorrelation> byParentOrder(Long pOrder)
    {
        return this.select()
            .eq(F.parentOrder, pOrder)
            .eq(F.status, OrderCorrelationStatus.NORMAL_ORDER)
            .isNotNull(F.parentOrder).exec();
    }
}

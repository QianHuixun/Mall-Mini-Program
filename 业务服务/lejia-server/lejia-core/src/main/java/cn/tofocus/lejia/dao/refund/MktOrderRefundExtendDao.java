package cn.tofocus.lejia.dao.refund;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.refund.MktOrderRefundExtend;
import cn.tofocus.lejia.bean.entity.refund.MktOrderRefundExtend.F;

@Component
public class MktOrderRefundExtendDao extends JpaSpecificationDelegate<Integer, MktOrderRefundExtend>
{
    public MktOrderRefundExtend byRefundPkey(Integer refundPkey)
    {
        return this.selectOne().eq(F.refundPkey, refundPkey).exec();
    }
}

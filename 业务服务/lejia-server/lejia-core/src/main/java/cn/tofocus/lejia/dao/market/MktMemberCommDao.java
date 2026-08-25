package cn.tofocus.lejia.dao.market;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.member.MktMemberComm;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

@Component
public class MktMemberCommDao extends JpaSpecificationDelegate<Integer,MktMemberComm>
{
    public BigDecimal yesterdayComms(Integer ascription)
    {
        Number sum = this.aggregation().eq("ascription", ascription).execSum("comms");
        if(sum == null)
            return BigDecimal.ZERO;
        return new BigDecimal(sum.toString());
    }
}
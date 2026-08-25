package cn.tofocus.lejia.dao.vendor;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.vendor.MktSettlementTotal;
import cn.tofocus.lejia.bean.enums.SettlementType;
import cn.tofocus.lejia.utils.DateUtil;

import static cn.tofocus.core.query.exp.ExpUtil.f;
import static cn.tofocus.core.query.exp.ExpUtil.substring;

@Component
public class MktSettlementTotalDao extends JpaSpecificationDelegate<Long, MktSettlementTotal>
{
    public MktSettlementTotal getTotal(Date date)
    {
        MktSettlementTotal total = this.selectOne().eq("settlementDate", date).eq("type", SettlementType.DOING).exec();
        return total;
    }
    
    public void updTotalDoing(List<Date> dateList)
    {
        List<MktSettlementTotal> list = this.select().in("settlementDate", dateList.toArray()).exec();
        for(MktSettlementTotal t : list)
        {
            t.setType(SettlementType.DOING);
        }
        this.updateAll(list);
    }
    
    public List<MktSettlementTotal> getTimeTotal(Date date)
    {
        String time = DateUtil.formatDate(date, "yyyy-MM-dd");
        List<MktSettlementTotal> res = this.select()
            .eq(substring(f("createdTime"), 1, 10), time)
            .eq("type", SettlementType.DOING).exec();
        return res;
    }
}
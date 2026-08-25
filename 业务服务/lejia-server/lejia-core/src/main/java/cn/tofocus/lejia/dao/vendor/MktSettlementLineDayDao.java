package cn.tofocus.lejia.dao.vendor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.vendor.MktSettlementLineDay;
import cn.tofocus.lejia.bean.enums.SettlementType;

@Component
public class MktSettlementLineDayDao extends JpaSpecificationDelegate<Long, MktSettlementLineDay>
{
    public void updDayDoing(Integer settlementKey)
    {
        List<MktSettlementLineDay> list = this.select().eq("settlementPkey", settlementKey).exec();
        for(MktSettlementLineDay sld : list)
        {
            sld.setType(SettlementType.DOING);
        }
        this.updateAll(list);
    }
    
    public List<Date> listDate(Integer settlementKey)
    {
        List<Date> res = new ArrayList<>();
        List<MktSettlementLineDay> list = this.select().eq("settlementPkey", settlementKey).exec();
        for(MktSettlementLineDay sld : list)
        {
            res.add(sld.getSettlementDate());
        }
        res = res.stream().distinct().collect(Collectors.toList());
        return res;
    }
}
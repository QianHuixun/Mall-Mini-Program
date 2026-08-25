package cn.tofocus.lejia.dao.vendor;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.db.SelectBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.vendor.MktSettlement;
import cn.tofocus.lejia.bean.enums.SettlementType;
import cn.tofocus.lejia.utils.DateUtil;

import static cn.tofocus.core.query.exp.ExpUtil.f;
import static cn.tofocus.core.query.exp.ExpUtil.substring;

@Component
public class MktSettlementDao extends JpaSpecificationDelegate<Integer, MktSettlement>
{
    
    public MktSettlement findByDate(String startDate, String endDate, List<String> marketKeys)
    {
        return this.selectOne().le("startDate", endDate).ge("endDate", startDate).in("farmer", marketKeys).exec();
    }
    
    public List<MktSettlement> findByPkeys(String startDate, String endDate, List<String> marketKeys, Integer ascription)
    {
        return this.select()
        .in("farmer", marketKeys)
        .eq("ascription", ascription)
        .or()
        .between("startDate", startDate, endDate)
        .between("endDate", startDate, endDate)
        .close()
        .done()
        .exec();
    }
    
    public List<MktSettlement> findByMaket(String marketPkey, SettlementType type)
    {
        return this.select().eq("farmer", marketPkey).eq("type", type).sort("startDate", false).exec();
    }
    
    public List<MktSettlement> findByDate(Date date)
    {
        String time = DateUtil.formatDate(date, "yyyy-MM-dd");
        SelectBuilder<Integer, MktSettlement> builder = this.select()
            .eq("type", SettlementType.DOING)
            .or()
            .eq(substring(f("createdTime"), 1, 10), time)
            .eq(substring(f("updatedTime"), 1, 10), time)
            .close()
            .done()
            ;
        return builder.exec();
    }
    
    public List<MktSettlement> findByDateTest(Date date)
    {
        String time = DateUtil.formatDate(date, "yyyy-MM-dd");
        SelectBuilder<Integer, MktSettlement> builder = this.select()
            .or()
            .eq(substring(f("createdTime"), 1, 10), time)
            .eq(substring(f("updatedTime"), 1, 10), time)
            .close()
            .done()
            ;
        return builder.exec();
    }
}
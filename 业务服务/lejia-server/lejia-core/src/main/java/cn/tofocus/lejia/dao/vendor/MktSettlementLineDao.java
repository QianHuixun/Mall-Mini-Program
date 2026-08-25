package cn.tofocus.lejia.dao.vendor;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.db.SelectBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.vendor.MktSettlementLine;
import cn.tofocus.lejia.bean.enums.SettlementType;
import cn.tofocus.lejia.utils.DateUtil;

import static cn.tofocus.core.query.exp.ExpUtil.f;
import static cn.tofocus.core.query.exp.ExpUtil.substring;

@Component
public class MktSettlementLineDao extends JpaSpecificationDelegate<Long, MktSettlementLine>
{
    
    public <T> List<T> findByLine(Integer settlementPkey, SettlementType type, String keyword, Class<T> clazz)
    {
        return this.select()
            .eq("settlementPkey", settlementPkey)
            .eq("type", type)
            .or()
            .like("vendorName", keyword)
            .like("bankuser", keyword)
            .close()
            .done()
            .execDto(clazz);
    }
    
    public <T> List<T> findByLineV4(List<Integer> settlementPkeys, Integer ascription, Class<T> clazz)
    {
        return this.select()
            .eq("type", SettlementType.SUCCESS)
            .eq("ascription", ascription)
            .in("settlementPkey", settlementPkeys)
            .execDto(clazz);
    }
    
    public List<MktSettlementLine> findByLines(Integer settlementPkey, SettlementType type)
    {
        return this.select().eq("settlementPkey", settlementPkey).eq("type", type).exec();
    }
    
    public List<MktSettlementLine> findByDate(Date date)
    {
        String time = DateUtil.formatDate(date, "yyyy-MM-dd");
        SelectBuilder<Long, MktSettlementLine> builder = this.select()
            .eq("type", SettlementType.DOING)
            .or()
            .eq(substring(f("createdTime"), 1, 10), time)
            .eq(substring(f("updatedTime"), 1, 10), time)
            .close()
            .done()
            ;
        return builder.exec();
    }
}
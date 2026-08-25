package cn.tofocus.lejia.dao.market;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.db.SelectBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.member.MktMemberSign;
import cn.tofocus.lejia.repository.market.MktMemberSignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class MktMemberSignDao extends JpaSpecificationDelegate<Integer, MktMemberSign>
{
    @Autowired
    private MktMemberSignRepository repository;
    
    public List<MktMemberSign> getSigns(Integer member, Integer year, Integer month)
    {
        return repository.getSigns(member, year, month);
    }
    
    public List<MktMemberSign> getSignsDate(Integer member, Date date)
    {
        SelectBuilder<Integer, MktMemberSign> builder = select().eq("member", member).sort("createdTime", true);
        if (date != null) builder.eq("signDate", DateUtil.formatDate(date, "yyyy-MM-dd"));
        return builder.exec();
    }
    
    public Integer getSignNum(Integer member)
    {
        Integer signNum = 0;
        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        cal.add(Calendar.DATE, -1);
        Date yesterday = cal.getTime();
        List<MktMemberSign> todaySign = this.select().eq("member", member).eq("signDate", DateUtil.formatDate(today, "yyyy-MM-dd")).exec();
        List<MktMemberSign> yesterdaySign = this.select().eq("member", member).eq("signDate", DateUtil.formatDate(yesterday, "yyyy-MM-dd")).exec();
        if (todaySign.size() == 1)
        {
            return todaySign.get(0).getSignNum();
        }
        else if (yesterdaySign.size() == 1)
        {
            return yesterdaySign.get(0).getSignNum();
        }
        return signNum;
    }
}

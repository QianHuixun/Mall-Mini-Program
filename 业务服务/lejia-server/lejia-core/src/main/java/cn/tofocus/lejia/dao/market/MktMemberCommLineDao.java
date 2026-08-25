package cn.tofocus.lejia.dao.market;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.member.MktMemberCommLine;
import cn.tofocus.lejia.bean.enums.CommSourceType;
import cn.tofocus.lejia.repository.market.MktMemberCommLineRepository;

@Component
public class MktMemberCommLineDao extends JpaSpecificationDelegate<Integer, MktMemberCommLine>
{
    @Autowired
    private MktMemberCommLineRepository repository;
    
    public List<List<Object>> getCommsNum(String marketPkey, String memberName, int page, int pagesize)
    {
        if (StringUtils.isBlank(memberName)) memberName = null;
        List<List<Object>> list = repository.getCommsNum(marketPkey, memberName, page * pagesize, pagesize);
        return list;
    }
    
    public List<List<Object>> getComms(String marketPkey, String memberName, int page, int pagesize)
    {
        if (StringUtils.isBlank(memberName)) memberName = null;
        List<List<Object>> list = repository.getComms(marketPkey, memberName, page * pagesize, pagesize);
        return list;
    }
    
    //	public List<List<Object>> getCommsDetail(String marketPkey,String startTime, String endTime,
    //			int page,  int pagesize)
    //	{
    //	
    //		if(StringUtils.isNotBlank(startTime))
    //		{
    //			if(StringUtils.isBlank(endTime))
    //				endTime = "2100-01-01";
    //			else
    //				endTime = endTime + " 23:59:59";
    //		}
    //		else
    //			startTime = null;
    //		List<List<Object>> list = repository.getCommsDetail(marketPkey, startTime, endTime, page * pagesize, pagesize);
    //		return list;
    //	}
    //	
    public BigDecimal yesterdayComms(String time)
    {
        return repository.yesterdayComms(time);
    }
    
    public PageResult<MktMemberCommLine> queryMemberCommLine(int page, int pagesize, Integer member,
        CommSourceType source, List<Integer> memberPkey, String startDate, String endDate, Boolean direct, Integer ascription)
    {
        
        SelectPageBuilder<Integer, MktMemberCommLine> builder =
            selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq("ascription", ascription)
            .sort("createdTime", true);
        if (!memberPkey.isEmpty()) builder.in("member", memberPkey.toArray());
        if (member != null) builder.eq("member", member);
        if (source != null) builder.eq("source", source);
        if (StringUtils.isNotBlank(startDate)) builder.ge("createdTime", startDate);
        if (direct != null) builder.eq("direct", direct);
        if (StringUtils.isNotBlank(endDate))
        {
            try
            {
                Date str = DateUtil.formatDateStr(endDate);
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(str);
                calendar.add(Calendar.DATE, 1);
                str = calendar.getTime();
                builder.le("createdTime", str);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            
        }
        return builder.exec();
    }
    
    public MktMemberCommLine byFormIdAndSource(String formid, CommSourceType source)
    {
        return this.selectOne().eq("formId", formid).eq("source", source).exec();
    }
    
    
}
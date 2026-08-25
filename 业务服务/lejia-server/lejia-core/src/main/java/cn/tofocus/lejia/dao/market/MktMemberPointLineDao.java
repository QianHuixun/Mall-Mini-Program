package cn.tofocus.lejia.dao.market;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.member.MktMemberPointLine;
import cn.tofocus.lejia.bean.enums.SourceType;

@Component
public class MktMemberPointLineDao extends JpaSpecificationDelegate<Integer,MktMemberPointLine>
{
	public PageResult<MktMemberPointLine> queryMemberPointLine(int page, int pagesize, Integer member,
            SourceType source, List<Integer> memberPkey, String startDate, String endDate, Boolean direct, Integer ascription) 
	{
		
		SelectPageBuilder<Integer, MktMemberPointLine> builder = selectPage()
				.page(page)
                .pagesize(pagesize)
                .eq("ascription", ascription)
                .sort("createdTime", true);
		if(memberPkey.size() > 0)
			builder.in("member", memberPkey.toArray());
        if (member != null)
            builder.eq("member", member);
        if (source != null)
            builder.eq("source", source);
        if (StringUtils.isNotBlank(startDate))
            builder.ge("createdTime", startDate);
        if(direct != null)
        	builder.eq("direct", direct);
        if (StringUtils.isNotBlank(endDate)) {
            try {
                Date str = DateUtil.formatDateStr(endDate);
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(str);
                calendar.add(Calendar.DATE, 1);
                str = calendar.getTime();
                builder.le("createdTime", str);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return builder.exec();
	}
	
	public Integer byFormId(String formId)
	{
	    MktMemberPointLine exec = this.selectOne().eq("formId", formId).eq("direct", true)
	    .eq("source", SourceType.POINTS_CONSUMPTION).exec();
	    if(exec != null)
	        return exec.getPoints();
	    return 0;
	}
}
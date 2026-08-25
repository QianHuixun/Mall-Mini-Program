package cn.tofocus.lejia.dao.market;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.dto.market.MktMemberPayOnList;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.member.MktMemberPay;
import cn.tofocus.lejia.bean.enums.MemberPType;
import cn.tofocus.lejia.bean.enums.PayStatus;
import cn.tofocus.lejia.repository.market.MktMemberPayRepository;

@Component
public class MktMemberPayDao extends JpaSpecificationDelegate<Integer,MktMemberPay>
{
	@Autowired
	private MktMemberPayRepository repository;
	
	@Autowired
	private MktMemberDao memberDao;
	
	public PageResult<MktMemberPayOnList> queryPayLines(int page, int pagesize, String mobile, String startTime, String endTime, Integer ascription)
	{
		if(StringUtils.isNotBlank(endTime))
		{
			Date date = DateUtil.formatDateStr(endTime, "yyyy-MM-dd");
			Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(Calendar.DATE,1); 
            date = calendar.getTime(); 
            endTime = DateUtil.formatDate(date, "yyyy-MM-dd");
		}
		else
			endTime = null;
		if(StringUtils.isBlank(startTime))
			startTime = null;
		if(StringUtils.isBlank(mobile))
			mobile = null;	
		List<MktMemberPay> lines = repository.queryMemberPay(mobile, startTime, endTime, ascription);
		PageResult<MktMemberPayOnList> result = new PageResult<>();
		PageParameter pageable = new PageParameter(page, pagesize);
		pageable.setPageNumber(lines.size());
		result.setPageable(pageable);
		List<MktMemberPayOnList> content = new ArrayList<>();
		for(int i = page*pagesize; i< lines.size(); i++)
		{
			if(i >= (page+1)*pagesize)
				break;
			MktMemberPay memberPay = lines.get(i);
			MktMemberPayOnList dto = BeanUtil.beanFrom(MktMemberPayOnList.class, memberPay);
			dto.setPayType(memberPay.getPType());
			dto.setPayTypeName(memberPay.getPType().getName());
			MktMember member = memberDao.get(memberPay.getMember());
			if(member != null)
				dto.setMobile(member.getMobile());
			content.add(dto);
		}
		result.setContent(content);
		return result;
	}
	
	public List<List<Object>> getMemberPay(String startTime, String endTime, Integer ascription)
	{
		if(StringUtils.isNotBlank(startTime))
		{
			if(StringUtils.isBlank(endTime))
				endTime = "2100-01-01";
			else
				endTime = endTime + " 23:59:59";
		}
		return repository.getMemberPay(startTime, endTime, ascription);
	}
	
	
	public Long countMember(String time, Integer ascription)
	{
	    return aggregation().eq("ascription", ascription).count("pkey")
				.eq("pType", MemberPType.ANNUAL_FEE)
				.eq("status", PayStatus.PAYMENT_SUCCESSFUL)
				.between("payTime", time + " 00:00:00", time + " 23:59:59")
				.execCount();
	}
	
	public BigDecimal sumAmt(String time, Integer ascription)
	{
	    Number execSum = aggregation().eq("ascription", ascription).between("payTime", time + " 00:00:00", time + " 23:59:59").execSum("amt");
	    if(execSum != null)
	    {
	        return BigDecimal.valueOf(execSum.doubleValue());
	    }
        return BigDecimal.ZERO;
	}
	
}
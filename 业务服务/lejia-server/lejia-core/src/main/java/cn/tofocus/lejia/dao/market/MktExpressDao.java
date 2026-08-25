package cn.tofocus.lejia.dao.market;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.PageUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.file.DataSourceWithFileUrl;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.market.MktExpress;
import cn.tofocus.lejia.bean.enums.ExpressStatus;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.repository.market.MktExpressRepository;
import lombok.extern.slf4j.Slf4j;

import static cn.tofocus.core.query.exp.ExpUtil.f;
import static cn.tofocus.core.query.exp.ExpUtil.substring;

@Slf4j
@Component
@DataSourceWithFileUrl
public class MktExpressDao extends JpaSpecificationDelegate<Integer,MktExpress>
{
	@Autowired
	private MktExpressRepository repository;
	public PageResult<MktExpress> findExpressOrderorName(int page, int pagesize, ExpressStatus status,
			String startTime, String endTime, String courierName, String orderId, String marketPkey, Integer ascription)
	{
		
		if(StringUtils.isNotBlank(endTime))
		{
			try {
				Date date = DateUtil.formatDateStr(endTime);
			    Calendar calendar = new GregorianCalendar(); 
			    calendar.setTime(date); 
			    calendar.add(Calendar.DATE, 1);
			    date = calendar.getTime(); 
			    endTime = DateUtil.formatDate(date);
			    log.info("formatDate: {}", endTime);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else
			endTime = null;
		if(StringUtils.isBlank(startTime))
			startTime = null;
		
		Integer statusIndex = null;
		if(status != null)
			statusIndex = status.getIndex();
		if((Constant.Operation + ascription).equals(marketPkey))
			marketPkey = null;
		List<MktExpress> list = repository.findExpressOrderorName(courierName, orderId, statusIndex, startTime, endTime, marketPkey, ascription);
		return PageUtil.page(list, PageParameter.of(page, pagesize));
	}
	
	public Long getExpressOrder(Integer courier, String qrTime, Integer ascription) 
	{
		return repository.getCountExpress(courier, qrTime, ascription);
	}
	
	
	public List<List<Object>> getExpressCourierCount(String marketPkey, Integer status,
			String startTime, String endTime,
			int page, int pagesize, Integer ascription)
	{
		if(StringUtils.isNotBlank(startTime))
		{
			if(StringUtils.isBlank(endTime))
				endTime = "2100-01-01";
			else
				endTime = endTime + " 23:59:59";
		}
		else
			startTime = null;
		return repository.getExpressCourierCount(marketPkey, status, startTime, endTime, page * pagesize, pagesize, ascription);
	}
	
	// 获取已经到货的订单数量
	public Integer getArrived(String companyPkey, Date time, Integer ascription)
	{
		 List<MktExpress> exec = select()
	        .eq("status", ExpressStatus.EXPRESS_ARRIVED)
	        .eq("company", companyPkey)
	        .eq("ascription", ascription)
	        .eq(substring(f("qrTime"), 1, 10), DateUtil.formatDate(time, "yyyy-MM-dd")).exec();
		 if(exec == null)
			 return 0;
		 return exec.size();
	}
	// 获取已派单的订单数量
	public Integer getOrder(String companyPkey, Date time, Integer ascription)
	{
		 List<MktExpress> exec = select()
	        .eq("status", ExpressStatus.EXPRESS_ORDER)
	        .eq("ascription", ascription)
	        .eq("company", companyPkey)
	        .eq(substring(f("pdTime"), 1, 10), DateUtil.formatDate(time, "yyyy-MM-dd")).exec();
		 if(exec == null)
			 return 0;
		 return exec.size();
	}
	// 获取已揽货的订单数量
	public Integer getGoods(String companyPkey, Date time, Integer ascription)
	{
		 List<MktExpress> exec = select()
	        .eq("status", ExpressStatus.EXPRESS_GOODS)
	        .eq("company", companyPkey)
	        .eq("ascription", ascription)
	        .eq(substring(f("jdTime"), 1, 10), DateUtil.formatDate(time, "yyyy-MM-dd")).exec();
		 if(exec == null)
			 return 0;
		 return exec.size();
	}
}


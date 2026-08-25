package cn.tofocus.lejia.dao.market;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.market.MktOriTest;

@Component
public class MktOriTestDao extends JpaSpecificationDelegate<Integer, MktOriTest> {

	public PageResult<MktOriTest> queryOriTest(int page, int pagesize, String merchant, Date startDate, Date endDate,
			String goods, String entry, Boolean testResult, String marketPkey) {

		SelectPageBuilder<Integer, MktOriTest> builder = selectPage()
				.page(page)
				.pagesize(pagesize)
				.eq("farmer", marketPkey)
				.sort("createdTime", true);
		if (StringUtils.isNotBlank(entry))
			builder.like("entry", entry);
		if (StringUtils.isNotBlank(merchant))
			builder.like("merchant", merchant);
		if (StringUtils.isNotBlank(goods))
			builder.like("goods", goods);
		if (testResult != null)
			builder.eq("testResult", testResult);
		if (startDate != null)
			builder.ge("testDate", startDate);
		if (endDate != null) {
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(endDate);
			calendar.add(Calendar.DATE, 1);
			endDate = calendar.getTime();
			builder.le("testDate", endDate);
		}
		return builder.exec();
	}
}

package cn.tofocus.lejia.dao.market;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.market.MktAdvise;

@Component
public class MktAdviseDao extends JpaSpecificationDelegate<Integer,MktAdvise>
{
	public PageResult<MktAdvise> queryAdviset(int page, int pagesize, String mobile, String marketPkey, Integer ascription)
	{
		SelectPageBuilder<Integer,MktAdvise> builder = selectPage()
				.page(page)
				.pagesize(pagesize)
				.eq("ascription", ascription)
				.sort("createdTime", true);
		if(StringUtils.isNotBlank(mobile))
			builder.like("mobile", mobile);
		if(StringUtils.isNotBlank(marketPkey))
			builder.eq("farmer", marketPkey);
		return builder.exec();
	}
}
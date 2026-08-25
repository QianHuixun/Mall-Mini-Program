package cn.tofocus.lejia.dao.market;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.market.MktLogistics;

@Component
public class MktLogisticsDao extends JpaSpecificationDelegate<Integer,MktLogistics>
{
	public MktLogistics getLogistics(Integer pkey)
	{
		return selectOne().eq("pkey", pkey).eq("idDel", false).exec();
	}
	
	public PageResult<MktLogistics> queryLogistics(int page, int pagesize, String logisticsName, Boolean enabled, Integer ascription) 
	{
		SelectPageBuilder<Integer,MktLogistics> builder = selectPage()
				.page(page)
				.pagesize(pagesize)
				.eq("ascription", ascription)
				.eq("idDel", false)
				.sort("pkey", true);
		if(StringUtils.isBlank(logisticsName))
			builder.like("name", logisticsName);
		if(enabled != null)
			builder.eq("enabled", enabled);
		return builder.exec();
	}
}
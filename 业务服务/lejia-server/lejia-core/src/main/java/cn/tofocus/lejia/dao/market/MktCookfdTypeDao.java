package cn.tofocus.lejia.dao.market;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.market.MktCookfdType;
import cn.tofocus.lejia.Constant;

@Component
public class MktCookfdTypeDao extends JpaSpecificationDelegate<Integer, MktCookfdType> 
{
	
	public PageResult<MktCookfdType> queryCookfd(int page, int pagesize, String name, Boolean enabled, String marketPkey, Integer ascription)
	{
		SelectPageBuilder<Integer,MktCookfdType> builder = selectPage()
								.page(page)
								.pagesize(pagesize)
								.eq("idDel", false)
								.sort("sort", true);
		if(!(Constant.Operation + ascription).equals(marketPkey))
			builder.eq("farmer", marketPkey);
		if(StringUtils.isNotBlank(name))
			builder.like("name", name);
		if(enabled != null)
			builder.eq("enabled", enabled);
		return builder.exec();
	}
	
	public MktCookfdType getCookfdType(Integer pkey)
	{
		return selectOne().eq("pkey", pkey).eq("idDel", false).exec();
	}
	
	public List<MktCookfdType> listCookfdType(String farmer)
	{
		List<MktCookfdType> exec = select().eq("idDel", false).eq("enabled", true).eq("farmer", farmer).sort("sort", true).exec();
		return exec;
	}
}
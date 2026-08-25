package cn.tofocus.lejia.dao.market;

import cn.tofocus.lejia.bean.entity.market.MktAdvert;
import cn.tofocus.lejia.bean.enums.AdvertPosition;
import cn.tofocus.lejia.bean.enums.AdvertType;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.file.DataSourceWithFileUrl;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import org.springframework.stereotype.Component;

@Component
@DataSourceWithFileUrl
public class MktAdvertDao extends JpaSpecificationDelegate<Integer, MktAdvert> 
{
	public PageResult<MktAdvert> queryAdvert(int page, int pagesize, AdvertPosition position, String marketPkey, AdvertType type) 
	{
		SelectPageBuilder<Integer, MktAdvert> builder = selectPage()
				.page(page)
				.pagesize(pagesize)
				.eq("farmer", marketPkey)
				.eq("type", type)
				.sort("sort", true);
		if (position != null)
			builder.eq("position", position);
		return builder.exec();
	}
}
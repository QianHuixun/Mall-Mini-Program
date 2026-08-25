package cn.tofocus.lejia.dao.market;

import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.file.DataSourceWithFileUrl;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.market.MktOrderGroup;
import cn.tofocus.lejia.bean.enums.OrderGroupStatus;

@Component
@DataSourceWithFileUrl
public class MktOrderGroupDao extends JpaSpecificationDelegate<Integer,MktOrderGroup>
{
	public PageResult<MktOrderGroup> queryOrder(int page, int pagesize, Integer goods,
			OrderGroupStatus status, List<Integer> goodsIds) {
		SelectPageBuilder<Integer,MktOrderGroup> builder = selectPage().page(page).pagesize(pagesize).sort("pkey", true);
		if(goodsIds.size() > 0)
			builder.in("goods", goodsIds.toArray());
		else
			builder.isNull("goods");
		if(goods != null)
			builder.eq("goods", goods);
		if(status != null)
			builder.eq("status", status);
		return builder.exec();
	}
}
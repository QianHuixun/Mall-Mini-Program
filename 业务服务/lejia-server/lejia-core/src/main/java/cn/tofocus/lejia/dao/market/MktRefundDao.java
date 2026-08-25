package cn.tofocus.lejia.dao.market;

import cn.tofocus.lejia.bean.entity.market.MktRefund;
import cn.tofocus.lejia.bean.enums.RefundStatus;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import org.springframework.stereotype.Component;

@Component
public class MktRefundDao extends JpaSpecificationDelegate<Integer, MktRefund>
{
	public PageResult<MktRefund> queryRefund(int page, int pagesize, String code, RefundStatus status, Integer ascription) 
	{
		SelectPageBuilder<Integer, MktRefund> builder = selectPage()
				.page(page)
				.pagesize(pagesize)
				.eq("ascription", ascription)
				.sort("pkey", true);
		if (code != null)
			builder.like("code", code);
		if (status != null)
			builder.eq("status", status);
		return builder.exec();
	}
}
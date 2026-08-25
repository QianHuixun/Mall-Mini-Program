package cn.tofocus.lejia.dao.market;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.market.MktCourier;
import cn.tofocus.lejia.Constant;

@Component
public class MktCourierDao extends JpaSpecificationDelegate<Integer,MktCourier>
{
	public MktCourier addMktCourier(String name, String mobile, String marketPkey, String companyPkey, Integer ascription)
	{
		MktCourier courier = new MktCourier();
		courier.setName(name);
		courier.setMobile(mobile);
		courier.setRowVension(1);
		courier.setIdDel(false);
		courier.setEnabled(true);
		courier.setFarmer(marketPkey);
		courier.setCompany(companyPkey);
		courier.setAscription(ascription);
		return add(courier);
	}
	
	public PageResult<MktCourier> queryCourier(int page, int pagesize, String courierName, String courierMobile, Boolean enabled,String marketPkey, Integer ascription)
	{
		SelectPageBuilder<Integer,MktCourier> builder = selectPage()
				.page(page)
				.pagesize(pagesize)
				.eq("ascription", ascription)
				.eq("idDel", false)
				.sort("pkey", true);
		if(!(Constant.Operation + ascription).equals(marketPkey))
			builder.eq("farmer", marketPkey);
		if(StringUtils.isNotBlank(courierName))
			builder.like("name",courierName);
		if(StringUtils.isNotBlank(courierMobile))
			builder.like("mobile", courierMobile);
		if(enabled != null)
			builder.eq("enabled", enabled);
		return builder.exec();
	}
	
	public MktCourier getOne(Integer pkey)
	{
		return selectOne().eq("pkey", pkey).eq("idDel", false).exec();
	}
}
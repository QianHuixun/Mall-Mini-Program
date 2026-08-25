package cn.tofocus.lejia.domain.market;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.ExpressExportExcel;
import cn.tofocus.lejia.bean.dto.market.MktExpressOnList;
import cn.tofocus.lejia.bean.entity.market.MktCourier;
import cn.tofocus.lejia.bean.entity.market.MktExpress;
import cn.tofocus.lejia.bean.entity.sys.SysCompany;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.enums.ExpressStatus;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.market.MktCourierDao;
import cn.tofocus.lejia.dao.market.MktExpressDao;
import cn.tofocus.lejia.dao.sys.SysCompanyDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;

@Component
public class MktExpressManager 
{
	@Autowired
	private MktExpressDao expressdao;
	@Autowired
	private MktCourierDao courierDao;
	@Autowired
	private SysCompanyDao companyDao;
	@Autowired
	private SysFarmerDao farmerDao;
	
	public PageResult<MktExpressOnList> queryExpress(int page, int pagesize, ExpressStatus status,
			String startTime, String endTime, String courierName, String orderId) {
		PageResult<MktExpress> list = expressdao.findExpressOrderorName(page, pagesize, status, startTime, endTime, courierName, orderId, CurrentSession.marketPkey(), CurrentSession.ascriptionPkey());
		PageResult<MktExpressOnList> result = BeanUtil.beanPageFrom(MktExpressOnList.class, list);
		for(MktExpressOnList eo : result.getContent())
		{
			MktCourier courier = courierDao.get(eo.getCourier());
			if(courier != null)
				eo.setCourierName(courier.getName());
			eo.setStatusName(eo.getStatus().getName());
			SysCompany company = companyDao.get(eo.getCompany());
			if(company != null)
				eo.setCompanyName(company.getName());
			SysFarmer farmer = farmerDao.get(eo.getFarmer());
			if(farmer != null)
				eo.setFarmerName(farmer.getName());
			
		}
		return result;
	}
	
	public List<ExpressExportExcel> exportexcel()
	{
	    Integer ascription = CurrentSession.ascriptionPkey();
	    List<ExpressExportExcel> list = expressdao.select().eq("ascription", ascription).execDto(ExpressExportExcel.class);
	    Map<String, String> map = farmerDao.findNameMap(ascription);
	    for(ExpressExportExcel e : list)
	    {
	        if(map.containsKey(e.getFarmer()))
	            e.setFarmerName(map.get(e.getFarmer()));
	    }
	    return list;
	}
}

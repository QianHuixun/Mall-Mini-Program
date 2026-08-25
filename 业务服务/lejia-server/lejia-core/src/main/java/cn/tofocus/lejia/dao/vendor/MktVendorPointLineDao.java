package cn.tofocus.lejia.dao.vendor;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorPointLine;
import cn.tofocus.lejia.bean.enums.SourceType;
import cn.tofocus.lejia.repository.market.MktVendorPointLineRepository;

@Component
public class MktVendorPointLineDao extends JpaSpecificationDelegate<Integer, MktVendorPointLine> 
{
	@Autowired
	private MktVendorPointLineRepository repository;
	
    public PageResult<MktVendorPointLine> queryPageResult(Integer page, Integer pagesize, Integer venderPkey) {
        PageResult<MktVendorPointLine> pageResult = selectPage()
                .page(page)
                .pagesize(pagesize)
                .eq("vendor", venderPkey)
                .sort("createdTime", true).exec();
        return pageResult;
    }
    
    public PageResult<MktVendorPointLine> queryVendorPointLine(int page, int pagesize, Integer vendor,
			SourceType source, String startDate, String endDate, List<Integer> pkeys) 
    {
    	SelectPageBuilder<Integer, MktVendorPointLine> builder = selectPage().page(page)
				.pagesize(pagesize).sort("createdTime", true);
    	if(pkeys.size() > 0)
    	{
    	    builder.in("vendor", pkeys.toArray());
    	}
		if (vendor != null)
			builder.eq("vendor", vendor);
		if (source != null)
			builder.eq("source", source);
		if (StringUtils.isNotBlank(startDate))
			builder.ge("createdTime", startDate);
		if (StringUtils.isNotBlank(endDate))
			builder.le("createdTime", endDate + " 23:59:59");
		return builder.exec();
    }
    
    public List<List<Object>> getVendorSales(@Param("vendorName") String vendorName, 
			@Param("startTime") String startTime, @Param("endTime") String endTime,
			@Param("page") int page,  @Param("pagesize") int pagesize, Integer ascription)
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
    	return repository.vendorSales(vendorName, startTime, endTime, page * pagesize, pagesize, ascription);
    }


}

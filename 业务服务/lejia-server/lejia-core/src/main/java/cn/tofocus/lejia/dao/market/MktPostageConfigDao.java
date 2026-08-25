package cn.tofocus.lejia.dao.market;

import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.market.MktPostageConfig;
import cn.tofocus.lejia.repository.market.MktPostageConfigRepository;

@Component
public class MktPostageConfigDao extends JpaSpecificationDelegate<Integer, MktPostageConfig> 
{
	@Autowired
	private MktPostageConfigRepository repository;
	
	public List<MktPostageConfig> queryPostageConfig(String marketPkey, String companyPkey)
	{
		return select().eq("farmer", marketPkey).eq("company", companyPkey).sort("pkey", false).sort("createdTime", false).exec();
	}
	
	public List<List<Object>> getPostageCount(String startTime, String endTime,
			int page, int pagesize)
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
		List<List<Object>> sales = repository.getPostageCount(startTime, endTime, page * pagesize, pagesize);
		return sales;
	}
	
}


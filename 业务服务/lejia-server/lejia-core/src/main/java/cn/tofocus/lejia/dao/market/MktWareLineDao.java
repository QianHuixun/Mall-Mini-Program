package cn.tofocus.lejia.dao.market;



import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.dto.market.WareAggreDTO;
import cn.tofocus.lejia.bean.entity.market.MktWareLine;
import cn.tofocus.lejia.bean.enums.WareType;
import cn.tofocus.lejia.core.CurrentSession;

@Component
public class MktWareLineDao extends JpaSpecificationDelegate<Integer,MktWareLine>
{
	
	public PageResult<MktWareLine> queryWare(int page, int pagesize, WareType type, Integer goodsPkey) {
		SelectPageBuilder<Integer,MktWareLine> builder = selectPage().page(page).pagesize(pagesize)
		    .eq("ascription", CurrentSession.ascriptionPkey())
		    .sort("createdTime", true);
		if(type != null)
			builder.eq("wareType", type);
		if(goodsPkey != null)
			builder.eq("goods", goodsPkey);
		return builder.exec();
	}
	
	
	public List<WareAggreDTO> listWareAggre(Integer goodsPkey)
	{
		PageResult<WareAggreDTO> exec = aggregation().sum("num", "num").eq("goods", goodsPkey).groupby("wareType", "wareType").exec(WareAggreDTO.class);
		return exec.getContent();
	}
}

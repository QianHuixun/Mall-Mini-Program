package cn.tofocus.lejia.domain.market;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.MktAdviseOnList;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.market.MktAdviseDao;
import cn.tofocus.lejia.dao.market.MktMemberDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;

@Component
public class AdviseManager 
{
	@Autowired
    private MktAdviseDao mktAdviseDao;
	@Autowired
    private MktMemberDao mktMemberDao;
	@Autowired
    private SysFarmerDao farmerDao;

	public PageResult<MktAdviseOnList> queryAdviset(int page, int pagesize, String mobile) {
		String marketPkey = CurrentSession.marketPkey();
		Integer ascription = CurrentSession.ascriptionPkey();
		if((Constant.Operation + ascription).equals(marketPkey))
			marketPkey = null;
		PageResult<MktAdviseOnList> result = BeanUtil.beanPageFrom(MktAdviseOnList.class, mktAdviseDao.queryAdviset(page, pagesize, mobile, marketPkey, ascription));
		List<MktAdviseOnList> content = result.getContent();
		for(MktAdviseOnList bean : content)
		{
		    MktMember member = mktMemberDao.selectOne().eq("pkey", bean.getMember()).exec();
		    if(member != null)
		    {
		        bean.setMemberName(member.getName());
		    }
            SysFarmer farmer = farmerDao.get(bean.getFarmer());
            if (farmer != null) bean.setFarmerName(farmer.getName());
		}
		
		return result;
	}
	
	public Boolean delAdvise(Integer pkey) {
		return mktAdviseDao.removeById(pkey);
	}


}

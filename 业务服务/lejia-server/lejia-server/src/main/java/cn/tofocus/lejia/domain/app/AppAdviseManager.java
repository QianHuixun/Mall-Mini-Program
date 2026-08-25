package cn.tofocus.lejia.domain.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.lejia.bean.dto.app.market.AppAdviseDetailsDTO;
import cn.tofocus.lejia.bean.entity.market.MktAdvise;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.market.MktAdviseDao;

@Component
public class AppAdviseManager
{
	@Autowired
    private MktAdviseDao mktAdviseDao;

	public AppAdviseDetailsDTO insAppAdvise(String content){
		MktAdvise advise = new MktAdvise();
		MktMember member = MobileSession.member();
		advise.setMember(member.getPkey());
		advise.setMobile(member.getMobile());
		advise.setContent(content);
		advise.setFarmer(MobileSession.farmerPkey());
		advise.setAscription(MobileSession.appid());
		advise = mktAdviseDao.add(advise);
		return BeanUtil.beanFrom(AppAdviseDetailsDTO.class, advise);
	}

}

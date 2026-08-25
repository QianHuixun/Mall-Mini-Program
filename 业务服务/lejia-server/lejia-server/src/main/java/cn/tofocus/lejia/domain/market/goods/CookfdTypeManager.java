package cn.tofocus.lejia.domain.market.goods;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.MktCookfdTypeOnList;
import cn.tofocus.lejia.bean.entity.market.MktCookfd;
import cn.tofocus.lejia.bean.entity.market.MktCookfdType;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.market.MktCookfdDao;
import cn.tofocus.lejia.dao.market.MktCookfdTypeDao;
import cn.tofocus.lejia.exception.WsaleErrCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CookfdTypeManager 
{
    @Autowired
    private MktCookfdTypeDao cookfdTypeDao;
    
    @Autowired
    private MktCookfdDao cookfdDao;
    
	public Integer insCookfdType(String name, int sort) {
		MktCookfdType bean = new MktCookfdType();
		bean.setFarmer(CurrentSession.marketPkey());
		bean.setCompany(CurrentSession.companyPkey());
		bean.setAscription(CurrentSession.ascriptionPkey());
		bean.setName(name);
		bean.setSort(sort);
		bean.setRowVension(1);
		bean.setEnabled(true);
		bean.setIdDel(false);
		MktCookfdType add = cookfdTypeDao.add(bean);
		return add.getPkey();
	}

	
	public PageResult<MktCookfdTypeOnList> queryCookfdType(int page, int pagesize, String name, Boolean enabled) 
	{
		return BeanUtil.beanPageFrom(MktCookfdTypeOnList.class, cookfdTypeDao.queryCookfd(page, pagesize, name, enabled, CurrentSession.marketPkey(), CurrentSession.ascriptionPkey()));
	}

	
	public Boolean updCookfdType(MktCookfdTypeOnList entity) 
	{
		MktCookfdType cookfdType = cookfdTypeDao.get(entity.getPkey());
		if(cookfdType == null)
			throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
		BeanUtils.copyProperties(entity, cookfdType, "enabled");
		if(entity.getSort() == null)
            throw TofocusException.of(WsaleErrCode.SORT_NOT_EMPTY);
		cookfdTypeDao.update(cookfdType);
		return true;
	}

	
	public Boolean delCookfdType(Integer pkey) 
	{
		log.info("pkey: {}", pkey);
		MktCookfdType cookfdType = cookfdTypeDao.getCookfdType(pkey);
        if (cookfdType.getEnabled())
            throw TofocusException.of(WsaleErrCode.NOT_DELETED);
        cookfdType.setIdDel(true);
        cookfdTypeDao.update(cookfdType);
        return true;
	}

	
	public Boolean enabledCookfdType(Integer pkey, Boolean flag) {
		log.info("pkey: {}", pkey);
		MktCookfdType cookfdType = cookfdTypeDao.getCookfdType(pkey);
		if(cookfdType == null)
			throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
		if(!flag)
		{
		    List<MktCookfd> exec = cookfdDao.select().eq("idDel", false).eq("ctype", pkey).exec();
		    if(exec != null && exec.size() > 0)
		        throw TofocusException.of(WsaleErrCode.COOKFD_TYPE_USE);
		}
		cookfdType.setEnabled(flag);
        cookfdTypeDao.update(cookfdType);
        return true;
	}

	
}

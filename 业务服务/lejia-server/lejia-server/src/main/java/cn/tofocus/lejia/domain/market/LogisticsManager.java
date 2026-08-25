package cn.tofocus.lejia.domain.market;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.MktLogisticsOnList;
import cn.tofocus.lejia.bean.entity.market.MktLogistics;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.market.MktLogisticsDao;
import cn.tofocus.lejia.exception.WsaleErrCode;

@Component
public class LogisticsManager 
{
	@Autowired
    private MktLogisticsDao logisticsDao;
    
	public MktLogisticsOnList insLogistics(MktLogisticsOnList entity) {
		MktLogistics logistics = BeanUtil.beanFrom(MktLogistics.class, entity);
		logistics.setRowVension(1);
		logistics.setIdDel(false);
		logistics.setAscription(CurrentSession.ascriptionPkey());
		if(entity.getEnabled() == null)
			logistics.setEnabled(true);
		MktLogistics add = logisticsDao.add(logistics);
		return BeanUtil.beanFrom(MktLogisticsOnList.class,add);
	}

	public MktLogisticsOnList getLogistics(Integer pkey) {
		MktLogistics logistics = logisticsDao.getLogistics(pkey);
		return BeanUtil.beanFrom(MktLogisticsOnList.class, logistics);
	}

	public PageResult<MktLogisticsOnList> queryLogistics(int page, int pagesize, String logisticsName, Boolean enabled) {
		PageResult<MktLogistics> pageResult = logisticsDao.queryLogistics(page, pagesize, logisticsName, enabled, CurrentSession.ascriptionPkey());
		return BeanUtil.beanPageFrom(MktLogisticsOnList.class, pageResult);
	}

	public MktLogisticsOnList updLogistics(Integer pkey, String name, String descp) {
		MktLogistics logistics = logisticsDao.getLogistics(pkey);
		if(logistics == null)
			throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
		if(StringUtils.isNotBlank(name))
			logistics.setName(name);
		logistics.setDescp(descp);
		MktLogistics update = logisticsDao.update(logistics);
		return BeanUtil.beanFrom(MktLogisticsOnList.class, update);
	}

	public Boolean delLogistics(Integer pkey) {
		MktLogistics logistics = logisticsDao.getLogistics(pkey);
		if(logistics == null)
			throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
		if(logistics.getEnabled())
			throw TofocusException.of(WsaleErrCode.NOT_DELETED);
		logistics.setIdDel(true);
		MktLogistics update = logisticsDao.update(logistics);
		if(update == null)
			return false;
		return true;
	}
	
	public Boolean enableLogistics(Integer pkey,Boolean enable)
	{
		MktLogistics logistics = logisticsDao.getLogistics(pkey);
		if(logistics == null)
			throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
		logistics.setEnabled(enable);
		MktLogistics update = logisticsDao.update(logistics);
		if(update == null)
			return false;
		return true;
	}
	
	
}

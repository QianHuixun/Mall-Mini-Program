package cn.tofocus.lejia.domain.market;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.lejia.bean.dto.market.MktOriVenOnList;
import cn.tofocus.lejia.bean.entity.market.MktOriVen;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.market.MktOriVenDao;
import cn.tofocus.lejia.exception.WsaleErrCode;

@Component
public class OriVenManager 
{
	@Autowired
	private MktOriVenDao oriVenDao;
	
	public MktOriVenOnList insOriVen(MktOriVenOnList entity) {
		MktOriVen oriVen = BeanUtil.beanFrom(MktOriVen.class, entity);
		oriVen.setRowVension(1);
		oriVen.setCompany(CurrentSession.companyPkey());
		oriVen.setFarmer(CurrentSession.marketPkey());
		oriVen.setAscription(CurrentSession.ascriptionPkey());
		MktOriVen add = oriVenDao.add(oriVen);
		return BeanUtil.beanFrom(MktOriVenOnList.class, add);
	}

	
	public MktOriVenOnList getOriVen(Integer pkey) {
		MktOriVen oriVen = oriVenDao.get(pkey);
		if(oriVen == null)
			throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
		return BeanUtil.beanFrom(MktOriVenOnList.class, oriVen);
	}

	
	public PageResult<MktOriVenOnList> queryOriVen(int page, int pagesize, String merchant, String goods,
			String vendor, Boolean flag) {
		String farmer = "";
		if(flag)
			farmer = CurrentSession.marketPkey();
		else
			farmer = MobileSession.farmerPkey();
		SelectPageBuilder<Integer,MktOriVen> builder = oriVenDao.selectPage()
		.page(page)
		.pagesize(pagesize)
		.eq("farmer", farmer)
		.sort("pkey", true);
		if(StringUtils.isNotBlank(vendor))
			builder.like("vendor", vendor);
		if(StringUtils.isNotBlank(merchant))
			builder.like("merchant", merchant);
		if(StringUtils.isNotBlank(goods))
			builder.like("goods", goods);
		PageResult<MktOriVen> pageResult = builder.exec();
		return BeanUtil.beanPageFrom(MktOriVenOnList.class, pageResult);
	}

	
	public MktOriVenOnList updOriVen(MktOriVenOnList entity) {
		MktOriVen oriVen = oriVenDao.get(entity.getPkey());
		if(oriVen == null)
			throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
		if(StringUtils.isNotBlank(entity.getVendor()))
			oriVen.setVendor(entity.getVendor());
		if(StringUtils.isNotBlank(entity.getMerchant()))
			oriVen.setMerchant(entity.getMerchant());
		if(StringUtils.isNotBlank(entity.getGoods()))
			oriVen.setGoods(entity.getGoods());
		if(entity.getOriDate() != null)
			oriVen.setOriDate(entity.getOriDate());
		MktOriVen update = oriVenDao.update(oriVen);
		return BeanUtil.beanFrom(MktOriVenOnList.class, update);
	}

	
	public Boolean delOriVen(Integer pkey) {
		return oriVenDao.removeById(pkey);
	}
	
	public Boolean importExcel(List<MktOriVen> list)
	{
		List<MktOriVen> addAll = oriVenDao.addAll(list);
		if(addAll == null)
			return false;
		return true;
	}
	
	
	
}

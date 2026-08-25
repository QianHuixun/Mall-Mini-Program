package cn.tofocus.lejia.domain.market;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.MktOriTestOnList;
import cn.tofocus.lejia.bean.entity.market.MktOriTest;
import cn.tofocus.lejia.bean.entity.sys.SysUser;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.market.MktOriTestDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.dao.sys.SysUserDao;
import cn.tofocus.lejia.exception.WsaleErrCode;

@Component
public class OriTestManager 
{
	@Autowired
	private MktOriTestDao oriTestDao;
	@Autowired
	private SysFarmerDao sysFarmerDao;
	@Autowired
	private SysUserDao sysUserDao;
	
	public MktOriTestOnList insOriTest(MktOriTestOnList entity) {
		MktOriTest oriTest = BeanUtil.beanFrom(MktOriTest.class, entity);
		oriTest.setRowVension(1);
		oriTest.setCompany(CurrentSession.companyPkey());
		oriTest.setFarmer(CurrentSession.marketPkey());
		oriTest.setAscription(CurrentSession.ascriptionPkey());
		MktOriTest add = oriTestDao.add(oriTest);
		return BeanUtil.beanFrom(MktOriTestOnList.class, add);
	}

	
	public MktOriTestOnList getOriTest(Integer pkey) {
		MktOriTest oriTest = oriTestDao.get(pkey);
		if(oriTest == null)
			throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
		return BeanUtil.beanFrom(MktOriTestOnList.class, oriTest);
	}

	
	public PageResult<MktOriTestOnList> queryOriTest(int page, int pagesize, String merchant,
			Date startDate, Date endDate, String goods, String entry, Boolean testResult, Boolean flag) {
		String farmer = "";
		if(flag)
			farmer = CurrentSession.marketPkey();
		else
			farmer = MobileSession.farmerPkey();
		PageResult<MktOriTest> pageResult = oriTestDao.queryOriTest(page, pagesize, merchant, startDate, endDate, 
				goods, entry, testResult, farmer);
		PageResult<MktOriTestOnList> result = BeanUtil.beanPageFrom(MktOriTestOnList.class, pageResult);
		assembleMktOriTestOnList(result.getContent());
		return result;
	}
	private void assembleMktOriTestOnList(List<MktOriTestOnList> list) 
	{
		for(MktOriTestOnList bean : list)
		{
			bean.setFarmer(sysFarmerDao.get(bean.getFarmer()).getName());
			if(bean.getCreatedBy() != null)
			{
			    SysUser sysUser = sysUserDao.get(bean.getCreatedBy());
			    if(sysUser != null)
			        bean.setCreatedByName(sysUser.getNickname());
			}
		}
	}

	public MktOriTestOnList updOriTest(Integer pkey, String merchant, String goods, String entry,
			Boolean testResult) {
		MktOriTest oriTest = oriTestDao.get(pkey);
		if(oriTest == null)
			throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
		if(StringUtils.isNotBlank(entry))
			oriTest.setEntry(entry);
		if(StringUtils.isNotBlank(merchant))
			oriTest.setMerchant(merchant);
		if(StringUtils.isNotBlank(goods))
			oriTest.setGoods(goods);
		if(testResult != null)
			oriTest.setTestResult(testResult);
		MktOriTest update = oriTestDao.update(oriTest);
		return BeanUtil.beanFrom(MktOriTestOnList.class, update);
	}

	
	public Boolean delOriTest(Integer pkey) {
		return oriTestDao.removeById(pkey);
	}
	
	public Boolean importExcel(List<MktOriTest> list)
	{
		List<MktOriTest> addAll = oriTestDao.addAll(list);
		if(addAll == null)
			return false;
		return true;
	}
	
	
}

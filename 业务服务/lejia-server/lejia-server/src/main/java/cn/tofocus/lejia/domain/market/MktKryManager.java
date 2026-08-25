package cn.tofocus.lejia.domain.market;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.lejia.bean.dto.market.MktKryOrderOnList;
import cn.tofocus.lejia.bean.dto.market.MktKryVendorOnList;
import cn.tofocus.lejia.bean.entity.market.MktKryVendor;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.market.MktKryOrderDao;
import cn.tofocus.lejia.dao.market.MktKryVendorDao;
import cn.tofocus.lejia.exception.WsaleErrCode;

@Component
public class MktKryManager {
	@Autowired
	private MktKryOrderDao kryOrderDao;
	@Autowired
	private MktKryVendorDao kryVendorDao;

	public MktKryVendorOnList insKryVendor(long uuid, String name, String mobile, String manager, String token) {
	    Integer ascription = CurrentSession.ascriptionPkey();
		MktKryVendor entity = new MktKryVendor();
		entity.setName(name);
		entity.setMobile(mobile);
		entity.setUuid(uuid);
		entity.setManager(manager);
		entity.setToken(token);

		entity.setAscription(ascription);
		entity.setRowVension(1);
		entity.setIdDel(false);
		entity.setEnabled(true);
		MktKryVendor add = kryVendorDao.add(entity);
		return BeanUtil.beanFrom(MktKryVendorOnList.class, add);
	}

	public PageResult<MktKryVendorOnList> queryKryVendor(int page, int pagesize, String name) {
	    Integer ascription = CurrentSession.ascriptionPkey();
		SelectPageBuilder<Integer, MktKryVendor> builder = kryVendorDao.selectPage().page(page).pagesize(pagesize)
		    .eq("ascription", ascription)
				.eq("idDel", false).sort("pkey", true);
		if (StringUtils.isNotBlank(name))
			builder.like("name", name);
		return BeanUtil.beanPageFrom(MktKryVendorOnList.class, builder.exec());
	}

	public MktKryVendorOnList updKryVendor(Integer pkey, long uuid, String name, String mobile, String manager,
			String token) {
		MktKryVendor kryVendor = kryVendorDao.get(pkey);
		if (StringUtils.isNotBlank(name))
			kryVendor.setName(name);
		kryVendor.setUuid(uuid);
		if (StringUtils.isNotBlank(mobile))
			kryVendor.setMobile(mobile);
		if (StringUtils.isNotBlank(manager))
			kryVendor.setManager(manager);
		if (StringUtils.isNotBlank(token))
			kryVendor.setToken(token);
		MktKryVendor update = kryVendorDao.update(kryVendor);
		return BeanUtil.beanFrom(MktKryVendorOnList.class, update);
	}

	public Boolean delKryVendor(Integer pkey) {
		MktKryVendor kryVendor = kryVendorDao.get(pkey);
		if (kryVendor.getEnabled())
			throw TofocusException.of(WsaleErrCode.NOT_DELETED);
		kryVendor.setIdDel(true);
		kryVendorDao.update(kryVendor);
		return true;
	}

	public Boolean enabledKryVendor(Integer pkey, Boolean flag) {
		MktKryVendor kryVendor = kryVendorDao.getVendor(pkey);
		if (kryVendor.getIdDel())
			throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
		kryVendor.setEnabled(flag);
		kryVendorDao.update(kryVendor);
		return true;
	}

	public PageResult<MktKryOrderOnList> queryKryOrder(int page, int pagesize, String name, String startDate,
			String endDate) {
	    Integer ascription = CurrentSession.ascriptionPkey();
		PageResult<MktKryOrderOnList> result = BeanUtil.beanPageFrom(MktKryOrderOnList.class, 
				kryOrderDao.queryKryOrder(page, pagesize, name, startDate, endDate, ascription));
		List<MktKryOrderOnList> content = new ArrayList<>();
		content.addAll(result.getContent());
		for(MktKryOrderOnList bean : content)
		{
			MktKryVendor vendor = kryVendorDao.selectOne().eq("uuid", bean.getUuid()).exec();
			bean.setVendorName(vendor.getName());
		}
		Iterator<MktKryOrderOnList> iterator = content.iterator();
		while (iterator.hasNext()) {
			MktKryOrderOnList next = iterator.next();
			if (StringUtils.isNotBlank(name))
				if (!next.getVendorName().contains(name))
					iterator.remove();
		}
		result.setContent(content);
		return result;
	}
}

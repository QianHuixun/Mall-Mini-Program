package cn.tofocus.lejia.dao.market;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.market.MktKryVendor;

@Component
public class MktKryVendorDao extends JpaSpecificationDelegate<Integer,MktKryVendor>
{
	public MktKryVendor getVendor(Integer pkey)
	{
		return selectOne().eq("pkey", pkey).eq("idDel", false).exec();
	}
}
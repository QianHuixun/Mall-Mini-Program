package cn.tofocus.lejia.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.db.excel.ExcelImportListener;
import cn.tofocus.lejia.bean.dto.MktGoodsMainExcel;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsMain;
import cn.tofocus.lejia.bean.entity.market.MktGtype;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.goods.MktGoodsMainDao;
import cn.tofocus.lejia.dao.market.MktGtypeDao;
import cn.tofocus.lejia.exception.WsaleErrCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DeviceExcelImportListener implements ExcelImportListener<MktGoodsMainExcel>
{

	@Autowired
    private MktGtypeDao gtypeDao;
	@Autowired
    private MktGoodsMainDao goodsMainDao;
	
	@Override
	public MktGoodsMainExcel check(MktGoodsMainExcel data) 
	{
		log.info("data: {}", data);
		data.setRowVension(1);
		data.setIdDel(false);
		if(data.getEnabled() == null)
		    data.setEnabled(true);
		if(data.getSort() == null)
		    data.setSort(0);
		MktGtype mktGtype = gtypeDao.selectOne()
		    .eq("name", data.getGtypeName())
		    .eq("farmer", CurrentSession.marketPkey())
		    .eq("ascription", CurrentSession.ascriptionPkey()).exec();
		if(mktGtype == null)
			throw TofocusException.of(WsaleErrCode.GOODS_TYPE_NAME_NOTEXIST);
		data.setGtype(mktGtype.getPkey());
		data.setAscription(CurrentSession.ascriptionPkey());
		data.setFarmer(mktGtype.getFarmer());
		MktGoodsMain exec = goodsMainDao.selectOne()
		    .eq("name", data.getName())
		    .eq("gtype", mktGtype.getPkey())
		    .eq("farmer", mktGtype.getFarmer())
		    .eq("ascription", CurrentSession.ascriptionPkey())
		    .exec();
		if(exec != null)
		{
			if(exec.getIdDel())
				data.setPkey(exec.getPkey());
			else
				throw TofocusException.of(WsaleErrCode.COMMODITY_LIBRARY_ALREADY_EXISTS);
			
		}
		return data;
		
	}

}

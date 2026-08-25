package cn.tofocus.lejia.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.db.excel.ExcelImportListener;
import cn.tofocus.lejia.bean.dto.MktGoodsMainExcel;
import cn.tofocus.lejia.bean.dto.MktGoodsMainThreeExcel;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsMain;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsMainThree;
import cn.tofocus.lejia.bean.entity.market.MktGtype;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.goods.MktGoodsMainDao;
import cn.tofocus.lejia.dao.goods.MktGoodsMainThreeDao;
import cn.tofocus.lejia.dao.market.MktGtypeDao;
import cn.tofocus.lejia.exception.WsaleErrCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DeviceThreeExcelImportListener implements ExcelImportListener<MktGoodsMainThreeExcel>
{
	@Autowired
    private MktGoodsMainDao goodsMainDao;
	
	@Autowired
	private MktGoodsMainThreeDao goodsMainThreeDao;
	
	@Override
	public MktGoodsMainThreeExcel check(MktGoodsMainThreeExcel data) 
	{
		log.info("data: {}", data);
//		data.setSort(0);
//		data.setEnabled(true);
		data.setRowVension(1);
		data.setIdDel(false);
		if(data.getEnabled() == null)
		    data.setEnabled(true);
		if(data.getSort() == null)
		    data.setSort(0);
		MktGoodsMain goodsMain = goodsMainDao.selectOne()
		    .eq("name", data.getTwoGtypeName())
		    .eq("farmer", CurrentSession.marketPkey())
		    .eq("ascription", CurrentSession.ascriptionPkey()).exec();
		if(goodsMain == null)
			throw TofocusException.of(WsaleErrCode.TWO_GOODS_MAIN_ERROR);
		data.setTwoGtype(goodsMain.getPkey());
		data.setGtype(goodsMain.getGtype());
		data.setAscription(CurrentSession.ascriptionPkey());
	    data.setFarmer(goodsMain.getFarmer());
		MktGoodsMainThree exec = goodsMainThreeDao.selectOne()
		    .eq("name", data.getName())
		    .eq("twoGtype", goodsMain.getPkey())
		    .eq("gtype", goodsMain.getGtype())
		    .eq("farmer", goodsMain.getFarmer())
		    .eq("ascription", CurrentSession.ascriptionPkey()).exec();
		if(exec != null)
		{
			if(exec.getIdDel())
				data.setPkey(exec.getPkey());
			else
				throw TofocusException.of(WsaleErrCode.TWO_GOODS_MAIN_EXISTS);
		}
		return data;
		
	}

}

package cn.tofocus.lejia.app.v1.market;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.MktOriTestOnList;
import cn.tofocus.lejia.bean.dto.market.MktOriVenOnList;
import cn.tofocus.lejia.domain.market.OriTestManager;
import cn.tofocus.lejia.domain.market.OriVenManager;


@RequestMapping("/v1/app/market/ori")
@RestController
public class AppOriApiImpl implements AppOriApi
{
	@Autowired
	private OriTestManager oriTestManager;
	@Autowired
	private OriVenManager oriVenManager;
	
	@Override
	public Result<PageResult<MktOriVenOnList>> queryOriVen(int page, int pagesize, String merchant, String goods,
			String vendor) {
		return new Result<>(oriVenManager.queryOriVen(page, pagesize, merchant, goods, vendor, false));
	}

	@Override
	public Result<PageResult<MktOriTestOnList>> queryOriTest(int page, int pagesize, String merchant, Date startDate,
			Date endDate, String goods, String entry, Boolean testResult) {
		return new Result<>(oriTestManager.queryOriTest(page, pagesize, merchant, startDate, endDate, goods, entry, testResult, false));
	}

}

package cn.tofocus.lejia.api.v1.market.goods;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.MktWareLineInsDTO;
import cn.tofocus.lejia.bean.dto.market.MktWareLineOnList;
import cn.tofocus.lejia.bean.dto.market.WareAggreDTO;
import cn.tofocus.lejia.bean.enums.WareType;
import cn.tofocus.lejia.domain.market.goods.WareManager;

@RequestMapping("/v1/market/ware")
@RestController
public class WareManagerApiImpl implements WareManagerApi
{

	@Autowired
	private WareManager manager;
	
	@Override
	public Result<Integer> insWare(MktWareLineInsDTO entity) {
		return new Result<>(manager.insWare(entity));
	}

	@Override
	public Result<PageResult<MktWareLineOnList>> queryWare(int page, int pagesize, int goodsPkey, WareType type) {
		return new Result<>(manager.queryWare(page, pagesize, goodsPkey, type));
	}

	@Override
	public Result<List<WareAggreDTO>> queryWareSum(int goodsPkey) {
		return new Result<>(manager.queryWareSum(goodsPkey));
	}

	
}

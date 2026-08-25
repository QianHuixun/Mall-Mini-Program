package cn.tofocus.lejia.api.v1.market;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.MktIndexAdvertOnList;
import cn.tofocus.lejia.domain.IndexAdvertManager;

@RequestMapping("/v1/market/index/img")
@RestController
public class MktIndexAdvertApiImpl implements MktIndexAdvertApi
{

	@Autowired
	private IndexAdvertManager manager;
	
	@Override
	public Result<Integer> insIndexAdvert(MktIndexAdvertOnList entity) {
		
		return new Result<>(manager.insIndexAdvert(entity));
	}

	@Override
	public Result<PageResult<MktIndexAdvertOnList>> queryDrawWin(int page, int pagesize) {
		
		return new Result<>(manager.queryIndexAdvert(page, pagesize));
	}

	@Override
	public Result<Integer> updIndexAdvert(MktIndexAdvertOnList entity) {
		
		return new Result<>(manager.updIndexAdvert(entity));
	}

	@Override
	public Result<Boolean> delIndexAdvert(Integer pkey) {
		
		return new Result<>(manager.delIndexAdvert(pkey));
	}

}

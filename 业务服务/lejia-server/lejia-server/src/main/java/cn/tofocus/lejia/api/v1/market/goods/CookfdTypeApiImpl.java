package cn.tofocus.lejia.api.v1.market.goods;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.MktCookfdTypeOnList;
import cn.tofocus.lejia.domain.market.goods.CookfdTypeManager;

@RequestMapping("/v1/market/goods/cookfd/type")
@RestController
public class CookfdTypeApiImpl implements CookfdTypeApi
{
	@Autowired
	private CookfdTypeManager manager;
	
	@Override
	public Result<Integer> insCookfdType(String name, int sort) {
		return new Result<>(manager.insCookfdType(name, sort));
	}

	@Override
	public Result<PageResult<MktCookfdTypeOnList>> queryCookfdType(int page, int pagesize, String name, Boolean enabled) {
		return new Result<>(manager.queryCookfdType(page, pagesize, name, enabled));
	}

	@Override
	public Result<Boolean> updCookfdType(MktCookfdTypeOnList entity) {
		return new Result<>(manager.updCookfdType(entity));
	}

	@Override
	public Result<Boolean> delCookfdType(Integer pkey) {
		return new Result<>(manager.delCookfdType(pkey));
	}

	@Override
	public Result<Boolean> startCookfdType(Integer pkey) {
		return new Result<>(manager.enabledCookfdType(pkey, true));
	}

	@Override
	public Result<Boolean> stopCookfdType(Integer pkey) {
		return new Result<>(manager.enabledCookfdType(pkey, false));
	}

}

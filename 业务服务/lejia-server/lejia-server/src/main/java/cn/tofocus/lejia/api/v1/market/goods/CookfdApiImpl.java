package cn.tofocus.lejia.api.v1.market.goods;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.log.LogApi;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.MktCookfdOnList;
import cn.tofocus.lejia.bean.dto.market.MktCookfdUpDTO;
import cn.tofocus.lejia.domain.market.goods.CookfdManager;


@RequestMapping("/v1/market/goods/cookfd")
@RestController
public class CookfdApiImpl implements CookfdApi
{
	@Autowired
    private CookfdManager cookfdManager;
	
	@Override
	@LogApi(operation = "新增菜谱", format = "新增菜谱名称:{entity.name}", resultFormat = "")
	public Result<Integer> insCookfd(MktCookfdOnList entity) {
		return new Result<>(cookfdManager.insCookfd(entity));
	}

	@Override
	public Result<MktCookfdOnList> getCookfd(Integer pkey) {
		return new Result<>(cookfdManager.getCookfd(pkey));
	}

	@Override
	public Result<PageResult<MktCookfdOnList>> queryCookfd(int page, int pagesize, String name, Boolean recom, Boolean enabled, Integer ctype) {
		return new Result<>(cookfdManager.queryCookfd(page, pagesize, name, recom, enabled, ctype));
	}

//	@Override
//	public Result<Boolean> updCookfd(Integer pkey, String name, Integer sort, String descp, Boolean recom,
//			String content) {
//		return new Result<>(cookfdManager.updCookfd(pkey, name, sort, descp, recom, content));
//	}
	@Override
	@LogApi(operation = "修改菜谱", format = "修改菜谱名称:{entity.name}")
	public Result<Boolean> updCookfd(@RequestBody MktCookfdUpDTO entity)
	{
		return new Result<>(cookfdManager.updCookfd(entity));
	}

	@Override
	@LogApi(operation = "删除菜谱", format = "删除菜谱")
	public Result<Boolean> delCookfd(Integer pkey) {
		return new Result<>(cookfdManager.delCookfd(pkey));
	}
	
	@Override
	@LogApi(operation = "启动菜谱", format = "启动菜谱")
	public Result<Boolean> startCookfd(Integer pkey) {
		return new Result<>(cookfdManager.enabledCookfd(pkey, true));
	}

	@Override
	@LogApi(operation = "停止菜谱", format = "停止菜谱")
	public Result<Boolean> stopCookfd(Integer pkey) {
		return new Result<>(cookfdManager.enabledCookfd(pkey, false));
	}

	@Override
	@LogApi(operation = "加入今日推荐", format = "加入今日推荐菜谱")
	public Result<Boolean> startRecomCookfd(Integer pkey) {
		return new Result<>(cookfdManager.recomCookfd(pkey, true));
	}

	@Override
	@LogApi(operation = "退出今日推荐", format = "退出今日推荐菜谱")
	public Result<Boolean> stopRecomCookfd(Integer pkey) {
		return new Result<>(cookfdManager.recomCookfd(pkey, false));
	}

	

}

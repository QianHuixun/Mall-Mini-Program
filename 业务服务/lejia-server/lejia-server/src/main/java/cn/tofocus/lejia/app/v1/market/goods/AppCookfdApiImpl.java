package cn.tofocus.lejia.app.v1.market.goods;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.app.market.MktAppCookfdDetailsDTO;
import cn.tofocus.lejia.bean.dto.app.market.MktAppCookfdTypeOnList;
import cn.tofocus.lejia.bean.dto.app.market.MktCookfdAppOnList;
import cn.tofocus.lejia.domain.app.AppCookfdManager;


@RequestMapping("/v1/app/market/goods/cookfd")
@RestController
public class AppCookfdApiImpl implements AppCookfdApi
{
	@Autowired
    private AppCookfdManager cookfdManager;
	
	@Override
	public Result<MktAppCookfdDetailsDTO> getCookfd(Integer pkey) {
		return new Result<>(cookfdManager.getAppCookfd(pkey));
	}


	@Override
	public Result<List<Map<String, Object>>> queryRelatedCookfd(Integer goodsPkey) {
		return new Result<>(cookfdManager.queryRelatedCookfd(goodsPkey));
	}

	@Override
	public Result<PageResult<MktCookfdAppOnList>> queryCookfd(Integer page, Integer pagesize, String name,
			Integer ctype, Boolean recom, Boolean hot) {
		return new Result<>(cookfdManager.queryAppCookfd(page, pagesize, name, ctype, recom, hot));
	}

	@Override
	public Result<List<MktAppCookfdTypeOnList>> queryCookfdType() {
		return new Result<>(cookfdManager.queryCookfdType());
	}

}

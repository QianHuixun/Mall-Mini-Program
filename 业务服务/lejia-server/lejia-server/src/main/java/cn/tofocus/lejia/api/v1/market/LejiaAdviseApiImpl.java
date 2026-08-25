package cn.tofocus.lejia.api.v1.market;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.log.LogApi;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.MktAdviseOnList;
import cn.tofocus.lejia.domain.market.AdviseManager;


@RequestMapping("/v1/market/advise")
@RestController
public class LejiaAdviseApiImpl implements LejiaAdviseApi
{
	@Autowired
    private AdviseManager adviseManager;
	@Override
	public Result<PageResult<MktAdviseOnList>> queryAdviset(int page, int pagesize, String mobile) {
		return new Result<>(adviseManager.queryAdviset(page, pagesize, mobile));
	}

	@Override
	@LogApi(operation = "删除建议反馈", format = "删除建议反馈")	
	public Result<Boolean> delAdviset(Integer pkey) {
		return new Result<>(adviseManager.delAdvise(pkey));
	}

}

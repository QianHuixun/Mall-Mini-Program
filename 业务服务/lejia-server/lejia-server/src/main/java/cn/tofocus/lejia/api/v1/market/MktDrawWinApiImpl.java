package cn.tofocus.lejia.api.v1.market;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.log.LogApi;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.MktDrawWinOnList;
import cn.tofocus.lejia.bean.enums.PrizeStatus;
import cn.tofocus.lejia.domain.market.DrawManager;


@RequestMapping("/v1/market/drawwin")
@RestController
public class MktDrawWinApiImpl implements MktDrawWinApi
{

	@Autowired
    private DrawManager drawManager;

	@Override
	public Result<PageResult<MktDrawWinOnList>> queryDrawWin(int page, int pagesize, PrizeStatus status) {
		return new Result<>(drawManager.queryDrawWin(page, pagesize, status));
	}

	@Override
	public Result<List<Map<String, Object>>> queryNumDrawWin() {
		return new Result<>(drawManager.queryNumDrawWin());
	}

	
	@Override
	@LogApi(operation = "设置奖品已发货", format = "快递公司: {logistics} , 快递单号: {express}")
	public Result<MktDrawWinOnList> updDrawWin(Integer pkey, String logistics, String express) {
		return new Result<>(drawManager.updDrawWin(pkey, logistics, express));
	}
    

}

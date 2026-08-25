package cn.tofocus.lejia.api.v1.market;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.log.LogApi;
import cn.tofocus.lejia.bean.dto.market.MktDrawConfOnList;
import cn.tofocus.lejia.domain.market.DrawManager;


@RequestMapping("/v1/market/drawconf")
@RestController
public class MktDrawConfApiImpl implements MktDrawConfApi
{

	@Autowired
    private DrawManager drawManager;
    
	@Override
	public Result<MktDrawConfOnList> getDrawConf() {
		return new Result<>(drawManager.getDrawConf());
	}

	@Override
	@LogApi(operation = "修改抽奖设置", format = "修改抽奖设置,支付积分改成 {point}")
	public Result<MktDrawConfOnList> updDrawConf(Integer pkey, Integer point) {
		return new Result<>(drawManager.updDrawConf(pkey, point));
	}

}

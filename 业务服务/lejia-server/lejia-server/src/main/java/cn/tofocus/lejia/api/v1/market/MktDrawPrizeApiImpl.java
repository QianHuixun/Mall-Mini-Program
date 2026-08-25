package cn.tofocus.lejia.api.v1.market;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.log.LogApi;
import cn.tofocus.lejia.bean.dto.market.MktDrawPrizeOnList;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.domain.market.DrawManager;


@RequestMapping("/v1/market/drawprize")
@RestController
public class MktDrawPrizeApiImpl implements MktDrawPrizeApi
{

	@Autowired
    private DrawManager drawManager;
	
	@Override
	public Result<List<MktDrawPrizeOnList>> queryDrawPrize() {
		return new Result<>(drawManager.queryDrawPrize(CurrentSession.ascriptionPkey()));
	}

	@Override
	@LogApi(operation = "修改礼品配置", format = "修改礼品配置名称: {entity.name}, 中奖描述: {enetiy.descp}")
	public Result<MktDrawPrizeOnList> updDrawPrize(MktDrawPrizeOnList entity) {
		return new Result<>(drawManager.updDrawPrize(entity));
	}

}

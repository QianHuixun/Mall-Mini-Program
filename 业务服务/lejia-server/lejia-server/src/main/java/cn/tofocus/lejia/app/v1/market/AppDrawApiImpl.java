package cn.tofocus.lejia.app.v1.market;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.bean.dto.app.market.AppDrawMsgDTO;
import cn.tofocus.lejia.bean.dto.app.market.AppDrawPrizeDTO;
import cn.tofocus.lejia.domain.app.AppDrawManager;


@RequestMapping("/v1/app/market/lm/draw")
@RestController
public class AppDrawApiImpl implements AppDrawApi {
    @Autowired
    private AppDrawManager drawManager;

    @Override
    public Result<AppDrawMsgDTO> getDrawMessage() {
        return new Result<>(drawManager.getDrawMessage());
    }

    @Override
    public Result<AppDrawPrizeDTO> draw() {
        return new Result<>(drawManager.draw());
    }

	@Override
	public Result<Boolean> insDrawAddr(Integer pkey, String addr) {
		return new Result<>(drawManager.insDrawAddr(pkey, addr));
	}
}

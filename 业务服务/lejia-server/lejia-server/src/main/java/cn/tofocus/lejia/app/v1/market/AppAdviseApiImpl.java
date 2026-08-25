package cn.tofocus.lejia.app.v1.market;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.bean.dto.app.market.AppAdviseDetailsDTO;
import cn.tofocus.lejia.domain.app.AppAdviseManager;


@RequestMapping("/v1/app/market/lm/advise")
@RestController
public class AppAdviseApiImpl implements AppAdviseApi
{
	@Autowired
    private AppAdviseManager adviseManager;
	

	@Override
	public Result<AppAdviseDetailsDTO> insAdvise(String content) {
		return new Result<>(adviseManager.insAppAdvise(content));
	}

}

package cn.tofocus.lejia.api.v1.market;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.log.LogApi;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.MktLogisticsOnList;
import cn.tofocus.lejia.domain.market.LogisticsManager;


@RequestMapping("/v1/market/logistics")
@RestController
public class LejiaLogisticsApiImpl implements LejiaLogisticsApi
{
	@Autowired
    private LogisticsManager logisticsManager;
	
	@Override
	@LogApi(operation = "新增快递公司", format = "新增快递公司,名称:{entity.name}", resultFormat = "")
	public Result<MktLogisticsOnList> insLogistics(MktLogisticsOnList entity) {
		return new Result<>(logisticsManager.insLogistics(entity));
	}

	@Override
	public Result<MktLogisticsOnList> getLogistics(Integer pkey) {
		return new Result<>(logisticsManager.getLogistics(pkey));
	}

	@Override
	public Result<PageResult<MktLogisticsOnList>> queryLogistics(int page, int pagesize, String logisticsName, Boolean enabled) {
		return new Result<>(logisticsManager.queryLogistics(page, pagesize, logisticsName, enabled));
	}

	@Override
	@LogApi(operation = "修改快递公司", format = "修改快递公司,名字为{name}")
	public Result<MktLogisticsOnList> updLogistics(Integer pkey, String name, String descp) {
		return new Result<>(logisticsManager.updLogistics(pkey, name, descp));
	}

	@Override
	@LogApi(operation = "删除快递公司", format = "删除快递公司")
	public Result<Boolean> delLogistics(Integer pkey) {
		return new Result<>(logisticsManager.delLogistics(pkey));
	}

	@Override
	@LogApi(operation = "启动快递公司", format = "启动快递公司")
	public Result<Boolean> startLogistics(Integer pkey) {
		return new Result<>(logisticsManager.enableLogistics(pkey, true));
	}

	@Override
	@LogApi(operation = "停止快递公司", format = "停止快递公司")
	public Result<Boolean> stopLogistics(Integer pkey) {
		return new Result<>(logisticsManager.enableLogistics(pkey, false));
	}

}

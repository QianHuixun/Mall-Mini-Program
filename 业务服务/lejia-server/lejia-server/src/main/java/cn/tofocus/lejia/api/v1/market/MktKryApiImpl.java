package cn.tofocus.lejia.api.v1.market;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.log.LogApi;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.MktKryOrderOnList;
import cn.tofocus.lejia.bean.dto.market.MktKryVendorOnList;
import cn.tofocus.lejia.domain.market.MktKryManager;


@RequestMapping("/v1/market/kry")
@RestController
public class MktKryApiImpl implements MktKryApi
{
	@Autowired
	private MktKryManager kryManager;
	
	@Override
	@LogApi(operation = "新增客如云商户", format = "新增客如云商户,名称:{entity.name}", resultFormat = "")
	public Result<MktKryVendorOnList> insKryVendor(Long uuid, String name, String mobile, String manager, String token) {
		return new Result<>(kryManager.insKryVendor(uuid, name, mobile, manager, token));
	}

	@Override
	public Result<PageResult<MktKryVendorOnList>> queryKryVendor(int page, int pagesize, String name) {
		return new Result<>(kryManager.queryKryVendor(page, pagesize, name));
	}

	@Override
	@LogApi(operation = "修改客如云商户", format = "修改客如云商户,名称:{name},电话{mobile}")
	public Result<MktKryVendorOnList> updKryVendor(Integer pkey, Long uuid, String name, String mobile, String manager, String token) {
		return new Result<>(kryManager.updKryVendor(pkey, uuid, name, mobile, manager, token));
	}

	@Override
	@LogApi(operation = "删除客如云商户", format = "删除客如云商户")
	public Result<Boolean> delKryVendor(Integer pkey) {
		return new Result<>(kryManager.delKryVendor(pkey));
	}

	@Override
	@LogApi(operation = "启动客如云商户", format = "启动客如云商户")
	public Result<Boolean> startKryVendor(Integer pkey) {
		return new Result<>(kryManager.enabledKryVendor(pkey, true));
	}

	@Override
	@LogApi(operation = "停止客如云商户", format = "停止客如云商户")
	public Result<Boolean> stopKryVendor(Integer pkey) {
		return new Result<>(kryManager.enabledKryVendor(pkey, false));
	}

	@Override
	public Result<PageResult<MktKryOrderOnList>> queryKryOrder(int page, int pagesize,String name, String startDate, String endDate) {
		return new Result<>(kryManager.queryKryOrder(page, pagesize, name, startDate, endDate));
	}

}

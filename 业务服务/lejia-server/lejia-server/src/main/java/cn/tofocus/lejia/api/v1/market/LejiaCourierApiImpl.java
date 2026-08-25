package cn.tofocus.lejia.api.v1.market;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.log.LogApi;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.MktCourierOnList;
import cn.tofocus.lejia.domain.market.LejiaCourierManager;


@RequestMapping("/v1/market/courier")
@RestController
public class LejiaCourierApiImpl implements LejiaCourierApi
{
	
	@Autowired
	private LejiaCourierManager lejiaCourierManager;
	
	@Override
	@LogApi(operation = "新增快递员", format = "新增快递员,名称为: {name}, 手机号码: {mobile}", resultFormat = "")
	public Result<MktCourierOnList> insCourier(String name, String mobile) {
		return new Result<>(lejiaCourierManager.insCourier(name, mobile));
	}
	@Override
	public Result<MktCourierOnList> getCourier(Integer pkey) {
		return new Result<>(lejiaCourierManager.getCourier(pkey));
	}

	@Override
	public Result<PageResult<MktCourierOnList>> queryCourier(int page, int pagesize, String courierName,  String courierMobile, Boolean enabled) {
		return new Result<>(lejiaCourierManager.queryCourier(page, pagesize, courierName, courierMobile, enabled));
	}

	@Override
	@LogApi(operation = "修改快递员", format = "修改快递员, 名称:{name}, 电话:{mobile}, 备注:{remark}")
	public Result<MktCourierOnList> updCourier(Integer pkey, String name, String mobile, String remark) {
		return new Result<>(lejiaCourierManager.updCourier(pkey, name, mobile, remark));
	}

	@Override
	@LogApi(operation = "删除快递员", format = "删除快递员")
	public Result<Boolean> delCourier(Integer pkey) {
		return new Result<>(lejiaCourierManager.delCourier(pkey));
	}

	@Override
	@LogApi(operation = "启用快递员", format = "启用快递员")
	public Result<Boolean> startCourier(Integer pkey) {
		return new Result<>(lejiaCourierManager.enabledCourier(pkey, true));
	}

	@Override
	@LogApi(operation = "启用快递员", format = "启用快递员")
	public Result<Boolean> stopCourier(Integer pkey) {
		return new Result<>(lejiaCourierManager.enabledCourier(pkey, false));
	}

	

}

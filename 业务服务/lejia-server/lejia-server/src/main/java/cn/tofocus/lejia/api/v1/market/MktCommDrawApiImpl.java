package cn.tofocus.lejia.api.v1.market;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.MktCommDrawOnList;
import cn.tofocus.lejia.bean.enums.CommDrawStatus;
import cn.tofocus.lejia.domain.CommDrawManager;

@RequestMapping("/v1/market/comm/draw")
@RestController
public class MktCommDrawApiImpl implements MktCommDrawApi
{
    @Autowired
    private CommDrawManager manager;
    
	@Override
	public Result<PageResult<MktCommDrawOnList>> queryCommDraw(int page, int pagesize, CommDrawStatus status, String orderNumber) {
		return new Result<>(manager.queryCommDraw(page, pagesize, status, orderNumber));
	}

	@Override
	public Result<Boolean> agreeCommDraw(Integer pkey, String remark) {
		
		return new Result<>(manager.agreeCommDraw(pkey, remark));
	}

	@Override
	public Result<Boolean> refuseCommDraw(Integer pkey, String remark) {
		
		return new Result<>(manager.refuseCommDraw(pkey, remark));
	}

	@Override
	public Result<Boolean> updCommDraw(Integer pkey, String remark) {
		return new Result<>(manager.updCommDraw(pkey, remark));
	}

	@Override
	public Result<Boolean> paidDraw(Integer pkey) {
		return new Result<>(manager.paidDraw(pkey));
	}

}

package cn.tofocus.lejia.api.v1.market;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.MktRefundOnList;
import cn.tofocus.lejia.bean.enums.RefundStatus;
import cn.tofocus.lejia.domain.market.RefundManager;


@RequestMapping("/v1/market/refund")
@RestController
public class MktRefundApiImpl implements MktRefundApi
{

	@Autowired
	private RefundManager refundManager;
	
	@Override
	public Result<PageResult<MktRefundOnList>> queryRefund(int page, int pagesize, String code,
			RefundStatus status) {
		return new Result<>(refundManager.queryRefund(page, pagesize, code, status));
	}

	@Override
	public Result<MktRefundOnList> updRefund(int pkey, RefundStatus status) {
		return new Result<>(refundManager.updRefund(pkey, status));
	}

}

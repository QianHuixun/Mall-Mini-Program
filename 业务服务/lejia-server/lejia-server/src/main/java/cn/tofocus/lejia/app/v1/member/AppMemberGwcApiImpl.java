package cn.tofocus.lejia.app.v1.member;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.app.market.MktGwcOnList;
import cn.tofocus.lejia.domain.app.AppMemberGwcManager;


@RequestMapping("/v1/app/market/lm/member/gwc")
@RestController
public class AppMemberGwcApiImpl implements AppMemberGwcApi {
    @Autowired
    private AppMemberGwcManager gwcManager;

    @Override
    public Result<Boolean> insGwc(int goodsPkey, int space, int goodsNum, Integer association)
    {
        return new Result<>(gwcManager.insGwc(goodsPkey, space, goodsNum, association));
    }

    @Override
    public Result<MktGwcOnList> queryGwc() {

        return new Result<>(gwcManager.queryGwc());
    }

    @Override
    public Result<Boolean> addGwcNum(int pkey, Integer association) {

        return new Result<>(gwcManager.modifyGwcNum(pkey, true, association));
    }

    @Override
    public Result<Boolean> lessGwcNum(int pkey, Integer association) {

        return new Result<>(gwcManager.modifyGwcNum(pkey, false, association));
    }

    @Override
    public Result<Boolean> delGwc(int pkey) {
        return new Result<>(gwcManager.delGwc(pkey));
    }

    @Override
    public Result<Boolean> delByPkeys(List<Integer> pkeys)
    {
        return new Result<>(gwcManager.delByPkeys(pkeys));
    }

    @Override
	public Result<Boolean> insCpGwc(int pkey) {
		return new Result<>(gwcManager.insGwcCp(pkey));
	}

	@Override
	public Result<PageResult<Map<String, Object>>> freeDeliveryGoods(Integer page, Integer pagesize) {
		return new Result<>(gwcManager.freeDeliveryGoods(page, pagesize));
	}

	@Override
	public Result<Integer> getGwcGoodsNum() {
		return new Result<>(gwcManager.getGwcGoodsNum());
	}

    @Override
    public Result<Boolean> addGwcNum(int goodsPkey, int space, int goodsNum, Integer association)
    {
        return new Result<>(gwcManager.addOrLessGwcNum(goodsPkey, space, goodsNum, true, association));
    }

    @Override
    public Result<Boolean> lessGwcNum(int goodsPkey, int space, int goodsNum, Integer association)
    {
        return new Result<>(gwcManager.addOrLessGwcNum(goodsPkey, space, goodsNum, false, association));
    }

    @Override
    public Result<BigDecimal> getGwcGoodsPrice()
    {
        return new Result<>(gwcManager.getGwcGoodsPrice());
    }

}

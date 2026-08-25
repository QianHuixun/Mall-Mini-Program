package cn.tofocus.lejia.app.v1.market.goods;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.bean.dto.app.goods.AppGtypeDTO;
import cn.tofocus.lejia.bean.dto.app.goods.AppMallGtypeTwoLevelsDTO;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.domain.app.AppGoodsGtypeManager;


@RequestMapping("/v1/app/market/goods/gtype")
@RestController
public class AppGtypeApiImpl implements AppGoodsGtypeApi {
	
    @Autowired
    private AppGoodsGtypeManager gtypeManager;

    @Override
    public Result<List<AppGtypeDTO>> queryGtype(Boolean showPoint, Boolean showMarket, Boolean flag, MType mtype) {
    	return new Result<>(gtypeManager.queryGtypeV2(showPoint, showMarket, flag, mtype));
    }

    @Override
    public Result<List<AppMallGtypeTwoLevelsDTO>> listMallTwoLevelsGtype(MType mtype) {
        return new Result<>(gtypeManager.listMallTwoLevelsGtype(mtype));
    }

}

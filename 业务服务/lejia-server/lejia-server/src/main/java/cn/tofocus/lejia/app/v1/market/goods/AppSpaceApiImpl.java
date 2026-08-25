package cn.tofocus.lejia.app.v1.market.goods;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.bean.dto.app.goods.AppSpaceDTO;
import cn.tofocus.lejia.domain.app.AppGoodsSpaceManager;


@RequestMapping("/v1/app/market/goods/space")
@RestController
public class AppSpaceApiImpl implements AppGoodsSpaceApi {

    @Autowired
    private AppGoodsSpaceManager goodsSpaceManager;

    @Override
    public Result<AppSpaceDTO> get(Integer pkey) {
        return new Result<>(goodsSpaceManager.getSpaceList(pkey));
    }
    
    @Override
    public Result<AppSpaceDTO> getMember(Integer pkey) {
        return new Result<>(goodsSpaceManager.getSpaceMemberList(pkey));
    }

    @Override
    public Result<Integer> totalAmount(Integer pkey) {
        return new Result<>(goodsSpaceManager.totalAmount(pkey));
    }
}

package cn.tofocus.lejia.app.v1.market;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.app.v1.market.goods.AppCollectionApi;
import cn.tofocus.lejia.bean.dto.app.market.AppCollectionDTO;
import cn.tofocus.lejia.domain.app.AppCollectionManager;


@RequestMapping("/v1/app/market/goods/collection")
@RestController
public class AppCollectionApiImpl implements AppCollectionApi
{

	@Autowired
	private AppCollectionManager collectionManager;
	
	@Override
	public Result<Integer> insCollection(Integer objKey, Integer ctype) {
		return new Result<>(collectionManager.insCollection(objKey, ctype));
	}

	@Override
	public Result<PageResult<AppCollectionDTO>> queryCollection(int page, int pagesize, Integer ctype) {
		return new Result<>(collectionManager.queryCollection(page, pagesize, ctype));
	}

	@Override
	public Result<Boolean> delCollection(int pkey) {
		return new Result<>(collectionManager.delCollection(pkey));
	}

    @Override
    public Result<Map<String, Integer>> getCtypeNum()
    {
        return new Result<>(collectionManager.getCtypeNum());
    }

}

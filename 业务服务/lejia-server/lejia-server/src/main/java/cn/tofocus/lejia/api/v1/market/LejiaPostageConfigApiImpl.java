package cn.tofocus.lejia.api.v1.market;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.log.LogApi;
import cn.tofocus.lejia.bean.dto.market.MktDeliveryTimeConfig;
import cn.tofocus.lejia.bean.dto.market.MktPostageConfigOnList;
import cn.tofocus.lejia.bean.dto.market.PostageExpressConfigDTO;
import cn.tofocus.lejia.domain.market.LejiaPostageConfigManager;


@RequestMapping("/v1/market/postage")
@RestController
public class LejiaPostageConfigApiImpl implements LejiaPostageConfigApi
{
	@Autowired
    private  LejiaPostageConfigManager postageConfigManager;
	
//	@Override
//	public Result<MktPostageConfigOnList> insPostageConfig(MktPostageConfigOnList entity) {
//		return new Result<>(postageConfigManager.insPostageConfig(entity));
//	}

	@Override
	public Result<List<MktPostageConfigOnList>> queryPostageConfig() {
		return new Result<>(postageConfigManager.queryPostageConfig());
	}

	@Override
	@LogApi(operation = "修改运费配置", format = "")
	public Result<List<MktPostageConfigOnList>> updPostageConfig(List<MktPostageConfigOnList> entitys) {
		return new Result<>(postageConfigManager.updPostageConfig(entitys));
	}

	@Override
	@LogApi(operation = "修改运费配置、夜间配送、跑腿配置", format = "")
	public Result<Boolean> updPostageConfig(@Valid  PostageExpressConfigDTO entity) {
		return new Result<>(postageConfigManager.updPostageConfig(entity));
	}

    @Override
    public Result<List<MktDeliveryTimeConfig>> queryDeliveryTimeConfig()
    {
        return new Result<>(postageConfigManager.queryDeliveryTimeConfig());
    }


	
	
}

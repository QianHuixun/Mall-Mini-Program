package cn.tofocus.lejia.app.v1.courier;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.AppCourierDTO;
import io.swagger.v3.oas.annotations.Operation;

@FeignClient(value = "lejia-server", contextId = "lejia-server-app-courier", path = "/v1/app/courier", fallbackFactory = AppCourierApiFallback.class, configuration = FeignConfig.class)
public interface AppCourierApi 
{

	
	@Operation(summary = "个人信息", tags = AppTags.mobileCourier)
	@PostMapping("/get")
	public Result<AppCourierDTO> getCourier();
	
}

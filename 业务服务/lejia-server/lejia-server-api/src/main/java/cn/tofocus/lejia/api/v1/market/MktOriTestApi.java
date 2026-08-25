package cn.tofocus.lejia.api.v1.market;

import java.util.Date;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.MktOriTestOnList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-oritest", path = "/v1/market/oritest", 
fallbackFactory = MktOriTestApiFallback.class, configuration = FeignConfig.class)
public interface MktOriTestApi 
{
	@Operation(summary = "新增检测信息", tags = ApiTags.custOriTest)
	@PostMapping("/ins")
	public Result<MktOriTestOnList> insOriTest(@RequestBody MktOriTestOnList entity);
	
	@Operation(summary = "获取检测信息", tags = ApiTags.custOriTest)
	@PostMapping("/get")
	public Result<MktOriTestOnList> getOriTest(@RequestParam(value = "pkey") @Parameter(description = "检测信息主键") Integer pkey);
	
	@Operation(summary = "获取检测信息列表", tags = ApiTags.custOriTest)
    @PostMapping(value = "/query")
    public Result<PageResult<MktOriTestOnList>> queryOriTest(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号", hidden = false) int page,
        @RequestParam(value = "pagesize", defaultValue = "10000") @Parameter(description = "每页大小", hidden = false) int pagesize,
        @RequestParam(value = "merchant", required = false) @Parameter(description = "检测商户") String merchant,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "检查时间-开始") Date startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "检查时间-结束") Date endDate,
        @RequestParam(value = "goods", required = false) @Parameter(description = "检测商品 ", hidden = true) String goods,
        @RequestParam(value = "entry", required = false) @Parameter(description = "检测项目", hidden = true) String entry,
        @RequestParam(value = "testResult", required = false) @Parameter(description = "检测结果", hidden = true) Boolean testResult);
	
	@Operation(summary = "修改检测信息", tags = ApiTags.custOriTest)
    @PostMapping(value = "/upd")
    public Result<MktOriTestOnList> updOriTest(
    		@RequestParam(value = "pkey") @Parameter(description = "检测信息主键") Integer pkey,
            @RequestParam(value = "merchant", required = false) @Parameter(description = "检测商户") String merchant,
            @RequestParam(value = "goods", required = false) @Parameter(description = "检测商品 ") String goods,
            @RequestParam(value = "entry", required = false) @Parameter(description = "检测项目") String entry,
            @RequestParam(value = "testResult", required = false) @Parameter(description = "检测结果") Boolean testResult);
	
	@Operation(summary = "删除检测信息", tags = ApiTags.custOriTest)
    @PostMapping(value = "/del")
    public Result<Boolean> delOriTest(@RequestParam(name = "pkey") Integer pkey);
	
//	@Operation(summary = "导入excel", tags = ApiTags.custOriTest)
//	@PostMapping(value = "/importexcel")
//	public Result<Boolean> importExcel(MultipartFile myfile);
	
	
}

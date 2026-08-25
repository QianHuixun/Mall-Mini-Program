package cn.tofocus.lejia.api.v1.market;

import java.util.List;

import javax.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.CardStatisticsInfo;
import cn.tofocus.lejia.bean.dto.market.CardUpDTO;
import cn.tofocus.lejia.bean.dto.market.DropDTO;
import cn.tofocus.lejia.bean.dto.market.DropIntegerDown;
import cn.tofocus.lejia.bean.dto.market.MktCardInsDTO;
import cn.tofocus.lejia.bean.dto.market.MktCardOnList;
import cn.tofocus.lejia.bean.dto.market.MktMemberCardOnList;
import cn.tofocus.lejia.bean.enums.CardStatus;
import cn.tofocus.lejia.bean.enums.CardType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-card", path = "/v1/market/card", 
fallbackFactory = CardApiFallback.class, configuration = FeignConfig.class)
public interface CardApi 
{
	@Operation(summary = "新增优惠券", tags = ApiTags.custCard)
	@PostMapping("/ins")
	public Result<MktCardOnList> insCard(@RequestBody MktCardInsDTO entity);
	
	@Operation(summary = "获取优惠券", tags = ApiTags.custCard)
	@PostMapping("/get")
	public Result<MktCardOnList> getCard(@RequestParam(value = "pkey") @Parameter(description = "优惠券主键") Integer pkey);
	
	@Operation(summary = "获取优惠券列表", tags = ApiTags.custCard)
    @PostMapping(value = "/query")
    public Result<PageResult<MktCardOnList>> queryCard(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号", hidden = true) int page,
        @RequestParam(value = "pagesize", defaultValue = "10000") @Parameter(description = "每页大小", hidden = true) int pagesize,
        @RequestParam(value = "title", required = false) @Parameter(description = "标题") String title,
        @RequestParam(value = "cardType", required = false) @Parameter(description = "优惠券领取方式") CardType cardType,
        @RequestParam(value = "enabled", required = false) Boolean enabled,
        @RequestParam(value = "invalid", required = false) Boolean invalid);
	
	@Operation(summary = "获取优惠券列表", tags = ApiTags.custCard)
    @PostMapping(value = "/query/drop")
    public Result<List<DropDTO>> queryCard();
	
//	@Operation(summary = "修改优惠券", tags = ApiTags.custCard)
//    @PostMapping(value = "/upd")
//    public Result<MktCardOnList> updCard(
//    		@RequestParam(name = "pkey") Integer pkey, 
//    		@RequestParam(name = "title", required = false) String title, 
//    		@RequestParam(name = "cost", required = false) BigDecimal cost,
//    		@RequestParam(name = "limitCost", required = false) BigDecimal limitCost,
//    		@RequestParam(name = "effective", required = false) Integer effective,
//    		@RequestParam(name = "content", required = false) String content);
	
	@Operation(summary = "优惠券失效", tags = ApiTags.custCard)
    @PostMapping(value = "/invalid")
    public Result<Boolean> invalidCard(@RequestParam(value = "pkey")Integer pkey);
	
	@Operation(summary = "修改优惠券", tags = ApiTags.custCard)
    @PostMapping(value = "/upd")
    public Result<MktCardOnList> updCard(@RequestBody @Valid CardUpDTO entity);
	
	@Operation(summary = "删除优惠券", tags = ApiTags.custCard)
    @PostMapping(value = "/del")
    public Result<Boolean> delCard(@RequestParam(name = "pkey") Integer pkey);
	
	@Operation(summary = "优惠券启用", tags = ApiTags.custCard)
    @PostMapping(value = "/enable/start")
    public Result<Boolean> startCard(@RequestParam(name = "pkey") Integer pkey);
	
	@Operation(summary = "优惠券停用", tags = ApiTags.custCard)
    @PostMapping(value = "/enable/stop")
    public Result<Boolean> stopCard(@RequestParam(name = "pkey") Integer pkey);
	
	@Operation(summary = "二维码领取优惠券", tags = ApiTags.custCard)
	@GetMapping("/member/ins")
	public Result<Boolean> insCard(@RequestParam(name = "card")@Parameter(description = "优惠券pkey")Integer card);
	
	@Operation(summary = "优惠券发放多个用户", tags = ApiTags.custCard, hidden = false)
	@PostMapping("/member/ins/all")
	public Result<Boolean> insAllCard(
			@RequestParam(name = "status",required = true)@Parameter(description = "状态")Integer status, 
			@RequestParam(name = "card",required = true)@Parameter(description = "优惠券pkey")Integer card,
			@RequestParam(name = "member",required = false)@Parameter(description = "用户pkey") Integer member);
	
	@Operation(summary = "获取已使用优惠券列表", tags = ApiTags.custCard)
    @PostMapping(value = "/query/use")
    public Result<PageResult<MktMemberCardOnList>> queryUseCard(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "userFarmer", required = false) @Parameter(description = "核销市场") String userFarmer,
        @RequestParam(value = "startTime", required = false) @Parameter(description = "开始时间-使用") String startTime,
        @RequestParam(value = "endTime", required = false) @Parameter(description = "结束时间-使用") String endTime,
        @RequestParam(value = "st", required = false) @Parameter(description = "开始时间-领取") String st,
        @RequestParam(value = "et", required = false) @Parameter(description = "结束时间-领取")String et,
		@RequestParam(value = "mobile", required = false) @Parameter(description = "手机号") String mobile,
		@RequestParam(value = "title", required = false) @Parameter(description = "卡券名称") String title,
		@RequestParam(value = "status", required = false) @Parameter(description = "卡券状态") CardStatus status,
		@RequestParam(value = "invalid", required = false) @Parameter(description = "卡券状态,false:未失效") Boolean invalid);

	@Operation(summary = "优惠券发放记录合计", tags = ApiTags.custCard)
	@PostMapping(value = "/query/use/sum")
	public Result<CardStatisticsInfo> queryUseSumCard(
	    @RequestParam(value = "userFarmer", required = false) @Parameter(description = "核销市场") String userFarmer,
	    @RequestParam(value = "startTime", required = false) @Parameter(description = "开始时间-使用") String startTime,
	    @RequestParam(value = "endTime", required = false) @Parameter(description = "结束时间-使用") String endTime,
	    @RequestParam(value = "st", required = false) @Parameter(description = "开始时间-领取") String st,
	    @RequestParam(value = "et", required = false) @Parameter(description = "结束时间-领取")String et,
	    @RequestParam(value = "mobile", required = false) @Parameter(description = "手机号") String mobile,
	    @RequestParam(value = "title", required = false) @Parameter(description = "卡券名称") String title,
	    @RequestParam(value = "status", required = false) @Parameter(description = "卡券状态") CardStatus status,
	    @RequestParam(value = "invalid", required = false) @Parameter(description = "卡券状态,false:未失效") Boolean invalid);
	
	@Operation(summary = "优惠券加入领券中心", tags = ApiTags.custCard)
    @PostMapping(value = "/center")
    public Result<Boolean> setCenterCard(@RequestParam(name = "pkey") Integer pkey);
	
	@Operation(summary = "新增优惠券适用专区下拉", tags = ApiTags.custCard)
    @PostMapping(value = "/mtype/drop")
	public Result<List<DropIntegerDown>> dropMtypeName( @RequestParam(value = "farmer", required = false) @Parameter(description = "市场") String farmer);
}

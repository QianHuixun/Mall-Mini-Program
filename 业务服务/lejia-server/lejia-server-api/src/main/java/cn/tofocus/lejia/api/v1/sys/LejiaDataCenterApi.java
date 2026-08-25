package cn.tofocus.lejia.api.v1.sys;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.data.SpecialAreaOnPage;
import cn.tofocus.lejia.bean.dto.goods.GoodsLineSum;
import cn.tofocus.lejia.bean.dto.goods.GoodsLineSummary;
import cn.tofocus.lejia.bean.dto.market.CommsDetailOnPage;
import cn.tofocus.lejia.bean.dto.market.DropIntegerDown;
import cn.tofocus.lejia.bean.dto.market.DropStringDown;
import cn.tofocus.lejia.bean.dto.market.MktSupplierSaleSummary;
import cn.tofocus.lejia.bean.dto.order.MktGoodsOrderLineOnPage;
import cn.tofocus.lejia.bean.dto.order.MktGoodsOrderLineSummary;
import cn.tofocus.lejia.bean.dto.order.MktSupplierOrderLineOnPage;
import cn.tofocus.lejia.bean.dto.sys.FarmerOption;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.PayType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-data", path = "/v1/sys/data/center", 
fallbackFactory = LejiaDataCenterFallback.class, configuration = FeignConfig.class)
public interface LejiaDataCenterApi 
{

	@Operation(summary = "各专区报表", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/mtype")
	public Result<PageResult<SpecialAreaOnPage>> mTypeData(
			@RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
	        @RequestParam(value = "pagesize", defaultValue = "20") @Parameter(description = "每页大小") int pagesize,
			@RequestParam(value = "marketPkey", required = false)String marketPkey, 
			@RequestParam(value = "companyPkey", required = false)String companyPkey, 
			@RequestParam(value = "startTime", required = false)String startTime, 
			@RequestParam(value = "endTime", required = false)String endTime) ;
	
	@Operation(summary = "各商品报表", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/goods")
	public Result<PageResult<Map<String,Object>>> goodsData(
			@RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
	        @RequestParam(value = "pagesize", defaultValue = "20") @Parameter(description = "每页大小") int pagesize,
			@RequestParam(value = "marketPkey", required = false)String marketPkey, 
			@RequestParam(value = "companyPkey", required = false)String companyPkey, 
			@RequestParam(value = "startTime", required = false)String startTime, 
			@RequestParam(value = "endTime", required = false)String endTime) ;
	
	@Operation(summary = "各商品分析", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/goods/analysis")
	public Result<List<Map<String,Object>>> goodsAnalysis(@RequestParam(value = "goodsPkey")Integer goodsPkey, 
			@RequestParam(value = "startTime", required = false)String startTime, 
			@RequestParam(value = "endTime", required = false)String endTime);
	
	@Operation(summary = "异常货品分析", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/goods/abnormal")
	public Result<PageResult<Map<String,Object>>> goodsAbnormal(
			@RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
	        @RequestParam(value = "pagesize", defaultValue = "20") @Parameter(description = "每页大小") int pagesize);
	
	@Operation(summary = "奖品报表", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/drawwin")
	public Result<List<Map<String,Object>>> drawWin();
	
	@Operation(summary = "时间段明细 折线图", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/goods/hour/abnormal")
	public Result<List<Map<String,Object>>> goodsHourAnalysis(
			@RequestParam(value = "goodsPkey", required = false)Integer goodsPkey, 
			@RequestParam(value = "time", required = false)String time);
	
	@Operation(summary = "时间段明细表", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/goods/hour/detail")
	public Result<PageResult<Map<String,Object>>> goodsHourDetail(
			@RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
	        @RequestParam(value = "pagesize", defaultValue = "20") @Parameter(description = "每页大小") int pagesize,
			@RequestParam(value = "goodsPkey",required = false)Integer goodsPkey, 
			@RequestParam(value = "time", required = false)String time);
	
	@Operation(summary = "年费会员办卡数量报表", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/annual/memberPay")
	public Result<List<Map<String,Object>>> annualMemberPay( 
			@RequestParam(value = "startTime", required = false)String startTime, 
			@RequestParam(value = "endTime", required = false)String endTime);
	
	@Operation(summary = "积分兑换统计报表", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/goods/integral/sales")
	public Result<PageResult<Map<String,Object>>> goodsIntegralSales(
			@RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
	        @RequestParam(value = "pagesize", defaultValue = "20") @Parameter(description = "每页大小") int pagesize,
			@RequestParam(value = "startTime", required = false)String startTime, 
			@RequestParam(value = "endTime", required = false)String endTime);
	
	
	@Operation(summary = "付费会员消费分析报表", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/member/goods/sales")
	public Result<PageResult<Map<String,Object>>> memberGoodsSales(
			@RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
	        @RequestParam(value = "pagesize", defaultValue = "20") @Parameter(description = "每页大小") int pagesize,
			@RequestParam(value = "startTime", required = false)String startTime, 
			@RequestParam(value = "endTime", required = false)String endTime);
	
	@Operation(summary = "商场用户访问报表", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/mall/access")
	public Result<List<Map<String,Object>>> getMallAccessNum(
			@RequestParam(value = "startTime", required = false)String startTime, 
			@RequestParam(value = "endTime", required = false)String endTime);
	
	@Operation(summary = "商场新增用户报表", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/add/member/count")
	public Result<List<Map<String,Object>>> getAddMemberCount(
			@RequestParam(value = "startTime", required = false)String startTime, 
			@RequestParam(value = "endTime", required = false)String endTime);
	
	@Operation(summary = "优惠券使用统计报表", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/query/farmer/card")
	public Result<PageResult<Map<String,Object>>> queryFarmerCardCount(
			@RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
	        @RequestParam(value = "pagesize", defaultValue = "20") @Parameter(description = "每页大小") int pagesize,
	        @RequestParam(value = "marketPkey", required = false)String marketPkey, 
			@RequestParam(value = "companyPkey", required = false)String companyPkey, 
			@RequestParam(value = "startTime", required = false)String startTime, 
			@RequestParam(value = "endTime", required = false)String endTime);
	
	@Operation(summary = "菜品类别销售统计报表", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/goods/type/sales")
	public Result<PageResult<Map<String,Object>>> goodsTypeSales(
			@RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
	        @RequestParam(value = "pagesize", defaultValue = "20") @Parameter(description = "每页大小") int pagesize,
	        @RequestParam(value = "marketPkey", required = false)String marketPkey, 
			@RequestParam(value = "companyPkey", required = false)String companyPkey, 
			@RequestParam(value = "startTime", required = false)String startTime, 
			@RequestParam(value = "endTime", required = false)String endTime);
	
	@Operation(summary = "积分商户销售额统计报表", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/vendor/sales")
	public Result<PageResult<Map<String,Object>>> vendorSales(
			@RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
	        @RequestParam(value = "pagesize", defaultValue = "20") @Parameter(description = "每页大小") int pagesize,
	        @RequestParam(value = "vendorName", required = false)String vendorName,
			@RequestParam(value = "startTime", required = false)String startTime, 
			@RequestParam(value = "endTime", required = false)String endTime);
	
	
	@Operation(summary = "市场销售统计报表", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/farmer/sales")
	public Result<PageResult<Map<String,Object>>> getFarmerSales(
			@RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
			@RequestParam(value = "pagesize", defaultValue = "20") @Parameter(description = "每页大小") int pagesize,
			@RequestParam(value = "marketPkey", required = false)String marketPkey, 
			@RequestParam(value = "companyPkey", required = false)String companyPkey, 
			@RequestParam(value = "startTime", required = false)String startTime, 
			@RequestParam(value = "endTime", required = false)String endTime);
	
	@Operation(summary = "公司销售统计报表", tags = ApiTags.custDataCenter)
	@PostMapping(value = "/company/sales")
	public Result<PageResult<Map<String,Object>>> getCompanySales(
			@RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
			@RequestParam(value = "pagesize", defaultValue = "20") @Parameter(description = "每页大小") int pagesize,
			@RequestParam(value = "companyPkey", required = false)String companyPkey, 
			@RequestParam(value = "startTime", required = false)String startTime, 
			@RequestParam(value = "endTime", required = false)String endTime);
	
	@Operation(summary = "配送员绩效表报表", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/express/courier/count")
	public Result<PageResult<Map<String,Object>>> getExpressCourierCount(
			@RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
	        @RequestParam(value = "pagesize", defaultValue = "20") @Parameter(description = "每页大小") int pagesize,
			@RequestParam(value = "startTime", required = false)String startTime, 
			@RequestParam(value = "endTime", required = false)String endTime);
	
	@Operation(summary = "运费报表", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/postage/count")
	public Result<List<Map<String,Object>>> getPostageCount(
			@RequestParam(value = "startTime", required = false)String startTime, 
			@RequestParam(value = "endTime", required = false)String endTime);
	
	@Operation(summary = "佣金达人报表", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/comms")
	public Result<PageResult<Map<String,Object>>> getComms(
			@RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
			@RequestParam(value = "pagesize", defaultValue = "20") @Parameter(description = "每页大小") int pagesize,
			@RequestParam(value = "memberName", required = false) String memberName);
	
	@Operation(summary = "佣金明细报表", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/comms/detail")
	public Result<PageResult<CommsDetailOnPage>> getCommsDetail(
			@RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
	        @RequestParam(value = "pagesize", defaultValue = "20") @Parameter(description = "每页大小") int pagesize,
			@RequestParam(value = "startTime", required = false)String startTime, 
			@RequestParam(value = "endTime", required = false)String endTime);
	
	@Operation(summary = "对外数据统计", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/foreign")
    public Result<Map<String,Object>> getForeignDetail(@RequestParam(value = "startTime", required = false)String startTime, 
        @RequestParam(value = "endTime", required = false)String endTime);
	
	@Operation(summary = "对外数据统计-交易", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/foreign2")
    public Result<Map<String,Object>> getForeignDetailOrder();
    
    @Operation(summary = "供应商销售统计", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/supplier/sales")
    public Result<PageResult<MktSupplierSaleSummary>> getSupplierSales(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "20") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "startTime", required = false) @Parameter(description = "开始时间yyyy-MM-dd") String startTime,
        @RequestParam(value = "endTime", required = false) @Parameter(description = "结束时间yyyy-MM-dd") String endTime,
        @RequestParam(value = "supplierName", required = false) @Parameter(description = "供应商名称") String supplierName);
    
    @Operation(summary = "供应商销售统计合计", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/supplier/sales/sum")
    public Result<MktSupplierSaleSummary> sumSupplierSales(
        @RequestParam(value = "startTime", required = false) @Parameter(description = "开始时间yyyy-MM-dd") String startTime,
        @RequestParam(value = "endTime", required = false) @Parameter(description = "结束时间yyyy-MM-dd") String endTime,
        @RequestParam(value = "supplierName", required = false) @Parameter(description = "供应商名称") String supplierName);
    
    @Operation(summary = "供应商交易明细查询", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/supplier/order/line/query")
    public Result<PageResult<MktSupplierOrderLineOnPage>> querySupplierOrderLine(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "20") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "startTime", required = false) @Parameter(description = "开始时间yyyy-MM-dd") String startTime,
        @RequestParam(value = "endTime", required = false) @Parameter(description = "结束时间yyyy-MM-dd") String endTime,
        @RequestParam(value = "kcCode", required = false) @Parameter(description = "订单编号") String kcCode,
        @RequestParam(value = "supplierName", required = false) @Parameter(description = "供应商名称") String supplierName,
        @RequestParam(value = "goodsName", required = false) @Parameter(description = "商品名称") String goodsName,
        @RequestParam(value = "payTypes", required = false) @Parameter(description = "支付方式") List<PayType> payTypes, 
        @RequestParam(value = "tags", required = false) @Parameter(description = "用户标签") List<Integer> tags);
    
    @Operation(summary = "供应商交易明细合计", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/supplier/order/line/sum")
    public Result<BigDecimal> sumSupplierOrderLine(
        @RequestParam(value = "startTime", required = false) @Parameter(description = "开始时间yyyy-MM-dd") String startTime,
        @RequestParam(value = "endTime", required = false) @Parameter(description = "结束时间yyyy-MM-dd") String endTime,
        @RequestParam(value = "kcCode", required = false) @Parameter(description = "订单编号") String kcCode,
        @RequestParam(value = "supplierName", required = false) @Parameter(description = "供应商名称") String supplierName,
        @RequestParam(value = "goodsName", required = false) @Parameter(description = "商品名称") String goodsName,
        @RequestParam(value = "payTypes", required = false) @Parameter(description = "支付方式") List<PayType> payTypes, 
        @RequestParam(value = "tags", required = false) @Parameter(description = "用户标签") List<Integer> tags);
    
    @Operation(summary = "市场/运营端下拉列表", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/farmer/options")
    Result<List<FarmerOption>> listFarmerOptions();
    
    @Operation(summary = "商品明细统计", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/goods/line/summary")
    Result<PageResult<GoodsLineSummary>> goodsLineSummary(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "20") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "startTime", required = false) @Parameter(description = "开始时间yyyy-MM-dd") String startTime,
        @RequestParam(value = "endTime", required = false) @Parameter(description = "结束时间yyyy-MM-dd") String endTime,
        @RequestParam(value = "goodsName", required = false) @Parameter(description = "商品名称") String goodsName,
        @RequestParam(value = "farmer", required = false) @Parameter(description = "市场/运营端主键") String farmer);
    
    @Operation(summary = "商品明细统计合计", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/goods/line/sum")
    Result<GoodsLineSum> goodsLineSum(
        @RequestParam(value = "startTime", required = false) @Parameter(description = "开始时间yyyy-MM-dd") String startTime,
        @RequestParam(value = "endTime", required = false) @Parameter(description = "结束时间yyyy-MM-dd") String endTime,
        @RequestParam(value = "goodsName", required = false) @Parameter(description = "商品名称") String goodsName,
        @RequestParam(value = "farmer", required = false) @Parameter(description = "市场/运营端主键") String farmer);
    
    @Operation(summary = "商品明细统计-明细", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/goods/order/line/query")
    Result<PageResult<MktGoodsOrderLineOnPage>> queryGoodsOrderLine(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "20") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "startTime", required = false) @Parameter(description = "开始时间yyyy-MM-dd") String startTime,
        @RequestParam(value = "endTime", required = false) @Parameter(description = "结束时间yyyy-MM-dd") String endTime,
        @RequestParam(value = "kcCode", required = false) @Parameter(description = "订单编号") String kcCode,
        @RequestParam(value = "memberMobile", required = false) @Parameter(description = "用户手机号") String memberMobile,
        @RequestParam(value = "status", required = false) @Parameter(description = "订单状态") OrderStatus status,
        @RequestParam(value = "deliveryType", required = false) @Parameter(description = "配送方式（1：配送，2：自提）") Integer deliveryType,
        @RequestParam(value = "goods", required = false) @Parameter(description = "商品主键") Integer goods,
        @RequestParam(value = "goodsName", required = false) @Parameter(description = "商品名称") String goodsName,
        @RequestParam(value = "space", required = false) @Parameter(description = "规格") Integer space);
    
    @Operation(summary = "商品明细统计-明细合计", tags = ApiTags.custDataCenter)
    @PostMapping(value = "/goods/order/line/sum")
    Result<MktGoodsOrderLineSummary> sumGoodsOrderLine(
        @RequestParam(value = "startTime", required = false) @Parameter(description = "开始时间yyyy-MM-dd") String startTime,
        @RequestParam(value = "endTime", required = false) @Parameter(description = "结束时间yyyy-MM-dd") String endTime,
        @RequestParam(value = "kcCode", required = false) @Parameter(description = "订单编号") String kcCode,
        @RequestParam(value = "memberMobile", required = false) @Parameter(description = "用户手机号") String memberMobile,
        @RequestParam(value = "status", required = false) @Parameter(description = "订单状态") OrderStatus status,
        @RequestParam(value = "deliveryType", required = false) @Parameter(description = "配送方式（1：配送，2：自提）") Integer deliveryType,
        @RequestParam(value = "goods", required = false) @Parameter(description = "商品主键") Integer goods,
        @RequestParam(value = "goodsName", required = false) @Parameter(description = "商品名称") String goodsName,
        @RequestParam(value = "space", required = false) @Parameter(description = "规格") Integer space);
	
    @Operation(summary = "支付类型下拉", tags = ApiTags.custDataCenter)
    @PostMapping("/payType/list/drop")
    Result<List<DropStringDown>> listDrop();
}

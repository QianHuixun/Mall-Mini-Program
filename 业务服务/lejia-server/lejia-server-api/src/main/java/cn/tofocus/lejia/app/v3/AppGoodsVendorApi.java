//package cn.tofocus.lejia.app.v3;
//
//import java.util.List;
//
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import cn.tofocus.core.Result;
//import cn.tofocus.core.page.PageResult;
//import cn.tofocus.lejia.app.AppTags;
//import cn.tofocus.lejia.bean.dto.market.MktGtypeOnList;
//import cn.tofocus.lejia.bean.dto.market.v3.MktVendorGoodsOnInfo;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//
//public interface AppGoodsVendorApi
//{
//    @Operation(summary = "获取商品列表", tags = AppTags.mobileVendorGoods)
//    @PostMapping(value = "/query")
//    public Result<PageResult<MktVendorGoodsOnInfo>> queryGoods(
//        @RequestParam(value = "page", required = false, defaultValue = "0") @Parameter(description = "页号") int page,
//        @RequestParam(value = "pagesize", required = false, defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
//        @RequestParam(value = "title", required = false) @Parameter(description = "标题") String title,
//        @RequestParam(value = "gtype", required = false) @Parameter(description = "分类id") Integer gtype,
//        @RequestParam(value = "enabled", required = false) @Parameter(description = "是否上架") Boolean enabled,
//        @RequestParam(value = "status", defaultValue = "0") @Parameter(description = "发售状态") Integer status);
//    
//    @Operation(summary = "获取商品分类列表", tags = AppTags.mobileVendorGoods)
//    @PostMapping(value = "/gtype/query")
//    public Result<List<MktGtypeOnList>> queryGtype();
//    
//    @Operation(summary = "新增商品", tags = AppTags.mobileVendorGoods)
//    @PostMapping("/ins")
//    public Result<Integer> insGoods(@RequestBody MktVendorGoodsOnInfo entity);
//    
//    @Operation(summary = "修改商品", tags = AppTags.mobileVendorGoods)
//    @PostMapping("/upd")
//    public Result<Integer> updGoods(@RequestBody MktVendorGoodsOnInfo entity);
//}

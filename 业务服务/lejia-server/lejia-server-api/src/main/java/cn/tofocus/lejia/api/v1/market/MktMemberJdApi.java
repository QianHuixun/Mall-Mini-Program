//package cn.tofocus.lejia.api.v1.market;
//
//import java.util.List;
//
//import javax.validation.Valid;
//
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import cn.tofocus.core.Result;
//import cn.tofocus.core.page.PageResult;
//import cn.tofocus.lejia.api.v1.ApiTags;
//import cn.tofocus.lejia.bean.dto.market.jd.MktMemberJdAdjustDTO;
//import cn.tofocus.lejia.bean.dto.market.jd.MktMemberJdLineOnPage;
//import cn.tofocus.lejia.bean.dto.market.jd.MktMemberJdOnPage;
//import cn.tofocus.lejia.bean.dto.market.jd.MktMemberJdTagDrop;
//import cn.tofocus.lejia.bean.enums.JdOperationType;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//
//public interface MktMemberJdApi
//{
//    @Operation(summary = "京东标签下拉", tags = ApiTags.custMemberJd)
//    @PostMapping("/tag/list/drop")
//    Result<List<MktMemberJdTagDrop>> listTagDrop();
//    
//    @Operation(summary = "查询京东账户", tags = ApiTags.custMemberJd)
//    @PostMapping("/query")
//    Result<PageResult<MktMemberJdOnPage>> query(
//        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
//        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
//        @RequestParam(value = "mobile", required = false) @Parameter(description = "手机") String mobile,
//        @RequestParam(value = "tags", required = false) @Parameter(description = "标签主键") List<Integer> tags);
//    
//    @Operation(summary = "清空京东余额", tags = ApiTags.custMemberJd)
//    @PostMapping("/balance/clear")
//    Result<Boolean> clearBalance(
//        @RequestParam(value = "tags", required = false) @Parameter(description = "标签主键") List<Integer> tags);
//    
//    @Operation(summary = "调整京东余额", tags = ApiTags.custMemberJd)
//    @PostMapping("/balance/adjust")
//    Result<Boolean> adjustBalance(@RequestBody @Valid MktMemberJdAdjustDTO dto);
//    
//    @Operation(summary = "查询京东明细", tags = ApiTags.custMemberJd)
//    @PostMapping("/line/query")
//    Result<PageResult<MktMemberJdLineOnPage>> queryLine(
//        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
//        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
//        @RequestParam(value = "mobile", required = false) @Parameter(description = "手机") String mobile,
//        @RequestParam(value = "tags", required = false) @Parameter(description = "标签主键") List<Integer> tags,
//        @RequestParam(value = "operationTypes", required = false) @Parameter(description = "操作类型") List<JdOperationType> operationTypes,
//        @RequestParam(value = "startDate", required = false) @Parameter(description = "开始日期") String startDate,
//        @RequestParam(value = "endDate", required = false) @Parameter(description = "结束日期") String endDate,
//        @RequestParam(value = "remark", required = false) @Parameter(description = "备注") String remark);
//}

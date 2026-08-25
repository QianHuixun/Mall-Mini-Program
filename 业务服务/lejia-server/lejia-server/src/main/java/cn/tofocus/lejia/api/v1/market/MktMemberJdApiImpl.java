//package cn.tofocus.lejia.api.v1.market;
//
//import java.util.List;
//
//import javax.servlet.http.HttpServletResponse;
//import javax.transaction.Transactional;
//import javax.validation.Valid;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//import cn.tofocus.core.Result;
//import cn.tofocus.core.page.PageResult;
//import cn.tofocus.lejia.api.v1.ApiTags;
//import cn.tofocus.lejia.bean.dto.market.jd.MktMemberJdAdjustDTO;
//import cn.tofocus.lejia.bean.dto.market.jd.MktMemberJdLineOnPage;
//import cn.tofocus.lejia.bean.dto.market.jd.MktMemberJdOnPage;
//import cn.tofocus.lejia.bean.dto.market.jd.MktMemberJdTagDrop;
//import cn.tofocus.lejia.bean.enums.JdOperationType;
//import cn.tofocus.lejia.domain.market.MktMemberJdManager;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//
//@RestController
//@RequestMapping("/v1/market/member/jd")
//public class MktMemberJdApiImpl implements MktMemberJdApi
//{
//    @Autowired
//    private MktMemberJdManager memberJdManager;
//    
//    @Override
//    public Result<List<MktMemberJdTagDrop>> listTagDrop()
//    {
//        List<MktMemberJdTagDrop> res = memberJdManager.listTagDrop();
//        return new Result<>(res);
//    }
//    
//    @Override
//    public Result<PageResult<MktMemberJdOnPage>> query(int page, int pagesize, String mobile, List<Integer> tags)
//    {
//        PageResult<MktMemberJdOnPage> res = memberJdManager.query(page, pagesize, mobile, tags);
//        return new Result<>(res);
//    }
//    
//    @Override
//    public Result<Boolean> clearBalance(List<Integer> tags)
//    {
//        boolean sign = memberJdManager.clearBalance(tags);
//        return new Result<>(sign);
//    }
//    
//    @Override
//    public Result<Boolean> adjustBalance(@Valid MktMemberJdAdjustDTO dto)
//    {
//        boolean sign = memberJdManager.adjustBalance(dto);
//        return new Result<>(sign);
//    }
//    
//    @Override
//    public Result<PageResult<MktMemberJdLineOnPage>> queryLine(int page, int pagesize, String mobile,
//        List<Integer> tags, List<JdOperationType> operationTypes, String startDate, String endDate, String remark)
//    {
//        PageResult<MktMemberJdLineOnPage> res =
//            memberJdManager.queryLine(page, pagesize, mobile, tags, operationTypes, startDate, endDate, remark);
//        return new Result<>(res);
//    }
//    
//    @Operation(summary = "下载充值模板", tags = ApiTags.custMemberMsd)
//    @Transactional
//    @PostMapping(value = "/recharge/template")
//    public void downloadRechargeTemplate(HttpServletResponse response)
//    {
//        memberJdManager.downloadRechargeTemplate(response);
//    }
//    
//    @Operation(summary = "导入批量充值", tags = ApiTags.custMemberMsd)
//    @Transactional
//    @PostMapping(value = "/recharge/import")
//    public void importRecharge(MultipartFile myfile, HttpServletResponse response)
//    {
//        memberJdManager.importRecharge(myfile, response);
//    }
//    
//    @Deprecated
//    @Operation(summary = "导出民生豆明细", tags = ApiTags.custMemberMsd)
//    @PostMapping(value = "/line/export")
//    public void exportLine(
//        @RequestParam(value = "mobile", required = false) @Parameter(description = "手机") String mobile,
//        @RequestParam(value = "tags", required = false) @Parameter(description = "标签主键") List<Integer> tags,
//        @RequestParam(value = "operationTypes", required = false) @Parameter(description = "操作类型") List<JdOperationType> operationTypes,
//        @RequestParam(value = "startDate", required = false) @Parameter(description = "开始日期") String startDate,
//        @RequestParam(value = "endDate", required = false) @Parameter(description = "结束日期") String endDate,
//        @RequestParam(value = "remark", required = false) @Parameter(description = "备注") String remark,
//        HttpServletResponse response)
//    {
//        memberJdManager.exportLine(mobile, tags, operationTypes, startDate, endDate, remark, response);
//    }
//}

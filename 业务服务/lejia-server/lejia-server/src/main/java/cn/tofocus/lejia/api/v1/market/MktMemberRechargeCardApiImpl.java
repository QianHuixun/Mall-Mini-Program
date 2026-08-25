package cn.tofocus.lejia.api.v1.market;


import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.lejia.bean.enums.member.RechargeCardType;
import cn.tofocus.lejia.exception.LejiaErrCode;
import com.alibaba.excel.exception.ExcelDataConvertException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cn.tofocus.common.excel.ExcelHelper;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.MemberRechargeCardExportExcel;
import cn.tofocus.lejia.bean.dto.MemberRechargeCardTemplateExcel;
import cn.tofocus.lejia.bean.dto.market.recharge.RechargeCardOnPage;
import cn.tofocus.lejia.bean.dto.market.recharge.RechargeCardSum;
import cn.tofocus.lejia.domain.RechargeCardManager;
import cn.tofocus.lejia.util.ExportUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@Slf4j
@RestController
@RequestMapping("/v1/market/member/recharge/card")
public class MktMemberRechargeCardApiImpl
{
    @Autowired
    private RechargeCardManager manager;
    
    @Autowired
    private ExcelHelper excelHelper;
    
    @Operation(summary = "分页查询", tags = ApiTags.memberRechargeCard)
    @PostMapping("/query")
    public Result<PageResult<RechargeCardOnPage>> query(
        @RequestParam(value = "page", defaultValue = "0", required = false) @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10", required = false) @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "types", required = false) @Parameter(description = "卡类型（多个用英文逗号分隔）") List<String> types,
        @RequestParam(value = "cardNumber", required = false) @Parameter(description = "卡号") String cardNumber,
        @RequestParam(value = "status", required = false) @Parameter(description = "卡号") String status,
        @RequestParam(value = "mobile", required = false) @Parameter(description = "使用") String mobile,
        @RequestParam(value = "createdStart", required = false) @Parameter(description = "创建-开始") String createdStart,
        @RequestParam(value = "createdEnd", required = false) @Parameter(description = "创建-结束") String createdEnd,
        @RequestParam(value = "useStart", required = false) @Parameter(description = "使用-开始") String useStart,
        @RequestParam(value = "useEnd", required = false) @Parameter(description = "使用-结束") String useEnd)
    {
        PageResult<RechargeCardOnPage> res = manager
            .query(page, pagesize, types, cardNumber, status, mobile, createdStart, createdEnd, useStart, useEnd);
        return new Result<>(res);
    }
    
    @Operation(summary = "合计", tags = ApiTags.memberRechargeCard)
    @PostMapping("/query/sum")
    public Result<RechargeCardSum> querySum(
        @RequestParam(value = "types", required = false) @Parameter(description = "卡类型（多个用英文逗号分隔）") List<String> types,
        @RequestParam(value = "cardNumber", required = false) @Parameter(description = "卡号") String cardNumber,
        @RequestParam(value = "status", required = false) @Parameter(description = "卡号") String status,
        @RequestParam(value = "mobile", required = false) @Parameter(description = "使用") String mobile,
        @RequestParam(value = "createdStart", required = false) @Parameter(description = "创建-开始") String createdStart,
        @RequestParam(value = "createdEnd", required = false) @Parameter(description = "创建-结束") String createdEnd,
        @RequestParam(value = "useStart", required = false) @Parameter(description = "使用-开始") String useStart,
        @RequestParam(value = "useEnd", required = false) @Parameter(description = "使用-结束") String useEnd)
    {
        RechargeCardSum res =
            manager.querySum(types, cardNumber, status, mobile, createdStart, createdEnd, useStart, useEnd);
        return new Result<>(res);
    }
    
    @Operation(summary = "生成卡密", tags = ApiTags.memberRechargeCard)
    @PostMapping("/add")
    public Result<Boolean> add(@RequestParam(value = "cost") @Parameter(description = "面值") BigDecimal cost,
        @RequestParam(value = "num") @Parameter(description = "数量") Integer num,
        @RequestParam(value = "deadline") @Parameter(description = "截止日期") Date deadline,
        @RequestParam(value = "type") @Parameter(description = "卡类型") RechargeCardType type,
        @RequestParam(value = "tag", required = false) @Parameter(description = "标签") Integer tag)
    {
        return new Result<>(manager.add(cost, num, deadline, type, tag));
    }
    
    @Operation(summary = "作废", tags = ApiTags.memberRechargeCard)
    @PostMapping("/cancel")
    public Result<Boolean> cancel(@RequestParam(value = "keys")@Parameter(description = "主键")List<String> keys)
    {
        return new Result<>(manager.cancel(keys));
    }
    
    @Operation(summary = "下载导入模板", tags = ApiTags.memberRechargeCard)
    @PostMapping("/downTemplate")
    public void downTemplate(HttpServletRequest request, HttpServletResponse response)
    {
        OutputStream out;
        try
        {
            out = response.getOutputStream();
            excelHelper.excelTemplate(out, MemberRechargeCardTemplateExcel.class);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    @Operation(summary = "导入卡密", tags = ApiTags.memberRechargeCard)
    @PostMapping("/importExcel")
    public void importExcel(MultipartFile myfile, HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            manager.importExcel(myfile, response.getOutputStream());
        }
        catch (ExcelDataConvertException dataConvertException)
        {
            log.error("数据格式转换异常", dataConvertException);
            throw TofocusException.of(LejiaErrCode.IMPORT_ERROR, "存在格式异常数据，导入失败");
        }
        catch (Exception e)
        {
            log.error("导入京东商品失败", e);
            throw TofocusException.of(LejiaErrCode.IMPORT_ERROR);
        }
    }
    
    @Operation(summary = "导出", tags = ApiTags.memberRechargeCard)
    @PostMapping("/exportexcel")
    public void exportexcel(
        @RequestParam(value = "types", required = false) @Parameter(description = "卡类型（多个用英文逗号分隔）") List<String> types,
        @RequestParam(value = "cardNumber", required = false) @Parameter(description = "卡号") String cardNumber,
        @RequestParam(value = "status", required = false) @Parameter(description = "卡号") String status,
        @RequestParam(value = "mobile", required = false) @Parameter(description = "使用") String mobile,
        @RequestParam(value = "createdStart", required = false) @Parameter(description = "创建-开始") String createdStart,
        @RequestParam(value = "createdEnd", required = false) @Parameter(description = "创建-结束") String createdEnd,
        @RequestParam(value = "useStart", required = false) @Parameter(description = "使用-开始") String useStart,
        @RequestParam(value = "useEnd", required = false) @Parameter(description = "使用-结束") String useEnd,
        HttpServletResponse response)
    {
        PageResult<RechargeCardOnPage> pageResult =
            manager.query(0, 100000, types, cardNumber, status, mobile, createdStart, createdEnd, useStart, useEnd);
        List<MemberRechargeCardExportExcel> list =
            BeanUtil.beanListFrom(MemberRechargeCardExportExcel.class, pageResult.getContent());
        ExportUtil.exportData(MemberRechargeCardExportExcel.class, list, response, "充值卡密管理表", "Sheet1");
    }
}

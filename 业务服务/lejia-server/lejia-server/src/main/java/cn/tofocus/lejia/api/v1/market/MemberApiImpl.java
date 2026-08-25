package cn.tofocus.lejia.api.v1.market;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cn.tofocus.common.excel.ExcelHelper;
import cn.tofocus.core.Result;
import cn.tofocus.core.log.LogApi;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.MemberTagTemplateExcel;
import cn.tofocus.lejia.bean.dto.market.MktMemberCardDTO;
import cn.tofocus.lejia.bean.dto.market.MktMemberCommLineOnList;
import cn.tofocus.lejia.bean.dto.market.MktMemberConsumption;
import cn.tofocus.lejia.bean.dto.market.MktMemberOnList;
import cn.tofocus.lejia.bean.dto.market.MktMemberPointLineOnList;
import cn.tofocus.lejia.bean.dto.market.TagOnList;
import cn.tofocus.lejia.bean.enums.CommSourceType;
import cn.tofocus.lejia.bean.enums.LevelType;
import cn.tofocus.lejia.bean.enums.SourceType;
import cn.tofocus.lejia.domain.market.MemberManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RequestMapping("/v1/market/member")
@RestController
public class MemberApiImpl implements MktMemberApi
{
    
    @Autowired
    private MemberManager memerbManager;
    
    @Autowired
    private ExcelHelper excelHelper;
    
    @Override
    @LogApi(operation = "开通年费会员", format = "{name}开通年费会员")
    public Result<Boolean> openMember(Integer pkey, String name)
    {
        return new Result<>(memerbManager.openMember(pkey));
    }
    
    @Override
    @LogApi(operation = "调整会员积分", format = "调整会员积分,调整积分:{ponint}, 来源:{source}, 单据:{formid}, 备注:{remark}")
    public Result<Boolean> adjustmentPointMember(Integer pkey, Integer point, SourceType source, String formid,
        String remark)
    {
        return new Result<>(memerbManager.adjustmentPointMember(pkey, point, formid, remark, source));
    }
    
    @Override
    public Result<PageResult<MktMemberOnList>> queryMember(int page, int pagesize, LevelType level, String name,
        String mobile, String area, String remark, String startCreatedTime, String endCreatedTime,
        String startLastConsumeTime, String endLastConsumeTime, String lastConsumeFarmer, String source, List<Integer> tagKeys)
    {
        return new Result<>(memerbManager.queryMember(page,
            pagesize,
            level,
            name,
            mobile,
            area,
            remark,
            startCreatedTime,
            endCreatedTime,
            startLastConsumeTime,
            endLastConsumeTime,
            lastConsumeFarmer,
            source,
            tagKeys));
    }
    
    @Override
    public Result<PageResult<MktMemberPointLineOnList>> queryMemberPointLine(int page, int pagesize, Integer member,
        SourceType source, String mobile, String name, String startDate, String endDate, Boolean direct)
    {
        return new Result<>(memerbManager
            .queryMemberPointLine(page, pagesize, member, source, mobile, name, startDate, endDate, direct));
    }
    
    @Override
    public Result<PageResult<MktMemberConsumption>> queryMemberConsumption(int page, int pagesize, Integer member)
    {
        return new Result<>(memerbManager.queryMemberConsumption(member, page, pagesize));
    }
    
    @Override
    public Result<PageResult<MktMemberCardDTO>> queryMemberCard(int page, int pagesize, Integer member)
    {
        return new Result<>(memerbManager.queryMemberCard(member, page, pagesize));
    }
    
    @Override
    public Result<PageResult<MktMemberCommLineOnList>> queryMemberCommLine(int page, int pagesize,
        CommSourceType source, Integer member, Boolean direct, String mobile, String startDate, String endDate)
    {
        return new Result<>(
            memerbManager.queryMemberCommLine(page, pagesize, source, member, direct, mobile, startDate, endDate));
    }

    @Operation(summary = "导出余额明细列表", tags = ApiTags.custMember)
    @PostMapping("/comm/export")
    public void exportMemberCommLine(
        @RequestParam(value = "source", required = false) @Parameter(description = "积分来源") CommSourceType source,
        @RequestParam(value = "member", required = false) @Parameter(description = "会员pkey") Integer member,
        @RequestParam(value = "direct", required = false) @Parameter(description = "借贷") Boolean direct,
        @RequestParam(value = "mobile", required = false) @Parameter(description = "手机号码") String mobile,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "开始时间") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "结束时间") String endDate,
        HttpServletResponse response)
    {
        memerbManager.exportMemberCommLine(source, member, direct, mobile, startDate, endDate, response);
    }
    
    @Override
    public Result<Boolean> tags(Integer pkey, String remark)
    {
        return new Result<>(memerbManager.tags(pkey, remark));
    }
    
    @Operation(summary = "导出会员信息列表", tags = ApiTags.custMember)
    @PostMapping("/export/memberInfo")
    public void exportMemberInfo(
        @RequestParam(value = "name", required = false) @Parameter(description = "名称") String name,
        @RequestParam(value = "mobile", required = false) @Parameter(description = "手机") String mobile,
        @RequestParam(value = "remark", required = false) @Parameter(description = "备注") String remark,
        @RequestParam(value = "startCreatedTime", required = false) @Parameter(description = "开始建档时间") String startCreatedTime,
        @RequestParam(value = "endCreatedTime", required = false) @Parameter(description = "结束建档时间") String endCreatedTime,
        @RequestParam(value = "startLastConsumeTime", required = false) @Parameter(description = "开始最近消费时间") String startLastConsumeTime,
        @RequestParam(value = "endLastConsumeTime", required = false) @Parameter(description = "结束最近消费时间") String endLastConsumeTime,
        @RequestParam(value = "lastConsumeFarmer", required = false) @Parameter(description = "最近消费市场") String lastConsumeFarmer,
        @RequestParam(value = "source", required = false) @Parameter(description = "用户来源") String source,
        @RequestParam(value = "tagKeys", required = false) @Parameter(description = "标签主键") List<Integer> tagKeys,
        HttpServletResponse response)
    {
        memerbManager.exportMemberInfo(name,
            mobile,
            remark,
            startCreatedTime,
            endCreatedTime,
            startLastConsumeTime,
            endLastConsumeTime,
            lastConsumeFarmer,
            source,
            tagKeys,
            response);
    }
    
    @Operation(summary = "模板下载", tags = ApiTags.custMember)
    @PostMapping("/downTemplate")
    public void downTemplate(HttpServletRequest request, HttpServletResponse response)
    {
        OutputStream out;
        try
        {
            out = response.getOutputStream();
            excelHelper.excelTemplate(out, MemberTagTemplateExcel.class);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    @Operation(summary = "导入标签", tags = ApiTags.custMember)
    @PostMapping("/importexcel")
    public void importexcel(MultipartFile myfile, HttpServletRequest request, HttpServletResponse response)
    {
        try (OutputStream out = response.getOutputStream())
        {
            memerbManager.importExcel(myfile, out);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public Result<PageResult<TagOnList>> getMemberTags(int page, int pagesize, Integer pkey, String name, String description)
    {
        return new Result<>(memerbManager.getMemberTags(page, pagesize, pkey, name, description));
    }

    @Override
    public Result<Boolean> markMemberTags(List<Integer> pkeys, List<Integer> tagKeys)
    {
        return new Result<>(memerbManager.markMemberTags(pkeys, tagKeys));
    }

    @Override
    public Result<List<Integer>> listMemberTags(Integer pkey, String name, String description)
    {
        return new Result<>(memerbManager.listMemberTags(pkey, name, description));
    }
}

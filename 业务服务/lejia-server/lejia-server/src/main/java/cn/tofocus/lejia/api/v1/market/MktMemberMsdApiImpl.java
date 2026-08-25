package cn.tofocus.lejia.api.v1.market;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.validation.Valid;

import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.lejia.exception.LejiaErrCode;
import com.alibaba.excel.exception.ExcelDataConvertException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.config.MsdPayConfig;
import cn.tofocus.lejia.bean.dto.market.MktMemberMsdAdjustDTO;
import cn.tofocus.lejia.bean.dto.market.MktMemberMsdLineOnPage;
import cn.tofocus.lejia.bean.dto.market.MktMemberMsdOnPage;
import cn.tofocus.lejia.bean.dto.market.MktMemberMsdTagDrop;
import cn.tofocus.lejia.bean.enums.MsdOperationType;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.sys.SysDynamicAttributeDao;
import cn.tofocus.lejia.domain.market.MktMemberMsdManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@Slf4j
@RestController
@RequestMapping("/v1/market/member/msd")
public class MktMemberMsdApiImpl implements MktMemberMsdApi
{
    @Autowired
    private MktMemberMsdManager memberMsdManager;
    
    @Autowired
    private SysDynamicAttributeDao dynamicAttributeDao;
    
    @Override
    @Deprecated
    public Result<List<MktMemberMsdTagDrop>> listTagDrop()
    {
        List<MktMemberMsdTagDrop> res = memberMsdManager.listTagDrop();
        return new Result<>(res);
    }
    
    @Override
    public Result<PageResult<MktMemberMsdOnPage>> query(int page, int pagesize, String mobile, List<Integer> tags)
    {
        PageResult<MktMemberMsdOnPage> res = memberMsdManager.query(page, pagesize, mobile, tags);
        return new Result<>(res);
    }

    @Operation(summary = "导出民生豆账户", tags = ApiTags.custMemberMsd)
    @PostMapping(value = "/export")
    public void export(
        @RequestParam(value = "mobile", required = false) @Parameter(description = "手机") String mobile,
        @RequestParam(value = "tags", required = false) @Parameter(description = "标签主键") List<Integer> tags,
        HttpServletResponse response)
    {
        memberMsdManager.export(mobile, tags, response);
    }
    
    @Override
    public Result<Boolean> clearBalance(List<Integer> tags)
    {
        boolean sign = memberMsdManager.clearBalance(tags);
        return new Result<>(sign);
    }
    
    @Override
    public Result<Boolean> adjustBalance(@Valid MktMemberMsdAdjustDTO dto)
    {
        boolean sign = memberMsdManager.adjustBalance(dto);
        return new Result<>(sign);
    }
    
    @Override
    public Result<PageResult<MktMemberMsdLineOnPage>> queryLine(int page, int pagesize, String mobile,
        List<Integer> tags, List<MsdOperationType> operationTypes, String startDate, String endDate, String remark)
    {
        PageResult<MktMemberMsdLineOnPage> res =
            memberMsdManager.queryLine(page, pagesize, mobile, tags, operationTypes, startDate, endDate, remark);
        return new Result<>(res);
    }
    
    @Operation(summary = "下载充值模板", tags = ApiTags.custMemberMsd)
    @Transactional
    @PostMapping(value = "/recharge/template")
    public void downloadRechargeTemplate(HttpServletResponse response)
    {
        memberMsdManager.downloadRechargeTemplate(response);
    }
    
    @Operation(summary = "导入批量充值", tags = ApiTags.custMemberMsd)
    @Transactional
    @PostMapping(value = "/recharge/import")
    public void importRecharge(MultipartFile myfile, HttpServletResponse response)
    {
        try
        {
            memberMsdManager.importRecharge(myfile, response);
        }
        catch (ExcelDataConvertException dataConvertException)
        {
            log.error("数据格式转换异常", dataConvertException);
            throw TofocusException.of(LejiaErrCode.IMPORT_ERROR, "存在格式异常数据，导入失败");
        }
        catch (TofocusException e)
        {
            // 业务异常（如表头不匹配）直接透传，保留具体提示信息
            throw e;
        }
        catch (Exception e)
        {
            log.error("批量充值热力豆账户失败", e);
            throw TofocusException.of(LejiaErrCode.IMPORT_ERROR);
        }
    }
    
    @Deprecated
    @Operation(summary = "导出民生豆明细", tags = ApiTags.custMemberMsd)
    @PostMapping(value = "/line/export")
    public void exportLine(
        @RequestParam(value = "mobile", required = false) @Parameter(description = "手机") String mobile,
        @RequestParam(value = "tags", required = false) @Parameter(description = "标签主键") List<Integer> tags,
        @RequestParam(value = "operationTypes", required = false) @Parameter(description = "操作类型") List<MsdOperationType> operationTypes,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "开始日期") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "结束日期") String endDate,
        @RequestParam(value = "remark", required = false) @Parameter(description = "备注") String remark,
        HttpServletResponse response)
    {
        memberMsdManager.exportLine(mobile, tags, operationTypes, startDate, endDate, remark, response);
    }

    @Override
    public Result<MsdPayConfig> getMsdPayConfig()
    {
        MsdPayConfig res = dynamicAttributeDao.getSysAttribute(MsdPayConfig.class, CurrentSession.ascriptionPkey());
        if(res == null)
        {
            res = new MsdPayConfig();
            res.setFarmerGoods(false);
            res.setSysGoods(false);
        }
        return new Result<>(res);
    }

    @Override
    public Result<Boolean> setMsdPayConfig(MsdPayConfig config)
    {
        dynamicAttributeDao.setSysAttribute(config, CurrentSession.ascriptionPkey());
        return new Result<>(true);
    }
}

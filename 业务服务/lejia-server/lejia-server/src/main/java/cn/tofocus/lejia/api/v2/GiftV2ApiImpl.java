package cn.tofocus.lejia.api.v2;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.log.LogApi;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.CardStatisticsInfo;
import cn.tofocus.lejia.bean.dto.v2.gift.MktGiftV2Info;
import cn.tofocus.lejia.bean.dto.v2.gift.MktGiftV2OnPage;
import cn.tofocus.lejia.bean.dto.v2.gift.MktMemberGiftV2OnPage;
import cn.tofocus.lejia.bean.enums.CardStatus;
import cn.tofocus.lejia.domain.v2.GiftV2Manager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RequestMapping("/v2/market/gift")
@RestController
public class GiftV2ApiImpl implements GiftV2Api
{
    @Autowired
    private GiftV2Manager giftManager;
    
    @Override
    public Result<PageResult<MktGiftV2OnPage>> query(int page, int pagesize, String title, Boolean enabled,
        Boolean invalid)
    {
        PageResult<MktGiftV2OnPage> result = giftManager.query(page, pagesize, title, enabled, invalid);
        return new Result<>(result);
    }
    
    @Override
    public Result<PageResult<MktMemberGiftV2OnPage>> queryUse(int page, int pagesize, String userFarmer, String startTime, String endTime,
        String st, String et, String mobile, String title, CardStatus status, Boolean invalid)
    {
        PageResult<MktMemberGiftV2OnPage> result =
            giftManager.queryUse(page, pagesize, userFarmer, startTime, endTime, st, et, mobile, title, status, invalid);
        return new Result<>(result);
    }

    @Operation(summary = "导出已使用礼品券列表", tags = ApiTags.custGift_V2)
    @PostMapping(value = "/export/use")
    public void exportUse(
        @RequestParam(value = "userFarmer", required = false) @Parameter(description = "核销市场") String userFarmer,
        @RequestParam(value = "startTime", required = false) @Parameter(description = "开始时间") String startTime,
        @RequestParam(value = "endTime", required = false) @Parameter(description = "结束时间") String endTime,
        @RequestParam(value = "st", required = false) @Parameter(description = "开始时间-领取") String st,
        @RequestParam(value = "et", required = false) @Parameter(description = "结束时间-领取")String et,
        @RequestParam(value = "mobile", required = false) @Parameter(description = "手机号") String mobile,
        @RequestParam(value = "title", required = false) @Parameter(description = "卡券名称") String title,
        @RequestParam(value = "status", required = false) @Parameter(description = "卡券状态") CardStatus status,
        @RequestParam(value = "invalid", required = false) @Parameter(description = "卡券状态,false:未失效") Boolean invalid,
        HttpServletResponse response)
    {
        giftManager.exportUse(userFarmer, startTime, endTime, st, et, mobile, title, status, invalid, response);
    }
    
    @Override
    public Result<MktGiftV2Info> get(Integer pkey)
    {
        MktGiftV2Info info = giftManager.get(pkey);
        return new Result<>(info);
    }
    
    @Override
    @LogApi(operation = "新增礼品券", format = "新增礼品券[{info.title}]")
    public Result<Boolean> ins(@Valid MktGiftV2Info info)
    {
        if (info.getEffective() == null && (info.getStartDate() == null || info.getEndDate() == null))
            throw TofocusException.of(LejiaErrCode.DATA_NOT_EMPTY, "有效期不能为空");
        boolean sign = giftManager.save(info);
        return new Result<>(sign);
    }
    
    @Override
    @LogApi(operation = "修改礼品券", format = "修改礼品券[{info.title}({info.pkey})]")
    public Result<Boolean> upd(@Valid MktGiftV2Info info)
    {
        if (info.getPkey() == null) throw TofocusException.of(LejiaErrCode.DATA_NOT_EMPTY, "主键不能为空");
        if (info.getEffective() == null && (info.getStartDate() == null || info.getEndDate() == null))
            throw TofocusException.of(LejiaErrCode.DATA_NOT_EMPTY, "有效期不能为空");
        boolean sign = giftManager.save(info);
        return new Result<>(sign);
    }
    
    @Override
    @LogApi(operation = "失效礼品券", format = "失效礼品券 主键为:{pkey}")
    public Result<Boolean> invalid(Integer pkey)
    {
        boolean sign = giftManager.invalid(pkey);
        return new Result<>(sign);
    }
    
    @Override
    @LogApi(operation = "启用礼品券", format = "启用礼品券 主键为:{pkey}")
    public Result<Boolean> start(Integer pkey)
    {
        boolean sign = giftManager.enable(pkey, true);
        return new Result<>(sign);
    }
    
    @Override
    @LogApi(operation = "停用礼品券", format = "停用礼品券 主键为:{pkey}")
    public Result<Boolean> stop(Integer pkey)
    {
        boolean sign = giftManager.enable(pkey, false);
        return new Result<>(sign);
    }
    
    @Override
    public Result<Boolean> issue(Integer pkey, Integer member, String mobile, Integer num)
    {
        boolean sign = giftManager.issue(pkey, member, mobile, num);
        return new Result<>(sign);
    }

    @Override
    public Result<CardStatisticsInfo> queryUseSum(String userFarmer, String startTime, String endTime, String st,
        String et, String mobile, String title, CardStatus status, Boolean invalid)
    {
        return new Result<>(giftManager.queryUseSum(userFarmer, startTime, endTime, st, et, mobile, title, status, invalid));
    }
}

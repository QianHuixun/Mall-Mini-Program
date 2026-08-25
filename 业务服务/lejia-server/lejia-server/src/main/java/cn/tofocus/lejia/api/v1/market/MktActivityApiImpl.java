package cn.tofocus.lejia.api.v1.market;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.msgpipe.queue.MsgListener;
import cn.tofocus.core.msgpipe.queue.MsgSenderTemplate;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.Constant.SysConfig;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.MktActivityInfo;
import cn.tofocus.lejia.bean.dto.market.MktActivityIssueOnPage;
import cn.tofocus.lejia.bean.dto.market.MktActivityOnList;
import cn.tofocus.lejia.bean.dto.market.MktActivityOnPage;
import cn.tofocus.lejia.bean.entity.market.MktActivity;
import cn.tofocus.lejia.bean.entity.sys.AccountEntity;
import cn.tofocus.lejia.bean.enums.AccountType;
import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import cn.tofocus.lejia.dao.sys.AccountDao;
import cn.tofocus.lejia.dao.sys.SysConfigDao;
import cn.tofocus.lejia.domain.TagManager;
import cn.tofocus.lejia.domain.WxManager;
import cn.tofocus.lejia.domain.market.MktActivityManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.util.WxDataBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/v1/market/activity")
@RestController
@Slf4j
public class MktActivityApiImpl implements MktActivityApi
{
    @Autowired
    private MktActivityManager activityManager;
    
    @Autowired
    private TagManager tagManager;
    
    @Autowired
    private WxManager wxManager;
    
    @Autowired
    private MsgSenderTemplate msgSenderTemplate;
    
    @Autowired
    private SysConfigDao sysConfigDao;
    
    @Autowired
    private AccountDao accountDao;
    
    @Override
    public Result<PageResult<MktActivityOnPage>> query(int page, int pagesize, String name, Boolean enabled,
        String farmer)
    {
        PageResult<MktActivityOnPage> result = activityManager.query(page, pagesize, name, enabled, farmer);
        return new Result<>(result);
    }
    
    @Override
    public Result<List<MktActivityOnList>> list(String name, Boolean enabled, String farmer)
    {
        List<MktActivityOnList> result = activityManager.list(name, enabled, farmer);
        return new Result<>(result);
    }
    
    @Override
    public Result<PageResult<MktActivityIssueOnPage>> queryIssue(int page, int pagesize, String memberMobile,
        String startDate, String endDate, Integer activity)
    {
        PageResult<MktActivityIssueOnPage> result =
            activityManager.queryIssue(page, pagesize, memberMobile, startDate, endDate, activity);
        return new Result<>(result);
    }
    
    @Operation(summary = "导出卡券活动发放记录", tags = ApiTags.ACTIVITY)
    @PostMapping(value = "/issue/export")
    public void exportIssue(
        @RequestParam(value = "memberMobile", required = false) @Parameter(description = "会员手机号") String memberMobile,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "开始时间") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "结束时间") String endDate,
        @RequestParam(value = "activity", required = false) @Parameter(description = "卡券活动") Integer activity,
        HttpServletResponse response)
    {
        activityManager.exportIssue(memberMobile, startDate, endDate, activity, response);
    }
    
    @Override
    public Result<MktActivityInfo> get(Integer pkey)
    {
        MktActivityInfo info = activityManager.get(pkey);
        return new Result<>(info);
    }
    
    @Override
    public Result<Boolean> add(@Valid MktActivityInfo info)
    {
        MktActivity activity = activityManager.save(info);
        if (info.isSendWechatMsg())
        {
            try
            {
                msgSenderTemplate.put("", "", activity, new WxMsgSender());
            }
            catch (Exception e)
            {
                log.warn("发送优惠券通知异常", e);
            }
        }
        return new Result<>(true);
    }
    
    @Override
    public Result<Boolean> upd(@Valid MktActivityInfo info)
    {
        if (info.getPkey() == null)
            throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "主键不能为空");
        MktActivity activity = activityManager.save(info);
        if (info.isSendWechatMsg())
        {
            try
            {
                msgSenderTemplate.put("", "", activity, new WxMsgSender());
            }
            catch (Exception e)
            {
                log.warn("发送优惠券通知异常", e);
            }
        }
        return new Result<>(true);
    }
    
    class WxMsgSender implements MsgListener<MktActivity, Boolean>
    {
        
        @Override
        public Boolean handleMessage(String pipeId, String correlationId, MktActivity activity)
            throws Exception
        {
            Integer ascription = activity.getAscription();
            String templateid = sysConfigDao.getTemplate(SysConfig.TEMPLATE_ACTIVITY, ascription);
            if (templateid == null)
                return false;
            List<Integer> tags = MemberVisibleRange.TAG.equals(activity.getVisibleRange())
                ? tagManager.getActivityTags(activity.getPkey().longValue())
                : null;
            List<String> openids = tagManager.listMemberOpenid(ascription, tags);
            
            JSONObject data = new WxDataBuilder()
                .param("thing6")
                .value(activity.getName())
                .param("time11")
                .value(DateUtil.formatDate(activity.getStartTime()) + "~" + DateUtil.formatDate(activity.getEndTime()))
                .build();
            
            for (String openid : openids)
            {
                AccountEntity account = accountDao.get(ascription, AccountType.USER);
                wxManager.sendWeappSubscribeMessage(account, openid, templateid, "pages/activity/coupon/index?pkey=" + activity.getPkey(), data);
            }
            return true;
        }
        
        @Override
        public void handleResult(String pipeId, String correlationId, Result<Boolean> result)
            throws Exception
        {
            if (!result.isSuccess())
                log.warn("发送活动通知异常, {}", result.getMsg());
            else if (!result.getResult())
                log.warn("活动通知的模板未配置");
        }
    }
    
    @Override
    public Result<Boolean> enable(Integer pkey, Boolean enabled)
    {
        boolean sign = activityManager.enable(pkey, enabled);
        return new Result<>(sign);
    }
    
    @Operation(summary = "生成活动二维码", tags = ApiTags.ACTIVITY)
    @GetMapping(value = "/down/qrCode")
    public Result<Boolean> qrCode(@RequestParam(value = "pkey") @Parameter(description = "主键") Integer pkey,
        HttpServletRequest request, HttpServletResponse response)
    {
        boolean sign = activityManager.qrCode(pkey, request, response);
        return new Result<>(sign);
    }
    
    @Operation(summary = "生成弹框活动二维码", tags = ApiTags.ACTIVITY)
    @GetMapping(value = "/down/popUpQrCode")
    public Result<Boolean> popUpQrCode(@RequestParam(value = "pkey") @Parameter(description = "主键") Integer pkey,
        HttpServletRequest request, HttpServletResponse response)
    {
        boolean sign = activityManager.popUpQrCode(pkey, request, response);
        return new Result<>(sign);
    }
}

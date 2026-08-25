package cn.tofocus.lejia.app.v1.member;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.AppCardDTO;
import cn.tofocus.lejia.bean.dto.app.market.AppMemberCentreDTO;
import cn.tofocus.lejia.bean.dto.app.market.AppMemberCentreMsdLine;
import cn.tofocus.lejia.bean.dto.app.market.AppMemberSignOnList;
import cn.tofocus.lejia.bean.dto.app.market.AppOrderStatusNum;
import cn.tofocus.lejia.bean.dto.app.market.MktAppMemberCardOnList;
import cn.tofocus.lejia.bean.dto.app.market.MktAppMemberDetailsDTO;
import cn.tofocus.lejia.bean.dto.app.market.MktAppMemberDrawOnList;
import cn.tofocus.lejia.bean.dto.app.wxPay.WxPayData;
import cn.tofocus.lejia.bean.dto.market.MktGiftOnList;
import cn.tofocus.lejia.bean.dto.market.MktMemberOnList;
import cn.tofocus.lejia.bean.enums.MemberPType;
import cn.tofocus.lejia.bean.enums.PayType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-app-member", path = "/v1/app/market/lm/member", fallbackFactory = AppMemberFallback.class, configuration = FeignConfig.class)
public interface AppMemberApi
{
    @Operation(summary = "支付之前获取数据", tags = AppTags.mobileMember)
    @PostMapping("/beforePay")
    public Result<WxPayData> beforePay(@RequestParam(name = "amt") @Parameter(description = "支付金额") BigDecimal amt,
        @RequestParam(name = "memberPType") @Parameter(description = "类型") MemberPType memberPType,
        @RequestParam(name = "payType") @Parameter(description = "支付类型") PayType payType);
    
    @Operation(summary = "获取会员信息", tags = AppTags.mobileMember)
    @PostMapping("/get")
    public Result<MktMemberOnList> getMember();
    
    @Operation(summary = "更新会员信息", tags = AppTags.mobileMember)
    @PostMapping(value = "/upd")
    public Result<Boolean> upd(@RequestParam(value = "photo", required = false) @Parameter(description = "头像") String photo,
        @RequestParam(value = "name", required = false) @Parameter(description = "用户名") String name);
    
    @Operation(summary = "更新提现银行卡信息", tags = AppTags.mobileMember)
    @PostMapping(value = "/ins/cust/card")
    public Result<Boolean> ins(@RequestParam(value = "custCard", required = false) @Parameter(description = "提现银行卡") String custCard,
        @RequestParam(value = "custName", required = false) @Parameter(description = "提现银行卡用户名") String custName,
        @RequestParam(value = "accountBank", required = false) @Parameter(description = "提现银行卡 开户行") String accountBank);
    
    @Operation(summary = "获取会员签到页面", tags = AppTags.mobileMember)
    @PostMapping("/query")
    public Result<AppMemberSignOnList> queryMemberPoints(
        @RequestParam(name = "signMonth", required = false) String signMonth);
    
    @Operation(summary = "会员签到", tags = AppTags.mobileMember)
    @PostMapping("/ins")
    public Result<Boolean> insMemberPoints();
    
    @Operation(summary = "获取会员中心", tags = AppTags.mobileMember)
    @PostMapping("/get/centre")
    public Result<AppMemberCentreDTO> getMemberCentre();
    
    @Operation(summary = "获取会员中心-订单角标数量", tags = AppTags.mobileMember)
    @PostMapping("/get/orderStatusNum")
    public Result<AppOrderStatusNum> getOrderStatusNum();
    
    @Operation(summary = "获取热力豆明细", tags = AppTags.mobileMember)
    @PostMapping("/query/centre/msdLine")
    public Result<PageResult<AppMemberCentreMsdLine>> queryMsdLine(
        @RequestParam(value = "page", defaultValue = "0", required = false)int page, 
        @RequestParam(value = "pagesize", defaultValue = "0", required = false)int pagesize);
    
    @Operation(summary = "获取卡券列表", tags = AppTags.mobileMember)
    @PostMapping("/get/card")
    public Result<List<MktAppMemberCardOnList>> getMemberCard();
    
    @Operation(summary = "我的奖品列表", tags = AppTags.mobileMember)
    @PostMapping("/get/draw")
    public Result<PageResult<MktAppMemberDrawOnList>> getMemberDraw(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") Integer page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") Integer pagesize);
    
    @Operation(summary = "app扫二维码领取卡券/领券中心领取卡券", tags = AppTags.mobileMember)
    @PostMapping("/card/ins")
    public Result<Boolean> insCard(@RequestParam(name = "card") @Parameter(description = "卡券pkey") Integer card);

    @Operation(summary = "获取领券中心卡券列表", tags = AppTags.mobileMember)
    @PostMapping("/query/centercard")
    public Result<PageResult<AppCardDTO>> getCenterCard(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "cardPkey", required = false) Integer cardPkey);
    
    @Operation(summary = "获取会员优惠价和原价", tags = AppTags.mobileMember)
    @PostMapping("/get/price")
    public Result<Map<String, Object>> getMemberPrice();
    
    @Operation(summary = "获取推荐人列表", tags = AppTags.mobileMember)
    @PostMapping("/get/tjrList")
    public Result<List<MktAppMemberDetailsDTO>> getTjrList();
    
    @Operation(summary = "获取礼品券列表", tags = AppTags.mobileMember)
    @PostMapping(value = "/query/giftList")
    public Result<PageResult<MktGiftOnList>> giftList(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "status", required = false) Integer status);
    
    @Operation(summary = "获取邀请有礼图片", tags = AppTags.mobileMember)
    @PostMapping("/get/invPhoto")
    public Result<String> getInvitationPhoto();
    
    @Operation(summary = "注销用户", tags = AppTags.mobileMember)
    @PostMapping("/logOut")
    public Result<Boolean> logOut();
    
    @Operation(summary = "取消注销用户", tags = AppTags.mobileMember)
    @PostMapping("/cancel/logOut")
    public Result<Boolean> cancelLogOut();
    
}

package cn.tofocus.lejia.api.v1.market;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.MktMemberCardDTO;
import cn.tofocus.lejia.bean.dto.market.MktMemberCommLineOnList;
import cn.tofocus.lejia.bean.dto.market.MktMemberConsumption;
import cn.tofocus.lejia.bean.dto.market.MktMemberOnList;
import cn.tofocus.lejia.bean.dto.market.MktMemberPointLineOnList;
import cn.tofocus.lejia.bean.dto.market.TagOnList;
import cn.tofocus.lejia.bean.enums.CommSourceType;
import cn.tofocus.lejia.bean.enums.LevelType;
import cn.tofocus.lejia.bean.enums.SourceType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "lejia-server", contextId = "lejia-server-member", path = "/v1/market/member", fallbackFactory = MktMemberApiFallback.class, configuration = FeignConfig.class)
public interface MktMemberApi
{
    
    @Operation(summary = "开通年费会员", tags = ApiTags.custMember)
    @PostMapping("/open")
    public Result<Boolean> openMember(@RequestParam(value = "pkey") @Parameter(description = "pkey") Integer pkey,
        @RequestParam(value = "name", required = false) @Parameter(description = "name") String name);
    
    @Operation(summary = "调整积分", tags = ApiTags.custMember)
    @PostMapping("/adjustment")
    public Result<Boolean> adjustmentPointMember(
        @RequestParam(value = "pkey") @Parameter(description = "pkey", required = true) Integer pkey,
        @RequestParam(value = "point") @Parameter(description = "积分", required = true) Integer point,
        @RequestParam(value = "source") @Parameter(description = "积分来源", required = true) SourceType source,
        @RequestParam(value = "formid", required = false) @Parameter(description = "单据来源,没有可不传") String formid,
        @RequestParam(value = "remark", required = false) @Parameter(description = "备注,没有可不传") String remark);
    
    @Operation(summary = "获取会员信息列表", tags = ApiTags.custMember)
    @PostMapping("/query")
    public Result<PageResult<MktMemberOnList>> queryMember(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10000") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "level", required = false) @Parameter(description = "等级") LevelType level,
        @RequestParam(value = "name", required = false) @Parameter(description = "名称") String name,
        @RequestParam(value = "mobile", required = false) @Parameter(description = "手机") String mobile,
        @RequestParam(value = "area", required = false) @Parameter(description = "地区") String area,
        @RequestParam(value = "remark", required = false) @Parameter(description = "备注") String remark,
        @RequestParam(value = "startCreatedTime", required = false) @Parameter(description = "开始建档时间") String startCreatedTime,
        @RequestParam(value = "endCreatedTime", required = false) @Parameter(description = "结束建档时间") String endCreatedTime,
        @RequestParam(value = "startLastConsumeTime", required = false) @Parameter(description = "开始最近消费时间") String startLastConsumeTime,
        @RequestParam(value = "endLastConsumeTime", required = false) @Parameter(description = "结束最近消费时间") String endLastConsumeTime,
        @RequestParam(value = "lastConsumeFarmer", required = false) @Parameter(description = "最近消费市场") String lastConsumeFarmer,
        @RequestParam(value = "source", required = false) @Parameter(description = "用户来源") String source,
        @RequestParam(value = "tagKeys", required = false) @Parameter(description = "标签主键") List<Integer> tagKeys);
    
    @Operation(summary = "获取积分明细列表", tags = ApiTags.custMember)
    @PostMapping("/point/query")
    public Result<PageResult<MktMemberPointLineOnList>> queryMemberPointLine(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "member", required = false) @Parameter(description = "会员pkey") Integer member,
        @RequestParam(value = "source", required = false) @Parameter(description = "积分来源") SourceType source,
        @RequestParam(value = "mobile", required = false) @Parameter(description = "手机号码") String mobile,
        @RequestParam(value = "name", required = false) @Parameter(description = "名称") String name,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "开始时间") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "结束时间") String endDate,
        @RequestParam(value = "direct", required = false) @Parameter(description = "借贷") Boolean direct);
    
    @Operation(summary = "获取会员消费记录", tags = ApiTags.custMember)
    @PostMapping("/consumption/query")
    public Result<PageResult<MktMemberConsumption>> queryMemberConsumption(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "member") @Parameter(description = "会员pkey") Integer member);
    
    @Operation(summary = "获取会员优惠券记录", tags = ApiTags.custMember)
    @PostMapping("/card/query")
    public Result<PageResult<MktMemberCardDTO>> queryMemberCard(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "member") @Parameter(description = "会员pkey") Integer member);
    
    @Operation(summary = "获取余额明细列表", tags = ApiTags.custMember)
    @PostMapping("/comm/query")
    public Result<PageResult<MktMemberCommLineOnList>> queryMemberCommLine(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "source", required = false) @Parameter(description = "积分来源") CommSourceType source,
        @RequestParam(value = "member", required = false) @Parameter(description = "会员pkey") Integer member,
        @RequestParam(value = "direct", required = false) @Parameter(description = "借贷") Boolean direct,
        @RequestParam(value = "mobile", required = false) @Parameter(description = "手机号码") String mobile,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "开始时间") String startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "结束时间") String endDate);
    
    @Operation(summary = "备注", tags = ApiTags.custMember)
    @PostMapping("/tags")
    public Result<Boolean> tags(@RequestParam(value = "pkey") @Parameter(description = "pkey") Integer pkey,
        @RequestParam(value = "remark", required = false) @Parameter(description = "备注标签") String remark);
    
    @Operation(summary = "获取用户标签情况", tags = ApiTags.custMember)
    @PostMapping("/get/tag")
    public Result<PageResult<TagOnList>> getMemberTags(
        @RequestParam(value = "page", defaultValue = "0", required = false) @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10", required = false) @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "pkey", required = false) @Parameter(description = "会员主键,不填就返回所有标签") Integer pkey,
        @RequestParam(value = "name", required = false)@Parameter(description = "标签名称") String name,
        @RequestParam(value = "description", required = false)@Parameter(description = "标签描述") String description);
    
    @Operation(summary = "获取用户已勾选的标签", tags = ApiTags.custMember)
    @PostMapping("/get/tag/true")
    public Result<List<Integer>> listMemberTags(
        @RequestParam(value = "pkey")Integer pkey,
        @RequestParam(value = "name", required = false)@Parameter(description = "标签名称") String name,
        @RequestParam(value = "description", required = false)@Parameter(description = "标签描述") String description);
    
    @Operation(summary = "打标签", tags = ApiTags.custMember)
    @PostMapping("/mark/tag")
    public Result<Boolean> markMemberTags(@RequestParam(value = "pkeys") @Parameter(description = "会员主键") List<Integer> pkeys,
        @RequestParam(value = "tagKeys", required = false) @Parameter(description = "标签主键") List<Integer> tagKeys);
    
}

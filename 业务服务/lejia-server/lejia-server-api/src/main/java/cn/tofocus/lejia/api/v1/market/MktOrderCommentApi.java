package cn.tofocus.lejia.api.v1.market;

import java.util.List;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.MktOrderCommentConfigDTO;
import cn.tofocus.lejia.bean.dto.market.MktOrderGoodsCommentInfo;
import cn.tofocus.lejia.bean.dto.market.MktOrderGoodsCommentOnList;
import cn.tofocus.lejia.bean.dto.market.MktOrderGoodsCommentReplyDTO;
import cn.tofocus.lejia.bean.enums.CommentApplyStatus;
import cn.tofocus.lejia.bean.enums.CommentReplyStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

public interface MktOrderCommentApi
{
    @Operation(summary = "获取评价配置", tags = ApiTags.custOrderComment)
    @PostMapping(value = "/config/get")
    Result<MktOrderCommentConfigDTO> getConfig();
    
    @Operation(summary = "设置评价配置", tags = ApiTags.custOrderComment)
    @PostMapping(value = "/config/set")
    Result<Boolean> setConfig(@RequestBody @Valid MktOrderCommentConfigDTO dto);
    
    @Operation(summary = "查询交易商品评价", tags = ApiTags.custOrderComment)
    @PostMapping(value = "/query")
    Result<PageResult<MktOrderGoodsCommentOnList>> query(
        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize,
        @RequestParam(value = "memberMobile", required = false) @Parameter(description = "用户手机号") String memberMobile,
        @RequestParam(value = "orderCode", required = false) @Parameter(description = "订单号") String orderCode,
        @RequestParam(value = "goodsName", required = false) @Parameter(description = "商品名称") String goodsName,
        @RequestParam(value = "replyStatus", required = false) @Parameter(description = "回复状态") CommentReplyStatus replyStatus,
        @RequestParam(value = "applyStatus", required = false) @Parameter(description = "审核状态") CommentApplyStatus applyStatus);
    
    @Operation(summary = "获取交易商品评价", tags = ApiTags.custOrderComment)
    @PostMapping(value = "/get")
    Result<MktOrderGoodsCommentInfo> get(@RequestParam(value = "pkey") @Parameter(description = "评价主键") Integer pkey);
    
    @Operation(summary = "回复交易商品评价", tags = ApiTags.custOrderComment)
    @PostMapping(value = "/reply")
    Result<Boolean> reply(@RequestBody @Valid MktOrderGoodsCommentReplyDTO dto);
    
    @Operation(summary = "批量审核交易商品评价", tags = ApiTags.custOrderComment)
    @PostMapping(value = "/batchApply")
    Result<Boolean> batchApply(
        @RequestParam(value = "pkeys") @Parameter(description = "评价主键列表（英文逗号分隔）") List<Integer> pkeys,
        @RequestParam(value = "applyStatus") @Parameter(description = "审核状态") CommentApplyStatus applyStatus);
}

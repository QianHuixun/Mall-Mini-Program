package cn.tofocus.lejia.app.v1.market;

import java.util.List;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.market.AppOrderCommentForAdd;
import cn.tofocus.lejia.bean.dto.app.market.AppOrderLineCommentDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

public interface AppOrderCommentApi
{
    @Operation(summary = "新增交易商品评价", tags = AppTags.mobileOrderComment)
    @PostMapping(value = "/add")
    Result<Boolean> add(@RequestBody @Valid AppOrderCommentForAdd dto);
    
    @Operation(summary = "按订单列表商品评价", tags = AppTags.mobileOrderComment)
    @PostMapping(value = "/list")
    Result<List<AppOrderLineCommentDTO>> listByOrder(
        @RequestParam(value = "pkey") @Parameter(description = "订单主键") Integer pkey);
}

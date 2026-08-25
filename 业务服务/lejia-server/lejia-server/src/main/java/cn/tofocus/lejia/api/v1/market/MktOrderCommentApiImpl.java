package cn.tofocus.lejia.api.v1.market;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.MktOrderCommentConfigDTO;
import cn.tofocus.lejia.bean.dto.market.MktOrderGoodsCommentInfo;
import cn.tofocus.lejia.bean.dto.market.MktOrderGoodsCommentOnList;
import cn.tofocus.lejia.bean.dto.market.MktOrderGoodsCommentReplyDTO;
import cn.tofocus.lejia.bean.enums.CommentApplyStatus;
import cn.tofocus.lejia.bean.enums.CommentReplyStatus;
import cn.tofocus.lejia.domain.market.MktOrderCommentManager;

@RequestMapping("/v1/market/order/comment")
@RestController
public class MktOrderCommentApiImpl implements MktOrderCommentApi
{
    @Autowired
    private MktOrderCommentManager orderCommentManager;
    
    @Override
    public Result<MktOrderCommentConfigDTO> getConfig()
    {
        MktOrderCommentConfigDTO res = orderCommentManager.getConfig();
        return new Result<>(res);
    }
    
    @Override
    public Result<Boolean> setConfig(@Valid MktOrderCommentConfigDTO dto)
    {
        boolean sign = orderCommentManager.setConfig(dto);
        return new Result<>(sign);
    }
    
    @Override
    public Result<PageResult<MktOrderGoodsCommentOnList>> query(int page, int pagesize, String memberMobile,
        String orderCode, String goodsName, CommentReplyStatus replyStatus, CommentApplyStatus applyStatus)
    {
        PageResult<MktOrderGoodsCommentOnList> res =
            orderCommentManager.query(page, pagesize, memberMobile, orderCode, goodsName, replyStatus, applyStatus);
        return new Result<>(res);
    }
    
    @Override
    public Result<MktOrderGoodsCommentInfo> get(Integer pkey)
    {
        MktOrderGoodsCommentInfo res = orderCommentManager.get(pkey);
        return new Result<>(res);
    }
    
    @Override
    public Result<Boolean> reply(@Valid MktOrderGoodsCommentReplyDTO dto)
    {
        boolean sign = orderCommentManager.reply(dto);
        return new Result<>(sign);
    }
    
    @Override
    public Result<Boolean> batchApply(List<Integer> pkeys, CommentApplyStatus applyStatus)
    {
        boolean sign = orderCommentManager.batchApply(pkeys, applyStatus);
        return new Result<>(sign);
    }
}

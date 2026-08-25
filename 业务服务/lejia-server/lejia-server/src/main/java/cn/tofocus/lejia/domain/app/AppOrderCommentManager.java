package cn.tofocus.lejia.domain.app;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.tofocus.common.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Objects;

import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.lejia.bean.dto.app.AppWxMsgSecCheckResult;
import cn.tofocus.lejia.bean.dto.app.market.AppOrderCommentForAdd;
import cn.tofocus.lejia.bean.dto.app.market.AppOrderLineCommentDTO;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.market.MktOrderGoodsComment;
import cn.tofocus.lejia.bean.entity.market.MktOrderLine;
import cn.tofocus.lejia.bean.enums.CommentApplyStatus;
import cn.tofocus.lejia.bean.enums.CommentReplyStatus;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.market.MktOrderGoodsCommentDao;
import cn.tofocus.lejia.dao.market.MktOrderLineDao;
import cn.tofocus.lejia.domain.WxManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppOrderCommentManager
{
    @Autowired
    private MktOrderGoodsCommentDao orderGoodsCommentDao;
    
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private MktOrderLineDao orderLineDao;
    
    @Autowired
    private WxManager wxManager;
    
    @Transactional(rollbackFor = Exception.class)
    public boolean add(AppOrderCommentForAdd dto)
    {
        Integer currentMember = MobileSession.memberPkey();
        if (currentMember == null)
            throw TofocusException.of(LejiaErrCode.MEMBER_NOT_LOGIN);
        MktOrder order = orderDao.get(dto.getOrderPkey());
        if (order == null)
            throw TofocusException.of(LejiaErrCode.NOT_FIND_ORDER);
        if (!Objects.equal(order.getMember(), currentMember))
            throw TofocusException.of(LejiaErrCode.NOT_RIGHT);
        if (order.getStatus() != OrderStatus.CONFIRM_ORDER)
            throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "订单未完成，不支持评价");
        List<MktOrderLine> orderLines = orderLineDao.listOrder(dto.getOrderPkey());
        Map<Integer, List<MktOrderLine>> goodsLineMap = new HashMap<>();
        for (MktOrderLine orderLine : orderLines)
        {
            List<MktOrderLine> value = goodsLineMap.computeIfAbsent(orderLine.getGoods().intValue(), k -> new ArrayList<>());
            value.add(orderLine);
        }
        List<MktOrderGoodsComment> list = new ArrayList<>();
        for (AppOrderCommentForAdd.AppOrderGoodsCommentForAdd line : dto.getLines())
        {
            List<MktOrderLine> lines = goodsLineMap.get(line.getGoods());
            if (lines == null)
                throw TofocusException.of(LejiaErrCode.NOT_FIND_ORDER_DETAIL, "订单中没有该商品的交易");
            // 判断退完的不允许评价
            if (isWholeRefund(lines))
            {
                String goodsName = lines.get(0).getGoodsName() == null ? "" : "[" + lines.get(0).getGoodsName() + "]";
                throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "商品" + goodsName + "已退完，不支持评价");
            }
            // 检查文本合规
            if (StringUtil.isNotBlank(line.getContent()))
            {
                AppWxMsgSecCheckResult checkRes =
                    wxManager.commentSecCheck(line.getContent(), MobileSession.openid(), MobileSession.appid());
                if (!checkRes.isSuccess())
                {
                    log.error("商品评价文本内容安全识别异常，响应内容：{}，评价内容：{}", checkRes, line.getContent());
                    throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, "评价文本内容安全识别出错");
                }
                if (!checkRes.isPass())
                    throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "评价内容包含敏感词汇");
            }
            
            MktOrderGoodsComment bean = MktOrderGoodsComment.of(lines.get(0));
            bean.setMember(currentMember);
            bean.setScore(line.getScore());
            bean.setContent(line.getContent());
            bean.setPhoto(line.getPhoto());
            bean.setReplyStatus(CommentReplyStatus.NOT_REPLIED);
            bean.setApplyStatus(CommentApplyStatus.NOT_APPLY);
            list.add(bean);
        }
        orderGoodsCommentDao.addAll(list);
        return true;
    }
    
    private boolean isWholeRefund(List<MktOrderLine> lines)
    {
        BigDecimal oSumAmt = BigDecimal.ZERO;
        BigDecimal refundAmt = BigDecimal.ZERO;
        for (MktOrderLine line : lines)
        {
            if (line.getRefundAmt() != null && line.getRefundAmt().compareTo(BigDecimal.ZERO) > 0)
                refundAmt = refundAmt.add(line.getRefundAmt());
            if (line.getCouponAmt() != null)
            {
                oSumAmt = oSumAmt.add(line.getCouponAmt());
            }
            else if (line.getCouponPrice() != null)
            {
                BigDecimal multiply = line.getCouponPrice().multiply(new BigDecimal(line.getNum()));
                oSumAmt = oSumAmt.add(multiply);
            }
            else
            {
                BigDecimal multiply = line.getPrice().multiply(new BigDecimal(line.getNum()));
                oSumAmt = oSumAmt.add(multiply);
            }
        }
        return oSumAmt.compareTo(refundAmt) == 0;
    }
    
    public List<AppOrderLineCommentDTO> listByOrder(Integer pkey)
    {
        Integer currentMember = MobileSession.memberPkey();
        if (currentMember == null)
            throw TofocusException.of(LejiaErrCode.MEMBER_NOT_LOGIN);
        MktOrder order = orderDao.get(pkey);
        if (order == null)
            throw TofocusException.of(LejiaErrCode.NOT_FIND_ORDER);
        if (!Objects.equal(order.getMember(), currentMember))
            throw TofocusException.of(LejiaErrCode.NOT_RIGHT);
        return orderGoodsCommentDao.listByOrder(pkey, AppOrderLineCommentDTO.class);
    }
}

package cn.tofocus.lejia.domain.market;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.core.security.SecurityContextUtil;
import cn.tofocus.lejia.bean.dto.market.MktRefundOnList;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.market.MktOrderDesc;
import cn.tofocus.lejia.bean.entity.market.MktRefund;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.enums.CommSourceType;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.RefundStatus;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.market.MktMemberDao;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.market.MktOrderDescDao;
import cn.tofocus.lejia.dao.market.MktRefundDao;
import cn.tofocus.lejia.exception.WsaleErrCode;

@Component
public class RefundManager
{
    @Autowired
    private MktRefundDao refundDao;
    
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private MktOrderDescDao orderDescDao;
    
    @Autowired
    private MktMemberDao memberDao;
    
    @Autowired
    private MemberCommManager commManager;
    
    @Autowired
    private SecurityContextUtil context;
    
    public PageResult<MktRefundOnList> queryRefund(int page, int pagesize, String code, RefundStatus status)
    {
        PageResult<MktRefund> exec = refundDao.queryRefund(page, pagesize, code, status, CurrentSession.ascriptionPkey());
        PageResult<MktRefundOnList> result = BeanUtil.beanPageFrom(MktRefundOnList.class, exec);
        for (MktRefundOnList bean : result)
        {
            bean.setStatusName(bean.getStatus().getName());
            MktMember member = memberDao.get(bean.getMember());
            if (member != null) bean.setMemberName(member.getName());
        }
        return result;
    }
    
    @Transactional
    public MktRefundOnList updRefund(int pkey, RefundStatus status)
    {
        MktRefund refund = refundDao.get(pkey);
        
        if (status.getIndex() == 0)
        {
            if (refund.getStatus().getIndex() != 0) throw TofocusException.of(WsaleErrCode.REFUND_STATUS_APPLYING);
        }
        
        if (status.getIndex() == 1)
        {//同意
            if (refund.getStatus().getIndex() != 0 && refund.getStatus().getIndex() != 1)
                throw TofocusException.of(WsaleErrCode.REFUND_STATUS_APPLYING);
        }
        if (status.getIndex() == 2)
        {//退款
            if (refund.getStatus().getIndex() != 1) throw TofocusException.of(WsaleErrCode.REFUND_STATUS_AGREE);
            commManager
                .updComm(refund.getMember(), refund.getAmtre(), true, CommSourceType.COMM_RETURN, refund.getCode(), CurrentSession.ascriptionPkey());
            MktOrder order = orderDao.get(refund.getOrderNum());
            order.setStatus(OrderStatus.REFUNDED_ORDER);
            orderDao.update(order);
        }
        
        if (status.getIndex() == 3)
        {//拒绝
            if (refund.getStatus().getIndex() != 0 && refund.getStatus().getIndex() != 3)
                throw TofocusException.of(WsaleErrCode.REFUND_STATUS_APPLYING);
            MktOrder order = orderDao.get(refund.getOrderNum());
            MktOrderDesc orderDesc = orderDescDao.get(refund.getOrderNum());
            if (StringUtil.isEmpty(orderDesc.getKdCode()))
                order.setStatus(OrderStatus.DELIVERED_ORDER);
            else
                order.setStatus(OrderStatus.SHIPPED_ORDER);
            orderDao.update(order);
        }
        refund.setStatus(status);
        refund.setDelBy(context.currentUserkey().intValue());
        refund.setDelTime(new Date());
        MktRefund update = refundDao.update(refund);
        return BeanUtil.beanFrom(MktRefundOnList.class, update);
    }
    
}

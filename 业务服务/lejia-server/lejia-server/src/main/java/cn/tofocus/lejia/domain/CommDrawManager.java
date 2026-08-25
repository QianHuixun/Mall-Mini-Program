package cn.tofocus.lejia.domain;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.core.security.AuthenticationContext;
import cn.tofocus.core.security.SecurityContextUtil;
import cn.tofocus.lejia.bean.dto.market.MktCommDrawOnList;
import cn.tofocus.lejia.bean.entity.market.MktCommDraw;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.enums.CommDrawStatus;
import cn.tofocus.lejia.bean.enums.CommSourceType;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.market.MktCommDrawDao;
import cn.tofocus.lejia.dao.market.MktMemberDao;
import cn.tofocus.lejia.domain.market.MemberCommManager;
import cn.tofocus.lejia.exception.WsaleErrCode;
import io.micrometer.core.instrument.util.StringUtils;

@Component
public class CommDrawManager 
{
    @Autowired
    private MemberCommManager memberCommManager;
    @Autowired
    private MktCommDrawDao commDrawDao;
    @Autowired
    private MktMemberDao memberDao;
    
	public PageResult<MktCommDrawOnList> queryCommDraw(int page, int pagesize, CommDrawStatus status, String orderNumber) {
		PageResult<MktCommDraw> pageResult = commDrawDao.queryCommDraw(page, pagesize, status, orderNumber, CurrentSession.ascriptionPkey());
		PageResult<MktCommDrawOnList> result = BeanUtil.beanPageFrom(MktCommDrawOnList.class, pageResult);
		for(MktCommDrawOnList dto : result.getContent())
		{
			MktMember member = memberDao.get(dto.getMember());
			if(member != null)
			{
				dto.setAccountBank(member.getAccountBank());
				dto.setCustCard(member.getCustCard());
				dto.setCustName(member.getCustName());
			}
			dto.setStatusName(dto.getStatus().getName());
		}
		return result;
	}

	// 同意提现 佣金在申请的时候已经扣除 这里只更新状态 
	public Boolean agreeCommDraw(Integer pkey, String remark) {
		AuthenticationContext authenticationContext = SecurityContextUtil.getAuthenticationContext();
		MktCommDraw commDraw = commDrawDao.get(pkey);
		if(commDraw == null)
			throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
		commDraw.setStatus(CommDrawStatus.COMMDRAW_SENT);
		commDraw.setCheckTime(new Date());
		commDraw.setCheckBy(authenticationContext.getUserkey().intValue());
		if(StringUtils.isNotBlank(remark))
			commDraw.setRemark(remark);
		commDrawDao.update(commDraw);
		return true;
	}

	// 拒绝提现,将扣除的佣金返回 增加一条佣金返回的明细
	@Transactional
	public Boolean refuseCommDraw(Integer pkey, String remark) {
		AuthenticationContext authenticationContext = SecurityContextUtil.getAuthenticationContext();
		MktCommDraw commDraw = commDrawDao.get(pkey);
		if(commDraw == null)
			throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
		commDraw.setStatus(CommDrawStatus.COMMDRAW_REFUSE);
		commDraw.setCheckTime(new Date());
		commDraw.setCheckBy(authenticationContext.getUserkey().intValue());
		if(StringUtils.isNotBlank(remark))
			commDraw.setRemark(remark);
		commDrawDao.update(commDraw);
		memberCommManager.updComm(commDraw.getMember(), commDraw.getComms(), true, CommSourceType.WITHDRAW_REFUSE, commDraw.getOrderNumber(), commDraw.getAscription());
		return true;
	}
	
	
	public Boolean updCommDraw(Integer pkey, String remark) {
		MktCommDraw commDraw = commDrawDao.get(pkey);
		if(commDraw == null)
			throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
		commDraw.setRemark(remark);
		commDrawDao.update(commDraw);
		return true;
	}

	
	public Boolean paidDraw(Integer pkey) {
		MktCommDraw commDraw = commDrawDao.get(pkey);
		if(commDraw == null)
			throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
		commDraw.setStatus(CommDrawStatus.COMMDRAW_PAID);
		commDrawDao.update(commDraw);
		return true;
	}
}

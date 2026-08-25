package cn.tofocus.lejia.domain.app;

import static cn.tofocus.core.query.exp.ExpUtil.f;
import static cn.tofocus.core.query.exp.ExpUtil.substring;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.lejia.bean.dto.app.market.AppMemberCommLineOnList;
import cn.tofocus.lejia.bean.entity.market.MktCommDraw;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.member.MktMemberCommLine;
import cn.tofocus.lejia.bean.enums.CommDrawStatus;
import cn.tofocus.lejia.bean.enums.CommSourceType;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.market.MktCommDrawDao;
import cn.tofocus.lejia.dao.market.MktMemberCommLineDao;
import cn.tofocus.lejia.dao.market.MktMemberDao;
import cn.tofocus.lejia.domain.market.MemberCommManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.util.NumberUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppMemberCommManager {
    @Autowired
    private MemberCommManager memberCommManager;
    @Autowired
    private MktMemberCommLineDao memberCommLineDao;
    @Autowired
    private MktCommDrawDao commDrawDao;
    @Autowired
	private NumberUtils numberUtils;
    @Autowired
    private MktMemberDao memberDao;

    public BigDecimal loadComm() {
        Integer memberPkey = MobileSession.memberPkey();
        return memberCommManager.loadComm(memberPkey);
    }

    public PageResult<AppMemberCommLineOnList> queryLine(Integer page, Integer pagesize, Boolean direct) {
        Integer memberPkey = MobileSession.memberPkey();
        SelectPageBuilder<Integer, MktMemberCommLine> pageBuilder = memberCommLineDao.selectPage()
                .page(page)
                .pagesize(pagesize)
                .eq("member", memberPkey)
                .sort("createdTime", true);
        if (direct != null) {
            pageBuilder.eq("direct", direct);
        }
        PageResult<MktMemberCommLine> pageList = pageBuilder.exec();
        PageResult<AppMemberCommLineOnList> result = BeanUtil.beanPageFrom(AppMemberCommLineOnList.class, pageList);
        return result;
    }
    
    @Transactional
    public Boolean ins(BigDecimal comms, String custCard, String custName, String accountBank, String remark) {
    	Integer memberPkey = MobileSession.memberPkey();
    	log.info("comms: {}, memberPkey: {}", comms, memberPkey);
    	BigDecimal loadComm = memberCommManager.loadComm(memberPkey);
    	// 校验是否足够提现 
    	if(loadComm.compareTo(comms) < 0)
    		throw TofocusException.of(LejiaErrCode.BALANCE_INSUFFICIENT);
    	// 校验提现是否大于等于10
    	if(comms.compareTo(new BigDecimal(10)) < 0)
    		throw TofocusException.of(LejiaErrCode.COMMDRAW_LEAST);
    	// 校验提现是否小于等于10000
    	if(comms.compareTo(new BigDecimal(10000)) > 0)
    		throw TofocusException.of(LejiaErrCode.COMMDRAW_MAX);
    	// 校验是否当天已经提过现
        List<MktCommDraw> only = commDrawDao.select()
            .eq("member", memberPkey)
            .eq(substring(f("createdTime"), 1, 10), DateUtil.formatDate(new Date(), "yyyy-MM-dd"))
            .exec();
    	if(only != null && only.size() > 0)
    		throw TofocusException.of(LejiaErrCode.COMMDRAW_ONLY);
    	// 校验是否有提现未处理 处理后再申请
    	List<MktCommDraw> exec = commDrawDao.select().eq("member", memberPkey).eq("status", CommDrawStatus.COMMDRAW_INITIAL).exec();
    	if(exec != null && exec.size() > 0)
    		throw TofocusException.of(LejiaErrCode.COMMDRAW_UNTREATED);
    	MktCommDraw c = new MktCommDraw();
    	c.setMember(memberPkey);
    	c.setComms(comms);
    	c.setStatus(CommDrawStatus.COMMDRAW_INITIAL);
    	c.setRemark(remark);
    	c.setBankCode(custCard);
    	c.setOrderNumber(numberUtils.createOrderNumber());
    	c.setAscription(MobileSession.appid());
    	commDrawDao.add(c);
    	MktMember member = MobileSession.member();
    	if((StringUtil.isNotEmpty(member.getCustName()) && !member.getCustName().equals(custName)) 
    	    || (StringUtil.isNotEmpty(member.getCustCard()) && !member.getCustCard().equals(custCard) )
    	    || (StringUtil.isNotEmpty(member.getAccountBank()) && !member.getAccountBank().equals(accountBank)) )
    	{
    		member.setCustCard(custCard);
    		member.setAccountBank(accountBank);
    		member.setCustName(custName);
    		memberDao.update(member);
    	}
    	memberCommManager.updComm(memberPkey, comms, false, CommSourceType.WITHDRAW, c.getOrderNumber(), MobileSession.appid());
		return true;
	}
    
    
}

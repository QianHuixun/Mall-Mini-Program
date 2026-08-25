package cn.tofocus.lejia.domain.app;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.lejia.bean.entity.member.MktMemberPay;
import cn.tofocus.lejia.bean.enums.MemberPType;
import cn.tofocus.lejia.bean.enums.PayStatus;
import cn.tofocus.lejia.bean.enums.PayType;
import cn.tofocus.lejia.dao.market.MktMemberPayDao;
import cn.tofocus.lejia.util.NumberUtils;

@Component
public class AppMemberPayManager {
	@Autowired
	private MktMemberPayDao pPayDao;

	@Autowired
	private NumberUtils numUtils;

	public MktMemberPay createdOrder(int memberPkey, BigDecimal amt, MemberPType type, PayType payType, Integer ascription) {
		MktMemberPay pay = new MktMemberPay();
		pay.setMember(memberPkey);
		String code = numUtils.createPayNumber();
		if(type.equals(MemberPType.ANNUAL_FEE))
			pay.setOrderNumber("92"+code);
		else
			pay.setOrderNumber("93"+code);
		pay.setPType(type);
		pay.setStatus(PayStatus.PAY_INITIAL);
		pay.setPayType(payType);
		pay.setAmt(amt);
		pay.setAscription(ascription);
		pPayDao.add(pay);
		return pay;
	}
	/*
	 * 支付成功
	 */
	public void paySuccess(String orderNumber){
		MktMemberPay pay = pPayDao.selectOne().eq("orderNumber", orderNumber).exec();
		pay.setStatus(PayStatus.PAYMENT_SUCCESSFUL);
		pPayDao.update(pay);
	}
	
	/*
	 * 支付失败
	 */
	public void payFailed(String orderNumber){
		MktMemberPay pay = pPayDao.selectOne().eq("orderNumber", orderNumber).exec();
		pay.setStatus(PayStatus.PAYMENT_FAILED);
		pPayDao.update(pay);
	}
}

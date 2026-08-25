package cn.tofocus.lejia.domain.app;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.lejia.bean.entity.market.MktPointPay;
import cn.tofocus.lejia.bean.enums.PType;
import cn.tofocus.lejia.dao.market.MktPointPayDao;
import cn.tofocus.lejia.util.NumberUtils;

import static cn.tofocus.core.query.exp.ExpUtil.f;
import static cn.tofocus.core.query.exp.ExpUtil.substring;

@Component
public class AppPointPayManager {
	@Autowired
	private MktPointPayDao pPayDao;

	@Autowired
	private NumberUtils numUtils;

	public MktPointPay createdOrder(int memberPkey, int point, PType type, Integer ascription) {
		MktPointPay pay = new MktPointPay();
		pay.setMember(memberPkey);
		pay.setOrderNumber(numUtils.createPointNumber());
		pay.setPoints(point);
		pay.setPType(type);
		pay.setAscription(ascription);
		pPayDao.add(pay);
		return pay;
	}
	
	// 获取今天抽奖的次数
	public Integer getTodayQD(int memberPkey)
	{
		Integer num = null;
		List<MktPointPay> exec = pPayDao.select()
		.eq("member", memberPkey)
		.eq("pType", PType.DRAW)
		.eq(substring(f("createdTime"), 1, 10), DateUtil.formatDate(new Date(), "yyyy-MM-dd"))
		.exec();
		if(exec != null)
			num = exec.size();
		return num;
	}
}

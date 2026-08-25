package cn.tofocus.lejia.domain.market;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.db.redis.lock.RedisLockTemplate;
import cn.tofocus.lejia.bean.entity.market.MktPointPay;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorPoint;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorPointLine;
import cn.tofocus.lejia.bean.enums.PType;
import cn.tofocus.lejia.bean.enums.SourceType;
import cn.tofocus.lejia.dao.vendor.MktVendorPointDao;
import cn.tofocus.lejia.dao.vendor.MktVendorPointLineDao;
import cn.tofocus.lejia.domain.app.AppPointPayManager;

@Component
public class VendorPointManager {
	@Autowired
	private MktVendorPointDao vendorPointDao;
	@Autowired
	private MktVendorPointLineDao vendorPointLineDao;

	@Autowired
	private AppPointPayManager pointManager;
	
	@Autowired
	private RedisLockTemplate lock;

	private MktVendorPoint getMemPoint(int pkey) {
		MktVendorPoint mp = vendorPointDao.get(pkey);
		if (mp == null)
			mp = initMemberPoints(pkey);
		return mp;
	}

	private MktVendorPoint initMemberPoints(int pkey) {
		MktVendorPoint vendorPoints = new MktVendorPoint();
		vendorPoints.setPoints(0);
		vendorPoints.setPkey(pkey);
		vendorPoints.setUpdateTime(new Date());
		vendorPointDao.add(vendorPoints);
		return vendorPoints;
	}

	/*
	 * 读取商户当前积分
	 */
	public int loadPoints(int pkey) {
		return getMemPoint(pkey).getPoints();
	}

	/**
	 * 积分变更
	 * 
	 * @param vendorPkey
	 *            用户Pkey
	 * @param point
	 *            变更积分值
	 * @param direct
	 *            true:加 false:减
	 * @param source
	 *            来源类型 POINTS_BUY(0, "购买"), POINTS_CONSUMPTION(1, "消费"),
	 *            POINTS_ACTIVITY(2, "活动"), POINTS_MANUAL_ADD(3,"手动"),
	 * @param formid
	 *            来源表单ID
	 * @param remark
	 *            备注 来源类型为手动是填写操作员，来源类型为购买时填写市场名称
	 */
	@Transactional
	public MktVendorPointLine updPoint(int vendorPkey,int memberPkey, int point, SourceType source, Integer formid, Integer ascription) {
		try {
			lock.lock("lejia", "lejia-server", "vendorPoint" + vendorPkey);// 业务锁
			MktPointPay porder = pointManager.createdOrder(memberPkey, point, PType.CONSUME, ascription);
			MktVendorPointLine line = new MktVendorPointLine();
			line.setMember(memberPkey);
			line.setVendor(vendorPkey);
			line.setPoints(point);
			line.setSource(source);
			line.setFormId(porder.getPkey());
			MktVendorPoint mp = getMemPoint(vendorPkey);
			mp.setPoints(mp.getPoints() + line.getPoints());
			vendorPointDao.update(mp);
			line.setBalance(mp.getPoints());
			line.setAscription(ascription);
			vendorPointLineDao.add(line);
			return line;
		} finally {
			lock.unlock("lejia", "lejia-server", "vendorPoint" + vendorPkey);
		}
	}
}

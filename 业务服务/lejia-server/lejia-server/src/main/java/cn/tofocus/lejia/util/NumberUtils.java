package cn.tofocus.lejia.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.lejia.cache.OrderNumberMap;

@Component
public class NumberUtils {

	@Autowired
	private OrderNumberMap numberMap;

	/*
	 * 取订单号
	 */
	public String createOrderNumber() {
		String date = new SimpleDateFormat("ddMMyy").format(new Date());
		ArrayList<String> list = numberMap.get("order" + date);
		if (list == null)
			list = new ArrayList<String>();
		do {
			String sequ = randomNum(6);
			if (!list.contains(sequ)) {
				list.add(sequ);
				numberMap.put("order" + date, list);
				return "91" + date + sequ;
			}
		} while (true);
	}

	/*
	 * 取H5订单号
	 */
	public String createH5OrderNumber() {
	    String date = new SimpleDateFormat("ddMMyy").format(new Date());
	    ArrayList<String> list = numberMap.get("order" + date);
	    if (list == null)
	        list = new ArrayList<String>();
	    do {
	        String sequ = randomNum(6);
	        if (!list.contains(sequ)) {
	            list.add(sequ);
	            numberMap.put("order" + date, list);
	            return "51" + date + sequ;
	        }
	    } while (true);
	}
	
	/*
	 * 取退款订单号
	 */
	public String createRefundOrderNumber() {
	    String date = new SimpleDateFormat("ssddMMyy").format(new Date());
	    ArrayList<String> list = numberMap.get("order" + date);
	    if (list == null)
	        list = new ArrayList<String>();
	    do {
	        String sequ = randomNum(6);
	        if (!list.contains(sequ)) {
	            list.add(sequ);
	            numberMap.put("order" + date, list);
	            return "31" + date + sequ;
	        }
	    } while (true);
	}

	/*
	 * 取个人优惠券号
	 */
	public String createCardNumber() {
		String date = new SimpleDateFormat("ddMMyy").format(new Date());
		
		ArrayList<String> list = numberMap.get("card" + date);
		if (list == null)
			list = new ArrayList<String>();
		do {
			String sequ = randomNum(6);
			if (!list.contains(sequ)) {
				list.add(sequ);
				numberMap.put("card" + date, list);
				return date + sequ;
			}
		} while (true);
	}

	/*
	 * 取个人礼品券号
	 */
	public String createGiftNumber() {
		String date = new SimpleDateFormat("ddMMyy").format(new Date());

		ArrayList<String> list = numberMap.get("gift" + date);
		if (list == null)
			list = new ArrayList<String>();
		do {
			String sequ = randomNum(6);
			if (!list.contains(sequ)) {
				list.add(sequ);
				numberMap.put("gift" + date, list);
				return date + sequ;
			}
		} while (true);
	}

	/*
	 * 取积分消费单号
	 */
	public String createPointNumber() {
		String date = new SimpleDateFormat("ddMMyy").format(new Date());
		
		ArrayList<String> list = numberMap.get("point" + date);
		if (list == null)
			list = new ArrayList<String>();
		do {
			String sequ = randomNum(6);
			if (!list.contains(sequ)) {
				list.add(sequ);
				numberMap.put("point" + date, list);
				return date + sequ;
			}
		} while (true);
	}

	/*
	 * 取支付消费单号
	 */
	public String createPayNumber() {
		String date = new SimpleDateFormat("ddMMyy").format(new Date());
		
		ArrayList<String> list = numberMap.get("pay" + date);
		if (list == null)
			list = new ArrayList<String>();
		do {
			String sequ = randomNum(4);
			if (!list.contains(sequ)) {
				list.add(sequ);
				numberMap.put("pay" + date, list);
				return date + sequ;
			}
		} while (true);
	}

	/*
	 * 取物流单号
	 */
	public String createOrderExpressNo() {
		String date = new SimpleDateFormat("ddMMyy").format(new Date());
		ArrayList<String> list = numberMap.get("orderExpress" + date);
		if (list == null)
			list = new ArrayList<String>();
		do {
			String sequ = randomNum(6);
			if (!list.contains(sequ)) {
				list.add(sequ);
				numberMap.put("orderExpress" + date, list);
				return date + sequ;
			}
		} while (true);
	}

	/*
	 * 取卡券领券码
	 */
	public static String createCardCode() {
		String sequ = randomNum(7);
		return sequ;
	}
	
	public static String createCheckCode(){
		String num = randomNum(4);
		return num;
	}

	private static String randomNum(int length) {
		Random random = new Random();
		int max = (int) Math.pow(10, length);
		int tmp = random.nextInt(max);
		while (tmp < max / 10)
			tmp = random.nextInt(max);
		return "" + tmp;
	}
}

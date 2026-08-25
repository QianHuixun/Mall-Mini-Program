//package cn.tofocus.lejia.util.wx;
//
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//import java.security.MessageDigest;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ThreadLocalRandom;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import mxcg.wxpub.util.Crypto;
//import mxcg.wxpub.util.WeixinUtil;
//import tofocus.svr.bean.BeanBase;
//import tofocus.wx.bean.wx.Wx;
//import tofocus.wx.bean.wx.WxAccount;
//import tofocus.wx.bean.wx.WxAccountDAO;
//import tofocus.wx.bean.wx.WxUser;
//import tofocus.wx.bean.wx.WxUserDAO;
//import tofocus.wx.bean.wx.WxUserGroup;
//
//public class PayTools {
//
//	// 产生随机字符串
//	public static String randomStr(int length) {
//		StringBuilder builder = new StringBuilder(length);
//		for (int i = 0; i < length; i++) {
//			builder.append(
//					(char) (ThreadLocalRandom.current().nextInt(97, 122)));
//		}
//		return builder.toString();
//	}
//
//	// 生成sign签名
//	/**
//	 * @param map
//	 *            传参的键值对
//	 * @param key
//	 *            秘钥
//	 * @return sign
//	 */
//	public static String getSign(Map map, String key) {
//		Collection<String> keyset = map.keySet();
//		List<String> list = new ArrayList<String>(keyset);
//		// 对key键值按字典升序排序
//		Collections.sort(list);
//		String stringA = "";
//		for (int i = 0; i < list.size(); i++) {
//			if (map.get(list.get(i)) != null && map.get(list.get(i)) != "") {
//				stringA += list.get(i) + "=" + map.get(list.get(i)) + "&";
//			}
//		}
//		stringA = stringA.substring(0, stringA.length() - 1);
//		String stringSignTemp = "";
//		if (key != null && key != "") {
//			stringSignTemp = stringA + "&key=" + key;
//		} else {
//			stringSignTemp = stringA;
//		}
//		String sign = stringMD5(stringSignTemp).toUpperCase();
//		return sign;
//	}
//
//	/**
//	 * MD5加密算法
//	 * 
//	 * @param inStr
//	 * @return
//	 */
//	public static String stringMD5(String inStr) {
//		MessageDigest md5 = null;
//		try {
//			md5 = MessageDigest.getInstance("MD5");
//		} catch (Exception e) {
//			System.out.println(e.toString());
//			e.printStackTrace();
//			return "";
//		}
//		char[] charArray = inStr.toCharArray();
//		byte[] byteArray = new byte[charArray.length];
//
//		for (int i = 0; i < charArray.length; i++)
//			byteArray[i] = (byte) charArray[i];
//		byte[] md5Bytes = md5.digest(byteArray);
//		StringBuffer hexValue = new StringBuffer();
//		for (int i = 0; i < md5Bytes.length; i++) {
//			int val = ((int) md5Bytes[i]) & 0xff;
//			if (val < 16)
//				hexValue.append("0");
//			hexValue.append(Integer.toHexString(val));
//		}
//		return hexValue.toString();
//	}
//
//	/**
//	 * 生成mch_billno billno+yyyyMMdd+当天毫秒数
//	 * 
//	 * @param mch_id
//	 * @return
//	 */
//	public static String getmchbillno(String mch_id) {
//		String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
//
//		Calendar cal = Calendar.getInstance();
//		try {
//			cal.setTime(new SimpleDateFormat("yyyyMMdd").parse(date));
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Calendar now = Calendar.getInstance();
//		now.setTime(new Date());
//		long dif = (now.getTimeInMillis() - cal.getTimeInMillis());
//		String str = getNoRepeatStr(dif);
//		return mch_id + date + str;
//	}
//
//	public static void main(String[] args) {
//		System.out.println(getmchbillno("11111"));
//	}
//
//	private static String getNoRepeatStr(Long dif) {
//		String str = dif.toString();
//		int l = str.length();
//		for (int i = 1; i <= (10 - l); i++) {
//			str = "0" + str;
//		}
//		return str;
//	}
//
//	public static String authorizeCode(JSONObject param)
//			throws UnsupportedEncodingException, JSONException {
//		String where = "1=?";
//		WxAccount account = BeanBase.list(WxAccount.class, where, false, 1)
//				.get(0);
//		param.put("account", account.getPkey());
//
//		String request_uri = "http://znjy.tofocus.cn/expand_wx_Wechat_expand?param="
//				+ Crypto.encrypt(param.toString());
//		request_uri = URLEncoder.encode(request_uri.replace("+", "%2B")
//				.replace("*", "%2A").replace("~", "%7E").replace("#", "%23"),
//				"UTF-8");
//		System.err.println(request_uri);
//		return WeixinUtil.web_oauth_url
//				.replaceAll("APPID", account.getAccountAppid())
//				.replaceAll("REDIRECT_URI", request_uri)
//				.replaceAll("SCOPE", "snsapi_base").replaceAll("STATE", "1");
//	}
//
//	public static WxUser updUser(String openid) {
//		WxAccount wxAccount = WxAccountDAO.getByUser(1);
//		try {
//			return WxUserDAO.getInfo(openid, wxAccount.getPkey());
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		WxUserGroup defaultGroup = WxUserGroup.loadUniqueWxidAccount(false, 1,
//				wxAccount.getPkey());
//		WxUser user = new WxUser();
//		user = new WxUser().init();
//		user.setOpenId(openid);
//		user.setAccount(wxAccount.getPkey());
//		user.setUserGroup(defaultGroup.getPkey());
//		user.setSubscribeTime(new Date());
//		user.stStatus(Wx.OStatus.NOFOLLOW);
//		user.ins();
//		return user;
//	}
//
//	public static WxAccount getWcaccount() {
//		return WxAccountDAO.getByUser(1);
//	}
//
//	public static WxAccount getAccount() {
//		List<WxAccount> list = WxAccount.list(WxAccount.class, "1=?", false, 1);
//
//		return list.get(0);
//	}
//
//	public static int getWcorgid() {
//		return 1;
//	}
//}

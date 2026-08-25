package cn.tofocus.lejia.domain.market.keruyun;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Component
public class KeruyunTool {

	// public static final String server = "https://gldopenapi.keruyun.com";
	public static final String server = "https://openapi.keruyun.com";
	public static final String appid = System.getenv().getOrDefault("KERUYUN_APP_ID", "CHANGE_ME");
	public static final String appSecret = System.getenv().getOrDefault("KERUYUN_APP_SECRET", "CHANGE_ME");
	public static final String token = System.getenv().getOrDefault("KERUYUN_TOKEN", "CHANGE_ME");

	/*
	 * 取token
	 */
	public static void loadToken(String shopIdenty) throws Exception {
		long sopId = Long.parseLong(shopIdenty);
		long timeSt = System.currentTimeMillis();
		RestTemplate restTemplate = new RestTemplate();
		String url = server + "/open/v1/token/get?appKey=" + appid + "&shopIdenty=" + shopIdenty + "&timestamp="
				+ timeSt + "&version=1.0&sign=" + signForToken(sopId, timeSt);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
		System.out.println(response.getBody());
	}

	/*
	 * 取订单列表
	 */
	public static JSONObject loadOrderList(long shopIdenty, String token,int pageNo) throws Exception {
		long timeSt = System.currentTimeMillis();
		RestTemplate restTemplate = new RestTemplate();
		String url = server + "/open/v1/data/order/export2?appKey=" + appid + "&shopIdenty=" + shopIdenty
				+ "&timestamp=" + timeSt + "&version=1.0&sign=" + loadSign(shopIdenty, token, timeSt);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Date date = new Date();
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
		String curDate = s.format(date.getTime()); // 当前日期
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = s.parse(curDate, pos);
		long ed = strtodate.getTime();
		long bg = ed - 24 * 60 * 60 * 1000;
//		Calendar calendar = Calendar.getInstance();
//		ed = calendar.getTimeInMillis();
//		calendar.add(Calendar.DATE, -15);
//		bg = calendar.getTimeInMillis();
		OrderList ol = new OrderList();
		ol.setShopIdenty(shopIdenty);
		ol.setSource(new Integer[]{1,2,3,33,191});
		ol.setStartTime(bg);
		ol.setEndTime(ed);
		ol.setTimeType(2);
		ol.setPageNo(pageNo);
		ol.setPageSize(20);
		ResponseEntity<Result> response = restTemplate.postForEntity(url, ol, Result.class);
		System.out.println(response.getBody().toString());
		if(response.getBody().getCode()>0)
			return null;
		System.out.println(response.getBody().getResult());
		return response.getBody().getResult();
	}

	/*
	 * 取订单详情
	 */
	public static JSONArray loadOrder(long shopIdenty, Long[] ids, String token) throws Exception {
		RestTemplate restTemplate = new RestTemplate();
		long timeSt = System.currentTimeMillis();
		String url = server + "/open/v1/data/order/exportDetail?appKey=" + appid + "&shopIdenty=" + shopIdenty
				+ "&timestamp=" + System.currentTimeMillis() + "&version=1.0&sign=" + loadSign(shopIdenty, token, timeSt);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Order ol = new Order();
		ol.setShopIdenty(shopIdenty);
		ol.setIds(ids);
		ResponseEntity<Results> response = restTemplate.postForEntity(url, ol, Results.class);
		if(response.getBody().getCode()>0)
			return null;
		return response.getBody().getResult();
	}

	/*
	 * 取会员详情
	 */
	public static void loadCustomer(String shopIdenty, long customerId, String token) throws Exception {
		long sopId = Long.parseLong(shopIdenty);
		RestTemplate restTemplate = new RestTemplate();
		long timeSt = System.currentTimeMillis();
		String url = server + "/open/v1/data/order/exportDetail?appKey=" + appid + "&shopIdenty=" + shopIdenty
				+ "&timestamp=" + System.currentTimeMillis() + "&version=1.0&sign=" + loadSign(sopId, token, timeSt);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Customer ol = new Customer();
		ol.setCustomerId(customerId);
		ResponseEntity<String> response = restTemplate.postForEntity(url, ol, String.class);
		System.out.println(response.getBody());
	}

	/*
	 * 取门店信息
	 */
	public static void loadVendor(String shopIdenty, String token) throws Exception {
		long sopId = Long.parseLong(shopIdenty);
		RestTemplate restTemplate = new RestTemplate();
		long timeSt = System.currentTimeMillis();
		String url = server + "/open/v1/shop/shopdetails?appKey=" + appid + "&shopIdenty=" + shopIdenty + "&timestamp="
				+ System.currentTimeMillis() + "&version=1.0&sign=" + loadSign(sopId, token, timeSt);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);
		System.out.println(response.getBody());
	}

	/**
	 * @Description: SHA256加密字符串
	 * @param
	 * @return String
	 * @throws NoSuchAlgorithmException
	 */
	private static String getSign(String sortedParams) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		messageDigest.update(sortedParams.getBytes());
		byte byteBuffer[] = messageDigest.digest();
		StringBuffer strHexString = new StringBuffer();
		for (int i = 0; i < byteBuffer.length; i++) {
			String hex = Integer.toHexString(0xff & byteBuffer[i]);
			if (hex.length() == 1) {
				strHexString.append('0');
			}
			strHexString.append(hex);
		}
		// 得到返回結果
		String SHA256Sign = strHexString.toString();
		return SHA256Sign;
	}

	private static String loadSign(long shopIdenty, String token, long timest) {
		Map<String, Object> params = new TreeMap<>();
		params.put("appKey", appid);
		params.put("shopIdenty", shopIdenty);
		params.put("timestamp", timest);
		params.put("version", "1.0");
		StringBuilder sortedParams = new StringBuilder();
		params.entrySet().stream().forEachOrdered(paramEntry -> sortedParams.append(paramEntry.getKey()).append(paramEntry.getValue()));
		sortedParams.append(token);//请替换成真实的token
		try {
			String sign = getSign(sortedParams.toString());
			return sign;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String signForToken(long shopId, long timest) {
		Map<String, Object> params = new TreeMap<>();
		params.put("appKey", appid);
		params.put("shopIdenty", shopId);
		params.put("version", "1.0");
		params.put("timestamp", timest);
		StringBuilder sortedParams = new StringBuilder();
		params.entrySet().stream()
				.forEachOrdered(paramEntry -> sortedParams.append(paramEntry.getKey()).append(paramEntry.getValue()));
		sortedParams.append(appSecret);
		String SHA256Sign = null;
		try {
			SHA256Sign = getSign(sortedParams.toString());
			System.err.println(SHA256Sign);
			return SHA256Sign;
		} catch (NoSuchAlgorithmException e) {
			System.err.println("出错");
		}
		return "";
	}

	public static void main(String[] args) throws Exception {
//		loadToken("810094162");
//		long aa = System.currentTimeMillis();
//		System.err.println(aa);
//		System.err.println(aa - 24 * 60 * 60 * 1000*5);
//		loadVendor("810094162", token);
//		loadOrder("810094162",new Long[]{375362887998705664l}, token);
		loadOrderList(810094162, token,1);
//		String aa = "appKey[d68b6581d5cdbaf8fe78ffe99bcff8f5]shopIdenty[810094162]version[1.0]timestamp[1594616178438]789d98a0124d45782849e597535c277c";
//		aa.replaceAll("[", "").replaceAll("]","");
//		System.err.println(aa.replaceAll("[", ""));
	}
}

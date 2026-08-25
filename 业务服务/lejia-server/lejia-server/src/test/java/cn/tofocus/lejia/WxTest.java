package cn.tofocus.lejia;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSONObject;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.lejia.bean.dto.app.market.MemberDTO;
import cn.tofocus.lejia.bean.entity.sys.AccountEntity;
import cn.tofocus.lejia.bean.enums.AccountType;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.domain.WxManager;
import cn.tofocus.lejia.domain.market.MemberManager;
import cn.tofocus.lejia.repository.sys.AccountRepository;
import cn.tofocus.lejia.util.WxUtils;
import cn.tofocus.lejia.util.wx.PayReqData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class WxTest {
	
	@Autowired
	private MemberManager memberManger;
	@Test
	public void test()
	{
		String encryptedData = "";
		String session_key = "ALdMPo8GgNKayaHgih8mQg==";
		String iv = "M0+cPrZdosenVFK/2w0BUQ==";
		String result = WxUtils.wxDecrypt(encryptedData, session_key, iv);
		System.out.println(result);
	}
	
	@Test
	public void test2()
	{
		MemberDTO member = memberManger.getMemberDTO("oesC25OgKUxaE45_z38vrlmzfp-0");
		System.out.println(member);
	}
	@Autowired
	private MktOrderDao orderDao;
	@Test
	public void test3()
	{
//		List<Map<String,Integer>> list = orderDao.getStatusNum(3);
//		for(Map<String,Integer> map : list)
//		{
//			for(String key : map.keySet())
//			{
//				log.info("key: {}", key);
//				log.info("value: {}", map.get(key));
//			}
//		}
	}
	
//	@Value("${wei.xin.config.app.id}")
//	public  String APP_ID;
//	@Value("${wei.xin.config.mch.id}")
//	public  String MCH_ID;
//	@Value("${wei.xin.config.re.url}")
//	public  String RE_URL;
	@Test
	public void test4() throws IllegalAccessException, InstantiationException, ClassNotFoundException
	{
//		System.out.println("APP_ID " + APP_ID);
//		System.out.println("MCH_ID " + MCH_ID);
//		System.out.println("RE_URL " + RE_URL);
//		PayReqData req = new PayReqData(11, "致一云农贸", "浙江致一云农贸科技有限公司", "11", "22", "https://www.lejiaxianda.com/lejia/v1/wx/pay/reur", "5555", "");
//		PayService service = new PayService();
		
//		System.out.println("APP_ID " + WeixinConfig.APP_ID);
//		System.out.println("MCH_ID " + WeixinConfig.MCH_ID);
//		System.out.println("RE_URL " + WeixinConfig.RE_URL);
	}
	
	@Autowired
	private WxManager wxManager;
	
    @Autowired
    private AccountRepository accountRepository;
	
	@Test
    public void test6() 
    {
	    Boolean uploadShippingInfo = wxManager.uploadShippingInfo(
            null,
            "932410243654" + "1",
            "1575634231",
            "手动",
            3,
            null,
            null,
            null,
            null,
            "o0bmw6zWRhdJqApS_UakhTPBXb1Y",
            22);
//	    mp.wechatSendMsg(arg0, arg1, arg2, arg3, arg4, arg5)
//	    Map<String,String> map = new HashMap<>();
//        map.put("thirdAppid", "wx7ce71dd525579a1c");
//        map.put("appid", "wxf377f28e4a3ff603");
//        map.put("url", "fromurl");
//        String string = HttpUtil.postBodyString("https://weixin.xinanshizu.com/wx/wechatOpenid", map);
//        System.out.println("string: " + string);
//	    wxManager.uploadShippingInfo(
//	        null,
//	        "912310243855432",
//	        
//	        );
    }

	@Test
    public void test7() 
    {
	    JSONObject data = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("value", "订单编号1122");
        data.put("first", jsonObject);
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("value", DateUtil.formatDate(new Date()));
        data.put("keyword1", jsonObject2);
        JSONObject jsonObject3 = new JSONObject();
        jsonObject3.put("value", "青菜");
        data.put("keyword2", jsonObject3);
        JSONObject jsonObject4 = new JSONObject();
        jsonObject4.put("value", "11111111");
        data.put("keyword3", jsonObject4);
        JSONObject jsonObject5 = new JSONObject();
        jsonObject5.put("value", "有新订单来啦");
        data.put("remark", jsonObject5);
        
        Map<String,String> map = new HashMap<>();
        map.put("thirdAppid", "wx7ce71dd525579a1c");
        map.put("appid", "wx8b4db457e120ce0d");
        map.put("templateid", "VfCZXAYhcxMLcZqMYUiML-BU0VtFgVbNs4k52FeykY0");
        map.put("openid", "o7rOf560FZa8g8groVioQ7gqL98w");
//        map.put("url", "");
        map.put("data", data.toString());
//        String string = HttpUtil.postBodyString("https://weixin.xinanshizu.com/v1/wechatSendMsg", map);
//        System.out.println("string: " + string);
    }
	
	
}

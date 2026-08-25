package cn.tofocus.lejia.util.wx;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.tencent.common.RandomStringGenerator;
import com.tencent.common.Signature;

public class PayJs {
 
	private String  appId;
	private String timeStamp;
	private String  nonceStr;
	private String pack;
	private String signType;
	private String paySign;
	public PayJs(PayRes res) {
		setAppId(res.getAppid());
		setTimeStamp(String.valueOf(System.currentTimeMillis()));
		setNonceStr(RandomStringGenerator.getRandomStringByLength(32));
		setPack("prepay_id="+res.getPrepay_id());
		setSignType("MD5");
		String sign = Signature.getSign(toMap());
		System.out.println("appId="+getAppId()+"--timeStamp:"+getTimeStamp()+"--nonceStr:"+getNonceStr()+"package:"+getPack()+"signType:"+getSignType());
		System.out.println(sign);
    setPaySign(sign);//把签名数据设置到Sign这个属性中		
	}
	public PayJs(String pack,String appid ,String timestamp ,String nocestr) {
		setAppId(appid);
		setTimeStamp(timestamp);
		setNonceStr(nocestr);
		setPack(pack);
		setSignType("MD5");
		String sign = Signature.getSign(toMap());
    setPaySign(sign);//把签名数据设置到Sign这个属性中		
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}


	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getNonceStr() {
		return nonceStr;
	}
	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}
	public String getPack() {
		return pack;
	}
	public void setPack(String pack) {
		this.pack = pack;
	}
	public String getSignType() {
		return signType;
	}
	public void setSignType(String signType) {
		this.signType = signType;
	}
	public String getPaySign() {
		return paySign;
	}
	public void setPaySign(String paySign) {
		this.paySign = paySign;
	}
	public Map<String,Object> toMap(){
    Map<String,Object> map = new HashMap<String, Object>();
    Field[] fields = this.getClass().getDeclaredFields();
    for (Field field : fields) {
        Object obj;
        try {
            obj = field.get(this);
            if(obj!=null){
            		if(field.getName().equals("pack")) {
            		  map.put("package", obj);
            		  System.out.println("package"+obj);
            		}else {
                map.put(field.getName(), obj);
            		}
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    return map;
}
}

package cn.tofocus.lejia.util.wx;

import com.tencent.service.BaseService;

public class PayService extends BaseService
{
    
    public PayService()
        throws IllegalAccessException, InstantiationException, ClassNotFoundException
    {
        super("https://api.mch.weixin.qq.com/pay/unifiedorder");
    }
    
    /**
     * 请求支付服务
     * 
     * @param scanPayReqData
     *            这个数据对象里面包含了API要求提交的各种数据字段
     * @return API返回的数据
     * @throws Exception
     */
    public String request(PayReqData payReqData)
        throws Exception
    {
        
        // --------------------------------------------------------------------
        // 发送HTTPS的Post请求到API地址
        // --------------------------------------------------------------------
        String responseString = sendPost(payReqData);
        
        return responseString;
    }
}

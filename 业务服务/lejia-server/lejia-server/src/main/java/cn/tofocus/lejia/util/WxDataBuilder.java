package cn.tofocus.lejia.util;

import com.alibaba.fastjson.JSONObject;

import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;

public class WxDataBuilder
{
    private JSONObject data = new JSONObject();
    
    private String currentParam = null;
    
    public WxDataBuilder param(String param)
    {
        currentParam = param;
        return this;
    }
    
//    public WxDataBuilder data(Object obj)
//    {
//        return addData("DATA", obj);
//    }

    public WxDataBuilder value(Object obj)
    {
        return addData("value", obj);
    }

    private WxDataBuilder addData(String k, Object obj)
    {
        if (currentParam == null)
            throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_NULL, "请先设置 param");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(k, obj);
        data.put(currentParam, jsonObject);
        currentParam = null;
        return this;
    }
    
    public JSONObject build()
    {
        return data;
    }
    
}

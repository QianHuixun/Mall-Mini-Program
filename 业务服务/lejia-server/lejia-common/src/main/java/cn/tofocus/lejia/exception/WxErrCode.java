package cn.tofocus.lejia.exception;

import cn.tofocus.core.exception.ErrCode;

public enum WxErrCode implements ErrCode
{
	GET_ACCESS_TOKEN_FAIL("wx-0101", "获取ACCESS_TOKEN失败"),
	HTTP_ERROR("wx-0102", "http请求异常");

	private final String code;

    private final String description;

    private WxErrCode(String code, String description)
    {
        this.code = code;
        this.description = description;
    }
    
    @Override
    public String getCode()
    {
        return code;
    }
    
    @Override
    public String getDescription()
    {
        return description;
    }
    
    @Override
    public boolean equalsCode(String code)
    {
        return this.code.equals(code);
    }
}

package cn.tofocus.lejia.utils;

import java.util.Date;

import cn.tofocus.common.util.security.MD5;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OrderVerifyCodeGenerator
{
    public static String build(String code, Integer pkey, Date createdTime)
    {
        String decode = code + pkey + createdTime.getTime();
        return MD5.getMD5(decode);
    }
}

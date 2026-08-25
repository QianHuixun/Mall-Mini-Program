package cn.tofocus.file.log;

import cn.tofocus.file.bean.ThumbType;

public interface FileAccessLogger
{
    default void prepareLog(long pkey, String code, long size, ThumbType thumb, String address, String referer,
        String userAgent)
    {
        
    }
    
    default void setSize(long size)
    {
        
    }
    
    default void setStatus(String status)
    {
        
    }
    
    default void flushLog()
    {
        
    }
}

package cn.tofocus.file.domain;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.db.redis.id.RedisCounter;
import cn.tofocus.file.bean.ThumbType;
import cn.tofocus.file.cache.FileAccessLogCache;
import cn.tofocus.file.es.doc.FileAccessLog;
import cn.tofocus.file.log.FileAccessLogger;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgent.ImmutableUserAgent;

@Component
public class Logger implements FileAccessLogger
{
    @Autowired
    private FileAccessLogCache cache;
    
    private UserAgentAnalyzer uaa = UserAgentAnalyzer.newBuilder().hideMatcherLoadStats().withCache(10000).build();
    
    @Autowired
    private RedisCounter redisCounter;
    
    private ThreadLocal<FileAccessLog> localLog = new ThreadLocal<>();
    
    @Override
    public void prepareLog(long pkey, String code, long size, ThumbType thumb, String address, String referer,
        String userAgent)
    {
        FileAccessLog log = new FileAccessLog();
        log.setPkey(redisCounter.increment("tfcloud", "file", "fileAccess"));
        log.setFileMd5(code);
        log.setFilePkey(pkey);
        log.setSize(size);
        log.setThumb(thumb);
        log.setIp(address);
        log.setReferer(referer);
        log.setAccessTime(new Date());
        if (userAgent != null)
        {
            ImmutableUserAgent agent = uaa.parse(userAgent);
            log.setDeviceType(agent.getValue(UserAgent.DEVICE_CLASS));
            log.setDeviceName(agent.getValue(UserAgent.DEVICE_NAME));
            log.setOs(agent.getValue(UserAgent.OPERATING_SYSTEM_NAME));
            log.setOsVersion(agent.getValue(UserAgent.OPERATING_SYSTEM_VERSION));
            log.setAgentType(agent.getValue(UserAgent.AGENT_CLASS));
            log.setAgentName(agent.getValue(UserAgent.AGENT_NAME));
            log.setAgentVersion(agent.getValue(UserAgent.AGENT_VERSION));
        }
        localLog.set(log);
    }

    @Override
    public void setSize(long size)
    {
        FileAccessLog fileAccessLog = localLog.get();
        if (fileAccessLog != null)
            fileAccessLog.setSize(size);
    }

    @Override
    public void setStatus(String status)
    {
        FileAccessLog fileAccessLog = localLog.get();
        if (fileAccessLog != null)
            fileAccessLog.setStatus(status);
    }

    @Override
    public void flushLog()
    {
        FileAccessLog fileAccessLog = localLog.get();
        if (fileAccessLog != null)
            cache.putIntoWriteCache(fileAccessLog);
        localLog.remove();
    }
}

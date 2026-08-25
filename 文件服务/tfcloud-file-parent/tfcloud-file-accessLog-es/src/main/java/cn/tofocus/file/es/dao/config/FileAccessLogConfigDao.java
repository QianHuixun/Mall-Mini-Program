package cn.tofocus.file.es.dao.config;

import org.springframework.stereotype.Component;

import cn.tofocus.db.redis.custer.ShardConfigDao;

@Component
public class FileAccessLogConfigDao extends ShardConfigDao
{

    @Override
    protected String domain()
    {
        return "tfcloud";
    }

    @Override
    protected String tableId()
    {
        return "FileAccessLog";
    }
}

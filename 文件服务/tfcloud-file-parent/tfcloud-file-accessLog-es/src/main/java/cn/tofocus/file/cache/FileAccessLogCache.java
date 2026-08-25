package cn.tofocus.file.cache;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.cachemap.write.WriteCache;
import cn.tofocus.core.data.KeyValue;
import cn.tofocus.file.es.dao.FileAccessLogDao;
import cn.tofocus.file.es.doc.FileAccessLog;

@Component
public class FileAccessLogCache extends WriteCache<FileAccessLog>
{
    @Autowired
    private FileAccessLogDao logDao;
    
    @Override
    protected void synWrite(List<KeyValue<String, FileAccessLog>> values)
        throws Exception
    {
        if (!values.isEmpty())
        {
            List<FileAccessLog> list = new ArrayList<>();
            for (KeyValue<String, FileAccessLog> v : values)
            {
                list.add(v.getValue());
            }
            logDao.addAll(list);
        }
    }
    
    @Override
    public int MinWriteBatch()
    {
        return 100;
    }
    
    @Override
    public int MaxWriteWait()
    {
        return 30;
    }

    @Override
    public int getMaxWriteCacheSize()
    {
        return 100000;
    }
    
}

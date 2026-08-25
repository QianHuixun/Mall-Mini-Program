package cn.tofocus.lejia.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.cachemap.DataGroupWriter;
import cn.tofocus.common.cachemap.write.KVWriteCache;
import cn.tofocus.lejia.bean.entity.sys.SysLog;
import cn.tofocus.lejia.dao.sys.SysLogDao;

@Component
public class LogWriteCache extends KVWriteCache<Long, SysLog>
{
	@Autowired
	private SysLogDao dao;
	@Override
	protected DataGroupWriter<Long, SysLog> dataGroupWriter() {
		return dao;
	}

}

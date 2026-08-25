package cn.tofocus.lejia.repository.sys;

import cn.tofocus.lejia.bean.entity.sys.SysLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
*  sys_log
* @author lai 2020-06-15
*/

@Repository
public interface SysLogRepository extends JpaRepository<SysLog,Long>,  JpaSpecificationExecutor<SysLog> 
{
	
}

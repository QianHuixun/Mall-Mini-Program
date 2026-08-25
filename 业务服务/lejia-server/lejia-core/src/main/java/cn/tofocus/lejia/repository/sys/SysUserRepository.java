package cn.tofocus.lejia.repository.sys;

import cn.tofocus.lejia.bean.entity.sys.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
*  sys_user_role
* @author lai 2020-06-15
*/

@Repository
public interface SysUserRepository extends JpaRepository<SysUser,Integer>,  JpaSpecificationExecutor<SysUser> 
{
}

package cn.tofocus.lejia.dao.sys;

import cn.tofocus.lejia.bean.entity.sys.SysUser;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import org.springframework.stereotype.Component;

@Component
public class SysUserDao extends JpaSpecificationDelegate<Integer,SysUser>
{
	public SysUser findbyPhone(String phone)
    {
        return this.selectOne().eq("mobile", phone).exec();
    }
}
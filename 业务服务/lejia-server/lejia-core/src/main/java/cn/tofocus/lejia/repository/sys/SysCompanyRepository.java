package cn.tofocus.lejia.repository.sys;

import cn.tofocus.lejia.bean.entity.sys.SysCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
*  sys_company
* @author lai 2020-06-15
*/

@Repository
public interface SysCompanyRepository extends JpaRepository<SysCompany,String>,  JpaSpecificationExecutor<SysCompany> 
{
}

package cn.tofocus.lejia.repository.sys;

import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
*  sys_farmer
* @author lai 2020-06-15
*/

@Repository
public interface SysFarmerRepository extends JpaRepository<SysFarmer,String>,  JpaSpecificationExecutor<SysFarmer> 
{
}

package cn.tofocus.lejia.repository.sys;

import cn.tofocus.lejia.bean.entity.sys.SysFarmerConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
*  sys_farmer_config
* @author lai 2020-06-15
*/

@Repository
public interface SysFarmerConfigRepository extends JpaRepository<SysFarmerConfig,String>,  JpaSpecificationExecutor<SysFarmerConfig> 
{
}

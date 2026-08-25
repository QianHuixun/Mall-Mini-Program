package cn.tofocus.lejia.repository.sys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.sys.SysFarmerMtype;

/**
*  sys_farmer_mtype
* @author zdw 2022-01-27
*/

@Repository
public interface SysFarmerMtypeRepository
    extends JpaRepository<SysFarmerMtype, Integer>, JpaSpecificationExecutor<SysFarmerMtype>
{
}

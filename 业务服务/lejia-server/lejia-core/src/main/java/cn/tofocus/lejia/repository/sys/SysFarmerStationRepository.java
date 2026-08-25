package cn.tofocus.lejia.repository.sys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerStation;

@Repository
public interface SysFarmerStationRepository  extends JpaRepository< SysFarmerStation,Integer>,  JpaSpecificationExecutor< SysFarmerStation> 
{
    
}

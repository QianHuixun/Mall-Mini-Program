package cn.tofocus.lejia.repository.market;

import cn.tofocus.lejia.bean.entity.market.MktLogistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
*  快递公司
* @author lai 2020-06-15
*/

@Repository
public interface MktLogisticsRepository extends JpaRepository<MktLogistics,Integer>,  JpaSpecificationExecutor<MktLogistics> 
{
}

package cn.tofocus.lejia.repository.market;

import cn.tofocus.lejia.bean.entity.market.MktCourier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
*  快递员
* @author lai 2020-06-15
*/

@Repository
public interface MktCourierRepository extends JpaRepository<MktCourier,Integer>,  JpaSpecificationExecutor<MktCourier> 
{
}

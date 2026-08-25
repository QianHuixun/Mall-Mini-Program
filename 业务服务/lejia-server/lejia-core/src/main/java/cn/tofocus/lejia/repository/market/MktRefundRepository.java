package cn.tofocus.lejia.repository.market;

import cn.tofocus.lejia.bean.entity.market.MktRefund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
*  退款
* @author zdw 2020-07-20
*/

@Repository
public interface MktRefundRepository extends JpaRepository<MktRefund,Integer>,  JpaSpecificationExecutor<MktRefund> 
{
}

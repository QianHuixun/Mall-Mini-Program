package cn.tofocus.lejia.repository.market;

import cn.tofocus.lejia.bean.entity.market.MktPointPay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * mkt_point_pay
 *
 * @author pty 2020-07-30
 */

@Repository
public interface MktPointPayRepository extends JpaRepository<MktPointPay, Integer>, JpaSpecificationExecutor<MktPointPay> {
}

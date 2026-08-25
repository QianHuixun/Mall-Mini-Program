package cn.tofocus.lejia.repository.vendor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.vendor.MktSettlementLineDay;

/**
*  结算报表
* @author zdw 2021-12-07
*/

@Repository
public interface MktSettlementLineDayRepository
    extends JpaRepository<MktSettlementLineDay, Long>, JpaSpecificationExecutor<MktSettlementLineDay>
{
}

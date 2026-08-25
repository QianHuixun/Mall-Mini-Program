package cn.tofocus.lejia.repository.vendor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.vendor.MktSettlementTotal;

/**
*  记录每天清分数据
* @author zdw 2021-12-29
*/

@Repository
public interface MktSettlementTotalRepository
    extends JpaRepository<MktSettlementTotal, Long>, JpaSpecificationExecutor<MktSettlementTotal>
{
}

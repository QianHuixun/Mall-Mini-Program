package cn.tofocus.lejia.repository.market;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.vendor.MktVendorPoint;

/**
*  mkt_vendor
*/

@Repository
public interface MktVendorPointRepository extends JpaRepository<MktVendorPoint,Integer>,  JpaSpecificationExecutor<MktVendorPoint> 
{
}

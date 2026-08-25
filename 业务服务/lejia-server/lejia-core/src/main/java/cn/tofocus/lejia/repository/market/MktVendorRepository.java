package cn.tofocus.lejia.repository.market;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.vendor.MktVendor;

/**
*  mkt_vendor
* @author lai 2020-06-15
*/

@Repository
public interface MktVendorRepository extends JpaRepository<MktVendor,Integer>,  JpaSpecificationExecutor<MktVendor> 
{
}

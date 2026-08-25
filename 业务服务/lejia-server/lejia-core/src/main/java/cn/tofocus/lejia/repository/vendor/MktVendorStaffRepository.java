package cn.tofocus.lejia.repository.vendor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.vendor.MktVendorStaff;

/**
*  商户店员
* @author zdw 2022-01-27
*/

@Repository
public interface MktVendorStaffRepository
    extends JpaRepository<MktVendorStaff, Integer>, JpaSpecificationExecutor<MktVendorStaff>
{
}

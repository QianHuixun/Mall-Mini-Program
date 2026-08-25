package cn.tofocus.lejia.repository.market;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.vendor.MktVendorGoods;

/**
*  mkt_vendor_goods
* @author zdw 2020-10-09
*/

@Repository
public interface MktVendorGoodsRepository extends JpaRepository<MktVendorGoods,Integer>,  JpaSpecificationExecutor<MktVendorGoods> 
{
}

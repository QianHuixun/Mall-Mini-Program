package cn.tofocus.lejia.repository.market;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.vendor.MktVendorBigData;

/**
 * 商户大数据表(MktVendorBigdata) 原生JPA的dao层
 * 
 * @author geshaojian
 * @since 2021-10-12 10:47:56
 */
@Repository
public interface MktVendorBigdataRepository extends JpaRepository<MktVendorBigData,Integer>
{

}
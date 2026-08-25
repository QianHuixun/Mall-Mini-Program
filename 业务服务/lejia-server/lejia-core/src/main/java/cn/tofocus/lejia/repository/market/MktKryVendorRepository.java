package cn.tofocus.lejia.repository.market;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.market.MktKryVendor;


@Repository
public interface MktKryVendorRepository extends JpaRepository<MktKryVendor,Integer>,  JpaSpecificationExecutor<MktKryVendor> 
{
}

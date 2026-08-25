package cn.tofocus.lejia.repository.market;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.vendor.MktVendorPointLine;

/**
*  mkt_vendor
*/

@Repository
public interface MktVendorPointLineRepository extends JpaRepository<MktVendorPointLine,Integer>,  JpaSpecificationExecutor<MktVendorPointLine> 
{
	@Query(value = "SELECT v.name,v.mobile,sum(points) s from mkt_vendor_point_line l, mkt_vendor v " 
			+ "where l.vendor = v.pkey and l.ascription = :ascription "  
			+ "and if(:vendorName != '', v.name like CONCAT('%',:vendorName,'%'), 1=1) "
			+ "and if(:startTime != '', l.created_time between :startTime and :endTime, 1=1)"
			+ "group by v.name,v.mobile  order by s desc limit :page, :pagesize " , nativeQuery = true)
	List<List<Object>> vendorSales(
			@Param("vendorName") String vendorName, @Param("startTime") String startTime, @Param("endTime") String endTime,
			@Param("page") int page,  @Param("pagesize") int pagesize, @Param("ascription")Integer ascription);
}

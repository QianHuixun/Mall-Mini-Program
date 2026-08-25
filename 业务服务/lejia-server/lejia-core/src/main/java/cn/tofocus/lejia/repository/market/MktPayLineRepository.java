package cn.tofocus.lejia.repository.market;

import cn.tofocus.lejia.bean.entity.market.MktPayLine;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
*  mkt_pay_line
* @author zdw 2020-07-24
*/

@Repository
public interface MktPayLineRepository extends JpaRepository<MktPayLine,Integer>,  JpaSpecificationExecutor<MktPayLine> 
{
	@Query(value = "select LEFT(s.pay_time,:timeLength),sum(s.amt),COUNT(s.pkey) from "
			+ "(select * from mkt_pay_line where if(:startTime != '', pay_time BETWEEN :startTime and :endTime, 1=1) "
			+ "and order_number in (:orderNumList) group by order_number ) s  "  
			+ "GROUP BY LEFT(s.pay_time,:timeLength) ", nativeQuery = true)
	List<List<Object>> queryPayLines(@Param("startTime") String startTime, @Param("endTime") String endTime, 
			@Param(value = "orderNumList") List<String> orderNumList, @Param("timeLength")Integer timeLength);
}

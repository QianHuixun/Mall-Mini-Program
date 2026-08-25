package cn.tofocus.lejia.repository.market;

import java.math.BigDecimal;
import java.util.List;


import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.vendor.MktVendorOrder;

/**
*  mkt_vendor_order
* @author zdw 2020-10-09
*/

@Repository
public interface MktVendorOrderRepository
    extends JpaRepository<MktVendorOrder, Integer>, JpaSpecificationExecutor<MktVendorOrder>
{
    /**
     * 商户对账/撤销-总金额
     * @return    结果
     */
    @Query(value = "SELECT SUM(price * num) FROM mkt_vendor_order "
        + "WHERE if(COALESCE(:pkeys, NULL) IS NOT NULL, `pkey` IN (:pkeys), 1=1) "
        + "AND if(COALESCE(:marketPkeys, NULL) IS NOT NULL, `farmer` IN (:marketPkeys), 1=1) "
        + "AND if(COALESCE(:vendorPkey, NULL) IS NOT NULL, `vendor` IN (:vendorPkey), 1=1) "
        + "AND if(:startTime != '', DATE_FORMAT(`created_time`, '%Y-%m-%d %H:%d:%s') BETWEEN :startTime AND :endTime, 1=1) "
        + "AND if(COALESCE(:purchaseStatus, NULL) IS NOT NULL, `purchase_status` IN (:purchaseStatus), 1=1)"
        + "and ascription = :ascription "
        + "AND if(COALESCE(:status, NULL) IS NOT NULL, `status` IN (:status), 1=1)", nativeQuery = true)
    BigDecimal sumTotalPrice(@Param("pkeys") List<Integer> pkeys, @Param("marketPkeys") List<String> marketPkeys,
        @Param("vendorPkey") List<Integer> vendorPkey, @Param("startTime") String startTime,
        @Param("endTime") String endTime, @Param("purchaseStatus") List<Integer> purchaseStatus,
        @Param("status") List<Integer> status, @Param("ascription")Integer ascription);
    
    @Query(value = "select sum(amt) from mkt_vendor_order where vendor = :vendorPkey "
        + "and created_time > :startTime and purchase_status != 4 ", nativeQuery = true)
    BigDecimal countAmtDate(@Param("vendorPkey") Integer vendorPkey, @Param("startTime") String startTime);
    
    @Query(value = "select vendor,DATE_FORMAT(created_time,'%Y-%m-%d') time,COUNT(1),SUM(total_price) from mkt_vendor_order  "
        + "where status = :status and farmer = :market "
        + "AND if(COALESCE(:vendorKeys, NULL) IS NOT NULL, `vendor` IN (:vendorKeys), 1=1) "
        + "AND if(:startTime != '', DATE_FORMAT(created_time,'%Y-%m-%d') BETWEEN :startTime AND :endTime, 1=1) "
        + "GROUP BY vendor,time " + "ORDER BY :#{#sort} ", nativeQuery = true)
    List<List<Object>> purchaseReportDay(@Param("status") Integer status, @Param("vendorKeys") List<Integer> vendorKeys,
        @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("market") String market,
        Sort sort);
    
    @Query(value = "select vendor,DATE_FORMAT(created_time,'%Y-%m') time, COUNT(1), SUM(total_price) from mkt_vendor_order  "
        + "where status = :status and farmer = :market "
        + "AND if(COALESCE(:vendorKeys, NULL) IS NOT NULL, `vendor` IN (:vendorKeys), 1=1) "
        + "AND if(:startTime != '', DATE_FORMAT(created_time,'%Y-%m') BETWEEN :startTime AND :endTime, 1=1) "
        + "GROUP BY vendor,time " + "ORDER BY :#{#sort} ", nativeQuery = true)
    List<List<Object>> purchaseReportMonth(@Param("status") Integer status,
        @Param("vendorKeys") List<Integer> vendorKeys, @Param("startTime") String startTime,
        @Param("endTime") String endTime, @Param("market") String market, Sort sort);
    
    @Query(value = "select vendor,DATE_FORMAT(created_time,'%Y') time, COUNT(1), SUM(total_price) from mkt_vendor_order  "
        + "where status = :status and farmer = :market "
        + "AND if(COALESCE(:vendorKeys, NULL) IS NOT NULL, `vendor` IN (:vendorKeys), 1=1) "
        + "AND if(:startTime != '', DATE_FORMAT(created_time,'%Y') BETWEEN :startTime AND :endTime, 1=1) "
        + "GROUP BY vendor,time " + "ORDER BY :#{#sort} ", nativeQuery = true)
    List<List<Object>> purchaseReportYear(@Param("status") Integer status,
        @Param("vendorKeys") List<Integer> vendorKeys, @Param("startTime") String startTime,
        @Param("endTime") String endTime, @Param("market") String market, Sort sort);
    
    @Modifying
    @Query(value = "UPDATE MktVendorOrder a SET a.settlementPkey = :settlementPkey, a.status = 2 " 
        + "where a.vendor <> 0 "
        + "AND a.status = 1 AND a.purchaseStatus = 3 AND a.settlementPkey is null "
        + "AND a.orderPkey in (:keys)")
    void updateSettlementPkey(@Param("settlementPkey")Integer settlementPkey,
        @Param("keys") List<Integer> keys);
    
}

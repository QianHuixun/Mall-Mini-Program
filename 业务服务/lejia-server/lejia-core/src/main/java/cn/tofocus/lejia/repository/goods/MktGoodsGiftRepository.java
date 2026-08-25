package cn.tofocus.lejia.repository.goods;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.goods.MktGoodsGift;

/**
*  礼品券商品扩展数据
* @author zdw 2022-03-14
*/

@Repository
public interface MktGoodsGiftRepository
    extends JpaRepository<MktGoodsGift, Integer>, JpaSpecificationExecutor<MktGoodsGift>
{
    @Query(value = "SELECT sum(amtn) from mkt_member_gift g, mkt_order o "
        + "where g.user_vendor = :vendor "
        + "and if(:startDate != '', g.user_time between :startDate and :endDate, 1=1) "
        + "and g.order_pkey = o.pkey and g.status = 1 ", nativeQuery = true)
    BigDecimal sumAmtn(@Param("vendor") Integer vendor, 
        @Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    @Query(value = "SELECT * from mkt_goods_gift " 
        + "where pkey in (:keys) and gift_type = 1 "
        + "order by IF(ISNULL(end_date),0,1),end_date desc LIMIT :page, :pagesize", nativeQuery = true)
    List<MktGoodsGift> listGiftV3True(@Param("keys")List<Integer> keys, @Param("page") int page, @Param("pagesize") int pagesize);
    
    @Query(value = "SELECT * from mkt_goods_gift " 
        + "where pkey in (:keys) and gift_type = 1 "
        + "order by IF(ISNULL(end_date),1,0),end_date LIMIT :page, :pagesize", nativeQuery = true)
    List<MktGoodsGift> listGiftV3False(@Param("keys")List<Integer> keys, @Param("page") int page, @Param("pagesize") int pagesize);
}

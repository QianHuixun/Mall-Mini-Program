package cn.tofocus.lejia.repository.market;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.goods.MktGoods;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 商品(在售)
 *
 * @author lai 2020-06-15
 */

@Repository
public interface MktGoodsRepository extends JpaRepository<MktGoods, Integer>, JpaSpecificationExecutor<MktGoods> {

    @Modifying
    @Query("update MktGoods set viewCount = viewCount + 1 where pkey = :pkey")
    void autoViewCount(@Param("pkey") Integer pkey);

    @Modifying
    @Query("update MktGoods set enabled = false where endDate < :today and ascription = :ascription ")
    void disableExpiredGoods(@Param("today") Date today, @Param("ascription") Integer ascription);
    
    @Modifying
    @Query("update MktGoods set enabled = true where startDate = :today and ascription = :ascription ")
    void enableExpiredGoods(@Param("today") Date today, @Param("ascription") Integer ascription);

    @Query("from MktGoods where enabled = true and startDate <= current_timestamp and endDate >= current_timestamp")
    List<MktGoods> findAllByOnSell();
    
    @Query(value = "select g.title,sum(kc.kc_num) k from mkt_goods g, mkt_goods_space s, mkt_space_kc kc "  
    		+ "where g.pkey = s.goods and g.m_type = :mType and s.pkey = kc.pkey and g.ascription = :ascription " 
    		+ "and g.id_del = 0 "
    		+ "and if(:marketPkey != '', g.farmer = :marketPkey, 1=1) "
			+ "and if(:companyPkey != '', g.company = :companyPkey, 1=1) "
    		+ "group by g.title  "  
    		+ "HAVING k < 11 "  
    		+ "ORDER BY k ", nativeQuery = true)
	List<List<Object>> getGoodsKc(@Param("marketPkey") String marketPkey, 
			@Param("companyPkey") String companyPkey, @Param("mType") Integer mType, @Param("ascription") Integer ascription);
    
    
    @Query(value = "select g.pkey,g.title,g.photo1,s.price,s.price_old,s.pkey sPkey from mkt_goods g, mkt_goods_space s " 
    		+ "where farmer = :marketPkey  and m_type = 1 and id_del = 0 and g.pkey = s.goods "  
    		+ "and s.kc_num > 0 and s.price >= :pool  order by s.price desc limit :page, :pagesize ", nativeQuery = true)
	List<List<Object>> getGoodsPool(@Param("marketPkey") String marketPkey, 
			 @Param("pool") BigDecimal pool, @Param("page") int page,  @Param("pagesize") int pagesize);
}

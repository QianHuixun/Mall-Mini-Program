package cn.tofocus.lejia.repository.market;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.bean.enums.v3.GoodsSpaceKcV3Dto;

/**
*  商品规格
* @author lai 2020-06-15
*/

@Repository
public interface MktGoodsSpaceRepository
    extends JpaRepository<MktGoodsSpace, Integer>, JpaSpecificationExecutor<MktGoodsSpace>
{
    Integer countAllByGoods(Integer goods);
    
    @Query(value = "select min(price) from mkt_goods_space where goods = :goods group by goods", nativeQuery = true)
    BigDecimal minPrice(@Param("goods") Integer goods);
    
    
    @Query(value = "SELECT goods,sum(kc_num) kcNum from mkt_goods_space  where goods in (:keys)  "
                   + "GROUP BY goods " 
                   + "ORDER BY sum(kc_num) desc limit :page,:pagesize ", nativeQuery = true)
    List<GoodsSpaceKcV3Dto> listKcNumSort(@Param("page") Integer page, @Param("pagesize") Integer pagesize, 
        @Param("keys") List<Integer> keys);
}

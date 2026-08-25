package cn.tofocus.lejia.repository.market;

import cn.tofocus.lejia.bean.entity.market.MktCookfd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 菜谱
 *
 * @author lai 2020-06-15
 */

@Repository
public interface MktCookfdRepository extends JpaRepository<MktCookfd, Integer>, JpaSpecificationExecutor<MktCookfd> {
    @Modifying
    @Query("update MktCookfd set viewCount = viewCount + 1 where pkey = :pkey")
    void autoViewCount(@Param("pkey") Integer pkey);

    @Modifying
    @Query("update MktCookfd set collCount = collCount + :num where pkey = :pkey")
    void autoCollCount(@Param("pkey") Integer pkey, @Param("num") Integer num);

    @Query(value = "select a.* from mkt_cookfd a , mkt_cookfd_line b "
        + "where a.pkey = b.cookfd and b.goods = :goods and enabled = 1 and id_del = 0 " , nativeQuery = true)
    List<MktCookfd> queryByGoods(@Param("goods") Integer goods);
}

package cn.tofocus.lejia.repository.market;

import cn.tofocus.lejia.bean.entity.market.MktCookfdLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 食材清单
 *
 * @author lai 2020-06-15
 */

@Repository
public interface MktCookfdLineRepository extends JpaRepository<MktCookfdLine, Integer>, JpaSpecificationExecutor<MktCookfdLine> {

    @Query("from MktCookfdLine where cookfd = :cookfd order by sort asc")
    List<MktCookfdLine> findByCookfd(@Param("cookfd") Integer cookfd);
    
    
}

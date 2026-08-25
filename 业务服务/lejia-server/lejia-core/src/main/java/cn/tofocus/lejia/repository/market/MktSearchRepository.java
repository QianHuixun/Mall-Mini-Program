package cn.tofocus.lejia.repository.market;

import cn.tofocus.lejia.bean.entity.market.MktSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * mkt_search
 *
 * @author lai 2020-06-15
 */

@Repository
public interface MktSearchRepository extends JpaRepository<MktSearch, Integer>, JpaSpecificationExecutor<MktSearch> {
    @Query(value = "select descp from mkt_search "
        + "where created_time between ? and ? "
        + "and ascription = ? "
        + "and stype=? group by descp order by count(descp) desc limit 0,8", nativeQuery = true)
    List<Map<String, Object>> queryGroupByDescp(String sTime, String eTime, Integer ascription, Integer stype);
}

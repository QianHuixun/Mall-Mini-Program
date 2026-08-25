package cn.tofocus.lejia.repository.market;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.market.MktDrawWin;

/**
*  广告位
* @author lai 2020-06-15
*/

@Repository
public interface MktDrawWinRepository extends JpaRepository<MktDrawWin, Integer>, JpaSpecificationExecutor<MktDrawWin>
{
    
    @Query(value = "SELECT p.name,w.p_type,count(w.pkey)  from mkt_draw_win w, mkt_draw_prize p "
        + "where w.prize = p.pkey "
        + "and w.ascription = :ascription " 
        + "group by p.name ,w.p_type ", nativeQuery = true)
    List<List<Object>> getDrawWin(@Param("ascription")Integer ascription);
}

package cn.tofocus.lejia.repository.market;

import cn.tofocus.lejia.bean.entity.market.MktSearchHot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
*  mkt_search_hot
* @author pty 2020-07-02
*/

@Repository
public interface MktSearchHotRepository extends JpaRepository<MktSearchHot,Integer>,  JpaSpecificationExecutor<MktSearchHot>
{
}

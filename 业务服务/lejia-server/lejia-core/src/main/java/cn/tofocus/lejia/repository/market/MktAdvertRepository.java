package cn.tofocus.lejia.repository.market;

import cn.tofocus.lejia.bean.entity.market.MktAdvert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
*  广告位
* @author lai 2020-06-15
*/

@Repository
public interface MktAdvertRepository extends JpaRepository<MktAdvert,Integer>,  JpaSpecificationExecutor<MktAdvert> 
{
}

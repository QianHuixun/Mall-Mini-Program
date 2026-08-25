package cn.tofocus.lejia.repository.market;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.market.MktAdvise;

/**
*  广告位
* @author lai 2020-06-15
*/

@Repository
public interface MktAdviseRepository extends JpaRepository<MktAdvise,Integer>,  JpaSpecificationExecutor<MktAdvise> 
{
}

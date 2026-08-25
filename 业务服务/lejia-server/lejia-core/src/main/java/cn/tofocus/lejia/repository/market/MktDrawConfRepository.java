package cn.tofocus.lejia.repository.market;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.market.MktDrawConf;

/**
*  广告位
* @author lai 2020-06-15
*/

@Repository
public interface MktDrawConfRepository extends JpaRepository<MktDrawConf,Integer>,  JpaSpecificationExecutor<MktDrawConf> 
{
}

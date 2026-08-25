package cn.tofocus.lejia.repository.market;

import cn.tofocus.lejia.bean.entity.market.MktOrderCut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
*  砍价记录
* @author zdw 2020-08-06
*/

@Repository
public interface MktOrderCutRepository extends JpaRepository<MktOrderCut,Integer>,  JpaSpecificationExecutor<MktOrderCut> 
{
}

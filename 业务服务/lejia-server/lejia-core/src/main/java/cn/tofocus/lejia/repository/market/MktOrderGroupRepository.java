package cn.tofocus.lejia.repository.market;

import cn.tofocus.lejia.bean.entity.market.MktOrderGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
*  团购记录
* @author zdw 2020-08-06
*/

@Repository
public interface MktOrderGroupRepository extends JpaRepository<MktOrderGroup,Integer>,  JpaSpecificationExecutor<MktOrderGroup> 
{
}

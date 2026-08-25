package cn.tofocus.lejia.repository.goods;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.goods.MktSpaceKc;

/**
*  mkt_space_kc
* @author zdw 2022-01-27
*/

@Repository
public interface MktSpaceKcRepository extends JpaRepository<MktSpaceKc, Integer>, JpaSpecificationExecutor<MktSpaceKc>
{
}

package cn.tofocus.lejia.repository.market;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.market.MktCollection;

/**
*  我的收藏
* @author zdw 2020-07-20
*/

@Repository
public interface MktCollectionRepository extends JpaRepository<MktCollection,Integer>,  JpaSpecificationExecutor<MktCollection> 
{
    Integer countAllByMemberAndCtypeAndObjKey(Integer member, Integer ctype, Integer objKey);
}

package cn.tofocus.lejia.repository.market;

import cn.tofocus.lejia.bean.entity.market.MktGtype;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
*  分类
* @author lai 2020-06-15
*/

@Repository
public interface MktGtypeRepository extends JpaRepository<MktGtype,Integer>,  JpaSpecificationExecutor<MktGtype> 
{
}

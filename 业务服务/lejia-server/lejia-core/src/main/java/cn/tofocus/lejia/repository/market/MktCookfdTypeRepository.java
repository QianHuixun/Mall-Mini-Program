package cn.tofocus.lejia.repository.market;

import cn.tofocus.lejia.bean.entity.market.MktCookfdType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
*  菜谱分类
* @author zdw 2020-08-12
*/

@Repository
public interface MktCookfdTypeRepository extends JpaRepository<MktCookfdType,Integer>,  JpaSpecificationExecutor<MktCookfdType> 
{
}

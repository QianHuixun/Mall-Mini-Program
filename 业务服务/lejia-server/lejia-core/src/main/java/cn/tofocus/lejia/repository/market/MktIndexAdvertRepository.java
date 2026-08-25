package cn.tofocus.lejia.repository.market;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import cn.tofocus.lejia.bean.entity.market.MktIndexAdvert;

/**
*  app弹窗广告
* @author zdw 2020-09-22
*/

@Repository
public interface MktIndexAdvertRepository extends JpaRepository<MktIndexAdvert,Integer>,  JpaSpecificationExecutor<MktIndexAdvert> 
{
}

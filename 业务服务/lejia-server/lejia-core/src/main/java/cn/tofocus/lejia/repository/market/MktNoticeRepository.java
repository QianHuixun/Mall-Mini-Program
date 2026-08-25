package cn.tofocus.lejia.repository.market;

import cn.tofocus.lejia.bean.entity.market.MktNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
*  公告
* @author lai 2020-06-15
*/

@Repository
public interface MktNoticeRepository extends JpaRepository<MktNotice,Integer>,  JpaSpecificationExecutor<MktNotice> 
{
}

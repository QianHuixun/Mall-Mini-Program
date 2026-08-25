package cn.tofocus.lejia.repository.market;

import cn.tofocus.lejia.bean.entity.market.MktAppMsg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
*  mkt_app_msg
* @author lai 2020-06-15
*/

@Repository
public interface MktAppMsgRepository extends JpaRepository<MktAppMsg,Integer>,  JpaSpecificationExecutor<MktAppMsg> 
{
}

package cn.tofocus.lejia.repository.market;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.market.MktZxPayLine;

/**
*  中信支付回调记录
* @author zdw 2021-11-18
*/

@Repository
public interface MktZxPayLineRepository extends JpaRepository<MktZxPayLine,Integer>,  JpaSpecificationExecutor<MktZxPayLine> 
{
}

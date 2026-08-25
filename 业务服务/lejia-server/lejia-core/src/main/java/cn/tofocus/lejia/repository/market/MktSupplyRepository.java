package cn.tofocus.lejia.repository.market;

import cn.tofocus.lejia.bean.entity.market.MktSupply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 商品供应库(MktSupply) 原生JPA的dao层
 *
 * @author geshaojian
 * @since 2021-09-17 22:47:39
 */
@Repository
public interface MktSupplyRepository
    extends JpaRepository<MktSupply, Integer>, JpaSpecificationExecutor<MktSupply>
{

}
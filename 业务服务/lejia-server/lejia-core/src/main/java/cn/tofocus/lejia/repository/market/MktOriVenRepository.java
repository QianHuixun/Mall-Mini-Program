package cn.tofocus.lejia.repository.market;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.market.MktOriVen;

@Repository
public interface MktOriVenRepository extends JpaRepository<MktOriVen,Integer>,  JpaSpecificationExecutor<MktOriVen> 
{
}

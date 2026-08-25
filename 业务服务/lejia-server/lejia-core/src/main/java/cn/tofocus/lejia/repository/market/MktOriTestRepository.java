package cn.tofocus.lejia.repository.market;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.market.MktOriTest;

@Repository
public interface MktOriTestRepository extends JpaRepository<MktOriTest,Integer>,  JpaSpecificationExecutor<MktOriTest> 
{
}

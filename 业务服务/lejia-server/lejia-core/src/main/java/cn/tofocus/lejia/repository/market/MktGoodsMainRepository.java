package cn.tofocus.lejia.repository.market;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.goods.MktGoodsMain;

@Repository
public interface MktGoodsMainRepository extends JpaRepository<MktGoodsMain,Integer>,  JpaSpecificationExecutor<MktGoodsMain> 
{
}

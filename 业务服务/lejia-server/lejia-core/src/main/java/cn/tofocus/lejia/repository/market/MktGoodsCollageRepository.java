package cn.tofocus.lejia.repository.market;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.goods.MktGoodsCollage;

/**
*  拼团商品
* @author lai 2020-06-15
*/

@Repository
public interface MktGoodsCollageRepository extends JpaRepository<MktGoodsCollage,Integer>,  JpaSpecificationExecutor<MktGoodsCollage> 
{
}

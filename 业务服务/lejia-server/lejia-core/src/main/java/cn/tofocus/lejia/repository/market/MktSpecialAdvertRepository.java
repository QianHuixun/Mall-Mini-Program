package cn.tofocus.lejia.repository.market;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.hasPkey.AdvertPkey;
import cn.tofocus.lejia.bean.entity.market.MktSpecialAdvert;

/**
*  专区广告
* @author zdw 2021-09-30
*/

@Repository
public interface MktSpecialAdvertRepository
    extends JpaRepository<MktSpecialAdvert, AdvertPkey>, JpaSpecificationExecutor<MktSpecialAdvert>
{
}

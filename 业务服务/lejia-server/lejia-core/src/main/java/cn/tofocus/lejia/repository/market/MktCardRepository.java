package cn.tofocus.lejia.repository.market;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.market.MktCard;

@Repository
public interface MktCardRepository extends JpaRepository<MktCard,Integer>,  JpaSpecificationExecutor<MktCard> 
{
	@Query(value = "select COUNT('pkey') num from mkt_card where 'id_del' = FALSE and 'enabled' = TRUE " + 
			"and 'pkey' not in (select 'card' from mkt_member_card where 'member_key' = :memberPkey ) ;", nativeQuery = true)
	public Integer getCardNum(@Param("memberPkey")Integer memberPkey);
}

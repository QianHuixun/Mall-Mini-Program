package cn.tofocus.lejia.repository.market;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.market.MktRichTemplate;

/**
*  富文本模板
* @author zdw 2021-12-01
*/

@Repository
public interface MktRichTemplateRepository extends JpaRepository<MktRichTemplate,Integer>,  JpaSpecificationExecutor<MktRichTemplate> 
{
}

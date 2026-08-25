package cn.tofocus.lejia.repository.sys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.sys.MktPromote;

/**
*  推广
* @author zdw 2022-05-25
*/

@Repository
public interface MktPromoteRepository 
    extends JpaRepository<MktPromote, Integer>, JpaSpecificationExecutor<MktPromote>
{
}

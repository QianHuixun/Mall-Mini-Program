package cn.tofocus.lejia.repository.sys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.sys.SysConfigEntity;

/**
*  mkt_config
* @author zdw 2021-09-28
*/

@Repository
public interface SysConfigRepository
    extends JpaRepository<SysConfigEntity, String>, JpaSpecificationExecutor<SysConfigEntity>
{
}

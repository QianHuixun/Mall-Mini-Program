package cn.tofocus.lejia.repository.zx;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.zx.ZxPostRecord;

/**
*  向中信请求记录
* @author zdw 2021-12-07
*/

@Repository
public interface ZxPostRecordRepository
    extends JpaRepository<ZxPostRecord, Integer>, JpaSpecificationExecutor<ZxPostRecord>
{
}

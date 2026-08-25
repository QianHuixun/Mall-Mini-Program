package cn.tofocus.lejia.repository.zx;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.zx.ZxFileRecord;

/**
*  中信文件生成和发送
* @author zdw 2021-12-07
*/

@Repository
public interface ZxFileRecordRepository
    extends JpaRepository<ZxFileRecord, Integer>, JpaSpecificationExecutor<ZxFileRecord>
{
}

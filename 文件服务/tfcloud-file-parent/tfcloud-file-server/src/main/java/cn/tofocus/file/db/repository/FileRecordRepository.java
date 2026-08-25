package cn.tofocus.file.db.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cn.tofocus.file.db.entity.FileRecordEntity;
import cn.tofocus.file.db.key.FileRecordKey;

public interface FileRecordRepository extends JpaRepository<FileRecordEntity, FileRecordKey>
{
    @Query(value = "select r.md5, r.size from file_record r"
        + " left join file_ref2 f on r.md5 = f.md5 and r.size = f.size where f.pkey is null", nativeQuery = true)
    List<Map<String, Object>> allOrphanFileRecord();
}

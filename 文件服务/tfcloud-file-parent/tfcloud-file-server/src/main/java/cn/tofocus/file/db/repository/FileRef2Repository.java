package cn.tofocus.file.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cn.tofocus.file.db.entity.FileRef2Entity;

public interface FileRef2Repository extends JpaRepository<FileRef2Entity, Long>
{
    @Query(value = "select f.pkey from file_ref2 f"
        + " left join file_ref_link l on f.pkey = l.file_pkey where l.pkey is null and f.created_time < date_add(now(), interval '-30' day)", nativeQuery = true)
    List<Object> allOrphanFileRef();
    
}

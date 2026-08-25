package cn.tofocus.file.db.service;

import java.util.List;
import java.util.Set;

import cn.tofocus.file.bean.FileRef2;

public interface FileRef2Service
{
    void addFileRef(FileRef2 ref);

    FileRef2 getFileRef(long id);

    List<FileRef2> listByPkeys(Set<Long> refPkeys);
    
}

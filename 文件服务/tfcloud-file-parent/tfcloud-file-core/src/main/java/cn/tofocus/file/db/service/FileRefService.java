package cn.tofocus.file.db.service;

import cn.tofocus.file.bean.FileRef;

@Deprecated
public interface FileRefService
{
    FileRef getFileRef(long id);

    boolean removeById(Long pkey);
}

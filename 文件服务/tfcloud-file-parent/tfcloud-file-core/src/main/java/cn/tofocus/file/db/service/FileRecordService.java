package cn.tofocus.file.db.service;

import cn.tofocus.file.bean.FileRecord;

public interface FileRecordService
{
    
    FileRecord getFileRecord(String md5, long size);

    void addFileRecord(FileRecord fileRecord);

    void updateFileRecord(FileRecord fileRecord);
    
}

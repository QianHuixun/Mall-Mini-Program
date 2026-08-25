package cn.tofocus.file.db.service;

import java.util.List;

import cn.tofocus.file.bean.FileRefLink;

public interface FileRefLinkService
{

    void putAllFileRefLink(List<FileRefLink> list);

    void delByPkey(String domain, String db, String table, List<String> pkeys);

    void delByTable(String domain, String db, String table);
    
}

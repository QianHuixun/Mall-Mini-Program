package cn.tofocus.file.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.MapedList;
import cn.tofocus.db.file.FileServer;
import cn.tofocus.db.file.RefChange;
import cn.tofocus.db.file.RefChangeList;
import cn.tofocus.db.file.Upd;
import cn.tofocus.file.api.v3.FileApiV3Impl;
import cn.tofocus.file.bean.FileInfoV3;
import cn.tofocus.file.bean.UpdateParam;
import cn.tofocus.file.bean.UploadType;

@Component
public class FileServerImpl implements FileServer
{
    @Value("${tofocus.file.baseUrl:default}")
    private String baseUrl;

    @Autowired
    private FileApiV3Impl api;

    @Override
    public String getFileBaseUrl()
    {
        return baseUrl;
    }
    
    @Override
    public String getFileBaseIntranetUrl()
    {
        return baseUrl;
    }
    
    @Override
    public void referencesFiles(String domain, String db, String table, List<RefChange> refs)
    {
        RefChangeList ref = new RefChangeList();
        ref.setDomain(domain);
        ref.setDb(db);
        ref.setTable(table);
        ref.setRefs(refs);
        api.referencesFiles(ref).fetchResult();
    }
    
    @Override
    public String update(String file, String domain, String db, String table, String pkey, String org, String dept)
    {
        if (file.contains("image"))
        {
            FileInfoV3 fileInfo =  api.update(file, domain, db, table, pkey, org, dept, UploadType.image).fetchResult();
            if (fileInfo != null)
                return fileInfo.getUrl();
            else
                return null;
        }
        else if (file.contains("download"))
        {
            FileInfoV3 fileInfo =  api.update(file, domain, db, table, pkey, org, dept, UploadType.file).fetchResult();
            if (fileInfo != null)
                return fileInfo.getDownloadUrl();
            else
                return null;
        }
        else
            return null;
    }
    
    @Override
    public List<String> updateList(List<Upd> list)
    {
        MapedList mapedList = new MapedList(list);
        
        List<UpdateParam> param = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++)
        {
            Upd upd = list.get(i);
            UpdateParam p = new UpdateParam();
            p.setDb(upd.getDb());
            p.setDept(upd.getDept());
            p.setDomain(upd.getDomain());
            p.setFile(upd.getFile());
            p.setOrg(upd.getOrg());
            p.setPkey(upd.getPkey());
            p.setTable(upd.getTable());
            
            if (upd.getFile().contains("image"))
            {
                p.setUpType(UploadType.image);
            }
            else if (upd.getFile().contains("download"))
            {
                p.setUpType(UploadType.file);
            }
            else
            {
                continue;
            }
            mapedList.map(param.size(), i);
            param.add(p);
        }
        
        List<FileInfoV3> fileInfos = api.updateList(param).fetchResult();
        for (int j = 0; j < fileInfos.size(); j++)
        {
            FileInfoV3 info = fileInfos.get(j);
            mapedList.set(j, info.getUrl());
        }
        return mapedList.getList();
    }
    
    @Override
    public void referencesAllbyTable(String domain, String db, String table, List<RefChange> refs)
    {
        RefChangeList ref = new RefChangeList();
        ref.setDomain(domain);
        ref.setDb(db);
        ref.setTable(table);
        ref.setRefs(refs);
        api.referencesAllbyTable(ref).fetchResult();
    }
    
}

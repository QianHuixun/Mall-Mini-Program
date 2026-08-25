package cn.tofocus.file.domain;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.common.util.Sequence;
import cn.tofocus.db.file.RefChange;
import cn.tofocus.file.bean.FileInfoV3;
import cn.tofocus.file.bean.UploadType;

@Component
public class FileServerV3 extends BaseFileServer
{
    private Sequence sequence = new Sequence();
    
    protected long newPkey()
    {
        long id = sequence.next();
        return id;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void referencesFiles(String domain, String db, String table, List<RefChange> refs)
    {
        super.referencesFiles(domain, db, table, refs);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void referencesAllbyTable(String domain, String db, String table, List<RefChange> refs)
    {
        super.referencesAllbyTable(domain, db, table, refs);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfoV3 updateFileRef(Long pkey, String domain, String db, String table, String dataPkey, String org,
        String dept, UploadType upType)
    {
        return super.updateFileRef(pkey, domain, db, table, dataPkey, org, dept, upType);
    }
}

package cn.tofocus.file.domain;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import cn.tofocus.db.redis.id.RedisCounter;
import cn.tofocus.common.util.ImageUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.common.util.Util;
import cn.tofocus.db.file.RefChange;
import cn.tofocus.file.bean.FileInfoV3;
import cn.tofocus.file.bean.MemoryMultipartFile;
import cn.tofocus.file.bean.UploadType;
import cn.tofocus.file.ueditor.ActionEnum;
import cn.tofocus.file.ueditor.StateEnum;
import cn.tofocus.file.ueditor.UeImgResult;
import cn.tofocus.file.ueditor.UeResult;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Component
public class FileServerV3 extends BaseFileServer
{
    @Autowired
    private RedisCounter redisCounter;
    
    protected long newPkey()
    {
        Long id = redisCounter.increment("fileServer", "fileRef");
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

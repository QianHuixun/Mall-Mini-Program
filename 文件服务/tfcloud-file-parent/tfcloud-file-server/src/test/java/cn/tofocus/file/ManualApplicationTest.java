package cn.tofocus.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import cn.tofocus.common.util.EntityUtil;
import cn.tofocus.common.util.NumUtil;
import cn.tofocus.file.bean.ClearStatus;
import cn.tofocus.file.bean.ThumbType;
import cn.tofocus.file.db.dao.FileRecordDao;
import cn.tofocus.file.db.entity.FileRef2Entity;
import cn.tofocus.file.db.entity.FileRefLinkEntity;
import cn.tofocus.file.db.key.FileRecordKey;
import cn.tofocus.file.db.repository.FileRecordRepository;
import cn.tofocus.file.db.repository.FileRef2Repository;
import cn.tofocus.file.domain.FileServerV3;
import cn.tofocus.file.domain.PathBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nl.basjes.parse.useragent.UserAgent.ImmutableUserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

@SpringBootTest
@Slf4j
public class ManualApplicationTest
{
    @Autowired
    private FileRecordRepository fileRecordRepository;
    
    @Autowired
    private FileRef2Repository fileRef2Repository;
    
    @Autowired
    private FileRecordDao fileRecordCache;
    
    private static final String root = "D:\\out\\uploads";
    
    @Test
    public void count1()
        throws IOException
    {
        log.info("开始清理孤儿物理文件");
        ClearStatus status = new ClearStatus();
        status.setName("删除没有数据库文件记录的文件");
        //扫描所有文件
        File rootfile = new File(root);
        File[] dirs0 = rootfile.listFiles();
        System.out.println();
        for (int i = 0; i < dirs0.length; i++)
        {
            double pstep1 = 1.0 / dirs0.length;
            double p1 = i * pstep1;
            File dir0 = dirs0[i];
            File[] dirs1 = dir0.listFiles();
            for (int j = 0; j < dirs1.length; j++)
            {
                double pstep2 = 1.0 / dirs1.length;
                double p2 = p1 + pstep1 * (j * pstep2);
                File dir1 = dirs1[j];
                File[] files = dir1.listFiles();
                for (int k = 0; k < files.length; k++)
                {
                    double pstep3 = 1.0 / files.length;
                    double p3 = p2 + pstep2 * (k * pstep3);
                    File file = files[k];
                    String[] sss = file.getName().split("_");
                    if (sss.length == 2)
                    {
                        long size = Long.parseLong(sss[1]);
                        FileRecordKey key = new FileRecordKey(sss[0], size);
                        //如果 file_record 里没有就删掉
                        if (!fileRecordCache.isExistKey(key))
                        {
                            Files.deleteIfExists(
                                new File(thumbPath(sss[0], size, FileServerV3.thumb2Size(ThumbType.small)).build())
                                    .toPath());
                            Files.deleteIfExists(
                                new File(thumbPath(sss[0], size, FileServerV3.thumb2Size(ThumbType.big)).build())
                                    .toPath());
                            Files.deleteIfExists(new File(filePath(sss[0], size).build()).toPath());
                            System.out.print("D");
                            status.setFileCount(status.getFileCount() + 1);
                        }
                    }
                    status.setPercent(p3);
                }
                System.out.print(".");
            }
        }
        log.info("孤儿物理文件清理完成");
    }
    
    public PathBuilder filePath(String md5, long filesize)
    {
        PathBuilder pb = new PathBuilder().root(root).md5(md5).size(filesize);
        return pb;
    }
    
    public PathBuilder thumbPath(String md5, long filesize, int thumb)
    {
        PathBuilder pb = new PathBuilder().root(root).md5(md5).size(filesize).thumb(thumb);
        return pb;
    }

    @Test
    public void query()
    {
        System.out.println(fileRecordCache.selectOne().exec());
    }
    @Test
    public void count()
    {
        List<Map<String, Object>> list = fileRecordRepository.allOrphanFileRecord();
        System.out.println(list.size());
        for (int i = 0; i < 10; i++)
        {
            Map<String, Object> map = list.get(i);
            System.out.print(map.get("md5"));
            System.out.print(",");
            System.out.println(map.get("size"));
        }
        
        List<Object> list2 = fileRef2Repository.allOrphanFileRef();
        System.out.println(list2);
    }
    
    /**
     * 重新加的实体产生建表Sql
     */
    @Test
    public void entity2Sql()
    {
        String sql = EntityUtil.entity2Sql(FileRef2Entity.class, FileRefLinkEntity.class);
        
        System.out.println(sql);
    }
    
    @Test
    public void uasParser3()
        throws IOException
    {
        UserAgentAnalyzer uaa = UserAgentAnalyzer.newBuilder().hideMatcherLoadStats().withCache(10000).build();
        long l = System.currentTimeMillis();
        print3(uaa,
            "Mozilla/5.0 (Linux; Android 9; COL-AL10 Build/HUAWEICOL-AL10; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/77.0.3865.120 MQQBrowser/6.2 TBS/045329 Mobile Safari/537.36 MMWEBID/6214 MicroMessenger/7.0.18.1740(0x27001271) Process/tools WeChat/arm64 NetType/WIFI Language/zh_CN ABI/arm64");
        print3(uaa, "Dalvik/2.1.0 (Linux; U; Android 6.0.1; MuMu Build/V417IR)");
        print3(uaa,
            "Mozilla/5.0 (Linux; U; Android 10; zh-Hans-CN; NOH-AN00 Build/HUAWEINOH-AN00) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/78.0.3904.108 Quark/5.4.8.200 Mobile Safari/537.36");
        print3(uaa, "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:95.0) Gecko/20100101 Firefox/95.0");
        print3(uaa, "XASZSH/1 CFNetwork/1327.0.4 Darwin/21.2.0");
        print3(uaa,
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36 Edg/96.0.1054.62");
        System.out.println(NumUtil.timeToCh(System.currentTimeMillis() - l, TimeUnit.MICROSECONDS));
    }
    
    @SneakyThrows
    private void print3(UserAgentAnalyzer uaa, String ua)
    {
        System.out.println(ua);
        
        ImmutableUserAgent agent = uaa.parse(ua);
        
        for (String fieldName : agent.getAvailableFieldNamesSorted())
        {
            System.out.println(fieldName + " = " + agent.getValue(fieldName));
        }
        System.out.println();
    }
    
}

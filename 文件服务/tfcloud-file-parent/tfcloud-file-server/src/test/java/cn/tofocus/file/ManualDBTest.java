package cn.tofocus.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;

import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import cn.tofocus.common.excel.ExcelUtil;
import cn.tofocus.common.util.EntityUtil;
import cn.tofocus.common.util.HttpUtil;
import cn.tofocus.common.util.ImageUtil;
import cn.tofocus.common.util.ImageUtil.ImageType;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.common.util.file.FileUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.file.bean.ClearStatus;
import cn.tofocus.file.bean.FileInfoV3;
import cn.tofocus.file.bean.ThumbType;
import cn.tofocus.file.db.key.FileRecordKey;
import cn.tofocus.file.domain.FileServerV3;
import cn.tofocus.file.domain.PathBuilder;
import lombok.extern.slf4j.Slf4j;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

/**
 * 
 * <一句话功能简述>
 * <功能详细描述>
 * 
 * @author  wyw
 * @version  [版本号, 2018年4月2日]
 */

@Slf4j
public class ManualDBTest
{
    private static final String root = "D:\\out\\uploads";
    
    @Test
    public void testIcc()
        throws FileNotFoundException, IOException, MagicParseException, MagicMatchNotFoundException, MagicException
    {
        resize("F://06029F3D436DAB8FEBEF02DD30C4499C_34445.jpeg");
        resize("F://0016A2738FCB71366327FA5940E4E293_5538.png");
        resize("F://无标题.png");
        resize("F://无标题.bmp");
        resize("F://无标题.gif");
        resize("F://无标题.jpg");
        resize("F://未标题-1.png");
        resize("F://icc.jpg");
        resize("F://食品经营许可证.jpg");
    }
    
    private void resize(String fileName)
        throws FileNotFoundException, IOException
    {
        ImageType type = null;
        File file = new File(fileName);
        
        try (InputStream fio = new FileInputStream(file);)
        {
            type = ImageUtil.checkImgType(fio);
        }
        System.out.println(type);
        
        byte[] fileContent = FileUtil.readFileContent(fileName);
        byte[] thumbContent = ImageUtil.reSize(null, fileContent, 200, 200);
        System.out.println(thumbContent.length);
        
        FileUtil.saveFile(fileName + ".thumb.jpg", thumbContent);
    }
    
    @Test
    public void test()
        throws MagicParseException, MagicMatchNotFoundException, MagicException
    {
        StringBuilder sb = new StringBuilder();
        
        MagicMatch match = Magic.getMagicMatch(new File("F://06029F3D436DAB8FEBEF02DD30C4499C_34445.jpeg"), true, true);
        sb.append(match.getMimeType()).append(System.lineSeparator());
        
        match = Magic.getMagicMatch(new File("F://0016A2738FCB71366327FA5940E4E293_5538.png"), true, true);
        sb.append(match.getMimeType()).append(System.lineSeparator());
        
        match = Magic.getMagicMatch(new File("F://无标题.png"), true, true);
        sb.append(match.getMimeType()).append(System.lineSeparator());
        
        match = Magic.getMagicMatch(new File("F://无标题.bmp"), true, true);
        sb.append(match.getMimeType()).append(System.lineSeparator());
        
        match = Magic.getMagicMatch(new File("F://无标题.gif"), true, true);
        sb.append(match.getMimeType()).append(System.lineSeparator());
        
        match = Magic.getMagicMatch(new File("F://无标题.jpg"), true, true);
        sb.append(match.getMimeType()).append(System.lineSeparator());
        
        match = Magic.getMagicMatch(new File("F://无标题.tif"), true, true);
        sb.append(match.getMimeType()).append(System.lineSeparator());
        
        match = Magic.getMagicMatch(new File("F://未标题-1.png"), true, true);
        sb.append(match.getMimeType()).append(System.lineSeparator());
        
        System.out.println(sb);
        
    }
    
    @Test
    public void count1()
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
                        System.out.println(JsonUtil.toString(key));
                        
                        System.out
                            .println(new File(thumbPath(sss[0], size, FileServerV3.thumb2Size(ThumbType.small)).build())
                                .toPath());
                        System.out.println(
                            new File(thumbPath(sss[0], size, FileServerV3.thumb2Size(ThumbType.big)).build()).toPath());
                        System.out.println(new File(filePath(sss[0], size).build()).toPath());
                        System.out.print("D");
                        status.setFileCount(status.getFileCount() + 1);
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
    public void postJson()
    {
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        
        Date d1 = HttpUtil
            .forResult("http://localhost:21009/test/trade/dateStr", new ParameterizedTypeReference<Result<DateStr>>()
            {
            })
            .form(param)
            .dateTimeFormat("yyyy-MM-dd HH:mm:ss")
            .token("8f48b816-a662-41d5-b71d-6d60a7f80099")
            .exec()
            .getDate();
        System.out.println(DateUtil.formatDate(d1));
        
        Date d2 = HttpUtil
            .forResult("http://localhost:21009/test/trade/dateLong", new ParameterizedTypeReference<Result<DateStr>>()
            {
            })
            .form(param)
            .token("8f48b816-a662-41d5-b71d-6d60a7f80099")
            .exec()
            .getDate();
        System.out.println(DateUtil.formatDate(d2));
        
        Date d3 = HttpUtil.forResult("http://localhost:21009/test/trade/dateFormat",
            new ParameterizedTypeReference<Result<DateFormat>>()
            {
            }).form(param).token("8f48b816-a662-41d5-b71d-6d60a7f80099").exec().getDate();
        System.out.println(DateUtil.formatDate(d3));
    }
}

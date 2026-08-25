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

import cn.tofocus.common.util.HttpUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.common.util.file.FileUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.file.bean.ClearStatus;
import cn.tofocus.file.bean.FileInfoV3;
import cn.tofocus.file.bean.ThumbType;
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
public class ManualTest
{
    
    @Test
    public void testIcc()
        throws FileNotFoundException, IOException, MagicParseException, MagicMatchNotFoundException, MagicException
    {
        imie("F:\\天彦\\prophet.rar");
        imie("F:\\天彦\\settings.xml");
        imie("F:\\天彦\\运维管理规范V1.0.docx");
        imie("F:\\旧目录\\Documents\\微信图片_20180330100052.jpg");
        imie("F:\\旧目录\\Documents\\微信图片_20200415164756.png");
        imie("F:\\旧目录\\Documents\\微信图片_20200415164756.psd");
        imie("F:\\旧目录\\Documents\\微信图片_20200814101132.zip");
        imie("F:\\旧目录\\Documents\\疫控登记.png");
    }
    
    private void imie(String filename)
        throws MagicParseException, MagicMatchNotFoundException, MagicException
    {
        
        MagicMatch match = Magic.getMagicMatch(new File(filename), true, true);
        String contentType = match.getMimeType();
        System.out.println(contentType);
    }
    
}

package cn.tofocus.lejia.zx.utilV2;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * @author wu
 *
 * */
public class Utils {
	/**
	 * 将文件转成base64 字符串
	 * @return
	 * @throws Exception
	 */
	public static String encodeBase64File(String path) throws Exception {
		File file = new File(path);
		FileInputStream inputFile = new FileInputStream(file);
		byte[] buffer = new byte[(int) file.length()];
		inputFile.read(buffer);
		inputFile.close();
		return new BASE64Encoder().encode(buffer);
	}

	/**
	 * 将base64字符解码保存文件
	 * @param base64Code
	 * @param targetPath
	 * @throws Exception
	 */
	public static void decoderBase64File(String base64Code, String targetPath) {
		try {
			if (StringUtils.isNotBlank(base64Code)) {
				byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
				FileOutputStream out = new FileOutputStream(targetPath);
				out.write(buffer);
				out.close();
				System.out.println("文件转换完成！  " + targetPath);
				return;
			}
			System.out.println("文件为空！ " + targetPath);
		} catch (Exception e) {
			System.err.println("文件转换错误！"+ e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * 压缩文件目录下的所有文件
	 *
	 * @param filePath
	 * @param zipName
	 */
	public static boolean dicFileZip(String filePath, String zipName) {

		int BUFFER = 2048;

		FileOutputStream dest = null;
		ZipOutputStream out = null;
		BufferedInputStream origin = null;
		try {

			File zipFile = new File(zipName);

			if (zipFile.exists()) {
				zipFile.delete();
			}

			dest = new FileOutputStream(zipName);

			out = new ZipOutputStream(new BufferedOutputStream(dest));

			// out.setMethod(ZipOutputStream.DEFLATED);

			byte data[] = new byte[BUFFER];

			// get a list of files from current directory

			File f = new File(filePath);

			String files[] = f.list();

			for (int i = 0; i < files.length; i++) {

				FileInputStream fi = new FileInputStream(filePath + File.separator + files[i]);

				origin = new BufferedInputStream(fi, BUFFER);

				ZipEntry entry = new ZipEntry(files[i]);

				out.putNextEntry(entry);

				int count;

				while ((count = origin.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("生成压缩包失败");
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (dest != null) {
					dest.close();
				}
				if (origin != null) {
					origin.close();
				}

			} catch (IOException e) {
			}

		}
		return false;
	}

	/**
	 * 压缩某一个文件
	 *
	 * @param filename
	 * @param zipName
	 */
    public static boolean fileZip(String filename, String zipName)
    {
        
        int BUFFER = 2048;
        
        FileOutputStream dest = null;
        ZipOutputStream out = null;
        BufferedInputStream origin = null;
        try
        {
            
            File zipFile = new File(zipName);
            
            if (zipFile.exists())
            {
                zipFile.delete();
            }
            
            dest = new FileOutputStream(zipName);
            
            out = new ZipOutputStream(new BufferedOutputStream(dest));
            
            // out.setMethod(ZipOutputStream.DEFLATED);
            
            byte data[] = new byte[BUFFER];
            
            // get a list of files from current directory
            
            File f = new File(filename);
            
            FileInputStream fi = new FileInputStream(filename);
            
            origin = new BufferedInputStream(fi, BUFFER);
            
            ZipEntry entry = new ZipEntry(f.getName());
            
            out.putNextEntry(entry);
            
            int count;
            
            while ((count = origin.read(data, 0, BUFFER)) != -1)
            {
                out.write(data, 0, count);
            }
            
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("生成压缩包失败");
        }
        finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                }
                if (dest != null)
                {
                    dest.close();
                }
                if (origin != null)
                {
                    origin.close();
                }
                
            }
            catch (IOException e)
            {
            }
            
        }
        return false;
    }

	/**
	 * 请求报文参与签名元素排序
	 *
	 * @param root
	 */
	public static String sortSignInfo(Element root)
	{
		List<Element> list = root.elements();
		List<String> signList = new ArrayList<String>();

		for (Element e : list)
		{
			if ("SIGN_INFO".equals(e.getName()) || "FILE_CONTENT".equals(e.getName()) || "LIST".equals(e.getName()) || "FILE_CONTENT_F".equals(e.getName()) || "FILE_CONTENT_B".equals(e.getName())) {
				continue;
			}
			if (e.getText() != null)
			{
				signList.add(e.getText());
			}
		}
		//排序
		Collections.sort(signList);

		StringBuffer signInfo = new StringBuffer();

		for (String sign  : signList)
		{
			signInfo = signInfo.append(sign);
		}
		//若遇到银行返回验证签名未通过，可与银行侧对比该串内容是否一致
		System.out.println("请求字段排序串："+signInfo);
		return signInfo.toString();
	}
}

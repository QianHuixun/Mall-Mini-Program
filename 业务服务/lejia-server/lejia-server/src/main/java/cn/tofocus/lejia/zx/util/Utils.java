package cn.tofocus.lejia.zx.util;


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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * @author wu
 * 
 * */
public class Utils {
	private static Log logger = new Log(LoggerFactory.getLogger(Utils.class));
	/**
	 * ʹ��dom4j��xml����ת����map����
	 * @param doc Document���� xml��תΪDocument����
	 * @param map<String, Object>
	 * */
	public static Map<String, Object> Dom2Map(Document doc){
		Map<String, Object> map = new HashMap<String, Object>();
		if (doc == null) {
			return map;
		}
		Element root = (Element) doc.getRootElement();
		for (Iterator<Element> iter = root.elementIterator(); iter.hasNext();) {
			Element e = iter.next();
			List<Element> list = e.elements();
			if (list.size() > 0) {
				map.put(e.getName(), Dom2Map(e));
			}else {
				map.put(e.getName(), e.getText());
			}
		}
		return map;
	}
	
	public static Map Dom2Map(Element e){  
        Map<String, Object> map = new HashMap<String, Object>();  
        List<Element> list = e.elements();  
        if(list.size() > 0){  
            for (int i = 0;i < list.size(); i++) {  
                Element iter = (Element) list.get(i);  
                List<Object> mapList = new ArrayList<Object>();  
                  
                if(iter.elements().size() > 0){  
                    Map m = Dom2Map(iter);  
                    if(map.get(iter.getName()) != null){  
                        Object obj = map.get(iter.getName());  
                        if(!obj.getClass().getName().equals("java.util.ArrayList")){  
                            mapList = new ArrayList();  
                            mapList.add(obj);  
                            mapList.add(m);  
                        }  
                        if(obj.getClass().getName().equals("java.util.ArrayList")){  
                            mapList = (List) obj;  
                            mapList.add(m);  
                        }  
                        map.put(iter.getName(), mapList);  
                    }else  
                        map.put(iter.getName(), m);  
                }  
                else{  
                    if(map.get(iter.getName()) != null){  
                        Object obj = map.get(iter.getName());  
                        if(!obj.getClass().getName().equals("java.util.ArrayList")){  
                            mapList = new ArrayList();  
                            mapList.add(obj);  
                            mapList.add(iter.getText());  
			  }  
                        if(obj.getClass().getName().equals("java.util.ArrayList")){  
                            mapList = (List) obj;  
                            mapList.add(iter.getText());  
                        }  
                        map.put(iter.getName(), mapList);  
                    }else  
                        map.put(iter.getName(), iter.getText());  
                }  
            }  
        }else  
            map.put(e.getName(), e.getText());  
        return map;  
    } 
	
	/**
	 * XMLת��ΪJavaBean
	 * @param xml xml��ʽ�ַ���
	 * @param clazz ��Ӧ����Ϣ��
	 * */
	public static Object getObjectFromXML(String xml, Class clazz){
	    XStream xStreamForResponseData = new XStream();
	    xStreamForResponseData.addPermission(AnyTypePermission.ANY);
//		XStream xStreamForResponseData = new XStream(new DomDriver("UTF-8"));
//		XStream.setupDefaultSecurity(xStreamForResponseData);
		xStreamForResponseData.alias("ROOT", clazz);
		xStreamForResponseData.ignoreUnknownElements();//���Ե�һЩ�����ֶ�
		return xStreamForResponseData.fromXML(xml);
	}
	
	/**  
	  * ���ļ�ת��base64 �ַ���  
	  * @param path�ļ�·��  
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
	  * ��base64�ַ����뱣���ļ�  
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
				 log("�ļ�ת����ɣ�  " + targetPath);
				 return;
			}
			 log("�ļ�Ϊ�գ� " + targetPath);
		} catch (Exception e) {
			logger.e("�ļ�ת������");
			e.printStackTrace();
		}
	   
	 }  
	 
	 public static boolean delFile(String filePath){
		 File file = new File(filePath);
		 try {
			if (file.exists() && file.isFile()) {
				if (file.delete()) {
					return true;
				}
				logger.e("ɾ���ļ� " + filePath + "ʧ�ܣ�");
				return false;
			}
		   logger.e("ɾ�������ļ�ʧ�ܣ�" + filePath + "�ļ������ڣ�");
		   return false;
		} catch (Exception e) {
			logger.e("ɾ�������ļ�����");
			e.printStackTrace();
		}
		 return false;
	 }
	 
	 /**
	  * ��ȡ�ļ�����
	  * @param filePath �ļ�·��
	  * */
	 public static String readFileContent(String filePath){
		StringBuffer strBuff = new StringBuffer();
		try {
			 File file = new File(filePath);
			//�ļ��Ƿ����
			 if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), "gbk");
				BufferedReader bufferRead = new BufferedReader(read);
				String strcon = null;
				while ((strcon = bufferRead.readLine()) != null) {
					strBuff.append(strcon);
				}
				return strBuff.toString();
			}else{
				logger.e("�Ҳ���ָ���ļ���");
			}
		} catch (Exception e) {
			logger.e("��ȡ�ļ����ݴ���");
			e.printStackTrace();
		}
		
		return null;
	 }
	 
	 //Md5����
	 public static String transMD5(String filePath) throws Exception {
			File file = new File(filePath);
			FileInputStream in = new FileInputStream(file);
			FileChannel ch = in.getChannel();
			MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0,
					file.length());
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(byteBuffer);
			byte[] resultByteArray = messageDigest.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < resultByteArray.length; offset++) {
				i = resultByteArray[offset];
				if (i < 0) {
					i += 256;
				}
				if (i < 16) {
					buf.append("0");
				}
				buf.append(Integer.toHexString(i));
			}
			in.close();
			return buf.toString();
	}
	 
	 /** ��ȡ�ļ���·��*/
	 public static String getRootPath(){
		 String path = "";
		 String realPath = Thread.currentThread()
				                 .getContextClassLoader()
				                 .getResource("/")
				                 .getPath();
		 int index = realPath.indexOf("classes/");
		 path = realPath.substring(0, index);
		 return path;
				                  
	 }
	 
	 /**����ļ���ָ��λ�� (΢��֧�����ض��˵�)**/
	public static boolean outputBill(String billStr, String fileName, String filePath){
		try {
				
				File file = new File(filePath + fileName);
				FileWriter fw = new FileWriter(file);
				BufferedWriter bw = new BufferedWriter(fw);
				//Ĭ��GBK����
				String writeStr = new String(billStr.getBytes("GBK"));
				//ȥ����һ�б�ͷ
				writeStr = writeStr.substring(writeStr.indexOf("\r\n") + 2)
						           .replace("`", "").replace("%", "");
				//ȥ��ĩβ��������
				writeStr = StringUtils.chomp(writeStr);
				writeStr = StringUtils.substringBeforeLast(writeStr,"\r\n");
			    writeStr = StringUtils.chomp(writeStr);
				writeStr = StringUtils.substringBeforeLast(writeStr,"\r\n");
				//д������
				bw.write(writeStr);
				bw.close();
				fw.close();
				//���سɹ�
				return true;
				
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			} 
	}
		
	public static String log(Object log)
	  {
	    logger.i(log.toString());

	    return log.toString();
	  }

	/**
	 * ѹ���ļ�
	 * 
	 * @param filePath
	 * @param zipName
	 * @throws CommException
	 */
	public static boolean fileZip(String filePath, String zipName) {

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
			System.out.println("����ѹ����ʧ��");
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
     * 请求报文参与签名元素排序
     *
     * @param root
     */
    public static String sortSignInfo(Element root)
    {
        List<Element> list = root.elements();
        List<java.lang.String> signList = new ArrayList<java.lang.String>();

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

        for (java.lang.String sign  : signList)
        {
            signInfo = signInfo.append(sign);
        }
        //若遇到银行返回验证签名未通过，可与银行侧对比该串内容是否一致
        System.out.println("请求字段排序串："+signInfo);
        return signInfo.toString();
    }
	
}

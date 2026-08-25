package cn.tofocus.lejia.zx.util;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;

public class HttpsPost {
	
	private static Log logger = new Log(LoggerFactory.getLogger(HttpsPost.class));
	/**
     * ���KeyStore.
     * @param keyStorePath
     *            ��Կ��·��
     * @param password
     *            ����
     * @return ��Կ��
	 * @throws KeyStoreException 
	 * @throws CertificateException 
	 * @throws NoSuchAlgorithmException 
     * @throws Exception
     */
    public static KeyStore getKeyStore(String password, String keyStorePath) throws Exception{
    	KeyStore ks = null;
    	// ʵ������Կ��
        ks = KeyStore.getInstance("JKS");
        // �����Կ���ļ���
        FileInputStream is = new FileInputStream(keyStorePath);
        // ������Կ��
        ks.load(is, password.toCharArray());
        // �ر���Կ���ļ���
        is.close();
        return ks;
    }

    /**
     * ���SSLSocketFactory.
     * @param password
     *            ����
     * @param keyStorePath
     *            ��Կ��·��
     * @param trustStorePath
     *            ���ο�·��
     * @return SSLSocketFactory
     * @throws Exception
     */
    public static SSLContext getSSLContext(String password,
            String keyStorePath, String trustStorePath) throws Exception {
        // ʵ������Կ��
        KeyManagerFactory keyManagerFactory = KeyManagerFactory
                .getInstance(KeyManagerFactory.getDefaultAlgorithm());
        // �����Կ��
        KeyStore keyStore = getKeyStore(password, keyStorePath);
        // ��ʼ����Կ����
        keyManagerFactory.init(keyStore, password.toCharArray());

        // ʵ�������ο�
        TrustManagerFactory trustManagerFactory = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        // ������ο�
        KeyStore trustStore = getKeyStore(password, trustStorePath);
        // ��ʼ�����ο�
        trustManagerFactory.init(trustStore);
        // ʵ����SSL������
        SSLContext ctx = SSLContext.getInstance("TLS");
        // ��ʼ��SSL������
        ctx.init(keyManagerFactory.getKeyManagers(),
                trustManagerFactory.getTrustManagers(), null);
        // ���SSLSocketFactory
        return ctx;
    }

    /**
     * ��ʼ��HttpsURLConnection.
     * @param password
     *            ����
     * @param keyStorePath
     *            ��Կ��·��
     * @param trustStorePath
     *            ���ο�·��
     * @throws Exception
     */
    public static void initHttpsURLConnection(String password,
            String keyStorePath, String trustStorePath) throws Exception {
        // ����SSL������
        SSLContext sslContext = null;
        // ʵ������������֤�ӿ�
        HostnameVerifier hnv = new MyHostnameVerifier();
        try {
            sslContext = getSSLContext(password, keyStorePath, trustStorePath);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        if (sslContext != null) {
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext
                    .getSocketFactory());
        }
        HttpsURLConnection.setDefaultHostnameVerifier(hnv);
    }

    private static void trustAllHttpsCertificates() throws Exception {   
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];   
        javax.net.ssl.TrustManager tm = new miTM();   
        trustAllCerts[0] = tm;   
        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext   
                .getInstance("SSL");   
        sc.init(null, trustAllCerts, null);   
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc   
                .getSocketFactory());   
    }   

    static class miTM implements javax.net.ssl.TrustManager,   
                javax.net.ssl.X509TrustManager {   
          public java.security.cert.X509Certificate[] getAcceptedIssuers() {   
              return null;   
          }

		public void checkClientTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
			// TODO Auto-generated method stub
			
		}

		public void checkServerTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
			// TODO Auto-generated method stub
			
		}   
    }
    
    /**
     * ��������.
     * @param httpsUrl
     *            ����ĵ�ַ
     * @param xmlStr
     *            ���������
     */
    public static String post(String httpsUrl, String xmlStr) {
    	if (StringUtils.isBlank(httpsUrl)) {
			logger.e("����urlΪ�� ��");
			return null;
		}
        
    	if (StringUtils.isBlank(xmlStr)) {
    		logger.e("��������Ϊ�� ��");
    		return null;
		}
        HttpsURLConnection urlCon = null;
        try {
        	trustAllHttpsCertificates();   
            urlCon = (HttpsURLConnection) (new URL(httpsUrl)).openConnection();
            Utils.log("executing request: " + urlCon.getURL());
            //HttpsURLConnection.setDefaultHostnameVerifier(hv); 
            urlCon.setDoInput(true);
            urlCon.setDoOutput(true);
            urlCon.setRequestMethod("POST");
            urlCon.setRequestProperty("Content-Length",
                    String.valueOf(xmlStr.getBytes().length));
            urlCon.setUseCaches(false);
            //����Ϊgbk���Խ������������ʱ��ȡ������������������
//            urlCon.getOutputStream().write(xmlStr.getBytes("GBK"));
            urlCon.getOutputStream().write(xmlStr.getBytes("UTF-8"));
            Utils.log("HTTPS,POST��ȥ��������: \n" + xmlStr);
            
            urlCon.getOutputStream().flush();
            urlCon.getOutputStream().close();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    urlCon.getInputStream()));
            StringBuffer strBuff = new StringBuffer();
            String line;
            while ((line = in.readLine()) != null) {
            	strBuff.append(line);
            }
            Utils.log("HTTPS,��������Ӧ�����: \n" + strBuff.toString());
            return strBuff.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    
    /**
     * 发送请求.
     * @param httpsUrl
     *            请求的地址
     * @param xmlStr
     *            请求的数据
     * @param merchantNo
     *            商户编号
     * @param tranCode
     *            交易码
     */
    public static String post(String httpsUrl, String xmlStr, String merchantNo, String tranCode) {
        if (StringUtils.isBlank(httpsUrl)) {
            System.out.println("请求url为空 ！");
            return null;
        }

        if (StringUtils.isBlank(xmlStr)) {
            System.out.println("发送内容为空 ！");
            return null;
        }
        HttpsURLConnection urlCon = null;
        try {
            trustAllHttpsCertificates();
            urlCon = (HttpsURLConnection) (new URL(httpsUrl)).openConnection();
            System.out.println("executing request: " + urlCon.getURL());
            //HttpsURLConnection.setDefaultHostnameVerifier(hv);
            urlCon.setDoInput(true);
            urlCon.setDoOutput(true);
            urlCon.setRequestMethod("POST");
            urlCon.setRequestProperty("merchantNo",merchantNo);
            urlCon.setRequestProperty("tranCode",tranCode);
            urlCon.setRequestProperty("serialNo","Ssn");
            urlCon.setRequestProperty("tranTmpt","2022-09-08-17.52.22.000000");
            urlCon.setRequestProperty("version","v1.0");
            urlCon.setRequestProperty("Content-Length",
                    String.valueOf(xmlStr.getBytes().length));
            urlCon.setUseCaches(false);
            //设置为gbk可以解决服务器接收时读取的数据中文乱码问题
//            urlCon.getOutputStream().write(xmlStr.getBytes("GBK"));
            urlCon.getOutputStream().write(xmlStr.getBytes("UTF-8"));
            System.out.println("HTTPS,POST过去的数据是: \n" + xmlStr);

            urlCon.getOutputStream().flush();
            urlCon.getOutputStream().close();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    urlCon.getInputStream()));
            StringBuffer strBuff = new StringBuffer();
            String line;
            while ((line = in.readLine()) != null) {
                strBuff.append(line);
            }
            System.out.println("HTTPS,服务器响应结果是: \n" + strBuff.toString());
            return strBuff.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
}


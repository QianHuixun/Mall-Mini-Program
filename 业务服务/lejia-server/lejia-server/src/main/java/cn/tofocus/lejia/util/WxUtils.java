package cn.tofocus.lejia.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;

import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.lejia.exception.WxErrCode;

public class WxUtils {
    public static final String ERR_CODE = "errcode";

    /**
     * 获取access_token的接口地址（GET） 限200（次/天）
     **/
    public static String access_token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

    /**
     * 小程序服务端登录凭证校验
     **/
    public static String auth_code2Session_url = "https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code";

    /**
     * 下发小程序和公众号统一的服务消息
     **/
    public static String uniformMessage_send_url = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/uniform_send?access_token=ACCESS_TOKEN";

    /**
     * 小程序发送订阅消息
     **/
    public static String subscribeMessage_send_url = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=ACCESS_TOKEN";

    /**
     * * 微信 数据解密<br/>
     * * 对称解密使用的算法为 AES-128-CBC，数据采用PKCS#7填充<br/>
     * * 对称解密的目标密文:encrypted=Base64_Decode(encryptData)<br/>
     * * 对称解密秘钥:key = Base64_Decode(session_key),aeskey是16字节<br/>
     * * 对称解密算法初始向量:iv = Base64_Decode(iv),同样是16字节<br/>
     * *
     * * @param encrypted 目标密文
     * * @param session_key 会话ID
     * * @param iv 加密算法的初始向量
     */
    public static String wxDecrypt(String encrypted, String session_key, String iv) {
        String result = null;
        byte[] encrypted64 = Base64.decodeBase64(encrypted);
        byte[] key64 = Base64.decodeBase64(session_key);
        byte[] iv64 = Base64.decodeBase64(iv);
        try {
            init();
            result = new String(decrypt(encrypted64, key64, generateIV(iv64)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * * 初始化密钥
     */

    public static void init() throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        KeyGenerator.getInstance("AES").init(128);
    }

    /**
     * * 生成iv
     */
    public static AlgorithmParameters generateIV(byte[] iv) throws Exception {
        // iv 为一个 16 字节的数组，这里采用和 iOS 端一样的构造方法，数据全为0
        // Arrays.fill(iv, (byte) 0x00);
        AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
        params.init(new IvParameterSpec(iv));
        return params;
    }

    /**
     * * 生成解密
     */
    public static byte[] decrypt(byte[] encryptedData, byte[] keyBytes, AlgorithmParameters iv)
            throws Exception {
        Key key = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        // 设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        System.err.println(encryptedData);
        return cipher.doFinal(encryptedData);
    }

    public static <T> T Post(String url , Object object , Class<T> responseType){
    	RestTemplate restTemplate = new RestTemplate();
    	restTemplate.getMessageConverters().set(1,new StringHttpMessageConverter(StandardCharsets.UTF_8));
    	ResponseEntity<T> rs = restTemplate.postForEntity(url, object, responseType);
        if (rs.getStatusCode().equals(HttpStatus.OK)) {
            T bodyResponse =  rs.getBody();
            return bodyResponse;
        } else {
            throw TofocusException.of(WxErrCode.HTTP_ERROR, String.valueOf(rs.getStatusCode()));
        }
    }

    
    /**
     * 发起https请求并获取结果 
     * @param requestUrl 请求地址
     * @param requestMethod 请求方式（GET、POST）
     * @param outputStr 提交的数据
     * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
     */
    public static JSONObject httpRequest(String requestUrl, String requestMethod, String outputStr) {
        JSONObject jsonObject = null;
        StringBuffer buffer = new StringBuffer();
        try {
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            TrustManager[] tm = { new MyX509TrustManager() };
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            System.out.println("--------------=-=-=-=-=-=-=-=-=-=-=-=-WeChat toResponse("+requestMethod+")-=-=-=-=-=-=-=-=-=-=-=-=--------------");
            System.out.println("--------------=-=-=-=-=-=-=-=-=-=-=-=-WeChat url:"+requestUrl);
            System.out.println("--------------=-=-=-=-=-=-=-=-=-=-=-=-WeChat data:"+outputStr);
            URL url = new URL(requestUrl);
            HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();
            httpUrlConn.setSSLSocketFactory(ssf);

            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            httpUrlConn.setRequestMethod(requestMethod);

            if ("GET".equalsIgnoreCase(requestMethod))
                httpUrlConn.connect();
            // 当有数据需要提交时
            if (null != outputStr) {
                OutputStream outputStream = httpUrlConn.getOutputStream();
                // 注意编码格式，防止中文乱码
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }
            // 将返回的输入流转换成字符串
            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            System.out.println(buffer);
            bufferedReader.close();
            inputStreamReader.close();
            // 释放资源
            inputStream.close();
            inputStream = null;
            httpUrlConn.disconnect();
            jsonObject = JSONObject.parseObject(buffer.toString());
        } catch (ConnectException ce) {
        } catch (Exception e) {
        }
        return jsonObject;
    }
}

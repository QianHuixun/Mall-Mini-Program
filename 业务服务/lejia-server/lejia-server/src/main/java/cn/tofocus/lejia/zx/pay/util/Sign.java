package cn.tofocus.lejia.zx.pay.util;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.lsy.baselib.crypto.protocol.PKCS7Signature;
import com.lsy.baselib.crypto.util.Base64;
import com.lsy.baselib.crypto.util.CryptUtil;
import com.lsy.baselib.crypto.util.FileUtil;

public class Sign
{
    private Sign()
    {
    }
    
    /**
     * 加签
     *
     * @param info        待加签数据
     * @param pwd         密码
     * @param filepathKey 私钥文件路径
     * @param filepathCer 公钥文件路径
     * @return 签名结果
     */
    public static String sign(String info, String pwd, String filepathKey, String filepathCer)
    {
        try
        {
            // 密码
            char[] keyPassword = pwd.toCharArray();
            // 获取私钥
            byte[] base64EncodedPrivateKey = FileUtil.read4file(filepathKey);
            PrivateKey signerPrivateKey =
                CryptUtil.decryptPrivateKey(Base64.decode(base64EncodedPrivateKey), keyPassword);
            // 获取公钥
            byte[] base64EncodedCert = FileUtil.read4file(filepathCer);
            X509Certificate signerCertificate = CryptUtil.generateX509Certificate(Base64.decode(base64EncodedCert));
            // 加签
            byte[] signature =
                PKCS7Signature.sign(info.getBytes("GBK"), signerPrivateKey, signerCertificate, null, false);
            return new String(Base64.encode(signature));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 验签
     *
     * @param info        待验签数据
     * @param sign        签名
     * @param filepathCer 公钥文件路径
     * @return true-验签成功 false-验签失败
     */
    public static Boolean verifySign(String info, String sign, String filepathCer)
    {
        try
        {
            // 获取公钥
            byte[] base64EncodedSenderCert = FileUtil.read4file(filepathCer);
            X509Certificate signerCertificate =
                CryptUtil.generateX509Certificate(Base64.decode(base64EncodedSenderCert));
            PublicKey senderPubKey = signerCertificate.getPublicKey();
            // 验签
            return PKCS7Signature
                .verifyDetachedSignature(info.getBytes("GBK"), Base64.decode(sign.getBytes()), senderPubKey);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 所有非空字段，按照key的ASCII码排序，将value值进行拼接
     *
     * @param map 待排序的数据
     * @return 排序后拼接结果
     */
    public static String sortByASCII(Map<String, String> map)
    {
        List<Map.Entry<String, String>> fields = new ArrayList<>(map.entrySet());
        
        // 按照key的ASCII码排序
        Collections.sort(fields, new Comparator<Map.Entry<String, String>>()
        {
            @Override
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2)
            {
                return (o1.getKey().compareTo(o2.getKey()));
            }
        });
        
        // 将排序后的value值进行拼接
        StringBuilder plain = new StringBuilder();
        for (Map.Entry<String, String> field : fields)
        {
            String value = field.getValue();
            plain.append(value);
        }
        
        return plain.toString();
    }
    public static String sortByASCIIObject(Map<String, Object> map)
    {
        List<Map.Entry<String, Object>> fields = new ArrayList<>(map.entrySet());
        
        // 按照key的ASCII码排序
        Collections.sort(fields, new Comparator<Map.Entry<String, Object>>()
        {
            @Override
            public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2)
            {
                return (o1.getKey().compareTo(o2.getKey()));
            }
        });
        
        // 将排序后的value值进行拼接
        StringBuilder plain = new StringBuilder();
        for (Map.Entry<String, Object> field : fields)
        {
            String value = field.getValue().toString();
            plain.append(value);
        }
        
        return plain.toString();
    }
    
}

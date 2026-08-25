package cn.tofocus.lejia.zx.utilV2;
// 中信银行(CNCB)外联平台(OLP)解密来自合作方(partner,PTNR)的请求或回应并验证签名类

import java.security.PublicKey;
import java.security.cert.X509Certificate;
import com.citicbank.baselib.crypto.protocol.PKCS7Signature;
import com.citicbank.baselib.crypto.util.Base64;
import com.citicbank.baselib.crypto.util.CryptUtil;
import com.citicbank.baselib.crypto.util.FileUtil;

public class OLPdecrypt {
	/**
	 * 验签
	 * @param msg 待验签数据
	 * @param sign signinfo
	 * @param sender_cer_file_nm 发送方cer证书文件名
	 */
	public static Boolean verifySign(byte[] msg,String sign, String sender_cer_file_nm)
	{
		byte[] base64EncodedSenderCert=null;
		try {
			base64EncodedSenderCert = FileUtil.read4file(sender_cer_file_nm);
		} catch (Exception e) {
			e.printStackTrace();
		}
		X509Certificate signerCertificate=null;
		try {
			signerCertificate = CryptUtil.generateX509Certificate(Base64.decode(base64EncodedSenderCert));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (signerCertificate == null)
		{
			return  false;
		}
		PublicKey senderPubKey = signerCertificate.getPublicKey();
		try {
			return PKCS7Signature.verifyDetachedSignature(msg, Base64.decode(sign.getBytes()), senderPubKey);
		} catch (Exception e) {
			System.out.println("验签异常:"+e.getMessage());
		}
		return false;
	}
}

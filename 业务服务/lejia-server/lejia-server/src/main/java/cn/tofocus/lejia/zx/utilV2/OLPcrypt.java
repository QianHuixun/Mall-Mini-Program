package cn.tofocus.lejia.zx.utilV2;

//中信银行(CNCB)外联平台(OLP)请求或回应报文加密/加签名类，以便将加密结果发送给合作方(partner,PTNR)
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import com.citicbank.baselib.crypto.protocol.PKCS7Signature;
import com.citicbank.baselib.crypto.util.Base64;
import com.citicbank.baselib.crypto.util.CryptUtil;
import com.citicbank.baselib.crypto.util.FileUtil;


public class OLPcrypt {
	/**
	 * 加签
	 * @param byte_msg 明文报文byte
	 * @param sender_pri_file_pwd 发送方私钥文件密码
	 * @param sender_pri_file_nm 发送方私钥文件名
	 * @param sender_cer_file_nm 发送方cer证书文件名
	 */
	public static String sign(byte[] byte_msg, String sender_pri_file_pwd,
							  String sender_pri_file_nm, String sender_cer_file_nm) {
		try {
			char[] keyPassword = new String(sender_pri_file_pwd).toCharArray();

			byte[] base64EncodedPrivatekey = FileUtil.read4file(sender_pri_file_nm);
			PrivateKey signerPrivatekey = CryptUtil.decryptPrivateKey(Base64.decode(base64EncodedPrivatekey), keyPassword);

			byte[] base64EncodedCert = FileUtil.read4file(sender_cer_file_nm);
			X509Certificate signerCertificate = CryptUtil.generateX509Certificate(Base64.decode(base64EncodedCert));

			byte[] signature = PKCS7Signature.sign(byte_msg, signerPrivatekey,signerCertificate, null, false);
			String b64StrSignature = new String(Base64.encode(signature));

			return b64StrSignature;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

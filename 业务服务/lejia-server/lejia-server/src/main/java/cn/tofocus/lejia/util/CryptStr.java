package cn.tofocus.lejia.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import cn.tofocus.common.util.Bytes;
import cn.tofocus.common.util.security.AESUtils;
import cn.tofocus.common.util.security.MD5;

public class CryptStr {
	public static String encryptStr(String data, String key) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		byte[] b = AESUtils.encrypt(Bytes.toBytes(data), MD5.getMD5byte(Bytes.toBytes(key)),
				MD5.getMD5byte(Bytes.toBytes(key)));
		return Bytes.byteArrayToHex(b);
	}

	public static String decryptStr(String data, String key) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		byte[] b = AESUtils.decrypt(Bytes.hexTobyteArray(data), MD5.getMD5byte(Bytes.toBytes(key)),
				MD5.getMD5byte(Bytes.toBytes(key)));
		return Bytes.toString(b);
	}
}

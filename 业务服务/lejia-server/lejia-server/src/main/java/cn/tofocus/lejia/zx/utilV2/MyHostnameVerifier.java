package cn.tofocus.lejia.zx.utilV2;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class MyHostnameVerifier implements HostnameVerifier {
	public boolean verify(String hostname, SSLSession session) {
		
			return true;
		
	}
}


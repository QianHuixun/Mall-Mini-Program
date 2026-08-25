package cn.tofocus.lejia.zx.util;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Description: 常量类
 * </p>
 */
public class Constants {
    /** 根目录*/
    public static final String ROOTPATH = System.getProperty("user.dir");
    /** 中信侧公钥路径*/
    public static final String PTNRTESTCER = ROOTPATH + "/config/PTNR/PTNRtest.cer";
    /** https私钥库*/
    public static final String KEYSTORE_PATH = ROOTPATH + "/config/PTNR/df_test.keystore";
    /** https信任库*/
    public static final String TRUSTSTORE_PATH = ROOTPATH + "/config/PTNR/df_test.keystore";
    /** 报文头*/
    public static final String XML_HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    /** 上传文件路径**/
    public static final String FILE_PATH = ROOTPATH + "/file/";
    //私钥库 密码
    public static final String PASSWORD = System.getenv().getOrDefault("ZX_KEYSTORE_PASSWORD", "CHANGE_ME");
}

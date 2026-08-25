package cn.tofocus.lejia.zx.utilV2;


public class Constants 
{
//    /** 根目录*/
//    public static final String ROOTPATH = System.getProperty("user.dir");
//    /** 中信侧公钥路径*/
//    public static final String PTNRTESTCER = ROOTPATH + "/config/PTNR/PTNRtest.cer";
//    /** https私钥库*/
//    public static final String KEYSTORE_PATH = ROOTPATH + "/config/PTNR/df_test.keystore";
//    /** https信任库*/
//    public static final String TRUSTSTORE_PATH = ROOTPATH + "/config/PTNR/df_test.keystore";
//    /** 报文头*/
//    public static final String XML_HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
//    /** 上传文件路径**/
//    public static final String FILE_PATH = ROOTPATH + "/file/";
//    //私钥库 密码
//    public static final String PASSWORD = "111111";
//
//    public static final String httpsUrl = "https://apitest.zyynm.com/api/public/";
//    
//    public static final String httpsFileUrl = "https://apitest.zyynm.com/dsgj/";
//    
//    public static final String MCHNT_ID = "J04059100000000";
//
//    public static final String USER_ROLE = "015001";
//    
//    public static final String QUDAO_NAME = "银联商务1033";
//    
//    public static final String FUNDS_TYPE = "015001";
//    
//    public static final String QUDAOBIANHAO = "1033";
//
//    public static final String RSP_CODE = "00000";
//    
//    public static final Integer ascription = 22;
    
    //************************************************** 以下是正式的配置 *************************
    
    /** 根目录*/
    public static final String ROOTPATH = System.getProperty("user.dir");
    /** 中信侧公钥路径*/
    public static final String PTNRTESTCER = ROOTPATH + "/PTNR/zx.cer";
    /** https私钥库*/
    public static final String KEYSTORE_PATH = ROOTPATH + "/PTNR/df_test.keystore";
    /** https信任库*/
    public static final String TRUSTSTORE_PATH = ROOTPATH + "/PTNR/df_test.keystore";
    /** 报文头*/
    public static final String XML_HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    /** 上传文件路径**/
    public static final String FILE_PATH = ROOTPATH + "/file/";
    //私钥库 密码
    public static final String PASSWORD = System.getenv().getOrDefault("ZX_KEYSTORE_PASSWORD", "CHANGE_ME");
    
    public static final String httpsUrl = "https://laas.citicbank.cn/api/public";
    
    public static final String httpsFileUrl = "https://cbpay.tpcg.citicbank.com:33148/dsgj";
    
    // 平台商户编号
    public static final String MCHNT_ID = "J01097900000000"; 
//    public static final String MCHNT_ID = "J01056700000000"; 
    // 用户角色
    public static final String USER_ROLE = "104002";
    // 渠道名称
    public static final String QUDAO_NAME = "银联商务1033";
    // 渠道编号
    public static final String FUNDS_TYPE = "104001";
    // 资金类型
    public static final String QUDAOBIANHAO = "1033";
    
    public static final String RSP_CODE = "00000";
    
    public static final Integer ascription = 13;
    
}

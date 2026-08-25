package cn.tofocus.lejia.zx.pay.constants;

public class Constants
{
    private Constants()
    {
    }
    
    public static final String PWD = System.getenv().getOrDefault("ZX_PAY_KEY_PASSWORD", "CHANGE_ME");
    
    public static final String PRI_KEY = "tools/421010060000001.key";
    
    public static final String PUB_CER = "tools/421010060000001.cer";
    
    public static final String ZX_PUB_CER = "tools/中信生产公钥.cer";
    
    /*
    // 锟教伙拷锟斤拷锟斤拷
    public static final String PWD = "CHANGE_ME";
    // 私钥证锟斤拷锟侥硷拷路锟斤拷
    public static final String PRI_KEY = "tools\\private.key";
    // 锟斤拷钥证锟斤拷锟侥硷拷路锟斤拷
    //  public static final String PUB_CER = "tools\\public.cer";
    public static final String PUB_CER = "tools\\023200010000001.cer";
    public static final String ZX_PUB_CER = "tools/中信测试公钥.cer"; 
      
    */
    
}

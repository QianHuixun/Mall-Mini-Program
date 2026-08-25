package cn.tofocus.lejia.syb.lib;

public class SybConstants
{
    //正式环境测试参数
    public static final String SYB_ORGID = "694037";//集团/机构模式下该参数不为空，且appid与key是与次参数对应
    
    public static final String SYB_CUSID = "61164686";
    
    public static final String SYB_APPID = "EZrCoLu58PVM";
    
    public static final String SYB_MD5_APPKEY = "a0ea3fa20dbd7bb4d5abf1d59d63bae8";
    
    public static final String SYB_APIURL =
        "https://dms-api.shopoint.cn/shopoint-openapis-web/szyx/apiweb/unitorder";
    
    public static final String SIGN_TYPE = "SM2";
    
    public static final String SYB_RSACUSPRIKEY =
        "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAO0HpPUP+eHk//Ba6ZOePvoZVDpOCRtt943oeVfCTllye43bqja1jVIaebX0MgX+yPYnWIQIOJ9ubSH0R4iyY9y1/HR00qkUpfW3/0usBPt9qn7r0xtFHerhVCd4dT2rKb2Oc5IhKOg05cw/BmMFohMkFsqt0jlrUXI8zJOlLIcxAgMBAAECgYA9lt/pAYa3iK5sQOMyhUrt54j4QXCiXPeXOxHUmNuM6G9sU+itoI0hCVoYymP5JNQJCf45CH3WB3Z5/SRdQ6Uoo1cjao6cCohPLxMSfJglsZCHckPH53o25RKEza4njIgKC+yN7HAhanKymhw/yYQ6i0aXq38zFIk8djMtE7R6xQJBAP6jvNy7UhPKO5rxGFKR+MvvbO3qnYH6x0jZCGY3FlxuGfbavueOiFtMeK67FuDv683dcUKi+M48yR4kH5CfIusCQQDuS9KF6mlm3kHAiZWgVhE8VVNYGpRLCRDgAKm4InGmvk5mUv+O1yAtAFVAEHWIgD4awC7Eqf1YFrSF/It9HV9TAkEAsXiU7JJxhfFw0XAvL30lFZ1tIfReinSp6A+7VuIV552k4vNaEjC4wEjv43fpXiRZCEXJ5lOHbNXYpfUvOrBuuQJAOpow8rf8Jc0g1G3Be0XPRUwii/c1YuKe4Meo9VybIIuKkkV1Dba/9fEwBepGTURkgYWjur+nSyOCT7UUxLcVewJAPLig8dVfKpsiNwYuveEYMcFaO5xoRuiB7v+CMmvxpuuK+rrFS+d7RdmwDbnBiDV4JkTgFObUiGvB7MtS+LGfhw==";
    
    public static final String SYB_RSATLPUBKEY =
        "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCm9OV6zH5DYH/ZnAVYHscEELdCNfNTHGuBv1nYYEY9FrOzE0/4kLl9f7Y9dkWHlc2ocDwbrFSm0Vqz0q2rJPxXUYBCQl5yW3jzuKSXif7q1yOwkFVtJXvuhf5WRy+1X5FOFoMvS7538No0RpnLzmNi3ktmiqmhpcY/1pmt20FHQQIDAQAB";
    
    //	/**商户sm2私钥,用于向通联发起请求前进行签名**/
    public static final String SYB_SM2PPRIVATEKEY =
        "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgqX4DFfS24N54ycSaeKXiYiaf2VLb48bv68jTqyHehaygCgYIKoEcz1UBgi2hRANCAARMRYq7ys/bwAF+eVqu8rFDfgdgaCu+73e4HuXCcHzt6g2xrpuC7QpuSeC1RVizb/3n4RZ2/Zne75E0ZdL9XiGB";
    
    //	/**通联平台sm2公钥，用于请求返回或者通联通知的验签**/
    public static final String SYB_SM2TLPUBKEY =
        "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAE81HDHWMqGVlwJ7ZYDo4O0pIc0AL6Zp83/cJKLQwL2U8hVXRVGn1il1xSlMceDLJej9sjGcwvSjLN0O2LsyPaQA==";
    
//    public static final String SYB_SM2TLPUBKEY =
//        "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAETEWKu8rP28ABfnlarvKxQ34HYGgrvu93uB7lwnB87eoNsa6bgu0KbkngtUVYs2/95+EWdv2Z3u+RNGXS/V4hgQ==";
    
//    public static final String SYB_ORGID = "100352";//集团/机构模式下该参数不为空，且appid与key是与次参数对应
//    
//    public static final String SYB_CUSID = "61056595";
//    
//    public static final String SYB_APPID = "28i8YAQF9p1J";
//    
//    public static final String SYB_MD5_APPKEY = "a0ea3fa20dbd7bb4d5abf1d59d63bae8";
//    
//    public static final String SYB_APIURL =
//        "https://dms-api-test.shopoint.cn/shopoint-openapis-web/szyx/apiweb/unitorder";//生产环境
//    
//    public static final String SIGN_TYPE = "SM2";
//    
//    public static final String SYB_RSACUSPRIKEY =
//        "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAO0HpPUP+eHk//Ba6ZOePvoZVDpOCRtt943oeVfCTllye43bqja1jVIaebX0MgX+yPYnWIQIOJ9ubSH0R4iyY9y1/HR00qkUpfW3/0usBPt9qn7r0xtFHerhVCd4dT2rKb2Oc5IhKOg05cw/BmMFohMkFsqt0jlrUXI8zJOlLIcxAgMBAAECgYA9lt/pAYa3iK5sQOMyhUrt54j4QXCiXPeXOxHUmNuM6G9sU+itoI0hCVoYymP5JNQJCf45CH3WB3Z5/SRdQ6Uoo1cjao6cCohPLxMSfJglsZCHckPH53o25RKEza4njIgKC+yN7HAhanKymhw/yYQ6i0aXq38zFIk8djMtE7R6xQJBAP6jvNy7UhPKO5rxGFKR+MvvbO3qnYH6x0jZCGY3FlxuGfbavueOiFtMeK67FuDv683dcUKi+M48yR4kH5CfIusCQQDuS9KF6mlm3kHAiZWgVhE8VVNYGpRLCRDgAKm4InGmvk5mUv+O1yAtAFVAEHWIgD4awC7Eqf1YFrSF/It9HV9TAkEAsXiU7JJxhfFw0XAvL30lFZ1tIfReinSp6A+7VuIV552k4vNaEjC4wEjv43fpXiRZCEXJ5lOHbNXYpfUvOrBuuQJAOpow8rf8Jc0g1G3Be0XPRUwii/c1YuKe4Meo9VybIIuKkkV1Dba/9fEwBepGTURkgYWjur+nSyOCT7UUxLcVewJAPLig8dVfKpsiNwYuveEYMcFaO5xoRuiB7v+CMmvxpuuK+rrFS+d7RdmwDbnBiDV4JkTgFObUiGvB7MtS+LGfhw==";
//    
//    public static final String SYB_RSATLPUBKEY =
//        "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCm9OV6zH5DYH/ZnAVYHscEELdCNfNTHGuBv1nYYEY9FrOzE0/4kLl9f7Y9dkWHlc2ocDwbrFSm0Vqz0q2rJPxXUYBCQl5yW3jzuKSXif7q1yOwkFVtJXvuhf5WRy+1X5FOFoMvS7538No0RpnLzmNi3ktmiqmhpcY/1pmt20FHQQIDAQAB";
//    
//    //	/**商户sm2私钥,用于向通联发起请求前进行签名**/
//    public static final String SYB_SM2PPRIVATEKEY =
//        "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgErY1NYrPofb3uBP2RJlbzeW/GvCozNE6/dLtHebSImOgCgYIKoEcz1UBgi2hRANCAATGzbQ9kouxdbANh+CDC9jyz562sFrm2OR9T6v8PotHCTBij5vg6EuO07SWYHgFVUVZmYhT8eMJ7JXfOmqo09wK";
//    
//    //	/**通联平台sm2公钥，用于请求返回或者通联通知的验签**/
//    public static final String SYB_SM2TLPUBKEY =
//        "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEyahVfna879jxfashrfAvYAw7mBZNK/tZO6mM9tYlCKMo/NwT7EqnTx+Ow2dAOvVvVxjQaONC7wXElmDDpRKhPg==";
    
    //测试环境调试参数
    //	public static final String SYB_ORGID = "";
    //	public static final String SYB_CUSID = "990581007426001";
    //	public static final String SYB_APPID = "00000051";
    //	public static final String SYB_MD5_APPKEY = "allinpay888";
    //	public static final String SYB_APIURL = "https://test.allinpaygd.com/apiweb/unitorder";//生产环境
    //	public static final String SIGN_TYPE = "MD5";
    /**商户RSA私钥,用于向通联发起请求前进行签名**/
    //	public static final String SYB_RSACUSPRIKEY = "CHANGE_ME";
    //	/**通联平台RSA公钥，用于请求返回或者通联通知的验签**/
    //	public static final String SYB_RSATLPUBKEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDYXfu4b7xgDSmEGQpQ8Sn3RzFgl5CE4gL4TbYrND4FtCYOrvbgLijkdFgIrVVWi2hUW4K0PwBsmlYhXcbR+JSmqv9zviVXZiym0lK3glJGVCN86r9EPvNTusZZPm40TOEKMVENSYaUjCxZ7JzeZDfQ4WCeQQr2xirqn6LdJjpZ5wIDAQAB";
    //
    //	/**商户sm2私钥,用于向通联发起请求前进行签名**/
    //	public static final String SYB_SM2PPRIVATEKEY = "CHANGE_ME";
    //	/**通联平台sm2公钥，用于请求返回或者通联通知的验签**/
    //	public static final String SYB_SM2TLPUBKEY = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAE/BnA8BawehBtH0ksPyayo4pmzL/u1FQ2sZcqwOp6bjVqQX4tjo930QAvHZPJ2eez8sCz/RYghcqv4LvMq+kloQ==";
    
}

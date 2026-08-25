package cn.tofocus.lejia.domain.app;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.thoughtworks.xstream.XStream;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.common.util.file.FileUtil;
import cn.tofocus.common.util.security.Base64;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.lejia.bean.dto.vendor.VendorOrderSettleOnList;
import cn.tofocus.lejia.bean.entity.vendor.MktSettlementLineDay;
import cn.tofocus.lejia.bean.entity.vendor.MktSettlementTotal;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorOrder;
import cn.tofocus.lejia.bean.entity.zx.ZxFileRecord;
import cn.tofocus.lejia.bean.entity.zx.ZxPostRecord;
import cn.tofocus.lejia.bean.enums.ZxFileStatus;
import cn.tofocus.lejia.bean.enums.ZxFileType;
import cn.tofocus.lejia.bean.enums.v2.VendorZxStatus;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.dao.zx.ZxFileRecordDao;
import cn.tofocus.lejia.dao.zx.ZxPostRecordDao;
import cn.tofocus.lejia.util.NumberUtils;
import cn.tofocus.lejia.util.ZipUtil;
import cn.tofocus.lejia.zx.bean.T21000001Request;
import cn.tofocus.lejia.zx.bean.T21000001Response;
import cn.tofocus.lejia.zx.bean.T21000003Request;
import cn.tofocus.lejia.zx.bean.T21000003Response;
import cn.tofocus.lejia.zx.bean.T21000007Request;
import cn.tofocus.lejia.zx.bean.T21000007Response;
import cn.tofocus.lejia.zx.bean.T21000008Request;
import cn.tofocus.lejia.zx.bean.T21000008Response;
import cn.tofocus.lejia.zx.bean.T21000009Request;
import cn.tofocus.lejia.zx.bean.T21000009Response;
import cn.tofocus.lejia.zx.bean.T21000012Request;
import cn.tofocus.lejia.zx.bean.T21000012Response;
import cn.tofocus.lejia.zx.bean.T21000014Request;
import cn.tofocus.lejia.zx.bean.T21000014Response;
import cn.tofocus.lejia.zx.bean.T21000024Request;
import cn.tofocus.lejia.zx.bean.T21000024Response;
import cn.tofocus.lejia.zx.bean.T21000029Request;
import cn.tofocus.lejia.zx.bean.T21000029Response;
import cn.tofocus.lejia.zx.bean.T21000035Request;
import cn.tofocus.lejia.zx.bean.T21000035Response;
import cn.tofocus.lejia.zx.bean.T21000036Request;
import cn.tofocus.lejia.zx.bean.T21000036Response;
import cn.tofocus.lejia.zx.bean.TRequest;
import cn.tofocus.lejia.zx.bean.TResponse;
import cn.tofocus.lejia.zx.util.HttpsPost;
import cn.tofocus.lejia.zx.util.SignUtil;
import cn.tofocus.lejia.zx.util.Utils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppZxEqManager
{
    /** 根目录*/
    private static String ROOTPATH = "";
    static
    {
        ROOTPATH = System.getProperty("user.dir");
        //        ROOTPATH = "/data/tofocus/server/zyysc";
    }
    
        private static final String PTNRTESTCER = ROOTPATH + "/PTNR/PTNRtest.cer";
        
        /** https私钥库*/
        private static final String KEYSTORE_PATH = ROOTPATH + "/PTNR/df_test.keystore";
        
        /** https信任库*/
        private static final String TRUSTSTORE_PATH = ROOTPATH + "/PTNR/df_test.keystore";
    
//    private static final String PTNRTESTCER = ROOTPATH + "/PTNR2/PTNRtest.cer";
//    
//    /** https私钥库*/
//    private static final String KEYSTORE_PATH = ROOTPATH + "/PTNR2/dsgj_new.keystore";
//    
//    /** https信任库*/
//    private static final String TRUSTSTORE_PATH = ROOTPATH + "/PTNR2/dsgj_new.keystore";
    
    /** 私钥库 密码 */
    String password = "111111";
    
    /** 报文头*/
    private static final String XML_HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    
    /** 上传文件路径**/
    private static final String FILE_PATH = ROOTPATH + "/file/";
//    private static final String FILE_PATH = "D:/gitclone/lejia-server/lejia-server/file/";
    
    //    private String MCHNT_ID = "J00024000000000";
    //    private String QUDAO_NAME = "1001全付通";
    
    private String MCHNT_ID = "J04059100000000";
//    private String MCHNT_ID = "J00030700000000";
    
    private String QUDAO_NAME = "中信全付通";
    
    private String USER_ROLE = "004001";
    
    private String FUNDS_TYPE = "004001";
    // %1$-55s
    
    @Autowired
    private MktVendorDao vendorDao;
    
    @Autowired
    private ZxFileRecordDao zxFileRecordDao;
    
    @Autowired
    private ZxPostRecordDao zxPostRecordDao;
    
    @Autowired
    private NumberUtils numberUtils;
    
    @Autowired
    private AppZxFileManager2 zxFileManager;
    
    Random r = new Random();
    
    String httpsUrl = "https://apitest.zyynm.com/api/public/";
//    String httpsUrl = "https://219.142.124.205:30466/dsgj/";
    //    String httpsUrl = "https://202.108.57.27:30466/dsgj/";
    //    String httpsUrl = "https://custtest.xinanshizu.com/dsgj/";
    
    // 用户注册
    public String zxRegister(MktVendor vendor)
    {
        XStream xtreamData = new XStream();
        //--------------- 新增商户信息测试数据 ---------------
        
        T21000001Request request = new T21000001Request();
        request.setTRANS_CODE("21000001");
        request.setREQ_SSN(getReqSsn());
        request.setMCHNT_ID(MCHNT_ID);//平台商户编号              
        request.setMCHNT_USER_ID(vendor.getPkey() + "");//平台商户自己记录的用户编号
        request.setUSER_TYPE("1");
        request.setUSER_NM(vendor.getBankuser()); // 用户姓名
        request.setUSER_ROLE(USER_ROLE); // 角色编号
        request.setSIGN_TYPE("00");//签约类型  00:自主注册 01:迁移注册
        
        request.setUSER_PHONE(vendor.getMobile());
//        request.setUSER_ID_TYPE("01");
//        request.setUSER_ID_NO(vendor.getZxIdentity());
        
        T21000001Response response = postZx(xtreamData, request, T21000001Request.class, T21000001Response.class);
        String res = null;
        if (response == null) return res;
        // 返回的参数 存到vendor
        if (response.isSucc())
        {
            vendor.setZxUserId(response.getUSER_ID());
            res = response.getUSER_ID();
        }
        else
        {
            vendor.setZxStatus(VendorZxStatus.AUDIT_FAILURE);
            vendor.setZxRemark(response.getRSP_MSG());
        }
        vendor.setZxRegisterTime(new Date());
        vendorDao.update(vendor);
        return res;
    }
    
    // 用户注册 身份证 不绑定
    public String zxRegister2(MktVendor vendor)
    {
        XStream xtreamData = new XStream();
        //--------------- 新增商户信息测试数据 ---------------
        
        T21000001Request request = new T21000001Request();
        request.setTRANS_CODE("21000001");
        request.setREQ_SSN(getReqSsn());
        request.setMCHNT_ID(MCHNT_ID);//平台商户编号              
        request.setMCHNT_USER_ID(vendor.getPkey() + "");//平台商户自己记录的用户编号
        request.setUSER_TYPE("1");
        request.setUSER_NM(vendor.getName()); // 用户姓名
        request.setUSER_ROLE(USER_ROLE); // 角色编号
        request.setSIGN_TYPE("00");//签约类型  00:自主注册 01:迁移注册
        
        request.setUSER_PHONE(vendor.getMobile());
        request.setUSER_ID_TYPE("");
        request.setUSER_ID_NO("");
        
        T21000001Response response = postZx(xtreamData, request, T21000001Request.class, T21000001Response.class);
        System.out.println("response: " + JsonUtil.toString(response, true));
        String res = null;
        if (response == null) return res;
        // 返回的参数 存到vendor
        if (response.isSucc())
        {
            vendor.setZxUserId(response.getUSER_ID());
            res = response.getUSER_ID();
        }
        else
        {
            vendor.setZxRemark(response.getRSP_MSG());
        }
        vendor.setZxRegisterTime(new Date());
        vendorDao.update(vendor);
        return res;
    }
    
    // 用户绑卡/解绑  true:绑卡  false:解绑
    public void tiedCard(MktVendor vendor, boolean flag)
    {
        XStream xtreamData = new XStream();
        //--------------- 用户绑卡/解绑 ---------------
        T21000024Request request = new T21000024Request();
        request.setTRANS_CODE("21000024");
        request.setREQ_SSN(getReqSsn());
        request.setMCHNT_ID(MCHNT_ID);//平台商户编号              
        request.setUSER_ID_TYPE("01"); // 01-个人身份证
        request.setUSER_ID(vendor.getZxUserId());
        // 1-绑卡  2-解绑
        String opType = "1";
        if (!flag) opType = "2";
        request.setOP_TYPE(opType);
        request.setACCT_NM(vendor.getBankuser());
        // 开户银行联行号不能为空
        request.setPAN_NUM("1");
        request.setPAN(vendor.getBankcard());
        request.setBANK_CARD_NO(vendor.getZxIdentity());
        request.setBANK_PHONE(vendor.getBankuserMoblie());
        request.setAUTH_PROTOCOL_VERSION("20220402");
        request.setAUTH_PROTOCOL_NO(DateUtil.formatDate(new Date(), "yyyyMMddHHmmss") + "0402");
        T21000024Response response = postZx(xtreamData, request, T21000024Request.class, T21000024Response.class);
        if (response.isSucc())
        {
            if (flag)
            {
                vendor.setZxStatus(VendorZxStatus.AUDIT_SUCCESS);
                vendor.setIsClear(true);
            }
            else
            {
                vendor.setZxStatus(VendorZxStatus.AUDIT_FAILURE);
                vendor.setIsClear(false);
            }
        }
        else
        {
            if (flag)
            {
                vendor.setZxStatus(VendorZxStatus.AUDIT_FAILURE);
                vendor.setIsClear(false);
            }
            vendor.setZxRemark(response.getRSP_MSG());
        }
        vendorDao.update(vendor);
    }
    
    // 用户信息变更 变更 姓名 证件号码  手机号码
    public void informationChange(MktVendor vendor)
    {
        XStream xtreamData = new XStream();
        //--------------- 用户信息变更 ---------------
        T21000003Request request = new T21000003Request();
        request.setTRANS_CODE("21000003");
        Random r = new Random();
        String reqSsn = MCHNT_ID + DateUtil.formatDate(new Date(), "yyyyMMddHHmmssSSS")
            + String.valueOf(r.nextLong()).substring(1, 8 + 1);
        request.setREQ_SSN(reqSsn);
        request.setMCHNT_ID(MCHNT_ID);//平台商户编号             
        request.setUSER_ID(vendor.getZxUserId());// 用户编号
        request.setUSER_NM(vendor.getBankuser());//用户变更姓名
        request.setUSER_CARD_TP("01");//用户证件类型
        request.setUSER_CARD_NO(vendor.getZxIdentity());//用户证件号码
        request.setUSER_PHONE(vendor.getBankuserMoblie());//用户手机号
        request.setUSER_ROLE(USER_ROLE);//用户角色
        postZx(xtreamData, request, T21000003Request.class, T21000003Response.class);
    }
    
    // 用户信息变更 变更 姓名 证件号码  手机号码
    public void informationChange2(MktVendor vendor)
    {
        XStream xtreamData = new XStream();
        //--------------- 用户信息变更 ---------------
        T21000003Request request = new T21000003Request();
        request.setTRANS_CODE("21000003");
        request.setREQ_SSN(getReqSsn());
        request.setMCHNT_ID(MCHNT_ID);//平台商户编号             
        request.setUSER_ID(vendor.getZxUserId());// 用户编号
        request.setUSER_NM(vendor.getBankuser());//用户变更姓名
        request.setUSER_CARD_TP("");//用户证件类型
        request.setUSER_CARD_NO("");//用户证件号码
        request.setUSER_PHONE(vendor.getMobile());//用户手机号
        request.setUSER_ROLE(USER_ROLE);//用户角色
        postZx(xtreamData, request, T21000003Request.class, T21000003Response.class);
    }
    
    // 用户余额查询
    public String getBalance(String userId)
    {
        XStream xtreamData = new XStream();
        //--------------- 用户余额查询 ---------------
        T21000009Request request = new T21000009Request();
        request.setTRANS_CODE("21000009");
        request.setREQ_SSN(getReqSsn());
        request.setMCHNT_ID(MCHNT_ID);
        request.setUSER_ID(userId);
        T21000009Response response = postZx(xtreamData, request, T21000009Request.class, T21000009Response.class);
        String amount = "0";
        if (response.isSucc())
        {
            amount = response.getAMOUNT();
        }
        return amount;
    }
    
    // 文件处理状态查询
    public String getFileStatus(String fileName)
    {
        XStream xtreamData = new XStream();
        //--------------- 文件处理状态查询 ---------------
        T21000012Request request = new T21000012Request();
        request.setTRANS_CODE("21000012");
        request.setREQ_SSN(getReqSsn());
        request.setMCHNT_ID(MCHNT_ID);
        request.setFILE_NAME(fileName);
        T21000012Response response = postZx(xtreamData, request, T21000012Request.class, T21000012Response.class);
        log.info("指定文件查询-文件处理状态查询: " + JsonUtil.toString(response, true));
        String res = null;
        if (response.isSucc())
        {
            String file_ST = response.getFILE_ST();
            switch (file_ST)
            {
                case "R":
                    res = "走快捷下载清分文件";
                    break;
                case "I":
                    res = "不走快捷下载清分文件";
                    break;
                case "0":
                    res = "清分文件初始状态";
                    break;
                case "P":
                    res = "文件校验中";
                    break;
                case "7":
                    res = "文件校验通过";
                    break;
                case "W":
                    res = "文件校验通过";
                    break;
                case "4":
                    res = "另行核对";
                    break;
                case "8":
                    res = "文件校验失败";
                    break;
                case "1":
                    res = "来帐校验通过";
                    break;
                case "2":
                    res = "异常长短款,来帐金额异常，清分文件挂起";
                    break;
                case "3":
                    res = "正常长短款,来帐校验通过，正在清分，不可修改";
                    break;
                case "U":
                    res = "明细核对失败";
                    break;
                case "B":
                    res = "资金未到";
                    break;
                case "G":
                    res = "挂帐,同一商户同一渠道小于当前交易日期的订单文件存在未来帐";
                    break;
                case "L":
                    res = "资金未到己勾消";
                    break;
                case "9":
                    res = "清分成功";
                    break;
                default:
                    res = response.getRSP_MSG();
                    break;
            }
        }
        return res;
    }
    
    // 文件处理状态查询
    public String getFileStatus2(String fileName, ZxFileRecord file)
    {
        XStream xtreamData = new XStream();
        //--------------- 文件处理状态查询 ---------------
        T21000012Request request = new T21000012Request();
        request.setTRANS_CODE("21000012");
        request.setREQ_SSN(getReqSsn());
        request.setMCHNT_ID(MCHNT_ID);
        request.setFILE_NAME(fileName);
        T21000012Response response = postZx(xtreamData, request, T21000012Request.class, T21000012Response.class);
        String res = null;
        String file_ST = "";
        if (response.isSucc())
        {
            file_ST = response.getFILE_ST();
            switch (file_ST)
            {
                case "R":
                    res = "走快捷下载清分文件";
                    break;
                case "I":
                    res = "不走快捷下载清分文件";
                    break;
                case "0":
                    res = "清分文件初始状态";
                    break;
                case "P":
                    res = "文件校验中";
                    break;
                case "7":
                    res = "文件校验通过";
                    break;
                case "W":
                    res = "文件校验通过";
                    break;
                case "4":
                    res = "另行核对";
                    file.setStatus(ZxFileStatus.SEPARATE);
                    break;
                case "8":
                    res = "文件校验失败";
                    file.setStatus(ZxFileStatus.FAIL);
                    break;
                case "1":
                    res = "来帐校验通过";
                    break;
                case "2":
                    res = "异常长短款,来帐金额异常，清分文件挂起";
                    file.setStatus(ZxFileStatus.ABNORMAL);
                    break;
                case "3":
                    res = "正常长短款,来帐校验通过，正在清分，不可修改";
                    break;
                case "U":
                    file.setStatus(ZxFileStatus.SEPARATE);
                    res = "明细核对失败";
                    break;
                case "B":
                    res = "资金未到";
                    file.setStatus(ZxFileStatus.ABNORMAL);
                    break;
                case "G":
                    res = "挂帐,同一商户同一渠道小于当前交易日期的订单文件存在未来帐";
                    file.setStatus(ZxFileStatus.ABNORMAL);
                    break;
                case "L":
                    file.setStatus(ZxFileStatus.ABNORMAL);
                    res = "资金未到己勾消";
                    break;
                case "9":
                    file.setStatus(ZxFileStatus.FINISH);
                    res = "清分成功";
                    break;
                default:
                    res = response.getRSP_MSG();
                    break;
            }
        }
        else
            res = "返回file_ST的值: " + file_ST + "  文件处理状态查询-异常";
        file.setAbnormalContent(res);
        return res;
    }
    
    // 智能提现
    public void withdraw(MktVendor vendor, BigDecimal amt)
    {
        XStream xtreamData = new XStream();
        //--------------- 智能提现 ---------------
        T21000014Request request = new T21000014Request();
        request.setTRANS_CODE("21000014");
        request.setREQ_SSN(getReqSsn());
        request.setMCHNT_ID(MCHNT_ID);
        request.setUSER_ID(vendor.getZxUserId());
        // 00
        request.setWITH_TYPE("00");
        // 提现流水号 平台商户端
        String payNumber = numberUtils.createOrderNumber();
        request.setBUSS_ID(payNumber);
        request.setTRANS_DT(DateUtil.formatDate(new Date(), "yyyyMMdd"));
        request.setTRANS_TM(DateUtil.formatDate(new Date(), "HHmmss"));
        request.setFEE_TYPE("1");
        request.setWITH_AMT(amt);
        request.setWITH_ACCOUNT(vendor.getBankcard());
        request.setWITH_ACCNAME(vendor.getBankuser());
        postZx(xtreamData, request, T21000014Request.class, T21000014Response.class);
    }
    
    // 智能提现
    public Boolean runWithdraw(String userId, String bankcard, String bankuser, BigDecimal amt)
    {
        XStream xtreamData = new XStream();
        //--------------- 智能提现 ---------------
        T21000014Request request = new T21000014Request();
        request.setTRANS_CODE("21000014");
        request.setREQ_SSN(getReqSsn());
        request.setMCHNT_ID(MCHNT_ID);
        request.setUSER_ID(userId);
        // 00
        request.setWITH_TYPE("00");
        // 提现流水号 平台商户端
        String payNumber = numberUtils.createOrderNumber();
        request.setBUSS_ID(payNumber);
        request.setTRANS_DT(DateUtil.formatDate(new Date(), "yyyyMMdd"));
        request.setTRANS_TM(DateUtil.formatDate(new Date(), "HHmmss"));
        request.setFEE_TYPE("1");
        request.setWITH_AMT(amt);
        request.setWITH_ACCOUNT(bankcard);
        request.setWITH_ACCNAME(bankuser);
        T21000014Response postZx = postZx(xtreamData, request, T21000014Request.class, T21000014Response.class);
        return postZx.isSucc();
    }
    
    // 登记簿交易明细查询
    public T21000029Response getRegisterDetail(String userId, String date, String transType)
    {
        try
        {
            XStream xtreamData = new XStream();
            //--------------- 登记簿交易明细查询 ---------------
            T21000029Request request = new T21000029Request();
            request.setTRANS_CODE("21000029");
            request.setREQ_SSN(getReqSsn());
            request.setMCHNT_ID(MCHNT_ID);
            request.setUSER_ID(userId);
            request.setTRANS_DATE(date);
            // 页码
            request.setPAGE("1");
            /*
            01- 入金分账
            02- 交易划转
            03- 提现
            04- 提现手续费
            05- 提现退汇
            06- 渠道来账
            99- 所有
             */
            if(StringUtils.isBlank(transType))
                transType = "99";
            request.setTRANS_TYPE(transType);
            //实体对象转换为XML
            xtreamData.alias("ROOT", T21000029Request.class);
            String restr = xtreamData.toXML(request).replace("__", "_");
            restr = XML_HEAD + restr;
            
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new ByteArrayInputStream(restr.getBytes()));
            Element root = doc.getRootElement();
            
            //--------加签名------
            String sign = SignUtil.sign(request.sortSignInfo(root).getBytes());
            request.setSIGN_INFO(sign);
            restr = XML_HEAD + xtreamData.toXML(request).replace("__", "_");
            //---------- 发送请求数据 ------//
            //初始化https请求参数
            HttpsPost.initHttpsURLConnection(password, KEYSTORE_PATH, TRUSTSTORE_PATH);
            //发送请求获得响应数据
            ZxPostRecord bean = zxPostRecordDao.addPostRecord("T21000029", restr);
            String resStr = HttpsPost.post(httpsUrl, restr);
            zxPostRecordDao.updPostRecord(bean, resStr);
            resStr = resStr.replace("ROOT", T21000029Response.class.getName());
            Document doc2 = DocumentHelper.parseText(resStr);
            JSONObject json = new JSONObject();
            dom4j2Json(doc2.getRootElement(), json);
            T21000029Response resData = JsonUtil.getBean(json.toString(), new TypeReference<T21000029Response>()
            {
            });
            if ("00000".equals(resData.getRSP_CODE()))
                resData.setSucc(true);
            else
                resData.setSucc(false);
            //获取签名信息
            return resData;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    // 公共登记簿交易明细查询
    public T21000035Response getRegisterBalance(String registerAttr)
    {
        XStream xtreamData = new XStream();
        //--------------- 智能提现 ---------------
        T21000035Request request = new T21000035Request();
        request.setTRANS_CODE("21000035");
        request.setREQ_SSN(getReqSsn());
        request.setMCHNT_ID(MCHNT_ID);
        /*
        00-公共计息收费登记薄
        12-自有资金登记薄
        13-担保登记薄
        17-待结算手续费登记簿
         */
        request.setREGISTER_ATTR(registerAttr);
        T21000035Response response = postZx(xtreamData, request, T21000035Request.class, T21000035Response.class);
        log.info("登记簿交易明细查询: {}", JsonUtil.toString(response, true));
        return response;
    }
    
    // 交易资金账户余额查询
    public T21000036Response getTradeBalance()
    {
        XStream xtreamData = new XStream();
        //--------------- 智能提现 ---------------
        T21000036Request request = new T21000036Request();
        request.setTRANS_CODE("21000036");
        request.setREQ_SSN(getReqSsn());
        request.setMCHNT_ID(MCHNT_ID);
        T21000036Response response = postZx(xtreamData, request, T21000036Request.class, T21000036Response.class);
        log.info("交易资金账户余额查询: {}", JsonUtil.toString(response, true));
        return response;
    }
    
    /**
     * 文件下载
     * @param fileName
     * @param fileType  101: 清分文件  102:用户提现文件  104: 用户退汇明细文件  999-人行联行号 113-内部户明细
     * 111-登记簿资金变动  109-自有资金明细文件   114-客户账对账明细
     * @param settleDt  清算日期
     */
    public String downloadFile(String fileName, ZxFileType fileType, String settleDt)
    {
        // String fileType, 
        XStream xtreamData = new XStream();
        //--------------- 新增商户信息测试数据 ---------------
        T21000007Request request = new T21000007Request();
        request.setTRANS_CODE("21000007");
        request.setREQ_SSN(getReqSsn());
        request.setMCHNT_ID(MCHNT_ID);
        request.setFILE_NAME(fileName);
        request.setFILE_TYPE(fileType.getIndex() + "");
        request.setSETTLE_DT(settleDt);
        request.setTRANS_TYPE("MSG");
        T21000007Response response = postZx(xtreamData, request, T21000007Request.class, T21000007Response.class);
        String content = null;
        if (response.isSucc())
        {
            content = response.getFILE_CONTENT();
        }
        return content;
    }
    
    // 文件上传 
    public Boolean runFileUpload(Map<String, List<VendorOrderSettleOnList>> data, String fileCount, Date endTime, String settleKey)
    {
        ZxFileRecord record = zxFileManager.runFile(data, endTime, settleKey);
        String fileSaveLj = record.getSavePath();
        String fileName = record.getName() + ".ZIP";
        try
        {
            XStream xtreamData = new XStream();
            //--------------- 文件上传 ---------------
            T21000008Request request = new T21000008Request();
            request.setTRANS_CODE("21000008");
            request.setREQ_SSN(getReqSsn());
            request.setMCHNT_ID(MCHNT_ID);
            request.setFILE_TYPE("001");
            request.setFILE_COUNT(fileCount);
            request.setFILE_NAME(fileName);
            request.setTRANS_TYPE("MSG");
            byte[] bs = FileUtil.readFileContent(fileSaveLj);
            String fileContent = Base64.encodeBytes(bs, Base64.DONT_BREAK_LINES);
            System.out.println("fileContent-bs: " + fileContent);
            
            //实体对象转换为XML
            xtreamData.alias("ROOT", T21000008Request.class);
            String restr = xtreamData.toXML(request).replace("__", "_");
            restr = XML_HEAD + restr;
            
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new ByteArrayInputStream(restr.getBytes()));
            Element root = doc.getRootElement();
            
            //--------加签名------
            String sign = SignUtil.sign(request.sortSignInfo(root).getBytes());
            request.setFILE_CONTENT(fileContent);
            request.setSIGN_INFO(sign);
            restr = XML_HEAD + xtreamData.toXML(request).replace("__", "_");
            //---------- 发送请求数据 ------//
            //初始化https请求参数
            HttpsPost.initHttpsURLConnection(password, KEYSTORE_PATH, TRUSTSTORE_PATH);
            //发送请求获得响应数据
            ZxPostRecord bean = zxPostRecordDao.addPostRecord("T21000008", restr);
            String resStr = HttpsPost.post(httpsUrl, restr);
            record.setUploadDate(new Date());
            zxPostRecordDao.updPostRecord(bean, resStr);
            resStr = resStr.replace("ROOT", T21000008Response.class.getName());
            
            //把xml为转换为实体对象
            T21000008Response resData = (T21000008Response)Utils.getObjectFromXML(resStr, T21000008Response.class);
            if ("00000".equals(resData.getRSP_CODE()))
            {
                record.setStatus(ZxFileStatus.UPLOAD_SYCCESS);
                zxFileRecordDao.update(record);
            }
            //-------- 验签 --------------
            //获取签名信息
            String sigStr = resData.getSIGN_INFO();
            //验签是否成功
            boolean isSucc = SignUtil.verifySign(sortSignInfoT08(resStr).getBytes(), sigStr, PTNRTESTCER);
            System.out.println("响应信息验签：" + (isSucc == true ? "验签成功！" : "验签失败，请检查签名！"));
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            zxFileRecordDao.update(record);
        }
        
        return false;
    }
    

    
    public static void main(String[] args) throws UnsupportedEncodingException
    {
//        String str = "SjAwMDMwNzAwMDAwMDAwMjAyMjA2MTAgICAgICAyMDIyMDYxOTkxMTAwNjIyNDA0MDY0ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICA5MTEwMDYyMjQwNDA2NCAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgOTExMDA2MjI0MDQwNjQgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIDkxMTAwNjIyNDA0MDY0ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAtMDAwMDAwMDAwMjUwMC0wMDAwMDAwMDAwMDA2MiAgICAgICAgIDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAgICAgICAgICAgWkZZVzAyINanuLa21NXKw/fPuM7EvP7T0KOs0rXO8baptaXD98+4zt4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgDQo=";
//        String str = "SjAwMDMwNzAwMDAwMDAwMjAyMjA2MTAgICAgICAyMDIyMDYxOTkxMTAwNjIyNDA0MDY0ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICA5MTEwMDYyMjQwNDA2NCAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgOTExMDA2MjI0MDQwNjQgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIDkxMTAwNjIyNDA0MDY0ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAtMDAwMDAwMDAwMjUwMC0wMDAwMDAwMDAwMDA2MiAgICAgICAgIDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAgICAgICAgICAgWkZZVzAyINanuLa21NXKw/fPuM7EvP7T0KOs0rXO8baptaXD98+4zt4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgDQo=";
//        String str = "SjAwMDMwNzAwMDAwMDAwMjAyMjA2MTAgICAgICAyMDIyMDYxOTkxMTAwNjIyNDA0MDY0ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICA5MTEwMDYyMjQwNDA2NCAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgOTExMDA2MjI0MDQwNjQgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIDkxMTAwNjIyNDA0MDY0ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAtMDAwMDAwMDAwMjUwMC0wMDAwMDAwMDAwMDA2MiAgICAgICAgIDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAgICAgICAgICAgWkZZVzAyINanuLa21NXKw/fPuM7EvP7T0KOs0rXO8baptaXD98+4zt4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgDQo=";
//        byte[] decode = java.util.Base64.getDecoder().decode(str.getBytes("GBK"));
//        System.out.println(new String(decode, "GBK"));
        AppZxEqManager manager = new AppZxEqManager();
        MktVendor vendor = new MktVendor();
        vendor.setMobile("15257716818");
        vendor.setPkey(0);
        vendor.setName("哈哈哈哈");
        manager.zxRegister2(vendor);
    }
    
 // 文件上传 
    public Boolean runFileUploadKey(Integer key, String fileCount)
    {
//        ZxFileRecord record = zxFileManager.runFile(data, endTime, settleKey, total);
        ZxFileRecord record = zxFileRecordDao.get(key);
//        ZxFileRecord record = runFile(data, endTime, settleKey, total);
        String fileSaveLj = record.getSavePath();
        String fileName = record.getName() + ".ZIP";
        try
        {
            XStream xtreamData = new XStream();
            //--------------- 文件上传 ---------------
            T21000008Request request = new T21000008Request();
            request.setTRANS_CODE("21000008");
            request.setREQ_SSN(getReqSsn());
            request.setMCHNT_ID(MCHNT_ID);
            request.setFILE_TYPE("001");
            request.setFILE_COUNT(fileCount);
            request.setFILE_NAME(fileName);
            request.setTRANS_TYPE("MSG");
            byte[] bs = FileUtil.readFileContent(fileSaveLj);
            String fileContent = Base64.encodeBytes(bs, Base64.DONT_BREAK_LINES);
            System.out.println("fileContent-bs: " + fileContent);
            
            //实体对象转换为XML
            xtreamData.alias("ROOT", T21000008Request.class);
            String restr = xtreamData.toXML(request).replace("__", "_");
            restr = XML_HEAD + restr;
            
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new ByteArrayInputStream(restr.getBytes()));
            Element root = doc.getRootElement();
            
            //--------加签名------
            String sign = SignUtil.sign(request.sortSignInfo(root).getBytes());
            request.setFILE_CONTENT(fileContent);
            request.setSIGN_INFO(sign);
            restr = XML_HEAD + xtreamData.toXML(request).replace("__", "_");
            //---------- 发送请求数据 ------//
            //初始化https请求参数
            HttpsPost.initHttpsURLConnection(password, KEYSTORE_PATH, TRUSTSTORE_PATH);
            //发送请求获得响应数据
            ZxPostRecord bean = zxPostRecordDao.addPostRecord("T21000008", restr);
            String resStr = HttpsPost.post(httpsUrl, restr);
            record.setUploadDate(new Date());
            zxPostRecordDao.updPostRecord(bean, resStr);
            resStr = resStr.replace("ROOT", T21000008Response.class.getName());
            
            //把xml为转换为实体对象
            T21000008Response resData = (T21000008Response)Utils.getObjectFromXML(resStr, T21000008Response.class);
            if ("00000".equals(resData.getRSP_CODE()))
            {
                record.setStatus(ZxFileStatus.UPLOAD_SYCCESS);
                zxFileRecordDao.update(record);
            }
            //-------- 验签 --------------
            //获取签名信息
            String sigStr = resData.getSIGN_INFO();
            //验签是否成功
            boolean isSucc = SignUtil.verifySign(sortSignInfoT08(resStr).getBytes(), sigStr, PTNRTESTCER);
            System.out.println("响应信息验签：" + (isSucc == true ? "验签成功！" : "验签失败，请检查签名！"));
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            zxFileRecordDao.update(record);
        }
        
        return false;
    }
    
    // 向中信发起请求
    private <T extends TRequest, H extends TResponse> H postZx(XStream xtreamData, T request, Class<T> calzz,
        Class<H> clahh)
    {
        H resData = null;
        try
        {
            //实体对象转换为XML
            xtreamData.alias("ROOT", calzz);
            String restr = xtreamData.toXML(request).replace("__", "_");
            restr = XML_HEAD + restr;
            
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new ByteArrayInputStream(restr.getBytes()));
            Element root = doc.getRootElement();
            
            //--------加签名------
            String sign = SignUtil.sign(request.sortSignInfo(root).getBytes());
            request.setSIGN_INFO(sign);
            restr = XML_HEAD + xtreamData.toXML(request).replace("__", "_");
            System.out.println("请求数据: " + restr);
            //---------- 发送请求数据 ------//
            //初始化https请求参数
            HttpsPost.initHttpsURLConnection(password, KEYSTORE_PATH, TRUSTSTORE_PATH);
            //发送请求获得响应数据
//            ZxPostRecord bean = zxPostRecordDao.addPostRecord(calzz.getName(), restr);
            String resStr = HttpsPost.post(httpsUrl, restr);
//            zxPostRecordDao.updPostRecord(bean, resStr);
            resStr = resStr.replace("ROOT", clahh.getName());
            //把xml为转换为实体对象
            resData = clahh.cast(Utils.getObjectFromXML(resStr, clahh));
            //-------- 验签 --------------
            //获取签名信息
            String sigStr = resData.getSIGN_INFO();
            //验签是否成功
            boolean isSucc = true;
            if (calzz.equals(T21000007Request.class))
                isSucc = SignUtil.verifySign(sortSignInfoT07(resStr).getBytes(), sigStr, PTNRTESTCER);
            else
                isSucc = SignUtil.verifySign(sortSignInfo(resStr).getBytes(), sigStr, PTNRTESTCER);
            if ("00000".equals(resData.getRSP_CODE()))
            {
                resData.setSucc(true);
            }
            else
            {
                resData.setSucc(false);
                resData.setRSP_MSG(resData.getRSP_MSG());
            }
            log.info("响应信息验签：{}", (isSucc == true ? "验签成功！" : "验签失败，请检查签名！"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return resData;
    }
    
//    public ZxFileRecord runFile(List<MktSettlementLineDay> data, Date endTime, String settleKey,
//        MktSettlementTotal total)
//    {
//        ZxFileRecord fileRecord = new ZxFileRecord();
//        fileRecord.setSettlementKey(settleKey);
//        String addFileTime = DateUtil.formatDate(endTime, "yyyyMMdd");
//        StringBuffer sb = new StringBuffer();
//        
//        // 优惠 总金额  .multiply(new BigDecimal("100"))
//        BigDecimal discountAmt = total.getDiscountAmt().setScale(0, BigDecimal.ROUND_HALF_UP);
//        Integer discount =
//            total.getDiscountAmt().multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
//        // 清分 总金额
//        BigDecimal clearingAmt = total.getClearingAmt().setScale(0, BigDecimal.ROUND_HALF_UP);
//        
//        for (int i = 0; i < data.size(); i++)
//        {
//            MktSettlementLineDay vo = data.get(i);
//            int disAmt = 0;
//            if(clearingAmt.compareTo(BigDecimal.ZERO) == 1)
//            {
//                disAmt = 
//                    vo.getAmt()
//                    .divide(clearingAmt, 2, BigDecimal.ROUND_HALF_UP)
//                    .multiply(discountAmt)
//                    .multiply(new BigDecimal("100"))
//                    .setScale(0, BigDecimal.ROUND_HALF_UP)
//                    .intValue();
//            }
//            if (i != data.size() - 1)
//            {
//                discount = discount - disAmt;
//            }
//            if (i == 0)
//            {
//                runFileContent(sb, vo, endTime, total, disAmt);
//            }
//            else
//            {
//                if (i == data.size() - 1)
//                {
//                    runFileContent(sb, vo, endTime, discount);
//                }
//                else
//                    runFileContent(sb, vo, endTime, disAmt);
//                
//            }
//        }
//        
//        fileRecord.setContent(sb.toString());
//        String fileName = MCHNT_ID;
//        String filelj = FILE_PATH + LocalDate.now() + "/";
//        try
//        {
//            // 渠道编号
//            String qudaobianhao = "1001";
//            fileName = fileName + qudaobianhao + "001" + addFileTime + "01";
//            File f = new File(filelj);
//            boolean mkdirs = f.mkdirs();
//            log.info("mkdirs: {}", mkdirs);
//            
//            FileOutputStream out = new FileOutputStream(filelj + fileName);
//            out.write(sb.toString().getBytes("GBK"));
//            out.flush();
//            out.close();
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//        String fileSaveLj = filelj + fileName + ".ZIP";
//        File zip = new File(fileSaveLj);
//        ZipUtil util = new ZipUtil();
//        util.createZip(filelj + fileName, zip);
//        
//        fileRecord.setName(fileName);
//        fileRecord.setSavePath(fileSaveLj);
//        fileRecord.setType(ZxFileType.Z21000008);
//        ZxFileRecord record = zxFileRecordDao.add(fileRecord);
//        return record;
//    }
    
//    private StringBuffer addFileContent(StringBuffer sb, String userId, MktVendorOrder vendorOrder)
//    {
//        // 平台商户编号
//        sb.append(MCHNT_ID);
//        // 用户编号
//        sb.append(userId);
//        // 交易日期 YYYYMMDD  交易时间 HHMMSS
//        sb.append(DateUtil.formatDate(vendorOrder.getFarmerTime(), "yyyyMMddHHmmss"));
//        String payNum = vendorOrder.getOrderPkey() + "line" + vendorOrder.getOrderLinePkey() + "";
//        String transaction_id = "tran" + payNum;
//        Integer amt = vendorOrder.getAmt().multiply(new BigDecimal("100")).intValue();
//        // 支付渠道名称   60
//        //        sb.append(String.format("%1$-57s", QUDAO_NAME));
//        sb.append(String.format("%1$-55s", QUDAO_NAME));
//        // 平台商户业务订单号  64
//        sb.append(String.format("%1$-64s", payNum));
//        // 平台商户支付订单号  64
//        sb.append(String.format("%1$-64s", payNum));
//        // 支付渠道交易流水号  64
//        sb.append(String.format("%1$-64s", "J00030700000000(0x03)" + transaction_id));
//        // 平台商户业务子订单号  64
//        sb.append(String.format("%1$-64s", payNum));
//        // 支付订单交易类型  2 (1-支付 2-退款 空格-其他)
//        sb.append("1 ");
//        // 业务订单交易类型  2 (1 -实时交易支付  2 -实时交易退货  3 -预付交易支付  4 -预付交易撤销  5 -预付交易完成)
//        sb.append("1 ");
//        // 清算资金来源 1 (1-支付渠道  2-内部划转)
//        sb.append("1");
//        // 渠道手续费承担方式 1 (1-平台商户承担  2-用户承担)
//        sb.append("1");
//        // 原始订单金额  14
//        sb.append(String.format("%014d", amt));
//        // 原始支付金额
//        sb.append(String.format("%014d", amt));
//        // 平台优惠金额
//        sb.append(String.format("%014d", 0));
//        // 平台分成金额
//        sb.append(String.format("%014d", 0));
//        // 平台垫款金额
//        sb.append(String.format("%014d", 0));
//        // 渠道手续费
//        sb.append(String.format("%014d", 0));
//        // 资金类型  6
//        sb.append(FUNDS_TYPE);
//        // 备注 200 
//        sb.append(String.format("%1$-200s", ""));
//        sb.append("\r\n");
//        return sb;
//    }
    
//    private StringBuffer runFileContent(StringBuffer sb, MktSettlementLineDay settle, Date date, int disAmt)
//    {
//        // 平台商户编号
//        sb.append(MCHNT_ID);
//        // 用户编号
//        sb.append(settle.getZxUserId());
//        // 交易日期 YYYYMMDD  交易时间 HHMMSS
//        sb.append(DateUtil.formatDate(date, "yyyyMMddHHmmss"));
//        //        String payNum = vendorOrder.getOrderPkey() + "line" + vendorOrder.getOrderLinePkey() + "";
//        String payNum = settle.getPkey() + "settlement" + settle.getSettlementPkey() + "";
//        String transaction_id = "tran" + payNum;
//        BigDecimal hundred = new BigDecimal("100");
//        // 订单支付金额
//        Integer amt = settle.getAmt().multiply(hundred).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
//        log.info("订单支付金额amt: {}", amt);
//        log.info("订单支付金额disAmt: {}", disAmt);
//        
//        // 支付渠道名称   60
//        //        sb.append(String.format("%1$-57s", QUDAO_NAME));
//        sb.append(String.format("%1$-55s", QUDAO_NAME));
//        // 平台商户业务订单号  64
//        sb.append(String.format("%1$-64s", payNum));
//        // 平台商户支付订单号  64
//        sb.append(String.format("%1$-64s", payNum));
//        // 支付渠道交易流水号  64
//        byte b1[]={0x03};
//        String separator=new String(b1);
//        sb.append(String.format("%1$-64s", "421010060000001" + separator + transaction_id));
//        // 平台商户业务子订单号  64
//        sb.append(String.format("%1$-64s", payNum));
//        // 支付订单交易类型  2 (1-支付 2-退款 空格-其他)
//        sb.append("1 ");
//        // 业务订单交易类型  2 (1 -实时交易支付  2 -实时交易退货  3 -预付交易支付  4 -预付交易撤销  5 -预付交易完成)
//        sb.append("1 ");
//        // 清算资金来源 1 (1-支付渠道  2-内部划转)
//        sb.append("1");
//        // 渠道手续费承担方式 1 (1-平台商户承担  2-用户承担)
//        sb.append("1");
//        // 原始订单金额  14 
//        sb.append(String.format("%014d", amt));
//        // 原始支付金额
//        sb.append(String.format("%014d", amt - disAmt));
//        // 平台优惠金额
//        sb.append(String.format("%014d", disAmt));
//        // 平台分成金额
//        sb.append(String.format("%014d", 0));
//        // 平台垫款金额
//        sb.append(String.format("%014d", 0));
//        // 渠道手续费
//        sb.append(String.format("%014d", 0));
//        // 资金类型  6
//        sb.append(FUNDS_TYPE);
//        // 备注 200 
//        sb.append(String.format("%1$-200s", ""));
//        sb.append("\r\n");
//        return sb;
//    }
    
//    private StringBuffer runFileContent(StringBuffer sb, MktSettlementLineDay settle, Date date,
//        MktSettlementTotal total, int disAmt)
//    {
//        // 平台商户编号
//        sb.append(MCHNT_ID);
//        // 用户编号
//        sb.append(settle.getZxUserId());
//        // 交易日期 YYYYMMDD  交易时间 HHMMSS
//        sb.append(DateUtil.formatDate(date, "yyyyMMddHHmmss"));
//        //        String payNum = vendorOrder.getOrderPkey() + "line" + vendorOrder.getOrderLinePkey() + "";
//        String payNum = settle.getPkey() + "settlement" + settle.getSettlementPkey() + "";
//        String transaction_id = "tran" + payNum;
//        BigDecimal hundred = new BigDecimal("100");
//        // 订单支付金额
//        Integer amt = settle.getAmt().multiply(hundred).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
//        // 平台分佣金额
//        Integer platformAmt = total.getPlatformAmt().multiply(hundred).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
//        // 渠道手续费
//        Integer qdAmt = total.getHandlingFee().multiply(hundred).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
//        // 优惠金额 
//        Integer discountAmt = total.getDiscountAmt().multiply(hundred).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
//        
//        log.info("订单支付金额amt: {}", amt);
//        log.info("渠道手续费qdAmt: {}", qdAmt);
//        log.info("平台分佣金额platformAmt: {}", platformAmt);
//        log.info("优惠金额discountAmt: {}", discountAmt);
//        log.info("优惠金额disAmt: {}", disAmt);
//        
//        // 支付渠道名称   60
//        //        sb.append(String.format("%1$-57s", QUDAO_NAME));
//        sb.append(String.format("%1$-55s", QUDAO_NAME));
//        // 平台商户业务订单号  64
//        sb.append(String.format("%1$-64s", payNum));
//        // 平台商户支付订单号  64
//        sb.append(String.format("%1$-64s", payNum));
//        // 支付渠道交易流水号  64
//        byte b1[]={0x03};
//        String separator=new String(b1);
//        sb.append(String.format("%1$-64s", "421010060000001" + separator + transaction_id));
//        // 平台商户业务子订单号  64
//        sb.append(String.format("%1$-64s", payNum));
//        // 支付订单交易类型  2 (1-支付 2-退款 空格-其他)
//        sb.append("1 ");
//        // 业务订单交易类型  2 (1 -实时交易支付  2 -实时交易退货  3 -预付交易支付  4 -预付交易撤销  5 -预付交易完成)
//        sb.append("1 ");
//        // 清算资金来源 1 (1-支付渠道  2-内部划转)
//        sb.append("1");
//        // 渠道手续费承担方式 1 (1-平台商户承担  2-用户承担)
//        sb.append("1");
//        // 原始订单金额  14 
//        sb.append(String.format("%014d", amt + platformAmt));
//        // 原始支付金额
//        sb.append(String.format("%014d", amt + platformAmt - disAmt));
//        // 平台优惠金额
//        sb.append(String.format("%014d", disAmt));
//        // 平台分成金额:  佣金抽层 + 运费 + 差价
//        sb.append(String.format("%014d", platformAmt));
//        // 平台垫款金额
//        sb.append(String.format("%014d", 0));
//        // 渠道手续费
//        sb.append(String.format("%014d", qdAmt));
//        // 资金类型  6
//        sb.append(FUNDS_TYPE);
//        // 备注 200 
//        sb.append(String.format("%1$-200s", ""));
//        sb.append("\r\n");
//        return sb;
//    }
    
//    public StringBuffer runFileContent(StringBuffer sb, MktSettlementLineDay settle, Date date, BigDecimal platformAmt,
//        BigDecimal discountAmt, BigDecimal handlingFee)
//    {
//        // 平台商户编号
//        sb.append(MCHNT_ID);
//        // 用户编号
//        sb.append(settle.getZxUserId());
//        // 交易日期 YYYYMMDD  交易时间 HHMMSS
//        sb.append(DateUtil.formatDate(date, "yyyyMMddHHmmss"));
//        //        String payNum = vendorOrder.getOrderPkey() + "line" + vendorOrder.getOrderLinePkey() + "";
//        String payNum = settle.getPkey() + "settlement" + settle.getSettlementPkey() + "";
//        String transaction_id = "tran" + payNum;
//        BigDecimal hundred = new BigDecimal("100");
//        // 订单支付金额
//        Integer amt = settle.getAmt().multiply(hundred).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
//        log.info("订单支付金额amt: {}", amt);
//        Integer platform = platformAmt.multiply(hundred).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
//        log.info("平台分成金额platformAmt: {}", platformAmt);
//        log.info("平台分成金额platform: {}", platform);
//        Integer discount = discountAmt.multiply(hundred).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
//        log.info("平台优惠金额discountAmt: {}", discountAmt);
//        log.info("平台优惠金额discount: {}", discount);
//        Integer handling = handlingFee.multiply(hundred).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
//        log.info("渠道手续费handlingFee: {}", handlingFee);
//        log.info("渠道手续费handlFee: {}", handling);
//        
//        // 支付渠道名称   60
//        //        sb.append(String.format("%1$-57s", QUDAO_NAME));
//        sb.append(String.format("%1$-55s", QUDAO_NAME));
//        // 平台商户业务订单号  64
//        sb.append(String.format("%1$-64s", payNum));
//        // 平台商户支付订单号  64
//        sb.append(String.format("%1$-64s", payNum));
//        // 支付渠道交易流水号  64
//        sb.append(String.format("%1$-64s", "J00030700000000(0x03)" + transaction_id));
//        // 平台商户业务子订单号  64
//        sb.append(String.format("%1$-64s", payNum));
//        // 支付订单交易类型  2 (1-支付 2-退款 空格-其他)
//        sb.append("1 ");
//        // 业务订单交易类型  2 (1 -实时交易支付  2 -实时交易退货  3 -预付交易支付  4 -预付交易撤销  5 -预付交易完成)
//        sb.append("1 ");
//        // 清算资金来源 1 (1-支付渠道  2-内部划转)
//        sb.append("1");
//        // 渠道手续费承担方式 1 (1-平台商户承担  2-用户承担)
//        sb.append("1");
//        // 原始订单金额  14 
//        sb.append(String.format("%014d", amt + discount));
//        // 原始支付金额
//        sb.append(String.format("%014d", amt));
//        // 平台优惠金额
//        sb.append(String.format("%014d", discount));
//        // 平台分成金额
//        sb.append(String.format("%014d", platform));
//        // 平台垫款金额
//        sb.append(String.format("%014d", 0));
//        // 渠道手续费
//        sb.append(String.format("%014d", handling));
//        // 资金类型  6
//        sb.append(FUNDS_TYPE);
//        // 备注 200 
//        sb.append(String.format("%1$-200s", ""));
//        sb.append("\r\n");
//        return sb;
//    }
    
    public void delFile(String lj)
    {
        File f = new File(lj);
        if (f.isDirectory())
        {
            File[] listFiles = f.listFiles();
            for (File df : listFiles)
            {
                df.delete();
            }
            f.delete();
            log.info("删除文件成功,文件路径:{}", lj);
        }
        log.info("删除文件方法结束");
    }
    
    private String getReqSsn()
    {
        return MCHNT_ID + DateUtil.formatDate(new Date(), "yyyyMMddHHmmssSSS")
            + String.valueOf(r.nextLong()).substring(1, 8 + 1);
    }
    
    //获取商户入驻接收报文中的验签字段
    public static String sortSignInfo(String xml)
    {
        List<String> signList = new ArrayList<String>();
        try
        {
            org.dom4j.Document document = DocumentHelper.parseText(xml);
            Element rootElement = document.getRootElement();
            for (Iterator<?> iter = rootElement.elementIterator(); iter.hasNext();)
            {
                Element element = (Element)iter.next();
                if (element.getName().equals("SIGN_INFO"))
                {
                    element.detach();
                    continue;
                }
                String tagName = element.getText();
                signList.add(tagName);
            }
        }
        catch (DocumentException e)
        {
            e.printStackTrace();
        }
        //排序
        Collections.sort(signList);
        
        StringBuffer signInfo = new StringBuffer();
        
        for (String sign : signList)
        {
            signInfo = signInfo.append(sign);
        }
        return signInfo.toString();
    }
    
    public static String sortSignInfoT08(String xml)
    {
        List<String> signList = new ArrayList<String>();
        try
        {
            org.dom4j.Document document = DocumentHelper.parseText(xml);
            Element rootElement = document.getRootElement();
            for (Iterator<?> iter = rootElement.elementIterator(); iter.hasNext();)
            {
                Element element = (Element)iter.next();
                if (element.getName().equals("SIGN_INFO") || element.getName().equals("RESULT_CODE")
                    || element.getName().equals("RESULT_MSG"))
                {
                    element.detach();
                    continue;
                }
                String tagName = element.getText();
                signList.add(tagName);
            }
        }
        catch (DocumentException e)
        {
            e.printStackTrace();
        }
        //排序
        Collections.sort(signList);
        
        StringBuffer signInfo = new StringBuffer();
        
        for (String sign : signList)
        {
            signInfo = signInfo.append(sign);
        }
        return signInfo.toString();
    }
    
    public static String sortSignInfoT07(String xml)
    {
        List<String> signList = new ArrayList<String>();
        try
        {
            org.dom4j.Document document = DocumentHelper.parseText(xml);
            Element rootElement = document.getRootElement();
            for (Iterator<?> iter = rootElement.elementIterator(); iter.hasNext();)
            {
                Element element = (Element)iter.next();
                if (element.getName().equals("SIGN_INFO") || element.getName().equals("RESULT_CODE")
                    || element.getName().equals("RESULT_MSG") || element.getName().equals("FILE_CONTENT"))
                {
                    element.detach();
                    continue;
                }
                String tagName = element.getText();
                signList.add(tagName);
            }
        }
        catch (DocumentException e)
        {
            e.printStackTrace();
        }
        //排序
        Collections.sort(signList);
        
        StringBuffer signInfo = new StringBuffer();
        
        for (String sign : signList)
        {
            signInfo = signInfo.append(sign);
        }
        return signInfo.toString();
    }
    
    /**
     * xml转json
     * @param element
     * @param json
     */
    public void dom4j2Json(Element element, JSONObject json)
    {
        //如果是属性
        for (Object o : element.attributes())
        {
            Attribute attr = (Attribute)o;
            if (!isEmpty(attr.getValue()))
            {
                json.put("@" + attr.getName(), attr.getValue());
            }
        }
        List<Element> chdEl = element.elements();
        if (chdEl.isEmpty() && !isEmpty(element.getText()))
        {//如果没有子元素,只有一个值
            json.put(element.getName(), element.getText());
        }
        
        for (Element e : chdEl)
        {//有子元素
            if (!e.elements().isEmpty())
            {//子元素也有子元素
                JSONObject chdjson = new JSONObject();
                dom4j2Json(e, chdjson);
                Object o = json.get(e.getName());
                if (o != null)
                {
                    JSONArray jsona = null;
                    if (o instanceof JSONObject)
                    {//如果此元素已存在,则转为jsonArray
                        JSONObject jsono = (JSONObject)o;
                        json.remove(e.getName());
                        jsona = new JSONArray();
                        jsona.add(jsono);
                        jsona.add(chdjson);
                    }
                    if (o instanceof JSONArray)
                    {
                        jsona = (JSONArray)o;
                        jsona.add(chdjson);
                    }
                    json.put(e.getName(), jsona);
                }
                else
                {
                    if (!chdjson.isEmpty())
                    {
                        json.put(e.getName(), chdjson);
                    }
                }
                
            }
            else
            {//子元素没有子元素
                for (Object o : element.attributes())
                {
                    Attribute attr = (Attribute)o;
                    if (!isEmpty(attr.getValue()))
                    {
                        json.put("@" + attr.getName(), attr.getValue());
                    }
                }
                if (!e.getText().isEmpty())
                {
                    json.put(e.getName(), e.getText());
                }
            }
        }
    }
    
    public boolean isEmpty(String str)
    {
        
        if (str == null || str.trim().isEmpty() || "null".equals(str))
        {
            return true;
        }
        return false;
    }
    
}

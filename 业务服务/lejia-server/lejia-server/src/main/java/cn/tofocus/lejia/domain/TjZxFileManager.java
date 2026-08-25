package cn.tofocus.lejia.domain;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Date;
import java.util.Random;

import cn.tofocus.lejia.zx.beanV2.*;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.excel.util.StringUtils;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.common.util.file.FileUtil;
import cn.tofocus.lejia.bean.entity.zx.ZxFileRecord;
import cn.tofocus.lejia.dao.zx.ZxFileRecordDao;
import cn.tofocus.lejia.zx.sendMethodV2.BaseSendMethod;
import cn.tofocus.lejia.zx.utilV2.Constants;
import cn.tofocus.lejia.zx.utilV2.HttpsPost;
import cn.tofocus.lejia.zx.utilV2.SignUtil;
import cn.tofocus.lejia.zx.utilV2.Utils;
import cn.tofocus.lejia.zx.utilV2.XstreamUtils;

@Component
public class TjZxFileManager
{
    // 字段分隔符
    private static final char COL_SEPARATOR = 0x03;
    
    // 行分隔符
    private static final String ROW_SEPARATOR = "\n";
    
    
    private static String ROOTPATH = "";
    static
    {
        ROOTPATH = System.getProperty("user.dir");
    }
    
    /** 上传文件路径**/
    private static final String FILE_PATH = ROOTPATH + "/file/";

    @Autowired
    private ZxFileRecordDao zxFileRecordDao;
    
    @Value("${wx.pay.test:false}")
    private Boolean wxPayTest;
    
    /**
     * @param zxUserId 云商城商户在中信注册的编号
     * @param code  云商城交易订单的code前14位
     * @param transactionId  支付渠道交易流水号
     * @param vendorOrderPkey 商户订单主键
     * @param amt 需要结算给商户的金额
     * @param commissions 给平台(集团方)的抽成
     * @param qdAmtBear 渠道手续费谁承担 1平台商户承担  2用户承担
     * @param postage 运费
     * @param date
     * @return
     */
    public StringBuilder assembleFileContent(StringBuilder sb, String zxUserId, String code, String transactionId,
        Integer vendorOrderPkey, BigDecimal amt, BigDecimal commissions,
        BigDecimal postage, Date date)
    {
        String sd = DateUtil.formatDate(date, "yyyyMMddHHmmss");
        // 平台商户业务订单号  平台商户支付订单号
        String payNum = code + "settlement" + sd;
        // 平台商户业务子订单号
        String childPayNum = vendorOrderPkey + sd;
        
        // 平台商户编号
        sb.append(Constants.MCHNT_ID).append(COL_SEPARATOR);
        // 用户编号
        sb.append(zxUserId).append(COL_SEPARATOR);
        // 交易日期 yyyyMMdd
        sb.append(DateUtil.formatDate(date, "yyyyMMdd")).append(COL_SEPARATOR);
        // 交易时间 HHmmss
        sb.append(DateUtil.formatDate(date, "HHmmss")).append(COL_SEPARATOR);
        // 支付渠道名称   60
        sb.append(Constants.QUDAO_NAME).append(COL_SEPARATOR);
        // 平台商户业务订单号  64
        sb.append(payNum).append(COL_SEPARATOR);
        // 平台商户支付订单号  64
        sb.append(payNum).append(COL_SEPARATOR);
        // 支付渠道交易流水号  64  TODO 收单商户号 目前不确定
        sb.append(Constants.MCHNT_ID + "|" + transactionId).append(COL_SEPARATOR);
        // 平台商户业务子订单号  64
        sb.append(childPayNum).append(COL_SEPARATOR);
        // 支付订单交易类型  2 (1-支付 2-退款 空格-其他)
        sb.append(" ").append(COL_SEPARATOR);
        // 业务订单交易类型  2 (1 -实时交易支付  2 -实时交易退货  3 -预付交易支付  4 -预付交易撤销  5 -预付交易完成)
        sb.append("5").append(COL_SEPARATOR);
        // 清算资金来源 1 (1-支付渠道  2-内部划转)
        sb.append("2").append(COL_SEPARATOR);
        // 渠道手续费承担方式 1 (1-平台商户承担  2-用户承担)
        sb.append("1").append(COL_SEPARATOR);
        // 原始订单金额  14
        System.out.println("amt: " + amt);
        System.out.println("commissions: " + commissions);
        System.out.println("postage: " + postage);
        // .add(commissions) 不用了 外面已经加了
        sb.append(amt.add(postage)).append(COL_SEPARATOR);
        // 原始支付金额 
        sb.append(amt.add(postage)).append(COL_SEPARATOR);
        // 优惠金额 直接从担保用户出
//        sb.append(amt.add(commissions).add(postage).subtract(discountAmt)).append(COL_SEPARATOR);
        // 平台优惠金额
        sb.append("0").append(COL_SEPARATOR);
//        sb.append(discountAmt).append(COL_SEPARATOR);
        // 平台分成金额  佣金抽层 + 运费 - 渠道手续费(在传进来的时候就减掉了)
        sb.append(commissions.add(postage)).append(COL_SEPARATOR);
        // 平台垫款金额
        sb.append("0").append(COL_SEPARATOR);
        // 渠道手续费
        sb.append("0").append(COL_SEPARATOR);
        // 资金类型  6
        sb.append(Constants.FUNDS_TYPE).append(COL_SEPARATOR);
        // 备注 200
        sb.append("");
        // 一行结束
        sb.append(ROW_SEPARATOR);
        return sb;
    }
    
    // 渠道入金 划到 担保登记簿 组合文件
    public StringBuilder assembleFileContentGuarantee(StringBuilder sb, String zxUserId, String code, 
       BigDecimal amt, Date date)
    {
        String sd = DateUtil.formatDate(date, "yyyyMMddHHmmss");
        // 平台商户业务订单号  平台商户支付订单号
        String payNum = code + "settlement" + sd;
        // 平台商户业务子订单号
//        String childPayNum = vendorOrderPkey + sd;
        
        // 平台商户编号
        sb.append(Constants.MCHNT_ID).append(COL_SEPARATOR);
        // 用户编号
        sb.append(zxUserId).append(COL_SEPARATOR);
        // 交易日期 yyyyMMdd
        sb.append(DateUtil.formatDate(date, "yyyyMMdd")).append(COL_SEPARATOR);
        // 交易时间 HHmmss
        sb.append(DateUtil.formatDate(date, "HHmmss")).append(COL_SEPARATOR);
        // 支付渠道名称   60
        sb.append(Constants.QUDAO_NAME).append(COL_SEPARATOR);
        // 平台商户业务订单号  64
        sb.append(payNum).append(COL_SEPARATOR);
        // 平台商户支付订单号  64
        sb.append(payNum).append(COL_SEPARATOR);
        // 支付渠道交易流水号  64  TODO 收单商户号 目前不确定
        sb.append(Constants.MCHNT_ID + "|" + code).append(COL_SEPARATOR);
        // 平台商户业务子订单号  64
        sb.append(code).append(COL_SEPARATOR);
        // 支付订单交易类型  2 (1-支付 2-退款 空格-其他)
        sb.append("1").append(COL_SEPARATOR);
        // 业务订单交易类型  2 (1 -实时交易支付  2 -实时交易退货  3 -预付交易支付  4 -预付交易撤销  5 -预付交易完成)
        sb.append("3").append(COL_SEPARATOR);
        // 清算资金来源 1 (1-支付渠道  2-内部划转)
        sb.append("1").append(COL_SEPARATOR);
        // 渠道手续费承担方式 1 (1-平台商户承担  2-用户承担)
        sb.append("1").append(COL_SEPARATOR);
        // 原始订单金额  14
        sb.append(amt).append(COL_SEPARATOR);
        // 原始支付金额
        sb.append(amt).append(COL_SEPARATOR);
        // 平台优惠金额
        sb.append(BigDecimal.ZERO).append(COL_SEPARATOR);
        // 平台分成金额  
        sb.append(BigDecimal.ZERO).append(COL_SEPARATOR);
        // 平台垫款金额
        sb.append("0").append(COL_SEPARATOR);
        // 渠道手续费
        sb.append("0").append(COL_SEPARATOR);
        // 资金类型  6
        sb.append(Constants.FUNDS_TYPE).append(COL_SEPARATOR);
        // 备注 200
        sb.append("");
        // 一行结束
        sb.append(ROW_SEPARATOR);
        return sb;
    }
    
    // 担保登记簿 划到 用户自有登记簿 组合文件
    public StringBuilder assembleFileContentGuaranteeArriveUser(StringBuilder sb, String zxUserId, String code, 
        BigDecimal amt, Date date, Boolean flag)
    {
        FileUtil.checkDirectory(FILE_PATH);
        FileUtil.checkDirectory(FILE_PATH);
        
        String sd = DateUtil.formatDate(date, "yyyyMMddHHmmss");
        // 平台商户业务订单号  平台商户支付订单号
        String payNum = code + "settlement" + sd;
        // 平台商户业务子订单号
//        String childPayNum = vendorOrderPkey + sd;
        
        // 平台商户编号
        sb.append(Constants.MCHNT_ID).append(COL_SEPARATOR);
        // 用户编号
        sb.append(zxUserId).append(COL_SEPARATOR);
        // 交易日期 yyyyMMdd
        sb.append(DateUtil.formatDate(date, "yyyyMMdd")).append(COL_SEPARATOR);
        // 交易时间 HHmmss
        sb.append(DateUtil.formatDate(date, "HHmmss")).append(COL_SEPARATOR);
        // 支付渠道名称   60
        sb.append(Constants.QUDAO_NAME).append(COL_SEPARATOR);
        // 平台商户业务订单号  64
        sb.append(payNum).append(COL_SEPARATOR);
        // 平台商户支付订单号  64
        sb.append(payNum).append(COL_SEPARATOR);
        // 支付渠道交易流水号  64  TODO 收单商户号 目前不确定
        sb.append(Constants.MCHNT_ID + "|" + code).append(COL_SEPARATOR);
        // 平台商户业务子订单号  64
        sb.append(code).append(COL_SEPARATOR);
        // 支付订单交易类型  2 (1-支付 2-退款 空格-其他)
        sb.append(" ").append(COL_SEPARATOR);
        // 业务订单交易类型  2 (1 -实时交易支付  2 -实时交易退货  3 -预付交易支付  4 -预付交易撤销  5 -预付交易完成)
        sb.append("5").append(COL_SEPARATOR);
        // 清算资金来源 1 (1-支付渠道  2-内部划转)
        sb.append("2").append(COL_SEPARATOR);
        // 渠道手续费承担方式 1 (1-平台商户承担  2-用户承担)
        sb.append("1").append(COL_SEPARATOR);
        // 原始订单金额  14
        sb.append(amt).append(COL_SEPARATOR);
        // 原始支付金额
        sb.append(amt).append(COL_SEPARATOR);
        // 平台优惠金额
        sb.append(BigDecimal.ZERO).append(COL_SEPARATOR);
        // 平台分成金额
        if(Boolean.TRUE.equals(flag))
            sb.append(amt).append(COL_SEPARATOR);
        else
            sb.append(BigDecimal.ZERO).append(COL_SEPARATOR);
        // 平台垫款金额
        sb.append("0").append(COL_SEPARATOR);
        // 渠道手续费
        sb.append("0").append(COL_SEPARATOR);
        // 资金类型  6
        sb.append(Constants.FUNDS_TYPE).append(COL_SEPARATOR);
        // 备注 200
        sb.append("");
        // 一行结束
        sb.append(ROW_SEPARATOR);
        return sb;
    }
    
    
    // day 格式 yyyyMMdd  fileCount
    public void addFile(StringBuilder sb, String day, String fileCount, ZxFileRecord fileRecord)
    {
        String fileName = "";
        String filelj = FILE_PATH + LocalDate.now() + "/";
        FileUtil.checkDirectory(filelj);
        try
        {
            String nextXuhao = zxFileRecordDao.getNextXuhao(day);
            // 渠道编号
            fileName = Constants.MCHNT_ID + Constants.QUDAOBIANHAO + "616" + day + nextXuhao;
            FileOutputStream out = new FileOutputStream(filelj + fileName);
            out.write(sb.toString().getBytes(StandardCharsets.UTF_8));
            out.flush();
            out.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
     
        Utils.fileZip(filelj + fileName, filelj + fileName + ".ZIP");
        fileRecord.setName(fileName);
        fileRecord.setSavePath(filelj + fileName + ".ZIP");
        zxFileRecordDao.update(fileRecord);
        if(!Boolean.TRUE.equals(wxPayTest))
            sendFile(fileName + ".ZIP", fileCount);
    }

    public void addFileNoSend(StringBuilder sb, String day, String fileCount, ZxFileRecord fileRecord, String nextXuhao)
    {
        String fileName = "";
        String filelj = FILE_PATH + LocalDate.now() + "/";
        FileUtil.checkDirectory(filelj);
        try
        {
            if(StringUtils.isBlank(nextXuhao))
                nextXuhao = zxFileRecordDao.getNextXuhao(day);
            // 渠道编号
            fileName = Constants.MCHNT_ID + Constants.QUDAOBIANHAO + "616" + day + nextXuhao;
            FileOutputStream out = new FileOutputStream(filelj + fileName);
            out.write(sb.toString().getBytes(StandardCharsets.UTF_8));
            out.flush();
            out.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        Utils.fileZip(filelj + fileName, filelj + fileName + ".ZIP");
        fileRecord.setName(fileName);
        fileRecord.setSavePath(filelj + fileName + ".ZIP");
        zxFileRecordDao.update(fileRecord);
    }
    
    public void sendFile(String fileName, String fileCount)
    {
       
        try
        {
            //-----------  编写测试数据  （测试只需要修改其中的数据即可）------------
            T21000031Request request = new T21000031Request();
            request.setTRANS_CODE("21000031");
            Random r = new Random();
            String reqSsn = Constants.MCHNT_ID + DateUtil.formatDate(new Date(), "yyyyMMddHHmmssSSS")
                + String.valueOf(r.nextLong()).substring(1, 8 + 1);
            request.setREQ_SSN(reqSsn);
            request.setMCHNT_ID(Constants.MCHNT_ID);
            request.setFILE_COUNT(fileCount);
            request.setFILE_TYPE("616");
            request.setFILE_NAME(fileName);
            request.setTRANS_TYPE("MSG");
            String filelj = FILE_PATH + LocalDate.now() + "/";
            
            request.setFILE_CONTENT(Utils.encodeBase64File(filelj + fileName).replaceAll("[\\s*\t\n\r]", ""));
//            request.setREQ_RESERVED("123456");
            

            //实体对象转换为XML
            String restr = XstreamUtils.toXml(request, request.getClass());
            
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new ByteArrayInputStream(restr.getBytes()));
            Element root = doc.getRootElement();
            
            //--------加签名------
            String sign = SignUtil.sign(Utils.sortSignInfo(root).getBytes());
            request.setSIGN_INFO(sign);
            restr = Constants.XML_HEAD + XstreamUtils.toXml(request, request.getClass());
            
            //---------- 发送请求数据 ------
            //初始化https请求参数
            HttpsPost.initHttpsURLConnection(Constants.PASSWORD, Constants.KEYSTORE_PATH, Constants.TRUSTSTORE_PATH);
            //发送请求获得响应数据
            String resStr = HttpsPost.post(Constants.httpsFileUrl, restr, Constants.MCHNT_ID, "21000031");
            //把xml为转换为实体对象
            T21000031Response resData = XstreamUtils.toBean(resStr, T21000031Response.class);
            
            //-------- 验签 --------------
            //获取签名信息
            String sigStr = resData.getDATA().getSIGN_INFO();
            //验签是否成功
            boolean isSucc = SignUtil.verifySign(BaseSendMethod.sortSignInfoNoR(resStr).getBytes(), sigStr, Constants.PTNRTESTCER);
            System.out.println("响应信息验签：" + (isSucc == true ? "验签成功！" : "验签失败，请检查签名！"));

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public T21000007ResponseData downloadFile(String fileName)
    {
        
        try
        {
            //-----------  编写测试数据  （测试只需要修改其中的数据即可）------------
            T21000007Request request = new T21000007Request();
            request.setTRANS_CODE("21000007");
            Random r = new Random();
            String reqSsn = Constants.MCHNT_ID + DateUtil.formatDate(new Date(), "yyyyMMddHHmmssSSS")
                + String.valueOf(r.nextLong()).substring(1, 8 + 1);
            request.setREQ_SSN(reqSsn);
            request.setMCHNT_ID(Constants.MCHNT_ID);
            request.setFILE_NAME(fileName);
            request.setFILE_TYPE("616");
            request.setTRANS_TYPE("MSG");
            
            //实体对象转换为XML
            String restr = XstreamUtils.toXml(request, request.getClass());
            
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new ByteArrayInputStream(restr.getBytes()));
            Element root = doc.getRootElement();
            
            //--------加签名------
            String sign = SignUtil.sign(Utils.sortSignInfo(root).getBytes());
            request.setSIGN_INFO(sign);
            restr = Constants.XML_HEAD + XstreamUtils.toXml(request, request.getClass());
            
            //---------- 发送请求数据 ------
            //初始化https请求参数
            HttpsPost.initHttpsURLConnection(Constants.PASSWORD, Constants.KEYSTORE_PATH, Constants.TRUSTSTORE_PATH);
            //发送请求获得响应数据
            
            String resStr = HttpsPost.post(Constants.httpsFileUrl, restr, Constants.MCHNT_ID, "21000007");
            //把xml为转换为实体对象
            T21000007Response resData = XstreamUtils.toBean(resStr, T21000007Response.class);
            
            //-------- 验签 --------------
            //获取签名信息
            String sigStr = resData.getDATA().getSIGN_INFO();
            //验签是否成功
            boolean isSucc =
                SignUtil.verifySign(BaseSendMethod.sortSignInfoNoR(resStr).getBytes(), sigStr, Constants.PTNRTESTCER);
            System.out.println("响应信息验签：" + (isSucc ? "验签成功！" : "验签失败，请检查签名！"));
            return resData.getDATA();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
}

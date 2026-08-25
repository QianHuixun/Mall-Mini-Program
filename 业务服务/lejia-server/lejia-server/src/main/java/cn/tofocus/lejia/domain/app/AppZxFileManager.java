package cn.tofocus.lejia.domain.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.lejia.bean.dto.vendor.VendorOrderSettleOnList;
import cn.tofocus.lejia.bean.entity.vendor.MktSettlementTotal;
import cn.tofocus.lejia.bean.entity.zx.ZxFileRecord;
import cn.tofocus.lejia.bean.enums.ZxFileType;
import cn.tofocus.lejia.dao.zx.ZxFileRecordDao;
import cn.tofocus.lejia.util.ZipUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppZxFileManager
{
    /** 根目录*/
    private static String ROOTPATH = "";
    static
    {
        ROOTPATH = System.getProperty("user.dir");
    }
    
    /** 私钥库 密码 */
    String password = "111111";
    
    /** 上传文件路径**/
    private static final String FILE_PATH = ROOTPATH + "/file/";
    
    private String MCHNT_ID = "J00030700000000";
    
    private String QUDAO_NAME = "中信全付通";
    
    private String FUNDS_TYPE = "004001";
    
    @Autowired
    private ZxFileRecordDao zxFileRecordDao;
    
    Random r = new Random();
    
    String httpsUrl = "https://219.142.124.205:30466/dsgj/";
    
    public ZxFileRecord runFile(List<VendorOrderSettleOnList> data, Date time, String settleKey,
        MktSettlementTotal total)
    {
        ZxFileRecord fileRecord = new ZxFileRecord();
        fileRecord.setSettlementKey(settleKey);
        String addFileTime = DateUtil.formatDate(time, "yyyyMMdd");
        StringBuffer sb = new StringBuffer();
        // 优惠 总金额  
        BigDecimal discountAmt = total.getDiscountAmt().setScale(0, BigDecimal.ROUND_HALF_UP);
        Integer discount =
            total.getDiscountAmt().multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        // 清分 总金额
        BigDecimal clearingAmt = total.getClearingAmt().setScale(0, BigDecimal.ROUND_HALF_UP);
        
        for (int i = 0; i < data.size(); i++)
        {
            VendorOrderSettleOnList vo = data.get(i);
            int disAmt = 0;
            if (clearingAmt.compareTo(BigDecimal.ZERO) == 1)
            {
                disAmt = vo.getAmt()
                    .divide(clearingAmt, 2, BigDecimal.ROUND_HALF_UP)
                    .multiply(discountAmt)
                    .multiply(new BigDecimal("100"))
                    .setScale(0, BigDecimal.ROUND_HALF_UP)
                    .intValue();
            }
            if (i != data.size() - 1)
            {
                discount = discount - disAmt;
            }
            if (i == 0)
            {
                runFileContentFirst(sb, vo, time, total, disAmt);
            }
            else
            {
                if (i == data.size() - 1)
                {
                    runFileContent(sb, vo, time, discount);
                }
                else
                    runFileContent(sb, vo, time, disAmt);
                
            }
        }
        
        fileRecord.setContent("文件内容过长 不存");
        String fileName = MCHNT_ID;
        String filelj = FILE_PATH + LocalDate.now() + "/";
        try
        {
            // 渠道编号
            String qudaobianhao = "1001";
            fileName = fileName + qudaobianhao + "001" + addFileTime + "01";
            File f = new File(filelj);
            boolean mkdirs = f.mkdirs();
            log.info("mkdirs: {}", mkdirs);
            FileOutputStream out = new FileOutputStream(filelj + fileName);
            out.write(sb.toString().getBytes("GBK"));
            out.flush();
            out.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        String fileSaveLj = filelj + fileName + ".ZIP";
        File zip = new File(fileSaveLj);
        ZipUtil util = new ZipUtil();
        util.createZip(filelj + fileName, zip);
        
        fileRecord.setName(fileName);
        fileRecord.setSavePath(fileSaveLj);
//        fileRecord.setType(ZxFileType.Z21000008);
        ZxFileRecord record = zxFileRecordDao.add(fileRecord);
        return record;
    }
    
    private StringBuffer runFileContent(StringBuffer sb, VendorOrderSettleOnList settle, Date date, int disAmt)
    {
        // 平台商户编号
        sb.append(MCHNT_ID);
        // 用户编号
        sb.append(settle.getZxUserId());
        // 交易日期 YYYYMMDD  交易时间 HHMMSS
        sb.append(DateUtil.formatDate(date, "yyyyMMddHHmmss"));
        // 平台商户业务订单号  平台商户支付订单号
        String payNum = settle.getCode();
        //            + "settlement" + settle.getSettlementPkey() + "";
        // 支付渠道交易流水号
        String transactionId = settle.getCode();
        // 平台商户业务子订单号
        String childPayNum = settle.getPkey() + "settlement" + settle.getSettlementPkey() + "";
        
        BigDecimal hundred = new BigDecimal("100");
        // 订单支付金额
        Integer amt = settle.getAmt().multiply(hundred).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        log.info("订单支付金额amt: {}", amt);
        log.info("订单支付金额disAmt: {}", disAmt);
        
        // 支付渠道名称   60
        sb.append(String.format("%1$-55s", QUDAO_NAME));
        // 平台商户业务订单号  64
        sb.append(String.format("%1$-64s", payNum));
        // 平台商户支付订单号  64
        sb.append(String.format("%1$-64s", payNum));
        // 支付渠道交易流水号  64
        byte b1[] = {0x03};
        String separator = new String(b1);
        sb.append(String.format("%1$-64s", "421010060000001" + separator + transactionId));
        // 平台商户业务子订单号  64
        sb.append(String.format("%1$-64s", childPayNum));
        // 支付订单交易类型  2 (1-支付 2-退款 空格-其他)
        sb.append("1 ");
        // 业务订单交易类型  2 (1 -实时交易支付  2 -实时交易退货  3 -预付交易支付  4 -预付交易撤销  5 -预付交易完成)
        sb.append("1 ");
        // 清算资金来源 1 (1-支付渠道  2-内部划转)
        sb.append("1");
        // 渠道手续费承担方式 1 (1-平台商户承担  2-用户承担)
        sb.append("1");
        // 原始订单金额  14 
        sb.append(String.format("%014d", amt));
        // 原始支付金额
        sb.append(String.format("%014d", amt - disAmt));
        // 平台优惠金额
        sb.append(String.format("%014d", disAmt));
        // 平台分成金额
        sb.append(String.format("%014d", 0));
        // 平台垫款金额
        sb.append(String.format("%014d", 0));
        // 渠道手续费
        sb.append(String.format("%014d", 0));
        // 资金类型  6
        sb.append(FUNDS_TYPE);
        // 备注 200 
        sb.append(String.format("%1$-200s", ""));
        sb.append("\r\n");
        return sb;
    }
    
    private StringBuffer runFileContentFirst(StringBuffer sb, VendorOrderSettleOnList settle, Date date,
        MktSettlementTotal total, int disAmt)
    {
        // 平台商户编号
        sb.append(MCHNT_ID);
        // 用户编号
        sb.append(settle.getZxUserId());
        // 交易日期 YYYYMMDD  交易时间 HHMMSS
        sb.append(DateUtil.formatDate(date, "yyyyMMddHHmmss"));
        // 平台商户业务订单号  平台商户支付订单号
        String payNum = settle.getCode() + "";
        //            + "settlement" + settle.getSettlementPkey() + "";
        // 支付渠道交易流水号
        String transactionId = settle.getCode();
        // 平台商户业务子订单号
        String childPayNum = settle.getPkey() + "settlement" + settle.getSettlementPkey() + "";
        
        BigDecimal hundred = new BigDecimal("100");
        // 订单支付金额
        Integer amt = settle.getAmt().multiply(hundred).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        // 平台分佣金额
        Integer platformAmt = total.getPlatformAmt().multiply(hundred).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        // 渠道手续费
        Integer qdAmt = total.getHandlingFee().multiply(hundred).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        // 优惠金额 
        Integer discountAmt = total.getDiscountAmt().multiply(hundred).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        
        log.info("订单支付金额amt: {}", amt);
        log.info("渠道手续费qdAmt: {}", qdAmt);
        log.info("平台分佣金额platformAmt: {}", platformAmt);
        log.info("优惠金额discountAmt: {}", discountAmt);
        log.info("优惠金额disAmt: {}", disAmt);
        
        // 支付渠道名称   60
        sb.append(String.format("%1$-55s", QUDAO_NAME));
        // 平台商户业务订单号  64
        sb.append(String.format("%1$-64s", payNum));
        // 平台商户支付订单号  64
        sb.append(String.format("%1$-64s", payNum));
        // 支付渠道交易流水号  64
        byte b1[] = {0x03};
        String separator = new String(b1);
        sb.append(String.format("%1$-64s", "421010060000001" + separator + transactionId));
        // 平台商户业务子订单号  64
        sb.append(String.format("%1$-64s", childPayNum));
        // 支付订单交易类型  2 (1-支付 2-退款 空格-其他)
        sb.append("1 ");
        // 业务订单交易类型  2 (1 -实时交易支付  2 -实时交易退货  3 -预付交易支付  4 -预付交易撤销  5 -预付交易完成)
        sb.append("1 ");
        // 清算资金来源 1 (1-支付渠道  2-内部划转)
        sb.append("1");
        // 渠道手续费承担方式 1 (1-平台商户承担  2-用户承担)
        sb.append("1");
        // 原始订单金额  14 
        sb.append(String.format("%014d", amt + platformAmt));
        // 原始支付金额
        sb.append(String.format("%014d", amt + platformAmt - disAmt));
        // 平台优惠金额
        sb.append(String.format("%014d", disAmt));
        // 平台分成金额:  佣金抽层 + 运费 + 差价
        sb.append(String.format("%014d", platformAmt));
        // 平台垫款金额
        sb.append(String.format("%014d", 0));
        // 渠道手续费
        sb.append(String.format("%014d", qdAmt));
        // 资金类型  6
        sb.append(FUNDS_TYPE);
        // 备注 200 
        sb.append(String.format("%1$-200s", ""));
        sb.append("\r\n");
        return sb;
    }
    
}

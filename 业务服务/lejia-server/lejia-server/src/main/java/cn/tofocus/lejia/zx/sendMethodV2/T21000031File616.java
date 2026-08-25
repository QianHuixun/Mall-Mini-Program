package cn.tofocus.lejia.zx.sendMethodV2;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import cn.tofocus.lejia.zx.utilV2.Constants;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.common.util.file.FileUtil;
import cn.tofocus.lejia.zx.utilV2.Utils;

@Component
public class T21000031File616
{
    // 字段分隔符
    private static final char COL_SEPARATOR = 0x03;
    
    // 行分隔符
    private static final String ROW_SEPARATOR = "\n";
    
    private static final String FILE_PATH = "/data/resources/file/";
    
    private static final String ZIP_PATH = FILE_PATH + "z/";
    
    @SuppressWarnings("resource")
    public static void main(String[] args)
        throws IOException
    {
        FileUtil.checkDirectory(FILE_PATH);
        FileUtil.checkDirectory(ZIP_PATH);

        FileWriter writer;
        LocalDate now = LocalDate.now();
        LocalDate day = now.minusDays(1);
        DateTimeFormatter dtfm = DateTimeFormatter.ofPattern("yyyyMMdd");
        StringBuffer sb = new StringBuffer();
        // 平台商户编号
        sb.append("J04059100000000").append(COL_SEPARATOR);
        // 用户编号
        sb.append("J04059100000002").append(COL_SEPARATOR);
        // 交易日期 YYYYMMDD
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_WEEK, -1);
        sb.append(DateUtil.formatDate(cal.getTime(), "yyyyMMdd")).append(COL_SEPARATOR);
        // 交易时间 HHMMSS
        sb.append(DateUtil.formatDate(cal.getTime(), "HHmmss")).append(COL_SEPARATOR);
        // 支付渠道名称   60
        sb.append("银联商务1033").append(COL_SEPARATOR);
        // 平台商户业务订单号  64
        sb.append("912609215395551").append(COL_SEPARATOR);
        // 平台商户支付订单号  64
        sb.append("912609215395551").append(COL_SEPARATOR);
        // 支付渠道交易流水号  64
        sb.append("J04059100000000" + "|" + "912609215395551").append(COL_SEPARATOR);
        // 平台商户业务子订单号  64
        sb.append("912609215395551").append(COL_SEPARATOR);
        // 支付订单交易类型  2 (1-支付 2-退款 空格-其他)
        sb.append("1").append(COL_SEPARATOR);
        // 业务订单交易类型  2 (1 -实时交易支付  2 -实时交易退货  3 -预付交易支付  4 -预付交易撤销  5 -预付交易完成)
        sb.append("1").append(COL_SEPARATOR);
        // 清算资金来源 1 (1-支付渠道  2-内部划转)
        sb.append("1").append(COL_SEPARATOR);
        // 渠道手续费承担方式 1 (1-平台商户承担  2-用户承担)
        sb.append("1").append(COL_SEPARATOR);
        // 原始订单金额  14
        sb.append("100.00").append(COL_SEPARATOR);
        // 原始支付金额
        sb.append("100.00").append(COL_SEPARATOR);
        // 平台优惠金额
        sb.append("0").append(COL_SEPARATOR);
        // 平台分成金额
        sb.append("10").append(COL_SEPARATOR);
        // 平台垫款金额
        sb.append("0").append(COL_SEPARATOR);
        // 渠道手续费
        sb.append("0").append(COL_SEPARATOR);
        // 资金类型  6
        sb.append(Constants.FUNDS_TYPE).append(COL_SEPARATOR);
        // 备注 200
        sb.append("备注");

        // 一行结束
        sb.append(ROW_SEPARATOR);

        String fileName = "J04059100000000";
        try
        {
            // 渠道编号
            String qudaobianhao = "1033";
            fileName = fileName + qudaobianhao + "616" + day.format(dtfm) + "01";
            FileOutputStream out = new FileOutputStream(FILE_PATH + fileName);
            out.write(sb.toString().getBytes(StandardCharsets.UTF_8));
            out.flush();
            out.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        Utils.fileZip(FILE_PATH + fileName, ZIP_PATH + fileName + ".ZIP");
    }
}

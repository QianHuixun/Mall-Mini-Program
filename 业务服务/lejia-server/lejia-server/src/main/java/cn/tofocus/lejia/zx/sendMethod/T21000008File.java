package cn.tofocus.lejia.zx.sendMethod;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Component;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.lejia.util.ZipUtil;

@Component
public class T21000008File
{
    private static String ROOTPATH = "";
    static
    {
        ROOTPATH = System.getProperty("user.dir");
    }
    
    /** 上传文件路径**/
    private static final String FILE_PATH = ROOTPATH + "/file/";
    
    
    @SuppressWarnings("resource")
    public static void main(String[] args)
        throws IOException
    {
        FileWriter writer;
        LocalDate now = LocalDate.now();
        LocalDate day = now.minusDays(1);
        DateTimeFormatter dtfm = DateTimeFormatter.ofPattern("yyyyMMdd");
        StringBuffer sb = new StringBuffer();
        String kg200 = "                                                                    "
            + "                                                                             "
            + "                                                       ";
        String kg64 = "                                                                ";
        String kg60 = "                                                            ";
        String kg50 = "                                                  ";
        String kg49 = "                                                 ";
        String kg23 = "                       ";
        String kg28 = "                            ";
        String kg013 = "0000000000000";
        // 平台商户编号
        sb.append("J00024000000000");
        // 用户编号
        sb.append("J00024000000002");
        // 交易日期 YYYYMMDD  交易时间 HHMMSS
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_WEEK, -1);
        sb.append(DateUtil.formatDate(cal.getTime(), "yyyyMMddHHmmss"));
        // 支付渠道名称   60
        byte[] b;
//        try
//        {
//            b = "全付通".getBytes("GBK");
//            System.out.println("" + b.toString());
////            String STR = new String("全付通".getBytes("utf-8"), "ANSI");
//            
//            sb.append("1001" + b.toString() + kg50);
//        }
//        catch (Exception e)
//        {
//            System.out.println("sss");
//        }
//        sb.append("1001" + "111" + kg50);
        sb.append("1001" + "全付通" + kg50);
        
        // 平台商户业务订单号  64
        sb.append("912609215395551" + kg49);
        // 平台商户支付订单号  64
        sb.append("912609215395551" + kg49);
        // 支付渠道交易流水号  64
//        String fg = "(0x03)";
//        byte[] b;
//        try
//        {
//            b = fg.getBytes("GBK");
//            int i = b.toString().length();
//            System.out.println("i: " + i);
////            sb.append("J00024000000000" + b.toString() + "912609215395551" + kg23);
//            sb.append("J00024000000000" + b.toString() + "912609215395551" + kg23 + "          ");
//            
//        }
//        catch (Exception e)
//        {
//            // TODO: handle exception
//        }
        sb.append("J00024000000000" + "(0x03)" + "912609215395551" + kg28);
        // 平台商户业务子订单号  64
        sb.append("912609215395551" + kg49);
        // 支付订单交易类型  2 (1-支付 2-退款 空格-其他)
        sb.append("1 ");
        // 业务订单交易类型  2 (1 -实时交易支付  2 -实时交易退货  3 -预付交易支付  4 -预付交易撤销  5 -预付交易完成)
        sb.append("1 ");
        // 清算资金来源 1 (1-支付渠道  2-内部划转)
        sb.append("1");
        // 渠道手续费承担方式 1 (1-平台商户承担  2-用户承担)
        sb.append("1");
        // 原始订单金额  14
        sb.append(kg013 + "1");
        // 原始支付金额
        sb.append(kg013 + "1");
        // 平台优惠金额
        sb.append(kg013 + "0");
        // 平台分成金额
        sb.append(kg013 + "0");
        // 平台垫款金额
        sb.append(kg013 + "0");
        // 渠道手续费
        sb.append(kg013 + "0");
        // 资金类型  6
        sb.append("004001");
        // 备注 200 
        sb.append(kg200);
//        sb.append("\r\n");
        String fileName = "J00024000000000";
//        String filelj = FILE_PATH + LocalDate.now() + "-" + "1122" + "/";
        try
        {
            // 渠道编号
            String qudaobianhao = "1001";
            fileName = fileName + qudaobianhao + "001" + day.format(dtfm) + "01";
//            new FileWriter(
//                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));)
//                {
//                    String str = null;
//                    while ((str = reader.readLine()) != null)
//                    {
//                        fileContent.append(str);
//                    }
//                }
            FileOutputStream out = new FileOutputStream("src/main/resources/file/" + fileName);
            out.write(sb.toString().getBytes("GBK"));
            out.flush();
            out.close();
            
//            writer = new FileWriter("src/main/resources/file/" + fileName);
//            writer.write("");//清空原文件内容
//            writer.write(sb.toString());
//            writer.flush();
//            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
//        File zip = new File(filelj + fileName + ".ZIP");
        File zip = new File("src/main/resources/file/z/" + fileName + ".ZIP");
        ZipUtil util = new ZipUtil();
        util.createZip("src/main/resources/file/" + fileName, zip);
        
        
        
        
    }
}

package cn.tofocus.lejia.util;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import sun.misc.BASE64Encoder;


public class QRCodeTool
{
    /**
     * 颜色
     */
    private static final int QRCOLOR = 0xFF000000;
    
    /**
     * 背景颜色
     */
    private static final int BGWHITE = 0xFFFFFFFF;
    
    public static void main(String[] args) throws Exception
    {
        Vector<String> v = new Vector();
        v.add("蒋玉英,1,UMS331001554169,9OWJHWQ7,50355,EC9345AD33D0EEFC,https://qr.95516.com/48020000/10X12011108771942157391739");
        v.add("陈小辉,2,UMS331001554217,FNI1EBQN,50356,CF262521C7BAFCF5,https://qr.95516.com/48020000/10X12011101981943152081231");
        v.add("郑丽霞,5,UMS331001549031,KYTET3CH,50359,D68173E421BF80F1,https://qr.95516.com/48020000/10X12011103881943155127409");
        v.add("许云祥,6,UMS331001551437,7ID02GCC,50360,C4C0B1C402702ED9,https://qr.95516.com/48020000/10X12011105641943157963658");
        v.add("李珍兰,11,UMS331001551507,RZS7AG9Q,50365,BA52421E4EA0436B,https://qr.95516.com/48020000/10X12011107001943153617773");
        v.add("林云娥,13,UMS331001554228,KGSQ3S4F,50367,1B10EAA3BC9DA88A,https://qr.95516.com/48020000/10X12011108911943154321914");
        v.add("项光琴,15, UMS331001556101,KNOK1788,50369,B1E855622A319664,https://qr.95516.com/48020000/10X12011100181944159273378");
        v.add("周玲琴,16, UMS331001556147,RHNQ2ZO4,50370,09EE3C9C51D7F87C,https://qr.95516.com/48020000/10X12011100971944152386237");
        v.add("金福连,22, UMS331001551496,D08AK8MO,50376,B5C3A83900A1C96E,https://qr.95516.com/48020000/10X12011102201944151607666");
        v.add("李小燕,23,UMS331001549291,6QUP7Z40,50377,44C7595F5E899E39,https://qr.95516.com/48020000/10X12011103181944151142160");
        v.add("应富青,24,UMS331001554738,VM6LQVW1,50378,86DF5390723B7528,https://qr.95516.com/48020000/10X12011104221944159805475");
        v.add("吴华芬,30, UMS331001554308,3E3A2SQ4,50384,DF8116E43AA1E416,https://qr.95516.com/48020000/10X12011105201944156254646");
        v.add("江小燕,34, UMS331001551434,3QDIB7UX,50389,F46F728EBBC5B6D3,https://qr.95516.com/48020000/10X12011106031944152581168");
        v.add("王雪云,38,UMS331001554318,A0143BTJ,50393,239C25AACA15BBB9,https://qr.95516.com/48020000/10X12011107311944154600667");
        v.add("王美青,40,UMS331001556219,03EYYQF3,50395,68771DBA090C87DE,https://qr.95516.com/48020000/10X12011108131944158542467");
        v.add("陈君明,41,UMS331001554367,D7S0Y462,50396,6791BA42160E7BB2,https://qr.95516.com/48020000/10X12011109111944151041636");
        v.add("林海波,42, UMS331001554392,ACVRRCB5,50397,2D8D65BFCCCE00D0,https://qr.95516.com/48020000/10X12011100011945153458221");
        v.add("张锦良,43,UMS331001551449,EMHSLNNO,50398,376653EAF0D5453A,https://qr.95516.com/48020000/10X12011100901945159769286");
        v.add("金小平,45,UMS331001554567,KNBKU0K3,50400,76EABA4556790CA1,https://qr.95516.com/48020000/10X12011101771945156614021");
        v.add("林云芬,52, UMS331001551474,M664BRBY,50407,E5189F5C74E2B76A,https://qr.95516.com/48020000/10X12011102621945158894940");
        v.add("孙领芽,54,UMS331001551461,INQTKR8G,50409,1471B290A230A110,https://qr.95516.com/48020000/10X12011103261945150042163");
        v.add("朱云香,57,UMS331001556240,M1Z5X154,50412,405A0676A24C1791,https://qr.95516.com/48020000/10X12011104561945154602360");
        v.add("江志琴,59,UMS331001551489,HVI0NSAC,50414,123C30E2632D88F4,https://qr.95516.com/48020000/10X12011105651945159024838");
        v.add("叶国方,60,UMS331001556297,7NUU13XL,50415,CDA1DCB2294B03E3,https://qr.95516.com/48020000/10X12011106521945157188076");
        v.add("张雨林,63,UMS331001554434,K204HVNZ,50418,DDDED837337A62D9,https://qr.95516.com/48020000/10X12011107351945159092930");
        v.add("颜营君,71,UMS331001551638,UDEK9N8W,50426,A37F482FF1EC1D74,https://qr.95516.com/48020000/10X12011108201945155569596");
        v.add("王美娟,75,UMS331001556338,05G0G9PP,50430,4124ACC16EF1DE2B,https://qr.95516.com/48020000/10X12011109021945156616813");
        v.add("周玲琴,76,UMS331001556147,RHNQ2ZO4,50431,1B8C83E76FFFDF9F,https://qr.95516.com/48020000/10X12011109731945152076781");
        v.add("赵妹芬,84/88, UMS331001551642,LE5F6RJX,50440,9594846BBF99306F,https://qr.95516.com/48020000/10X12011100771946159230081");
        v.add("贝雪方,91/92/93, UMS331001551471,K9E37CXT,50447,97CBF6E3CF3B5EAA,https://qr.95516.com/48020000/10X12011101791946156918218");
        v.add("莫恩喜,94,UMS331001551431,ZYHNUDRZ,50450,1C3907D9AB44F49B,https://qr.95516.com/48020000/10X12011103061946151739507");
        v.add("王修德,96,UMS331001551400,QR0K9L05,50452,D811B8E0C614D783,https://qr.95516.com/48020000/10X12011104131946156480158");
        v.add("莫丽芬,99,UMS331001549118,9HQXHYMV,50455,50074237FFA35654,https://qr.95516.com/48020000/10X12011105391946157482316");
        v.add("范张玲,100,UMS331001549099,KSU166QO,50456,DB153D902236CBBE,https://qr.95516.com/48020000/10X12011106331946158787298");
        v.add("王子林,101,UMS331001549062,JO2R5KHG,50457,CF3E516B2DFF4591,https://qr.95516.com/48020000/10X12011107421946150466679");
        v.add("周伟薇,102,UMS331001549146,MIK8NND2,50458,3F4F49DB8F6838DD,https://qr.95516.com/48020000/10X12011108441946158013952");
        v.add("莫美夫,103,UMS331001551733,2M8LCT48,50459,BF5C4C98F9F0264A,https://qr.95516.com/48020000/10X12011109521946150961393");
        v.add("王菊明,104, UMS331001556060,24B4W2E2,50460,CE796BE4D855FEC9,https://qr.95516.com/48020000/10X12011100451947150840679");
        v.add("黄彩娟,106,UMS331001554507,N776YSXU,50462,961D4C368E7C0706,https://qr.95516.com/48020000/10X12011101291947154392791");
        v.add("李金兰,107,UMS331001551462,39D1LNGC,50463,2794B93628A15900,https://qr.95516.com/48020000/10X12011102161947152334901");
        v.add("蒋雪兵,108,UMS331001549374,Y1TPG40U,50464,497946D01BF92940,https://qr.95516.com/48020000/10X12011103031947158659557");
        v.add("杨文献,109, UMS331001549052,MZYAFB30,50465,CBC5B7958DA70D63,https://qr.95516.com/48020000/10X12011104211947159099650");
        v.add("蒋雪英,110,UMS331001549119,V01ZQX1O,50466,E488BC986C29EFDB,https://qr.95516.com/48020000/10X12011105041947154690661");
        v.add("黄正德,111, UMS331001551435,6E4GY8GT,50467,2F49219A7520EBFA,https://qr.95516.com/48020000/10X12011106011947153331770");
        v.add("韩妹芬,114,UMS331001551478,7YUXHY7R,50470,1DE7D747459BC157,https://qr.95516.com/48020000/10X12011106961947150256918");
        v.add("颜新华,115,UMS331001551503,8M38Q057,50471,CEA2F3FAABD779EB,https://qr.95516.com/48020000/10X12011107701947155518988");
        v.add("蒋雪英,118, UMS331001549119,V01ZQX1O,50474,7DEDB10EFB5F51C8,https://qr.95516.com/48020000/10X12011108411947151345001");
        v.add("黄军正,119, UMS331001551495,S6AN3G7W,50475,F4E814FCCFEE4941,https://qr.95516.com/48020000/10X12011109201947152687729");
        v.add("周福云,121, UMS331001551828,ABFL2LV0,50477,A71CFAB6E3575918,https://qr.95516.com/48020000/10X12011100531948158505705");
        v.add("卢艳南,123/124, UMS331001549081,GNJDWG3I,50479,E4EB69FEDBBBC868,https://qr.95516.com/48020000/10X12011101601948154419941");
        v.add("朱群辉,127,UMS331001551516,TRUIFB2K,50483,85D720962E404FF1,https://qr.95516.com/48020000/10X12011102401948152791728");
        v.add("颜安佩,128,UMS331001551524 ,BB53LQYK,50484,9D7B8504DCDB6FB8,https://qr.95516.com/48020000/10X12011103031948151714418");
        v.add("程云华,130,UMS331001551731,0GP406CQ,50486,467070C7F81AFA86,https://qr.95516.com/48020000/10X12011103971948159477333");
        v.add("卢艳南,131/132, UMS331001549081,GNJDWG3I,50487,57733796864DF111,https://qr.95516.com/48020000/10X12011104701948150894402");
        v.add("陈纪夏,133,UMS331001551632,U9B427I0,50489,BBEE16D8E4BDD8E8,https://qr.95516.com/48020000/10X12011105481948153034400");
        v.add("陈林波,134,UMS331001554454,O4VKE4P9,50490,612F253A428515BE,https://qr.95516.com/48020000/10X12011106371948159945180");
        v.add("朱群辉,135,UMS331001551516,TRUIFB2K,50491,59FFAED3C2F0D812,https://qr.95516.com/48020000/10X12011107711948155313223");
        v.add("陈菊素,136,UMS331001554412,WQRXXBSM,50492,75D46B68BF5D37DE,https://qr.95516.com/48020000/10X12011108941948151903883");
        v.add("陈文忠,137,UMS331001549067,UBB3OLXQ,50493,A59AD389AB9BD858,https://qr.95516.com/48020000/10X12011109821948159355091");
        v.add("罗伟军,138,UMS331001551416,821UWWM4,50494,5C6584065F138316,https://qr.95516.com/48020000/10X12011100651949157033782");
        v.add("王冬富,146,UMS331001549071,VFJ1AX2A,50502,AEAB21CB0F56B273,https://qr.95516.com/48020000/10X12011101591949154052634");
        v.add("林素琴,147,UMS331001551509,8FKN78N8,50503,5C12D80C8575B23B,https://qr.95516.com/48020000/10X12011102561949153072594");
        v.add("吕敏华,148,UMS331001549050,X4P2Q20J,50504,82294115359538E2,https://qr.95516.com/48020000/10X12011103531949158157941");
        v.add("朱良,154, UMS331001555981,OS5NR67R,50510,B503201AD7F1FDD7,https://qr.95516.com/48020000/10X12011104541949154678733");
        v.add("郑素君,157/158,UMS331001551560 ,HRH9VXAS,50513,C3BB93797B3A19CF,https://qr.95516.com/48020000/10X12011105331949151998614");
        v.add("钟米娟,161, UMS331001556191,QYVRZH00,50517,33759B04566993F9,https://qr.95516.com/48020000/10X12011106281949158631146");
        v.add("陈巧,162, UMS331001554801,U5GCRB6F,50518,2AE81B061ED2D8D3,https://qr.95516.com/48020000/10X12011107991949155207387");
        v.add("张云花,163,UMS331001556177,K7IPCY87,50519,049EC76EAEDE1FFB,https://qr.95516.com/48020000/10X12011109291949154087744");
        v.add("陆春凤,164,UMS331001554879,V229WEE0,50520,3BB6F8431FF22255,https://qr.95516.com/48020000/10X12011100561950156684109");
        v.add("施达仁,165,UMS331001556313,DCCDN17S,50521,B5D94DCF7E0F8548,https://qr.95516.com/48020000/10X12011101571950157635349");
        v.add("郭招红,166,UMS331001556238,SJT7YGNU,50522,C77689CCB4E6145E,https://qr.95516.com/48020000/10X12011102691950152648435");
        v.add("王福英,167,UMS331001556384,K6W6X0C4,50523,289D236AAE660637,https://qr.95516.com/48020000/10X12011103631950159397189");
        v.add("潘红,169,UMS331001554760,IE5CJHPL,50525,43299DD941D90742,https://qr.95516.com/48020000/10X12011104971950157529447");
        v.add("徐素娇,171,UMS331001556224,H71KGWXD,50527,4131532A410A5F59,https://qr.95516.com/48020000/10X12011105991950155874849");
        v.add("吴正兆,175,UMS331001556404,QXCXKIY9,50531,93DF65208D4FA111,https://qr.95516.com/48020000/10X12011106791950150644029");
        v.add("朱萍,182,UMS331001533759,5QPANSFD,50538,DC6E22BC8D71B2E1,https://qr.95516.com/48020000/10X12011107741950159052056");
        v.add("连超云,183/184,UMS331001556167,1LMBF7VS,50539,C235CFF13970E838,https://qr.95516.com/48020000/10X12011108701950151897351");
        v.add("林小君,185, UMS331001551555,O4LEGENU,50541,BEF5740F1525A5B4,https://qr.95516.com/48020000/10X12011109471950158549884");
        v.add("孙春领,186, UMS331001556142,BBU5KG4G,50542,FE06891631896DAB,https://qr.95516.com/48020000/10X12011100261951158609508");
        v.add("覃寿艳,187,UMS331001551561,870IB45T,50543,A4D40E01E41050DD,https://qr.95516.com/48020000/10X12011101061951154857436");
        v.add("程素娟,189,UMS331001556115,WVEXCBGL,50545,01161984F005773A,https://qr.95516.com/48020000/10X12011101981951155849467");
        v.add("林云丽,190,UMS331001551529,OQ1WM5T7,50546,3BF6C50AD9D8C61A,https://qr.95516.com/48020000/10X12011102861951159765639");
        v.add("乔付伟,191/192, UMS331001556075,DKC03BA1,50547,02C20318B4B21E90,https://qr.95516.com/48020000/10X12011103631951159926299");
        v.add("颜建江,193,UMS331001556377,4QT4KLVR,50549,BAE41ED68EF5B090,https://qr.95516.com/48020000/10X12011104561951159228420");
        v.add("郑菊花,195,UMS331001556363,YFNSKYKR,50551,5CAEE4926BDF6FA5,https://qr.95516.com/48020000/10X12011105951951152710527");
        v.add("林海丽,198/199,UMS331001549380,9743ZNOF,50554,73BC3F7A4A32AAD6,https://qr.95516.com/48020000/10X12011106791951157042813");
        v.add("陈秀莲,202,UMS331001554560,IQ0PLPAS,50558,05F5142E41522DFE,https://qr.95516.com/48020000/10X12011107611951154028187");
        v.add("江灵利,203, UMS331001554586,0PLW4NO3,50559,948C0ACA74E48656,https://qr.95516.com/48020000/10X12011108821951157899312");
        v.add("陈桔香,204,UMS331001554611,DBDNQSSG,50560,DBD0B6374B705A1E,https://qr.95516.com/48020000/10X12011109621951150115860");
        v.add("陈云香,205,UMS331001549167,VDJ8A09G,50561,ECAFE6ED1040BDDF,https://qr.95516.com/48020000/10X12011100691952153844825");
        v.add("郑招弟,207,UMS331001551513,O01WW8TZ,50563,CE563409E7B4F677,https://qr.95516.com/48020000/10X12011101541952152722830");
        v.add("林贤球,208,UMS331001549200,C3C61L2S,50564,C9724EECC56D8177,https://qr.95516.com/48020000/10X12011102421952153999275");
        
        String path = "E:\\yx\\qrcode\\";
        for (String line : v)
        {
            String[] strs = line.split("\\,");
            String filename = strs[0].trim()+"_"+strs[2]+"_"+strs[1].trim().replaceAll("/", "_");
            //File employeeQRFile = new File(path + filename + ".jpg")
//            QRCodeTool.getqrcode(strs[6], path, filename);
        }
        
        System.out.println("OVER");
    }
    
//    public static void getqrcode(String content, String path, String filename)
//        throws Exception
//    {
//        String text = content;
//        int width = 500;
//        int height = 500;
//        //二维码的图片格式 
//        String format = "jpg";
//        Hashtable hints = new Hashtable();
//        //内容所使用编码 
//        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
//        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);
//        //生成二维码 
//        //  System.out.println("路径："+path+"/"+filename+".jpg");
//        File outputFile = new File(path + "/" + filename + ".jpg");
//        MatrixToImageWriter.writeToFile(bitMatrix, format, outputFile);
//    }
    
    public static BufferedImage getQrcodeImg(String content, String path, String filename)
        throws Exception
    {
        int width = 500;
        int height = 500;
        MultiFormatWriter multiFormatWriter = null;
        BitMatrix bm = null;
        BufferedImage image = null;
        try
        {
            multiFormatWriter = new MultiFormatWriter();
            bm = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, width, height, getDecodeHintType());
            int w = bm.getWidth();
            int h = bm.getHeight();
            image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < w; x++)
            {
                for (int y = 0; y < h; y++)
                {
                    image.setRGB(x, y, bm.get(x, y) ? QRCOLOR : BGWHITE);
                }
            }
        }
        catch (WriterException e)
        {
            e.printStackTrace();
        }
        
        return image;
    }
    
//    public static String getqrcode(String content)
//        throws Exception
//    {
//        String text = content;
//        int width = 500;
//        int height = 500;
//        //二维码的图片格式 
//        String format = "jpg";
//        Hashtable hints = new Hashtable();
//        //内容所使用编码 
//        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
//        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);
//        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
//        ByteArrayOutputStream os = new ByteArrayOutputStream();//新建流。
//        ImageIO.write(image, format, os);//利用ImageIO类提供的write方法，将bi以png图片的数据模式写入流。
//        byte b[] = os.toByteArray();//从流中获取数据数组。
//        return new BASE64Encoder().encode(b);
//    }
    
    /**
     * 设置二维码属性
     *
     * @return
     */
    public static Map<EncodeHintType, Object> getDecodeHintType()
    {
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, 0);
        hints.put(EncodeHintType.MAX_SIZE, 500);
        hints.put(EncodeHintType.MIN_SIZE, 100);
        return hints;
    }
    
    /**
     * @param str
     *            生产的图片文字
     * @param oldPath
     *            原图片保存路径
     * @param newPath
     *            新图片保存路径
     * @param width
     *            定义生成图片宽度
     * @param height
     *            定义生成图片高度
     * @return
     * @throws IOException
     */
    public static void create(String str, String oldPath, String newPath, int width, int height)
    {
        try
        {
            File oldFile = new File(oldPath);
            Image image = ImageIO.read(oldFile);
            
            File file = new File(newPath);
            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = bi.createGraphics();
            g2.setBackground(Color.WHITE);
            g2.clearRect(0, 0, width, height);
            g2.drawImage(image, 0, 0, width - 25, height - 25, null); //这里减去25是为了防止字和图重合
            /** 设置生成图片的文字样式 * */
            Font font = new Font("黑体", Font.BOLD, 25);
            g2.setFont(font);
            g2.setPaint(Color.BLACK);
            
            /** 设置字体在图片中的位置 在这里是居中* */
            FontRenderContext context = g2.getFontRenderContext();
            Rectangle2D bounds = font.getStringBounds(str, context);
            double x = (width - bounds.getWidth()) / 2;
            //double y = (height - bounds.getHeight()) / 2; //Y轴居中
            double y = (height - bounds.getHeight());
            double ascent = -bounds.getY();
            double baseY = y + ascent;
            
            /** 防止生成的文字带有锯齿 * */
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            /** 在图片上生成文字 * */
            g2.drawString(str, (int)x, (int)baseY);
            
            ImageIO.write(bi, "jpg", file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    
    public static void deletefile(File file) {
        // 考虑一种情况：如果单独删除一个文件，可以用file.delete
        // 如果需要删除文件夹还需要预先将文件夹中的内容删除掉才可以对文件夹进行删除
        // 删除一个文件下的所有项目只需要一个判断加循环加迭代
        // 删除文件夹中的所有文件只需要在上面过程中去掉myfile.delete即可。
        // 若要连根文件一并删除只需要在main方法中添加一个file.delete即可。
        if (file.isDirectory()) {
            // 是目录，进入，再判断
            File[] fs = file.listFiles();
            for (int i = 0; i < fs.length; i++) {
                File myfile = fs[i];
                QRCodeTool.deletefile(myfile);
                myfile.delete();
            }
            // file.delete();
        } else {
            file.delete();
        }
    } 
    
    
}

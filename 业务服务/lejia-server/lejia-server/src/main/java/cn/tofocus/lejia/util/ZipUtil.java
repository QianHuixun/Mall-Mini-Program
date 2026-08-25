package cn.tofocus.lejia.util;


import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

public class ZipUtil
{
    
//    public static void main(String[] args)
//    {
//        try
//        {
//            File file = new File("D:/uploads/1");
//            if (!file.exists() && !(file.isDirectory()))
//            {
//                file.mkdirs();
//            }
//            QRCodeTool.getqrcode("http://www.baidu.com", "D:/uploads/1", "1");
//            QRCodeTool.getqrcode("http://www.ifeng.com/?_zbs_firefox_gg", "D:/uploads/1", "2");
//            File zip = new File("D://uploads/1.zip");
//            ZipUtil util = new ZipUtil();
//            util.createZip("D:/uploads/1/", zip);
//            System.out.println("zip----");
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
    
    /**
     * 复制文件
     * 
     * @param src
     * @param des
     * @throws Exception
     */
    //	public static void fileCopy(String src, String des) throws Exception {
    //		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src));
    //		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(des));
    //		int i = -1;
    //		byte[] bt = new byte[2048];
    //		while ((i = bis.read(bt)) != -1) {
    //			bos.write(bt, 0, i);
    //		}
    //		bis.close();
    //		bos.close();
    //	}
    
    /**
     * 压缩打包
     * 
     * @param sourcePath 压缩文件路径
     * @param zipFile 压缩包路径
     */
    public void createZip(String sourcePath, File zipFile)
    {
        ZipOutputStream zos = null;
        try
        {
            zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile), 1024 * 10));
            writeZip(new File(sourcePath), "", zos);
        }
        catch (FileNotFoundException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            try
            {
                if (zos != null)
                {
                    zos.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                //throw new RuntimeException(e);
            }
            
        }
    }
    
    private void writeZip(File file, String parentPath, ZipOutputStream zos)
    {
        if (file.exists())
        {
            if (file.isDirectory())
            {
                parentPath += file.getName() + File.separator;
                File[] files = file.listFiles();
                for (File f : files)
                {
                    writeZip(f, parentPath, zos);
                }
            }
            else
            {
                DataInputStream dis = null;
                try
                {
                    dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
                    ZipEntry ze = new ZipEntry(parentPath + file.getName());
                    zos.putNextEntry(ze);
                    byte[] content = new byte[1024 * 10];
                    int len;
                    while ((len = dis.read(content)) != -1)
                    {
                        zos.write(content, 0, len);
                        zos.flush();
                    }
                    
                    zos.closeEntry();
                }
                catch (FileNotFoundException e)
                {
                    throw new RuntimeException(e);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
                finally
                {
                    try
                    {
                        if (dis != null)
                        {
                            dis.close();
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                        //throw new RuntimeException(e);
                    }
                }
            }
        }
    }
    
    //	public void saveFile(File file, String path) throws IOException{
    //	    
    //		FileInputStream fi = new FileInputStream(file);
    //		FileOutputStream fo = new FileOutputStream(path);//声明并创建FileOutputStream类型的对象，初始化对象
    //		byte [] b = new byte[fi.available()];//创建byte类型的数组
    //		 fi.read(b);//将图形文件读入b数组
    //	     fo.write(b);//将b数组的数据写入新文件'copyScenery.jpg'
    //	     fi.close();
    //	     fo.close();    	
    //	}
    
    /*public static void main(String[] args) throws IOException {
    	transformer("d://a.jpg","d://b.jpg",200);
    }
    */
    
    /**
     * 图片压缩
     * @param srcImage  源图片文件路径        （如：srcImage="G:/32/2015101713.jpg"）
     * @param tarImage  目的图片文件路径    （如：tarImage="G:/32/2015101713_720_720.jpg"）
     * @param maxPixel  转换的像素                 （如：maxPixel=720）
     * @param
     * @throws IOException 
     */
    public static void transformer(String srcImage, String tarImage, int maxPixel)
        throws IOException
    {
        //源图片文件
        File srcImageFile = new File(srcImage);
        //目的图片文件
        File tarImageFile = new File(tarImage);
        // 生成图片转化对象
        AffineTransform transform = new AffineTransform();
        // 通过缓存读入缓存对象
        BufferedImage image = null;
        //  try {
        image = ImageIO.read(srcImageFile);
        // } catch (IOException e) {
        //    e.printStackTrace();
        //}
        int imageWidth = image.getWidth();//原图片的高度
        int imageHeight = image.getHeight();//原图片的宽度
        int changeWidth = 0;//压缩后图片的高度
        int changeHeight = 0;//压缩后图片的宽度
        double scale = 0;// 定义小图片和原图片比例
        if (maxPixel != 0)
        {
            if (imageWidth > imageHeight)
            {
                changeWidth = maxPixel;
                scale = (double)changeWidth / (double)imageWidth;
                changeHeight = (int)(imageHeight * scale);
            }
            else
            {
                changeHeight = maxPixel;
                scale = (double)changeHeight / (double)imageHeight;
                changeWidth = (int)(imageWidth * scale);
            }
        }
        // 生成转换比例
        transform.setToScale(scale, scale);
        // 生成转换操作对象
        AffineTransformOp transOp = new AffineTransformOp(transform, null);
        //生成压缩图片缓冲对象
        BufferedImage basll = new BufferedImage(changeWidth, changeHeight, BufferedImage.TYPE_3BYTE_BGR);
        //生成缩小图片
        transOp.filter(image, basll);
        try
        {
            //输出缩小图片
            ImageIO.write(basll, "jpeg", tarImageFile);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

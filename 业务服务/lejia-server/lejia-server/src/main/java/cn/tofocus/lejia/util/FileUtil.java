package cn.tofocus.lejia.util;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class FileUtil {
	private final static Logger log = LoggerFactory.getLogger(FileUtil.class);

	/**
	 * 浏览器下载excel
	 *
	 * @param fileName
	 * @param wb
	 * @param response
	 */

	public static void buildExcelDocument(String ljName, String fileName, String path, HttpServletRequest request,
			HttpServletResponse response) {
		FileInputStream in = null;
		OutputStream out = null;
		File file = new File(path + "/" + ljName);
		try {
			String agent = request.getHeader("user-agent");
			if (agent.contains("FireFox")) {
				fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
			} else {
				fileName = URLEncoder.encode(fileName, "UTF-8");
			}
			String mineType = request.getServletContext().getMimeType(fileName);
			response.setContentType(mineType);
			response.setHeader("Content-disposition", "attachment; filename=" + fileName);
			out = response.getOutputStream();
			in = new FileInputStream(file);
			int len = 0;
			byte[] buffer = new byte[1024];
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
			out.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void buildExcelDocument(String fileName, BufferedImage img, HttpServletRequest request,
			HttpServletResponse response) {
		FileInputStream in = null;
		ByteArrayOutputStream out = null;

		try {
			out = new ByteArrayOutputStream();
			ImageIO.write(img, "png", out);
			byte[] b = out.toByteArray();
			InputStream fis = new ByteArrayInputStream(b);
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();
			response.reset();
			// 设置response的Header
			fileName += ".png";
			response.addHeader("Content-Disposition",
					"attachment;filename=" + new String(fileName.getBytes("utf-8"), "ISO-8859-1"));
			response.addHeader("Content-Length", "" + b.length);
			OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/octet-stream");
			toClient.write(buffer);
			toClient.flush();
			toClient.close();
		} catch (IOException e) {
			log.error(e.getMessage());
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static BufferedImage createImage(String content, int qrcode_width, int qrcode_height) throws Exception {
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		hints.put(EncodeHintType.MARGIN, 1);
		BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, qrcode_width,
				qrcode_height, hints);
		int width = bitMatrix.getWidth();
		int height = bitMatrix.getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
			}
		}
		return image;
	}

//    public static void downloadBatchByFile(HttpServletResponse response, Map<String, byte[]> files, String zipName){
//        try{
//            response.reset();
//            zipName = java.net.URLEncoder.encode(zipName, "UTF-8");
//            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
//            response.setHeader("Content-Disposition", "attachment;filename=" + zipName + ".zip");
//            
//            ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
//            BufferedOutputStream bos = new BufferedOutputStream(zos);
//            
//            for(Entry<String, byte[]> entry : files.entrySet()){
//                String fileName = entry.getKey();            //每个zip文件名
//                byte[]    file = entry.getValue();            //这个zip文件的字节
//                
//                BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(file));
//                zos.putNextEntry(new ZipEntry(fileName));
//                
//                int len = 0;
//                byte[] buf = new byte[10 * 1024];
//                while( (len=bis.read(buf, 0, buf.length)) != -1){
//                    bos.write(buf, 0, len);
//                }
//                bis.close();
//                bos.flush();
//            }
//            bos.close();
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//    }
//    
//    public static void zipFile(File inputFile, ZipOutputStream ouputStream) {
//		try {
//			if (inputFile.exists()) {
//				/**
//				 * 如果是目录的话这里是不采取操作的，  * 至于目录的打包正在研究中
//				 */
//				if (inputFile.isFile()) {
//					FileInputStream IN = new FileInputStream(inputFile);
//					BufferedInputStream bins = new BufferedInputStream(IN, 512);
//					// org.apache.tools.zip.ZipEntry
//					ZipEntry entry = new ZipEntry(inputFile.getName());
//					ouputStream.putNextEntry(entry);
//					// 向压缩文件中输出数据  
//					int nNumber;
//					byte[] buffer = new byte[512];
//					while ((nNumber = bins.read(buffer)) != -1) {
//						ouputStream.write(buffer, 0, nNumber);
//					}
//					// 关闭创建的流对象  
//					bins.close();
//					IN.close();
//				} else {
//					try {
//						File[] files = inputFile.listFiles();
//						for (int i = 0; i < files.length; i++) {
//							zipFile(files[i], ouputStream);
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
// 
//	}

	public static HttpServletResponse downLoadFiles(List<File> files, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			/**
			 * 这个集合就是你想要打包的所有文件， 这里假设已经准备好了所要打包的文件
			 */

			// List<File> files = new ArrayList<File>();

			/**
			 * 创建一个临时压缩文件， 我们会把文件流全部注入到这个文件中 这里的文件你可以自定义是.rar还是.zip 这里的file路径发布到生产环境时可以改为
			 */
			File file = new File(request.getSession().getServletContext().getRealPath("/qrcode.zip"));
			if (!file.exists()) {
				file.createNewFile();
			}
			response.reset();
			// response.getWriter()
			// 创建文件输出流
			FileOutputStream fous = new FileOutputStream(file);
			/**
			 * 打包的方法我们会用到ZipOutputStream这样一个输出流, 所以这里我们把输出流转换一下
			 */
			// org.apache.tools.zip.ZipOutputStream zipOut
			// = new org.apache.tools.zip.ZipOutputStream(fous);
			ZipOutputStream zipOut = new ZipOutputStream(fous);
			/**
			 * 这个方法接受的就是一个所要打包文件的集合， 还有一个ZipOutputStream
			 */
			zipFile(files, zipOut);
			zipOut.close();
			fous.close();
			return downloadZip(file, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		/**
		 * 直到文件的打包已经成功了， 文件的打包过程被我封装在FileUtil.zipFile这个静态方法中， 稍后会呈现出来，接下来的就是往客户端写数据了
		 */
		// OutputStream out = response.getOutputStream();

		return response;
	}
	
	public static HttpServletResponse downLoadFilesV2(List<File> files, String parentPath, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	    try {
	        /**
	         * 这个集合就是你想要打包的所有文件， 这里假设已经准备好了所要打包的文件
	         */
	        
	        // List<File> files = new ArrayList<File>();
	        
	        /**
	         * 创建一个临时压缩文件， 我们会把文件流全部注入到这个文件中 这里的文件你可以自定义是.rar还是.zip 这里的file路径发布到生产环境时可以改为
	         */
	        File file = new File(request.getSession().getServletContext().getRealPath("/qrcode.zip"));
	        if (!file.exists()) {
	            file.createNewFile();
	        }
	        response.reset();
	        // response.getWriter()
	        // 创建文件输出流
	        FileOutputStream fous = new FileOutputStream(file);
	        /**
	         * 打包的方法我们会用到ZipOutputStream这样一个输出流, 所以这里我们把输出流转换一下
	         */
	        // org.apache.tools.zip.ZipOutputStream zipOut
	        // = new org.apache.tools.zip.ZipOutputStream(fous);
	        ZipOutputStream zipOut = new ZipOutputStream(fous);
	        /**
	         * 这个方法接受的就是一个所要打包文件的集合， 还有一个ZipOutputStream
	         */
	        zipFileV2(files, parentPath, zipOut);
	        zipOut.close();
	        fous.close();
	        return downloadZip(file, response);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    /**
	     * 直到文件的打包已经成功了， 文件的打包过程被我封装在FileUtil.zipFile这个静态方法中， 稍后会呈现出来，接下来的就是往客户端写数据了
	     */
	    // OutputStream out = response.getOutputStream();
	    
	    return response;
	}

	/**
	 * 把接受的全部文件打成压缩包
	 * 
	 * @param List<File>;
	 * @param org.apache.tools.zip.ZipOutputStream
	 */
	public static void zipFile(List<File> files, ZipOutputStream outputStream) {
		int size = files.size();
		for (int i = 0; i < size; i++) {
			File file = (File) files.get(i);
			zipFile(file, outputStream);
		}
	}
	public static void zipFileV2(List<File> files, String parentPath, ZipOutputStream outputStream) {
	    int size = files.size();
	    for (int i = 0; i < size; i++) {
	        File file = (File) files.get(i);
	        zipFileV2(file, parentPath, outputStream);
	    }
	}

	public static HttpServletResponse downloadZip(File file, HttpServletResponse response) {
		try {
			// 以流的形式下载文件。
			InputStream fis = new BufferedInputStream(new FileInputStream(file.getPath()));
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();
			// 清空response
			response.reset();

			OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;filename=" + file.getName());
			toClient.write(buffer);
			toClient.flush();
			toClient.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				File f = new File(file.getPath());
				f.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	/**
	 * 根据输入的文件与输出流对文件进行打包
	 * 
	 * @param File
	 * @param org.apache.tools.zip.ZipOutputStream
	 */
	public static void zipFile(File inputFile, ZipOutputStream ouputStream) {
		try {
			if (inputFile.exists()) {
				/**
				 * 如果是目录的话这里是不采取操作的， 至于目录的打包正在研究中
				 */
				if (inputFile.isFile()) {
				    FileInputStream IN = new FileInputStream(inputFile);
				    BufferedInputStream bins = new BufferedInputStream(IN, 512);
				    // org.apache.tools.zip.ZipEntry
				    ZipEntry entry = new ZipEntry(inputFile.getName());
				    ouputStream.putNextEntry(entry);
				    // 向压缩文件中输出数据
				    int nNumber;
				    byte[] buffer = new byte[512];
				    while ((nNumber = bins.read(buffer)) != -1) {
				        ouputStream.write(buffer, 0, nNumber);
				    }
				    // 关闭创建的流对象
				    bins.close();
				    IN.close();
				} else {
				    try {
				        File[] files = inputFile.listFiles();
				        for (int i = 0; i < files.length; i++) {
				            zipFile(files[i], ouputStream);
				        }
				    } catch (Exception e) {
				        e.printStackTrace();
				    }
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void zipFileV2(File inputFile, String parentPath, ZipOutputStream ouputStream) {
	    try {
	        if (inputFile.exists()) {
	            /**
	             * 如果是目录的话这里是不采取操作的， 至于目录的打包正在研究中
	             */
	            if (inputFile.isDirectory()) {
	                try {
	                    parentPath += inputFile.getName() + File.separator; 
                        File[] files = inputFile.listFiles();
                        for (int i = 0; i < files.length; i++) {
                            zipFileV2(files[i], parentPath, ouputStream);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
	            } else {
	                FileInputStream IN = new FileInputStream(inputFile);
                    BufferedInputStream bins = new BufferedInputStream(IN, 512);
                    // org.apache.tools.zip.ZipEntry
                    ZipEntry entry = new ZipEntry(parentPath + inputFile.getName());
                    ouputStream.putNextEntry(entry);
                    // 向压缩文件中输出数据
                    int nNumber;
                    byte[] buffer = new byte[512];
                    while ((nNumber = bins.read(buffer)) != -1) {
                        ouputStream.write(buffer, 0, nNumber);
                    }
                    // 关闭创建的流对象
                    bins.close();
                    IN.close();
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

}

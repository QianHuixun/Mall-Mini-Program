package cn.tofocus.lejia.zx.pay.util;


import java.io.*;
import java.util.Base64;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class UncompressFile {
    private UncompressFile() {
    }

/**
 * @param fileData    文件数据
 * @param newFilePath 生成文件路径
 */
public static void uncompress(String fileData, String newFilePath) {
    Inflater inflater = new Inflater();
    inflater.setInput(Base64.getDecoder().decode(fileData));
    File file = new File(newFilePath);

    byte[] outByte = new byte[1024];
    try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); OutputStream out = new FileOutputStream(file)) {

        while (!inflater.finished()) {
            int len = inflater.inflate(outByte);
            if (len == 0) {
                break;
            }
            bos.write(outByte, 0, len);
            System.out.println("bos: " + bos.toString());
            out.write(bos.toByteArray());
            bos.reset();
        }
        inflater.end();
    } catch (IOException | DataFormatException e) {
        e.printStackTrace();
    }
}
}

package cn.tofocus.lejia.zx.pay;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Base64;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import cn.tofocus.lejia.zx.pay.util.UncompressFile;

public class DownloadFileDemo
{
    public static void main(String[] args) throws UnsupportedEncodingException
    {
        // 下载对账单接口返回的FILEDATA字段内容
//        String fileData = "eNrNlM9v0zAUx8/mr+CM3OL37CSNj/AXcOQOHDkgIf6fSaMFcViYklbrupSOFujakWU0lTYmVYgDYqhCoEmVtiHsuFHLRvlRqVLtxH7Psr/vfWzLpFaN+21v8yMl/sHr83LtTada3GhS8vLgeZjZug+e+KvabtU6pcw2KzJPK9UHUZeSpFgJuiW/biwzVgnCYTyKIkqqJ16cJPUBJf33ta/B0Oh3d8LhJMLEMzEmvrbMqnYSjcp7lJT3Gm3VfghWE5+S7ab/2RtkWW+frrfUzF48euepbHrBThrhe+xvfFk7MzqZN1HuHYaH1/eb1aMsvVbZCGnJ9vnW0dqZETNoGissaWGv0l/Z/0avEGTIcgA5FFeZKwVI5lBCCUOOjDFgaQFKbt9i4wIIwkU9iZK3p5rrBt7U26dUt6Ko9mnzlYoZRU2zielqh+UZM0vUSGpDXqgW81oyG6Pk0fSU6dZUG8Gx3WtpUaZO9RcEYNJC9f0LAv8/BH4ZgeXtBSAICSiZq4RZoQAcOKq/wBB+g6TM6Zpyay0AFKimgrAtAZbDhMUdQJ7halbvcdbnOieNY4M/hlUJw0VW3YoLrElxPdldiZpGJx69eBr82H3WOP7zFlxCtiRw9c2DLGYicxs517d5WZGd9KIu5JT1C/F35JxhfnD33sP7d0yoMQMsmN6WKKQF89Bbs+k5Wm4Bl/TAbSksKcQ8yPZsZE3N3eVFtiXM9ZI5s5FtLhT28iD/BGys22s=";
        String fileData = "SjAwMDMwNzAwMDAwMDAwMjAyMjA2MTAgICAgICAyMDIyMDYxOTkxMTAwNjIyNDA0MDY0ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICA5MTEwMDYyMjQwNDA2NCAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgOTExMDA2MjI0MDQwNjQgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIDkxMTAwNjIyNDA0MDY0ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAtMDAwMDAwMDAwMjUwMC0wMDAwMDAwMDAwMDA2MiAgICAgICAgIDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAgICAgICAgICAgWkZZVzAyINanuLa21NXKw/fPuM7EvP7T0KOs0rXO8baptaXD98+4zt4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgDQo=";
//        String fileData = "eNrNlN1u0zAUx6/NU/AA7rCPHX/kEoln4PmoNK2wm5UpSdfSpWJLNdK1y9o1lTqGKgQ3CFUDhFRpGyKOCW1FL3JVkQ/nHMfnb//sY6N2aziJ628+YeRNzx6C9rvz1l4zwuh0+josbPP1X3q7xu62z2uFbSMKzyh1Zkkfo3Sv4fdrXsdatq7hh/PhIkkwav2sD9O0M8No8r5968+tfr8Xzpc9LD3bx9I3lo2K02QRXGIUXL6Ns/Kjv5t6GJ1E3pf6rBj1yd1hN2s5Hi6u6tloxn4v7+HH0Gt+Pbi3OoW3VB5fh9dPRlHrphheN7BCRjJ+OL45uLdiFs1ghTUjXG9MqqPv+BECAlAhrMLoY6JdYC5zMOISFAUFxgRKKCGC5BfFSFNGCQMARrQgGJkm+S9GhBEjjFEmJdVcOub/xZ1BH1STyJaVo/102ozMfJvgXJORHZJL5TW5nZXSlEAdUtRlAJ8H0ziJk8MX4augGvwafEuT5HSUXHy4uj0bHO33jk2r58+ersishP+516gpuJy7oMpRS3AIyDVqp6DmVApKtJKqHDWITdRiO9TcNQ+Uo9ZcEw6r1JQX1Fowkk0JF7wctRI7zj/UVG+H2nE5uEyWo6YMKPBVavhLLUAKJiTXrORab8xwZ2vULEvyctQ8e5laW2u2muGOFkqVpOZsAzUl26EW5jQrneGCaO6s7WtaUEuldLbx6f96mv0GQtqjNQ==";
        // 下载对账单接口返回的FILEMD5字段内容
//        String fileMd5 = "35edfa0d7e976b171e85088f400b6b7d";
        // 生成CSV文件路径
        String newFilePath = "D:\\file.csv";
        // 数据处理并生成文件
        UncompressFile.uncompress(fileData, newFilePath);
        // 验证MD5
//        System.out.println(UncompressFile.isSameMd5(fileData,fileMd5));
    }
}

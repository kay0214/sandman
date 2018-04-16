package com.sandman.download.web.rest.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangj on 2018/4/16.
 */
public class FileUtils {
    private static Map<Integer,String> fileSizeMap = new HashMap<>();
    static{//静态代码块，将文件大小的数量级put到map中
        fileSizeMap.put(0,"B");
        fileSizeMap.put(1,"KB");
        fileSizeMap.put(2,"MB");
        fileSizeMap.put(3,"GB");
    }
    public static boolean uploadFile(byte[] file, String filePath, String fileName){
        File targetFile = new File(filePath);
        if(!targetFile.exists()){
            targetFile.mkdirs();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath+fileName);
            out.write(file);
            out.flush();
            return true;
        } catch (IOException e) {//写入过程中抛出异常
            return false;
        }finally {
            try {
                out.close();
            } catch (IOException e) {//关闭流的时候抛出异常
                return false;
            }
        }
    }
    /**
     * 根据文件名获取后缀名
     * */
    public static String getSuffixNameByFileName(String fileName){
        return fileName.substring(fileName.lastIndexOf(".")+1);
    }
    /**
     * 获取正确的文件名，防止文件名中文乱码，用于设置resName。例如：resName：新建文本文档.txt
     * */
    public static String getRightFileNameUseCode(String fileName){//先用GBK编码，再用ISO8859-1解码成string。解决文件名乱码而内容不乱码
        String rightFileName = "";
        try {
            rightFileName = new String(fileName.getBytes("GBK"),"iso-8859-1");
        } catch (UnsupportedEncodingException e) {
            System.out.println("解析文件名失败!");
        }
        return rightFileName;
    }
    /**
     * 获取正确的文件名，防止重名。在resUrl中使用，防止资源服务器中有重名文件。例如：resName：新建文本文档.txt ；resUrl：http://10.161.19.1/txt/新建文本文档_123456789.txt
     * 如果是按照用户名分文件夹，似乎是用不着防止重名。
     * */
    public static String getRightFileNameForUpload(String fileName){
        return null;
    }
    /**
     * 根据原文件byte大小获取到处理后的大小
     * */
    public static String getFileSize(Long fileSize){
        String fileSizeUnit = "";
        int mapKey = 0;
        double size = fileSize;
        while(size>1){//size>1，进入循环，得到下一个数量级,例如1025MB = 1GB
            mapKey++;
            size = size/1024;
        }
        //size<1，则跳出循环。此时数量级为 0.999GB，稍后进行处理
        fileSizeUnit = fileSizeMap.get(mapKey-1);//获取到静态代码块中put进去的值。
        size *= 1024;   //进入上一个数量级，得到一个合适的数量级，999MB 而非0.999GB
        size = NumberUtils.getDoubleByDouble(size,2);//四舍五入，保留2位小数
        return size + fileSizeUnit;
    }
}

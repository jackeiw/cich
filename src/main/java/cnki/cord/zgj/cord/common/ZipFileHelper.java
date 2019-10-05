/**
 * Copyright (C), 1998-2019, 同方知网（北京）技术有限公司
 * FileName: ZipFileHelper
 * Author:   Vincent
 * Date:     2019/10/5 20:52
 * Description: 加压解压共用类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cnki.cord.zgj.cord.common;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈加压解压共用类〉
 *
 * @author Vincent
 * @create 2019/10/5
 * @since 1.0.0
 */
public class ZipFileHelper {
    public static final int BUFFER_SIZE = 1024;

    /**
     * 解压 zip 文件
     * @param zipFile zip 压缩文件
     * @param destDir zip 压缩文件解压后保存的目录
     * @return 返回 zip 压缩文件里的文件名的 list
     * @throws Exception
     */
    public static List<String> unZip(File zipFile, String destDir) throws Exception {
        // 如果 destDir 为 null, 空字符串, 或者全是空格, 则解压到压缩文件所在目录
        if(StringUtils.isBlank(destDir)) {
            destDir = zipFile.getParent();
        }

        destDir = destDir.endsWith(File.separator) ? destDir : destDir + File.separator;
        ZipArchiveInputStream is = null;
        List<String> fileNames = new ArrayList<String>();

        try {
            is = new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(zipFile), BUFFER_SIZE));
            ZipArchiveEntry entry = null;

            while ((entry = is.getNextZipEntry()) != null) {
                fileNames.add(entry.getName());

                if (entry.isDirectory()) {
                    File directory = new File(destDir, entry.getName());
                    directory.mkdirs();
                } else {
                    OutputStream os = null;
                    try {
                        os = new BufferedOutputStream(new FileOutputStream(new File(destDir, entry.getName())), BUFFER_SIZE);
                        IOUtils.copy(is, os);
                    } finally {
                        IOUtils.closeQuietly(os);
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            IOUtils.closeQuietly(is);
        }

        return fileNames;
    }

    /**
     * 解压 zip 文件
     * @param zipFilePath zip 压缩文件的路径
     * @param destDir zip 压缩文件解压后保存的目录
     * @return 返回 zip 压缩文件里的文件名的 list
     * @throws Exception
     */
    public static List<String> unZip(String zipFilePath, String destDir) throws Exception {
        File zipFile = new File(zipFilePath);
        return unZip(zipFile, destDir);
    }

    /**
     * 测试解压 zip 文件
     * @param zipPath zip 压缩文件
     * @return 返回 zip 压缩文件是否损坏
     * @throws Exception
     */
    public static boolean testZipFile(String zipPath){
        try{
            ZipFile zipFile = new ZipFile(zipPath);
            //String encoding = zipFile.getEncoding();
            //unZip(zipPath, targetPath);
            return true;
        }
        catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * 测试解压 zip 文件
     * @param zipFilePath zip 压缩文件
     * @return 返回 zip 压缩文件是否包含指定后缀名的文件
     * @throws Exception
     */
    public static boolean containFiles(String zipFilePath, String suffixName) throws Exception {
        File zipFile = new File(zipFilePath);
        ZipArchiveInputStream is = null;

        try {
            is = new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(zipFile), BUFFER_SIZE));
            ZipArchiveEntry entry = null;

            while ((entry = is.getNextZipEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                } else {
                    if(!entry.getName().isEmpty()){
                        if(entry.getName().toLowerCase().contains(suffixName.toLowerCase())){
                            return true;
                        }
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            IOUtils.closeQuietly(is);
        }
        return false;
    }
}
package cnki.cord.zgj.cord.common;

import java.io.*;

public class FileHelper {
    /**
     * 文件读取到字节数组
     * 1.文件到程序：文件字节输入流FileInputStream
     * 2.程序到字节数组：字节数组输出流ByteArrayOutputStream
     * @param srcPath
     * @return
     */
    public static byte[] fileToByteArray(String srcPath) {
        // 创建源和目的地
        File src = new File(srcPath);
        byte[] datas = null;
        // 选择流
        try (InputStream is = new BufferedInputStream(new FileInputStream(src));
             ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
            // 操作（分段读取）
            byte[] flush = new byte[1024];// 缓冲容器
            int len = -1;// 接收长度
            while ((len = is.read(flush)) != -1) {
                baos.write(flush, 0, len);
            }
            baos.flush();
            // 获取数据
            datas = baos.toByteArray();
            return datas;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 字节数组写到文件
     * 1.字节数组读取到程序中：字节数组输入流ByteArrayInputStream
     * 2.程序写出到文件：文件字节输出流FileOutputStream
     * @param datas
     * @param destPath
     */
    public static void byteArrayToFile(byte[] datas, String destPath) {
        // 创建源
        File dest = new File(destPath);
        // 选择流
        try (InputStream is = new ByteArrayInputStream(datas);
             OutputStream os = new BufferedOutputStream(new FileOutputStream(dest, false));) {
            // 操作（分段读取）
            byte[] flush = new byte[1024];// 缓冲容器
            int len = -1;// 接收长度
            while ((len = is.read(flush)) != -1) {
                os.write(flush, 0, len);
            }
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

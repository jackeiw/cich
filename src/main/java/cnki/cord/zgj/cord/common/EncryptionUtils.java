package cnki.cord.zgj.cord.common;

import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;

public class EncryptionUtils {
    //盐，用于混交md5
    private static final String slat = "&%6688***&&%%$$#@";
    /**
     * 生成md5
     * @param str
     * @return
     */
    public static String getMD5(String str) {
        String base = str + "/" + slat;
        String md5 = "";
        //String md5 = DigestUtils.md5DigestAsHex(base.getBytes()); 注意这里字符编码不同，造成的结果也是不同的。
        try{
            md5 = DigestUtils.md5DigestAsHex(base.getBytes("UTF-8"));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return md5;
    }
}

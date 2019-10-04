package cnki.cord.zgj.cord.common;

import cnki.cord.zgj.cord.entity.BaseMessageObject;
import cnki.cord.zgj.cord.entity.CnkiConf;
import com.alibaba.csb.sdk.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
//import sun.misc.BASE64Encoder;

import java.util.HashMap;
import java.util.Map;

@Component
public class QsyjsFileHandler {
    private static final Logger newLog = LoggerFactory.getLogger(QsyjsFileHandler.class);

    @Autowired
    private Environment env;

    @Autowired
    private CnkiConf cnkiConf1;

    @Value("${cnkiconf.csb.url}")
    private String csb_url;

    @Value("${cnkiconf.csb.ak}")
    private String csb_ak;

    @Value("${cnkiconf.csb.sk}")
    private String csb_sk;

    @Value("${cnkiconf.csb.getQSYJS}")
    private String service;

    @Value("${cnkiconf.csb.getQSYJSVersion}")
    private String getQSYJSVersion;

    @Value("${cnkiconf.csb.getQSYJSMethon}")
    private String getQSYJSMethon;

    @Value("${cnkiconf.csb.getQSYJSSystemId}")
    private String getQSYJSSystemId;

    @Value("${cnkiconf.csb.getQSYJSToken}")
    private String getQSYJSToken;

    @Value("${cnkiconf.csb.getQSYJSWait}")
    private int getQSYJSWait;
    //1.获取起诉意见书
    public void requestQSYJS(BaseMessageObject wsMsgObject) {
        try{
            //newLog.info("处理获取文书延时:" + getQSYJSWait);
            Thread.sleep(getQSYJSWait);

            //newLog.info("设置CSB服务地址:" + csb_url);
            //String csb_url = env.getProperty("cnkiconf.csb.url");
            HttpParameters.Builder builder = new HttpParameters.Builder();
            builder.requestURL(csb_url) // 设置CSB服务地址
                    .api(service) // 设置服务名
                    .version(getQSYJSVersion) // 设置版本号
                    .method(getQSYJSMethon)// 设置调用方式, get/post
                    .accessKey(csb_ak).secretKey(csb_sk); // 设置accessKey 和 设置secretKey
            HttpReturn ret = null;
            try {
                //builder.setContentEncoding(ContentEncoding.gzip);// 设置请求消息压缩 3
                //builder.putParamsMap("bmsah", wsMsgObject.bmsah).putParamsMap("dwbm",wsMsgObject.dwbm).putParamsMap("mlmc","起诉意见书");
                //"bmsah": "汉东省院起诉受[2018]10000100001号", "bmsah", wsMsgObject.bmsah #部门受案号
                //"dwbm": "770000",  #单位编码
                //"mlmc": "起诉意见书"  #目录名称
                Map<String, String> kvMap = new HashMap<String, String>();
                kvMap.put("bmsah", wsMsgObject.bmsah);
                kvMap.put("dwbm",wsMsgObject.dwbm);
                kvMap.put("mlmc","起诉意见书");
               //builder.putParamsMap("model", JSON.toJSONString(kvMap));
                // 设置请求参数
                builder.contentBody(new ContentBody(JSON.toJSONString(kvMap)));

                builder.putHeaderParamsMap("systemid", getQSYJSSystemId).putHeaderParamsMap("token", getQSYJSToken);

                ret = HttpCaller.invokeReturn(builder.build());

            } catch (Exception e) {
                e.printStackTrace();
            }
            if(ret != null) {
                //String jsonData = "{\"code\": \"0\",\"success\": true, \"message\": \"根据传入的部门受案号、单位编码、目录名称，获取卷宗文件（压缩包）下载地址成功\",\"data\": \"http://192.168.2.239:8080/dzjz-TEST/api/rpcdzjz/getPdfZipFromClient?zipFile=%2Fdata%2Ftyyw2.0-file-test%2Fdzjz%2FZipRepository%2FDzjzWj_files_zip%2F2019-09-01-13-27-53OUT.zip\" }";
                String retJson = ret.getResponseStr();
                newLog.info(retJson);

                JSONObject jsonObject = JSONObject.parseObject(retJson);
                String code = jsonObject.getString("code");
                Boolean success = jsonObject.getBoolean("success");
                String message = jsonObject.getString("message");
                String data = jsonObject.getString("data");

                if (code.equals("0")) {
                    String url = data; //"http://localhost/files/test.rar";
                    newLog.info("获取【" + wsMsgObject.bmsah + "】下载地址成功，下载地址:" + url);
                    //downloadFile(url,wsMsgObject.bmsah,java.util.UUID.randomUUID() + ".zip");
                    downloadFile(url,wsMsgObject.bmsah,EncryptionUtils.getMD5(wsMsgObject.bmsah) + ".zip");
                } else {
                    newLog.info("获取【" + wsMsgObject.bmsah + "】下载地址失败，Info:" + message);
                }
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            newLog.info("获取【" + wsMsgObject.bmsah + "】下载地址异常，Info:" + e.getMessage());
        }
        catch (Exception e){
            e.printStackTrace();
            newLog.info("获取【" + wsMsgObject.bmsah + "】下载地址异常，Info:" + e.getMessage());
        }
    }

    //2.使用4-5接口？
    //public void get


    @Value("${cnkiconf.csb.getQSYJSPath}")
    private String getQSYJSPath;
    @Value("${cnkiconf.csb.sendFilepathURL}")
    private String sendFilepathURL;
    public void downloadFile(String url,String bmsah,String fileName) {
        try{
            String token = "v32Eo2Tw+qWI/eiKW3D8ye7l19mf1NngRLushO6CumLMHIO1aryun0/Y3N3YQCv/TqzaO/TFHw4=";
            boolean downOK = HttpUtils.downLoadFromUrl(url, fileName, getQSYJSPath, token);
            if(downOK) {
                newLog.info("起诉意见书下载完成：【" + url + "】");
                //System.out.println("下载完成");
                //TODO: 需要测试解压包，否则延时再重新下载。

            }
            else
            {
                //TODO: 下载失败，需要静待稳定后再行下载。
                return;
            }

            String encoderMD5Str = null;
            /*BASE64Encoder encoder = new BASE64Encoder();
            try {
                encoderStr = encoder.encode(bmsah.getBytes("UTF8"));
            } catch (UnsupportedEncodingException e) {
                newLog.error("字符串【" + bmsah + "】base64编码失败！", e);
            }*/
            encoderMD5Str = EncryptionUtils.getMD5(bmsah);

            String invokeURL = sendFilepathURL.replace("{bmsah}",encoderMD5Str).replace("{zippath}",getQSYJSPath + fileName);
            newLog.info("起诉意见书开始解析：【" + invokeURL + "】！");
            //触发解压和提取要素事件
            int retCode = HttpUtils.sendGetCode(invokeURL, "");
            newLog.info("起诉意见书解析完成：【" + retCode + "】！");
            if(retCode != 200){
                //TODO: 如果出现请求错误，备份各段时间需要续传

            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}

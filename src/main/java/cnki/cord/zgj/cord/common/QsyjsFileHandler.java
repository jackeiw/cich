package cnki.cord.zgj.cord.common;

import cnki.cord.zgj.cord.entity.BaseMessageObject;
import cnki.cord.zgj.cord.entity.CnkiConf;
import cnki.cord.zgj.cord.entity.JzwjObject;
import com.alibaba.csb.sdk.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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

    @Value("${cnkiconf.csb.getFileType}")
    private int getFileType;
    /**
     * 获取起诉意见书
     * @param wsMsgObject 文书基础实体
     * @throws Exception
     */
    public void requestQSYJS(BaseMessageObject wsMsgObject) {
        if(getFileType == 1)
        {
            getFilesByDir(wsMsgObject);
        }
        else
        {
            getQSYJSByZip(wsMsgObject);
        }

    }

    /**
     * 1.通过zip下载获取起诉意见书
     * @param wsMsgObject 文书基础实体
     * @throws Exception
     */
    public void getQSYJSByZip(BaseMessageObject wsMsgObject) {
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
                //newLog.info(retJson);

                JSONObject jsonObject = JSONObject.parseObject(retJson);
                String code = jsonObject.getString("code");
                Boolean success = jsonObject.getBoolean("success");
                String message = jsonObject.getString("message");
                String data = jsonObject.getString("data");

                if (code.equals("0")) {
                    String url = data; //"http://localhost/files/test.rar";
                    newLog.info("【" + wsMsgObject.bmsah + "】下载地址获取成功，下载地址:" + url);
                    //downloadFile(url,wsMsgObject.bmsah,java.util.UUID.randomUUID() + ".zip");
                    downloadFile(url, wsMsgObject,EncryptionUtils.getMD5(wsMsgObject.bmsah) + ".zip");
                } else {
                    newLog.info("【" + wsMsgObject.bmsah + "】下载地址获取失败，Info:" + message);
                    if(getFileType == 2){
                        getFilesByDir(wsMsgObject);
                    }
                }
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            newLog.info("【" + wsMsgObject.bmsah + "】下载地址获取异常，Info:" + e.getMessage());
        }
        catch (Exception e){
            e.printStackTrace();
            newLog.info("【" + wsMsgObject.bmsah + "】下载地址获取异常，Info:" + e.getMessage());
        }
    }

    /**
     * 2.使用4-5接口？获取起诉意见书
     * @param wsMsgObject 文书基础实体
     * @throws Exception
     */
    public void getFilesByDir(BaseMessageObject wsMsgObject){
        if(getAJJZAllDir(wsMsgObject)) {
            invokeDirExplain(wsMsgObject.bmsah);
        }
    }

    @Value("${cnkiconf.csb.getAJJZType}")
    private int getAJJZType;

    @Value("${cnkiconf.csb.getAJJZQML}")
    private String getAJJZQML;

    @Value("${cnkiconf.csb.getAJJZQMLVersion}")
    private String getAJJZQMLVersion;

    @Value("${cnkiconf.csb.getAJJZQMLMethon}")
    private String getAJJZQMLMethon;

    @Value("${cnkiconf.csb.getAJJZQMLSystemId}")
    private String getAJJZQMLSystemId;

    @Value("${cnkiconf.csb.getAJJZQMLToken}")
    private String getAJJZQMLToken;
    /**
     * 获取案件卷宗全目录
     * @param wsMsgObject 文书基础实体
     * @throws Exception
     */
    public boolean getAJJZAllDir(BaseMessageObject wsMsgObject){
        boolean retResult = false;
        HttpParameters.Builder builder = new HttpParameters.Builder();
        builder.requestURL(csb_url) // 设置CSB服务地址
                .api(getAJJZQML) // 设置服务名
                .version(getAJJZQMLVersion) // 设置版本号
                .method(getAJJZQMLMethon)// 设置调用方式, get/post
                .accessKey(csb_ak).secretKey(csb_sk); // 设置accessKey 和 设置secretKey
        HttpReturn ret = null;
        try {
            Map<String, String> kvMap = new HashMap<String, String>();
            kvMap.put("bmsah", wsMsgObject.bmsah);
            kvMap.put("dwbm",wsMsgObject.dwbm);
            // 设置请求参数
            builder.contentBody(new ContentBody(JSON.toJSONString(kvMap)));
            builder.putHeaderParamsMap("systemid", getAJJZQMLSystemId).putHeaderParamsMap("tyyw_token", getAJJZQMLToken);

            ret = HttpCaller.invokeReturn(builder.build());

        } catch (Exception e) {
            e.printStackTrace();
        }
        if(ret != null) {
            String retJson = ret.getResponseStr();
            newLog.info("【" +  wsMsgObject.bmsah + "】调用卷宗目录返回：" + retJson);
            JSONObject jsonObject = JSONObject.parseObject(retJson);
            String code = jsonObject.getString("code");
            Boolean success = jsonObject.getBoolean("success");
            String message = jsonObject.getString("message");
            int getFileFlag = 0;//获取卷宗标识，如果等于0，只获取所有; 等于1, 则获取必要的。
            if(success){
                String data = jsonObject.getString("data");
                if(StringUtils.isNotBlank(data)){
                    //卷宗目录
                    JSONObject mlObject = JSONObject.parseObject(data);
                    String mlbh = null; //目录编号
                    if(getAJJZType == 1){
                        JSONArray jzmlArray = mlObject.getJSONArray("jzml");
                        int mllx; //目录类型
                        String mlxsmc = null; //目录显示名称
                        for(int i = 0; i < jzmlArray.size(); i++){
                            JSONObject mlObj = (JSONObject)jzmlArray.get(i);
                            /*mllx = mlObj.getInteger("mllx");
                            if(mllx == 1){
                                break;
                            }*/
                            if(mlxsmc.equals("起诉意见书")){
                                mlbh = mlObj.getString("mlbh");
                                mlxsmc = mlObj.getString("mlxsmc");
                                getFileFlag = 1;
                                break;
                            }
                        }
                    }

                    //List<JzwjObject> jzwjObjectList = new LinkedList<JzwjObject>();
                    String wjxh,wjmc,wjhz;
                    int wjsxh;
                    //卷宗文件
                    JSONArray jzmlwjArray = mlObject.getJSONArray("jzmlwj");
                    for(int j = 0; j < jzmlwjArray.size(); j++){
                        JSONObject mlwjObj = (JSONObject)jzmlwjArray.get(j);
                        String tempMlbh = mlwjObj.getString("mlbh");
                        if(getAJJZType == 1 && getFileFlag == 1){
                            if(mlbh.equals(tempMlbh)){  //目录编号对上了，取相应目录的文件
                                wjxh = mlwjObj.getString("wjxh");
                                wjmc = mlwjObj.getString("wjmc");
                                wjhz = mlwjObj.getString("wjhz");
                                wjsxh = mlwjObj.getInteger("wjsxh");
                                JzwjObject jzwj = new JzwjObject();
                                jzwj.mlbh = mlbh; //目录编号
                                jzwj.wjxh = wjxh; //文件序号
                                jzwj.wjmc = wjmc; //文件名称
                                jzwj.wjhz = wjhz; //文件后缀
                                jzwj.wjsxh = wjsxh; //文件顺序号
                                //jzwjObjectList.add(jzwj);
                                retResult = retResult | getAJJZWJFiles(wsMsgObject, jzwj);
                            }
                        }
                        else{
                            wjxh = mlwjObj.getString("wjxh");
                            wjmc = mlwjObj.getString("wjmc");
                            wjhz = mlwjObj.getString("wjhz");
                            wjsxh = mlwjObj.getInteger("wjsxh");
                            JzwjObject jzwj = new JzwjObject();
                            jzwj.mlbh = mlbh; //目录编号
                            jzwj.wjxh = wjxh; //文件序号
                            jzwj.wjmc = wjmc; //文件名称
                            jzwj.wjhz = wjhz; //文件后缀
                            jzwj.wjsxh = wjsxh; //文件顺序号
                            retResult = retResult | getAJJZWJFiles(wsMsgObject, jzwj);
                        }
                    }
                }
            }
        }
        else{
            newLog.info("【" +  wsMsgObject.bmsah + "】调用卷宗目录返回为空！");
        }

        return retResult;
    }

    public void testJson(){
        String retJson = "{\"code\": \"0\",\"success\": true, \"message\": \"获取案件卷宗全目录成功\",\"data\": {\n" +
                "\"bmsah\": \"汉东省院起诉受[2018]10000100001号\", \n" +
                "\"jzml\":[ \n" +
                "{\n" +
                "  \"jzbh\": \"37000000012274\", \n" +
                "  \"mlbh\": \"dd84939fefbc443a8320f33e492ce78a\", \n" +
                "  \"mllx\": 1, \n" +
                "  \"fmlbh\": \"\", \n" +
                "  \"mlxsmc\": \"文书卷\", \n" +
                "  \"mlxx\": \"\", \n" +
                "  \"mlsxh\": 1 \n" +
                " },\n" +
                "{\n" +
                "  \"jzbh\": \"37000000012274\", \n" +
                "  \"mlbh\": \"1abd8438dd1e40dc982e67d86687b679\", \n" +
                "  \"mllx\": 3, \n" +
                "  \"fmlbh\": \"dd84939fefbc443a8320f33e492ce78a\", \n" +
                "  \"mlxsmc\": \"封面\", \n" +
                "  \"mlxx\": \"\", \n" +
                "  \"mlsxh\": 2 \n" +
                " }\n" +
                "],\n" +
                "\"jzmlwj\": [ \n" +
                "{\n" +
                "    \"jzbh\": \"37000000012274\", \n" +
                "    \"mlbh\": \"dd84939fefbc443a8320f33e492ce78a\", \n" +
                "    \"wjxh\": \"465dfc057bf1446d8bc9cc2ef9f20c3a\", \n" +
                "    \"wjmc\": \"fdiuewhfjj4wru43fijdsjfw4eu8rjew\", \n" +
                " \"wjlj\": \"\\\\100001\\\\0301\\\\汉东省院起诉受[2018]10000100001号\", \n" +
                "     \"wjxsmc\": \"第 1 页\", \n" +
                "     \"wjhz\": \".pdf \", \n" +
                "     \"wjksy\": 0, \n" +
                "     \"wjjsy\": 0, \n" +
                "     \"wjsxh\": 0  \n" +
                "}\n" +
                "]\n" +
                "} }";
        JSONObject jsonObject = JSONObject.parseObject(retJson);
        String code = jsonObject.getString("code");
        Boolean success = jsonObject.getBoolean("success");
        String message = jsonObject.getString("message");
        if(success){
            String data = jsonObject.getString("data");
            if(StringUtils.isNotBlank(data)){
                //卷宗目录
                JSONObject mlObject = JSONObject.parseObject(data);
                JSONArray jzmlArray = mlObject.getJSONArray("jzml");
                int mllx; //目录类型
                String mlxsmc = null; //目录显示名称
                String mlbh = null; //目录编号
                for(int i = 0; i < jzmlArray.size(); i++){
                    JSONObject mlObj = (JSONObject)jzmlArray.get(i);
                    mlxsmc = mlObj.getString("mlxsmc");
                    mllx = mlObj.getInteger("mllx");
                    mlbh = mlObj.getString("mlbh");
                    if(mllx == 1){
                        break;
                    }
                }

                List<JzwjObject> jzwjObjectList = new LinkedList<JzwjObject>();
                String wjxh,wjmc,wjhz;
                JSONArray jzmlwjArray = mlObject.getJSONArray("jzmlwj");
                for(int j = 0; j < jzmlwjArray.size(); j++){
                    JSONObject mlwjObj = (JSONObject)jzmlwjArray.get(j);
                    String temp = mlwjObj.getString("mlbh");
                    if(mlbh.equals(temp)){ //目录编号对上了，取相应目录的文件
                        wjxh = mlwjObj.getString("wjxh");
                        wjmc = mlwjObj.getString("wjmc");
                        wjhz = mlwjObj.getString("wjhz");
                        JzwjObject jzwj = new JzwjObject();
                        jzwj.mlbh = mlbh; //目录编号
                        jzwj.wjxh = wjxh; //文件序号
                        jzwj.wjmc = wjmc; //文件名称
                        jzwj.wjhz = wjhz; //文件后缀
                        jzwjObjectList.add(jzwj);
                    }
                }
                System.out.println(jzwjObjectList.size());
            }
        }
    }

    public void testFileSave(String retJson){
        JSONObject jsonObject = JSONObject.parseObject(retJson);
        String code = jsonObject.getString("code");
        Boolean success = jsonObject.getBoolean("success");
        String message = jsonObject.getString("message");
        if(success){
            try{
                String dataBase64Str = jsonObject.getString("data");
                byte[] fileByteStream = Base64Utils.decode(dataBase64Str);
                //文件保存位置
                File saveDir = new File("D:\\test\\qsyjs\\");
                if(!saveDir.exists()){
                    saveDir.mkdir();
                }

                File file = new File(saveDir + File.separator + "test111.pdf");
                if(file.exists()) {
                    file.delete();
                    file = new File(saveDir + File.separator + "test111.pdf");
                }
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(fileByteStream);
                if(fos!=null){
                    fos.close();
                }
            }
            catch (IOException e){

            }
        }
    }

    @Value("${cnkiconf.csb.getAJJZWJ}")
    private String getAJJZWJ;

    @Value("${cnkiconf.csb.getAJJZWJVersion}")
    private String getAJJZWJVersion;

    @Value("${cnkiconf.csb.getAJJZWJMethon}")
    private String getAJJZWJMethon;

    @Value("${cnkiconf.csb.getAJJZWJSystemId}")
    private String getAJJZWJSystemId;

    @Value("${cnkiconf.csb.getAJJZWJToken}")
    private String getAJJZWJToken;
    /**
     * 获取案件卷宗文件
     * @param wsMsgObject 文书基础实体
     * @param jzwj 文件实体
     * @throws Exception
     */
    public boolean getAJJZWJFiles(BaseMessageObject wsMsgObject, JzwjObject jzwj){
        boolean getOK = false;
        HttpParameters.Builder builder = new HttpParameters.Builder();
        builder.requestURL(csb_url) // 设置CSB服务地址
                .api(getAJJZWJ) // 设置服务名
                .version(getAJJZWJVersion) // 设置版本号
                .method(getAJJZWJMethon)// 设置调用方式, get/post
                .accessKey(csb_ak).secretKey(csb_sk); // 设置accessKey 和 设置secretKey
        HttpReturn ret = null;
        try {
            Map<String, String> kvMap = new HashMap<String, String>();
            kvMap.put("bmsah", wsMsgObject.bmsah);
            kvMap.put("dwbm", wsMsgObject.dwbm);
            kvMap.put("wjxh", jzwj.wjxh);
            // 设置请求参数
            builder.contentBody(new ContentBody(JSON.toJSONString(kvMap)));
            builder.putHeaderParamsMap("systemid", getAJJZWJSystemId).putHeaderParamsMap("tyyw_token", getAJJZWJToken);

            ret = HttpCaller.invokeReturn(builder.build());

        } catch (Exception e) {
            e.printStackTrace();
        }
        if(ret != null) {
            String retJson = ret.getResponseStr();
            JSONObject jsonObject = JSONObject.parseObject(retJson);
            String code = jsonObject.getString("code");
            Boolean success = jsonObject.getBoolean("success");
            String message = jsonObject.getString("message");
            if(success){
                newLog.info("【" +  wsMsgObject.bmsah + "】获取卷宗文件Get Success!"); //retJson
                try{
                    String dataBase64Str = jsonObject.getString("data");
                    byte[] fileByteStream = Base64Utils.decode(dataBase64Str);
                    //文件保存位置
                    File saveDir = new File(getQSYJSPath + File.separator + EncryptionUtils.getMD5(wsMsgObject.bmsah));
                    if(!saveDir.exists()){
                        saveDir.mkdir();
                    }

                    File file = new File(saveDir + File.separator + jzwj.wjsxh + "_" + jzwj.wjmc + jzwj.wjhz);
                    if(file.exists()) {
                        file.delete();
                        file = new File(saveDir + File.separator +  jzwj.wjsxh + "_" + jzwj.wjmc + jzwj.wjhz);
                    }
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(fileByteStream);
                    if(fos!=null){
                        fos.close();
                    }
                    getOK = true;
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
            else{
                newLog.info("获取卷宗文件Get Fail!");
            }
        }
        else{
            newLog.info("获取卷宗文件接口返回为空！");
        }
        return getOK;
    }

    @Value("${cnkiconf.csb.getQSYJSPath}")
    private String getQSYJSPath;
    /**
     * 下载起诉意见书
     * @param url 文件下载地址
     * @param wsMsgObject 部门受案号
     * @param fileName 保存文件名
     * @throws Exception
     */
    public void downloadFile(String url,BaseMessageObject wsMsgObject, String fileName) {
        try{
            String token = "v32Eo2Tw+qWI/eiKW3D8ye7l19mf1NngRLushO6CumLMHIO1aryun0/Y3N3YQCv/TqzaO/TFHw4=";
            boolean downOK = HttpUtils.downLoadFromUrl(url, fileName, getQSYJSPath, token);
            String zipPath = "";
            if(downOK) {
                newLog.info("【" +  wsMsgObject.bmsah + "】起诉意见书[" + fileName + "]下载完成：url【" + url + "】");
                //System.out.println("下载完成");
                zipPath = getQSYJSPath + fileName;
                //TODO: 需要测试解压包，否则延时再重新下载。
                if(!ZipFileHelper.testZipFile(zipPath)){
                    newLog.info("【" +  wsMsgObject.bmsah + "】起诉意见书zip包被损坏！");
                    getFilesByDir(wsMsgObject);
                }
                else if(!ZipFileHelper.containFiles(zipPath, ".pdf")){
                    newLog.info("【" +  wsMsgObject.bmsah + "】起诉意见书zip包文件为空！");
                    getFilesByDir(wsMsgObject);
                }
                else{
                    invokeZipExplain(wsMsgObject.bmsah, zipPath);
                }
            }
            else
            {
                //TODO: 下载失败，需要静待稳定后再行下载。
                return;
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Value("${cnkiconf.csb.sendFilepathURL}")
    private String sendFilepathURL;

    /**
     * 起诉意见书zip文件解析，抽取数据并计算类案
     * @param bmsah 部门受案号
     * @param zipPath 文书压缩包完整路径
     * @throws Exception
     */
    private void invokeZipExplain(String bmsah, String zipPath){
        String encoderMD5Str = null;
            /*BASE64Encoder encoder = new BASE64Encoder();
            try {
                encoderStr = encoder.encode(bmsah.getBytes("UTF8"));
            } catch (UnsupportedEncodingException e) {
                newLog.error("字符串【" + bmsah + "】base64编码失败！", e);
            }*/
        encoderMD5Str = EncryptionUtils.getMD5(bmsah);

        String invokeURL = sendFilepathURL.replace("{bmsah}",encoderMD5Str).replace("{zippath}", zipPath);
        newLog.info("【" +  bmsah + "】起诉意见书zip包开始解析：【" + invokeURL + "】！");
        //触发解压和提取要素事件
        int retCode = HttpUtils.sendGetCode(invokeURL, "");
        newLog.info("【" + bmsah + "】起诉意见书zip包解析完成：【" + retCode + "】！");
        if(retCode != 200){
            //TODO: 如果出现请求错误，备份各段时间需要续传

        }
    }

    @Value("${cnkiconf.csb.sendFilepathURL1}")
    private String sendFilepathURL1;
    /**
     * 目录解析；抽取数据并计算类案
     * @param bmsah 部门受案号
     * @throws Exception
     */
    private void invokeDirExplain(String bmsah){
        String encoderMD5Str = null;
        encoderMD5Str = EncryptionUtils.getMD5(bmsah);
        String path = getQSYJSPath + encoderMD5Str;
        String invokeURL = sendFilepathURL1.replace("{bmsah}", encoderMD5Str).replace("{path}", path);
        newLog.info("【" + bmsah + "】起诉意见书目录开始解析：【" + invokeURL + "】！");
        //触发解压和提取要素事件
        int retCode = HttpUtils.sendGetCode(invokeURL, "");
        newLog.info("【" + bmsah + "】起诉意见书目录解析完成：【" + retCode + "】！");
        if(retCode != 200){
            //TODO: 如果出现请求错误，备份各段时间需要续传

        }
    }
}

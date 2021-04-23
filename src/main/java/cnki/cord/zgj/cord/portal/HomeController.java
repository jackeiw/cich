package cnki.cord.zgj.cord.portal;

import cnki.cord.zgj.cord.common.EncryptionUtils;
import cnki.cord.zgj.cord.common.HttpUtils;
import cnki.cord.zgj.cord.common.ZipFileHelper;
import cnki.cord.zgj.cord.entity.CnkiConf;
import cnki.cord.zgj.cord.common.QsyjsFileHandler;
import cnki.cord.zgj.cord.entity.WsMessageObject;
import cnki.cord.zgj.cord.receiver.WenshuMsgReceiverProxy;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RestController
@RequestMapping("/home")
public class HomeController {
    private static final Logger newLog = LoggerFactory.getLogger(HomeController.class);
    @GetMapping("test")
    public String sendAsc() {
        System.out.println("服务启动！");
        newLog.info("服务启动！2");
        return "it's OK!";
    }

    @RequestMapping(value = "/handler", method = RequestMethod.POST)
    @ResponseBody
    public String getBodyData(@RequestBody WsMessageObject wsObj){
        //TODO
        System.out.println("接收到消息，此处用来处理接收到的消息："+ wsObj.bsbh + " " + wsObj.dwbm + " " + wsObj.bmsah + " " + wsObj.systemid);
        return "响应成功";
    }

    @Autowired
    private CnkiConf cnkiConf;

    @Value("${cnkiconf.csb.getQSYJS}")
    private String name;

    @GetMapping("getconfig")
    /**
     * 测试获取配置文件
     */
    public String getConfig() {
        return cnkiConf.getName();
        //return name + java.util.UUID.randomUUID() + " " + String.format("{0},{1}",2222,5554);
    }

    /**
     * 测试写日志
     */
    @RequestMapping(value = "/log")
    @ResponseBody
    public String testLogger(){
        newLog.info("今天天气不错，风和日丽！");
        return "请求写入成功！";
    }

    @Value("${cnkiconf.csb.url}")
    private String csb_url;
    @Autowired
    QsyjsFileHandler handler;

    /**
     * 测试获取起诉意见书
     */
    @GetMapping("testQsyjs")
    public String testGetQSYJS(HttpServletRequest request) {
        WsMessageObject WsMsgObj = new WsMessageObject();
        /*WsMsgObj.bmsah = "汉东省院起诉受[2018]10000100001号";
        WsMsgObj.dwbm = "770000";
        WsMsgObj.bsbh = "";*/
        if(StringUtils.isNotEmpty(request.getParameter("bmsah")))
        {
            WsMsgObj.bmsah = request.getParameter("bmsah");
        }
        else
        {
            WsMsgObj.bmsah = "贵州省院刑诉受[2019]520000100254号";//"贵州省院刑诉受[2019]520000100046号";
        }

        if(StringUtils.isNotEmpty(request.getParameter("dwbm")))
        {
            WsMsgObj.dwbm = request.getParameter("dwbm");
        }
        else
        {
            WsMsgObj.dwbm = "520000";
        }
        WsMsgObj.bsbh = "";
        //QsyjsFileHandler handler = new 下载地址成功QsyjsFileHandler();
        newLog.info("===DEBUG===\n测试文书：【" + WsMsgObj.bmsah + "】");
        handler.requestQSYJS(WsMsgObj);
        return "isOK";
    }

    @GetMapping("invokeDir")
    public void invokeDirExp() {
        handler.invokeDirExplain("1000");
    }

    /**
     * MD5加密，传过来是URL encode的字符串
     */
    @RequestMapping(value = "/testMD5/{str}", method = RequestMethod.GET)
    public String testMD5(@PathVariable("str")  String str){
        //str = "贵州省院刑诉受[2019]520000100131号";//"贵州省院刑诉受[2019]520000100024号";//"贵州省院刑诉受[2019]520000100099号";
        return EncryptionUtils.getMD5(str);
    }

    @Value("${cnkiconf.csb.getQSYJSPath}")
    private String getQSYJSPath;
    @Value("${cnkiconf.csb.sendFilepathURL}")
    private String sendFilepathURL;
    @Value("${cnkiconf.csb.sendFilepathURL1}")
    private String sendFilepathURL1;

    /**
     * 补录解析触发
     */
    @RequestMapping(value = "/bulu", method = RequestMethod.GET)
    public String buluQSYJS(){
        // 创建 File对象
        File file = new File(getQSYJSPath);
        // 取 文件/文件夹
        File files[] = file.listFiles();
        // 对象为空 直接返回
        if(files == null){
            return "文件夹不存在！";
        }
        // 目录下文件
        if(files.length == 0){
            System.out.println(getQSYJSPath + "该文件夹下没有文件");
        }

        // 存在文件 遍历 判断
        for (File f : files) {
            // 判断是否为 文件夹
            if(f.isDirectory()){
                //System.out.print("文件夹: ");
                //System.out.println(f.getAbsolutePath());
                // 为 文件夹继续遍历
                //recursiveFiles(f.getAbsolutePath());
                String directoryName = f.getName();
                String invokeURL = sendFilepathURL1.replace("{bmsah}",directoryName).replace("{path}",getQSYJSPath + directoryName);
                newLog.info("补录卷宗开始解析：【" + invokeURL + "】！");
                //触发解压和提取要素事件
                int retCode = HttpUtils.sendGetCode(invokeURL, "");
                newLog.info("补录卷宗解析完成：【" + retCode + "】！");
                if(retCode != 200){
                    //TODO: 如果出现请求错误，备份各段时间需要续传

                }

            } else if(f.isFile()){  // 判断是否为 文件
                System.out.print("文件: ");
                System.out.println(f.getAbsolutePath());

                String fileName = f.getName();
                if(fileName.endsWith("zip"))
                {
                    try{
                        String zipPath = getQSYJSPath + fileName;
                        String bmsah = fileName.replace(".zip","");
                        if(!ZipFileHelper.testZipFile(zipPath)){
                            newLog.info("【" +  bmsah + "】补录解析起诉意见书zip包被损坏！");
                        }
                        else if(!ZipFileHelper.containFiles(zipPath, ".pdf")){
                            newLog.info("【" +  bmsah + "】补录解析起诉意见书zip包文件为空！");
                        }
                        else{
                            String invokeURL = sendFilepathURL.replace("{bmsah}", bmsah).replace("{zippath}", zipPath);
                            newLog.info("补录起诉意见书开始解析：【" + invokeURL + "】！");
                            //触发解压和提取要素事件
                            int retCode = HttpUtils.sendGetCode(invokeURL, "");
                            newLog.info("起诉意见书解析完成：【" + retCode + "】！");
                            if(retCode != 200){
                                //TODO: 如果出现请求错误，备份各段时间需要续传

                            }
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                }

            } else {
                System.out.print("未知错误文件");
            }

        }
        return "补录解析完成！";
    }

    /**
     * 补录下载在log中存在的部门受案号
     */
    @RequestMapping(value = "/redownQSYJS", method = RequestMethod.GET)
    public String redownQSYJS(HttpServletRequest request){
        String targetPath = "C:\\Users\\Vincent\\Desktop\\最高检调试\\mq_log";
        if(StringUtils.isNotBlank(request.getParameter("path"))) {
            targetPath = request.getParameter("path");
        }
        //return targetPath;
        findInDir(targetPath);
        return "补录下载完成！";
    }

    //统计IP地址
    @RequestMapping(value = "/tj", method = RequestMethod.GET)
    public String tongji(HttpServletRequest request){
        String targetPath = "D:\\test\\tj\\";
        if(StringUtils.isNotBlank(request.getParameter("path"))) {
            targetPath = request.getParameter("path");
        }
        //return targetPath;
        TongJiInDir(targetPath);
        return "补录下载完成！";
    }

    /**
     * 测试卷宗目录文件下载
     */
    @RequestMapping(value = "/testDirGet", method = RequestMethod.GET)
    public String testDirGet(){
        WsMessageObject WsMsgObj = new WsMessageObject();
        WsMsgObj.bmsah = "广东省院刑诉受[2019]440000100029号";
        WsMsgObj.dwbm = "440000";
        WsMsgObj.bsbh = "";
        handler.getFilesByDir(WsMsgObj);
        return "通过文件目录下载文件完成！";
    }

    private void findInDir(String targetPath) {
        try {
            // 创建 File对象
            File file = new File(targetPath);
            // 取 文件/文件夹
            File files[] = file.listFiles();
            // 对象为空 直接返回
            if (files == null) {
                System.out.println("文件夹不存在！");
            }
            // 目录下文件
            if (files.length == 0) {
                System.out.println(targetPath + "该文件夹下没有文件");
            }

            // 存在文件 遍历 判断
            for (File f : files) {
                if (f.isDirectory()) {
                    findInDir(f.getAbsolutePath());

                } else if (f.isFile()) {  // 判断是否为 文件
                    //System.out.print("文件: ");
                    //System.out.println(f.getAbsolutePath());

                    String fileName = f.getName();
                    if (fileName.endsWith("txt") || fileName.endsWith("0") || fileName.endsWith("log")) {
                        FileInputStream fis = new FileInputStream(f.getAbsolutePath());
                        // 防止路径乱码   如果utf-8 乱码  改GBK     eclipse里创建的txt  用UTF-8，在电脑上自己创建的txt  用GBK
                        InputStreamReader isr = new InputStreamReader(fis, "GBK");
                        BufferedReader br = new BufferedReader(isr);
                        String line = "";
                        while ((line = br.readLine()) != null) {
                            //newLog.info(line);
                            //获取【贵州省院刑诉受[2019]520000100233号】下载地址成功，下载地址:
                            if (line.indexOf("下载地址") > 0 && line.indexOf("【") > 0) {
                                newLog.info(line);
                                try {
                                    String fileNamesPath = getQSYJSPath + "filenames.txt";
                                    File fNsfile = new File(fileNamesPath);

                                    // if file doesnt exists, then create it
                                    if (!fNsfile.exists()) {
                                        fNsfile.createNewFile();
                                    }

                                    FileWriter fw = new FileWriter(fNsfile.getAbsolutePath(), true);
                                    BufferedWriter bw = new BufferedWriter(fw);
                                    String bmsah = line.substring(line.indexOf('【') + 1, line.indexOf('】'));
                                    //String url = line.substring(line.indexOf("下载地址:"));
                                    if (line.indexOf("http://") > -1) {
                                        String url = line.substring(line.indexOf("http://"));
                                        bw.write(bmsah + "|" + EncryptionUtils.getMD5(bmsah) + "|" + url + "\r\n");
                                    } else {
                                        bw.write(bmsah + "|" + EncryptionUtils.getMD5(bmsah) + "|" + "NO-URL\r\n");
                                    }
                                    bw.close();
                                    //boolean downOK = HttpUtils.downLoadFromUrl(url,  EncryptionUtils.getMD5(bmsah)+".zip", getQSYJSPath+"new\\", "");
                                    WsMessageObject WsMsgObj = new WsMessageObject();
                                    WsMsgObj.bmsah = bmsah;
                                    WsMsgObj.dwbm = bmsah.substring(bmsah.indexOf(']') + 1, bmsah.indexOf(']') + 7); //"520000"; //"440000"
                                    WsMsgObj.bsbh = "";
                                    handler.requestQSYJS(WsMsgObj);
                                    //System.out.println("Done");

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        br.close();
                        isr.close();
                        fis.close();
                    }

                } else {
                    System.out.print("未知错误文件");
                }
            }
        } catch (Exception ex) {
            newLog.error(ex.getMessage());
        }
    }

    private void TongJiInDir(String targetPath){
        try{
            // 创建 File对象
            File file = new File(targetPath);
            // 取 文件/文件夹
            File files[] = file.listFiles();
            // 对象为空 直接返回
            if(files == null){
                System.out.println("文件夹不存在！");
            }
            Map<String, Integer> kvMap = new HashMap<String, Integer>();
            // 目录下文件
            if(files.length == 0){
                System.out.println(targetPath + "该文件夹下没有文件");
            }

            // 存在文件 遍历 判断
            for (File f : files) {
                if(f.isDirectory()){
                    findInDir(f.getAbsolutePath());
                } else if(f.isFile()){  // 判断是否为 文件
                    String fileName = f.getName();
                    if(fileName.endsWith("txt") || fileName.endsWith("0") || fileName.endsWith("log"))
                    {
                        FileInputStream fis = new FileInputStream(f.getAbsolutePath());
                        // 防止路径乱码   如果utf-8 乱码  改GBK     eclipse里创建的txt  用UTF-8，在电脑上自己创建的txt  用GBK
                        InputStreamReader isr = new InputStreamReader(fis, "GBK");
                        BufferedReader br = new BufferedReader(isr);
                        String line = "";
                        while ((line = br.readLine()) != null) {
                            line = line.trim();
                            if (line.indexOf(".") > 0 ) {
                                if(kvMap.containsKey(line)){
                                    kvMap.put(line, kvMap.get(line)+1);
                                }
                                else{
                                    kvMap.put(line, 1);
                                }
                            }
                        }
                        br.close();
                        isr.close();
                        fis.close();
                    }

                } else {
                    System.out.print("未知错误文件");
                }
            }

            try {
                String fileNamesPath = getQSYJSPath + "tongji.txt";
                File fNsfile = new File(fileNamesPath);
                if (!fNsfile.exists()) {
                    fNsfile.createNewFile();
                }

                FileWriter fw = new FileWriter(fNsfile.getAbsolutePath(), true);
                BufferedWriter bw = new BufferedWriter(fw);

                Iterator iter = kvMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    Object key = entry.getKey();
                    Object val = entry.getValue();
                    bw.write(key + "\t" + val + "\t" + "\r\n");
                }

                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        catch (Exception ex){
            newLog.error(ex.getMessage());
        }
    }



}

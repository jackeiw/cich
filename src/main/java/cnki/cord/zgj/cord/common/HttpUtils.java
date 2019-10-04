package cnki.cord.zgj.cord.common;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HttpUtils {
    /**
     * 从网络Url中下载文件
     * @param urlStr
     * @param fileName
     * @param savePath
     * @param token
     * @throws IOException
     */
    public static boolean downLoadFromUrl(String urlStr,String fileName,String savePath,String token) {
        boolean bRet = true;
        try{
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            //设置超时间为60秒
            conn.setConnectTimeout(60*1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            conn.setRequestProperty("cord_token", token);

            //得到输入流
            InputStream inputStream = conn.getInputStream();
            //获取自己数组
            byte[] getData = readInputStream(inputStream);

            //文件保存位置
            File saveDir = new File(savePath);
            if(!saveDir.exists()){
                saveDir.mkdir();
            }

            File file = new File(saveDir+File.separator+fileName);
            if(file.exists()) {
                file.delete();
                file = new File(saveDir+File.separator+fileName);
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(getData);
            if(fos!=null){
                fos.close();
            }
            if(inputStream!=null){
                inputStream.close();
            }
            System.out.println("info:" + url + " download success!");
        }
        catch (IOException ioEx){
            bRet = false;
        }
        catch(Exception ex) {
            bRet = false;
        }
        return bRet;
    }


    /**
     * 从输入流中获取字节数组
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static  byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    /**
     * 获取http文件大小
     *
     * @param path 待下载的文件地址
     * @return
     * @throws IOException
     */
    public static long getHttpFileLenth(String path) throws IOException {
        long length = 0;
        URL url = new URL(path);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        length = urlConnection.getContentLengthLong();
        urlConnection.disconnect();
        return length;
    }

    public void getUpdateAsset(String abstractURL) {
        Long milliSecond = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli() - 300000;
        String jsonParam = " { \"update\" : " + milliSecond + "}";

        String headersAuthor = sendPostUrlGetLoginToken("operator");
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        CloseableHttpResponse closeableHttpResponse;

        try {
            HttpPost httpPost = new HttpPost(abstractURL);
            httpPost.setHeader("Authorization", "Bearer " + headersAuthor);
            httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
            StringEntity entity = new StringEntity(jsonParam, ContentType.create("text/json", "UTF-8"));
            httpPost.setEntity(entity);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
            httpPost.setConfig(requestConfig);

            closeableHttpResponse = closeableHttpClient.execute(httpPost);

            int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                //TODO:状态码非200代表没有正常返回,此处处理你的业务
            }

            HttpEntity httpEntity = closeableHttpResponse.getEntity();
            String asset_synchronization = EntityUtils.toString(httpEntity, "UTF-8");
            JSONArray jsonArray = JSONArray.parseArray(asset_synchronization);
            if (jsonArray.size() > 0) {
                //TODO:判断返回的json数组是否不为空,此处即可处理你的业务
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String sendPostUrlGetLoginToken(String username) {
        //此处是我获取请求参数的方法,请忽略
        String[] author = authorApi();
        List<NameValuePair> nameValuePairs = new LinkedList<>();
        nameValuePairs.add(new BasicNameValuePair("username", "operator"));
        nameValuePairs.add(new BasicNameValuePair("timestamp", author[0]));
        nameValuePairs.add(new BasicNameValuePair("nonce", author[1]));
        nameValuePairs.add(new BasicNameValuePair("signature", author[2]));
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        CloseableHttpResponse closeableHttpResponse;
        String paramStr;
        try {
            String tokenUrl = "";
            paramStr = EntityUtils.toString(new UrlEncodedFormEntity(nameValuePairs));
            HttpPost httpPost = new HttpPost(appendString(tokenUrl, paramStr));
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
            httpPost.setConfig(requestConfig);
            //sendPostURL.
            closeableHttpResponse = closeableHttpClient.execute(httpPost);
            //HttpCode
            int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                //throw new Exception();//TOKEN_ERROR
            }
            //此处处理状态码200
            HttpEntity httpEntity = closeableHttpResponse.getEntity();
            String isLogin = EntityUtils.toString(httpEntity, "UTF-8");
            return isLogin.substring(1, isLogin.length() - 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String[] authorApi(){
        return new String[]{"", ""};
    }


    /**
     * 把请求参数通过?拼接
     * 示例: 1.1.1.1:80/?uname=1&token=1
     */
    public String appendString(String url, String paramStr) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(url);
        stringBuffer.append("?");
        stringBuffer.append(paramStr);
        return stringBuffer.toString();
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应Code
     */
    public static int sendGetCode(String url, String param) {
        int result = 404;
        try {
            String urlNameString = url + (StringUtils.isNotEmpty(param) ? "?" + param : "");
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            HttpURLConnection  connection = (HttpURLConnection)realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setConnectTimeout(100000);
            // 建立实际的连接
            connection.connect();
            result = connection.getResponseCode();
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        finally {

        }
        return result;
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + (StringUtils.isNotEmpty(param) ? "?" + param : "");
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection  connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }
}
package cnki.cord.zgj.cord.portal;

import com.alibaba.csb.sdk.*;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
public class CSBController {
    private static final Logger newLog = LoggerFactory.getLogger(CSBController.class);
    @Value("${cnkiconf.csb.url}")
    private String csb_url;

    @Value("${cnkiconf.csb.ak}")
    private String csb_ak;

    @Value("${cnkiconf.csb.sk}")
    private String csb_sk;

    @RequestMapping("/api/csb/{service}/{version}/{method}/")
    public Object csbProxyQuerystring(@PathVariable("service") String service,
                              @PathVariable("version") String version,
                              @PathVariable("method") String method,
                              HttpServletRequest request) {
        HttpParameters.Builder builder = new HttpParameters.Builder();
        builder.requestURL(csb_url) // 设置CSB服务地址
                .api(service) // 设置服务名
                .version(version) // 设置版本号
                .method(method)// 设置调用方式, get/post
                .accessKey(csb_ak)
                .secretKey(csb_sk); // 设置accessKey 和 设置secretKey
        HttpReturn ret = null;
        try {
            builder.setContentEncoding(ContentEncoding.gzip);// 设置请求消息压缩

            //添加header
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = (String) headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                if (headerValue.length() != 0) {
                    builder.putHeaderParamsMap(headerName, headerValue);
                }
            }

            Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = (String) paramNames.nextElement();
                String[] paramValues = request.getParameterValues(paramName);
                if (paramValues.length == 1) {
                    String paramValue = paramValues[0];
                    if (paramValue.length() != 0) {
                        //map.put(paramName, paramValue);
                        builder.putParamsMap(paramName, paramValue);
                    }
                }
            }

            ret = HttpCaller.invokeReturn(builder.build());

        } catch (Exception e) {
            // error process
            e.printStackTrace();
        }
        return ret != null ? ret.getResponseStr(): ret;
    }

    @RequestMapping("/api/csb_json/{service}/{version}/{method}")
    public Object csbProxyJson(@PathVariable("service") String service,
                                 @PathVariable("version") String version,
                                 @PathVariable("method") String method, HttpServletRequest request) {

        HttpParameters.Builder builder = new HttpParameters.Builder();
        builder.requestURL(csb_url) // 设置CSB服务地址
                .api(service) // 设置服务名
                .version(version) // 设置版本号
                .method(method) // 设置调用方式, get/post
                .accessKey(csb_ak)
                .secretKey(csb_sk); // 设置accessKey 和 设置secretKey
        HttpReturn ret = null;
        try {
            //builder.setContentEncoding(ContentEncoding.gzip);// 设置请求消息压缩
            //添加header
            Enumeration<String> headerNames = GetHeaders(service);
            while (headerNames.hasMoreElements()) {
                String headerName = (String) headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                if (headerValue.length() != 0) {
                    builder.putHeaderParamsMap(headerName, headerValue);
                }
            }
            //builder.putHeaderParamsMap("systemid", "getQSYJSSystemId").putHeaderParamsMap("token", "getQSYJSToken");

            Map<String, String> kvMap = new HashMap<String, String>();
            Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = (String) paramNames.nextElement();
                String[] paramValues = request.getParameterValues(paramName);
                if (paramValues.length == 1) {
                    String paramValue = paramValues[0];
                    if (paramValue.length() != 0) {
                        kvMap.put(paramName, paramValue);
                    }
                }
            }
            //kvMap.put("bmsah", "贵州省院刑诉受[2019]520000100046号"); //注意需要编码

            //builder.putParamsMap("model", JSON.toJSONString(kvMap));
            // 设置请求参数
            builder.contentBody(new ContentBody(JSON.toJSONString(kvMap)));

            // 设置请求参数
            ret = HttpCaller.invokeReturn(builder.build());

        } catch (Exception e) {
            // error process
            e.printStackTrace();
        }
        return ret != null ? ret.getResponseStr(): ret;
    }

    @RequestMapping("/api/csb_json1/{service}/{version}/{method}")
    public Object csbProxyJsonNew(@PathVariable("service") String service,
                               @PathVariable("version") String version,
                               @PathVariable("method") String method, HttpServletRequest request) {
        //newLog.info("请求地址是：" + request.getRequestURL().toString() + "?" + request.getQueryString());
        HttpParameters.Builder builder = new HttpParameters.Builder();
        builder.requestURL(csb_url) // 设置CSB服务地址
                .api(service) // 设置服务名
                .version(version) // 设置版本号
                .method(method) // 设置调用方式, get/post
                .accessKey(csb_ak)
                .secretKey(csb_sk); // 设置accessKey 和 设置secretKey
        HttpReturn ret = null;
        try {
            //builder.setContentEncoding(ContentEncoding.gzip);// 设置请求消息压缩
            //添加header，使用queryString传值
            Enumeration<String> headerNames = GetHeaders(service);
            while (headerNames.hasMoreElements()) {
                String headerName = (String) headerNames.nextElement();
                String[] headerValues = request.getParameterValues(headerName);
                if (headerValues.length == 1) {
                    String headerValue = headerValues[0];
                    if (headerValue.length() != 0) {
                        builder.putHeaderParamsMap(headerName, headerValue);
                    }
                }
            }
            //builder.putHeaderParamsMap("systemid", "getQSYJSSystemId").putHeaderParamsMap("token", "getQSYJSToken");

            Map<String, String> kvMap = new HashMap<String, String>();
            Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = (String) paramNames.nextElement();
                String[] paramValues = request.getParameterValues(paramName);
                if (paramValues.length == 1) {
                    String paramValue = paramValues[0];
                    if (paramValue.length() != 0) {
                        kvMap.put(paramName, paramValue);
                    }
                }
            }
            //kvMap.put("bmsah", "贵州省院刑诉受[2019]520000100046号"); //注意需要编码

            //builder.putParamsMap("model", JSON.toJSONString(kvMap));
            // 设置请求参数
            builder.contentBody(new ContentBody(JSON.toJSONString(kvMap)));

            // 设置请求参数
            ret = HttpCaller.invokeReturn(builder.build());

        } catch (Exception e) {
            // error process
            e.printStackTrace();
        }
        newLog.info("请求地址是：" + request.getRequestURL().toString() + "?" + request.getQueryString() + "\n返回数据："+(ret != null ? ret.getResponseStr(): "==空=="));
        return ret != null ? ret.getResponseStr(): ret;
    }

    @Value("${cnkiconf.csb.otherapis}")
    private String otherapis;
    //获取api需要拿到的headers
    private Enumeration<String> GetHeaders(String apiName) {
        Vector<String> vetor = new Vector<>();
        if(StringUtils.isNotEmpty(apiName)) {
            if(StringUtils.isNotEmpty(otherapis)){
                String[] tempValues = otherapis.split("\\$\\$");
                for(int i=0;i<tempValues.length;i++){
                    if(StringUtils.isNotEmpty(tempValues[i])){
                        String[] temp = tempValues[i].split("\\|");
                        if(temp.length == 2){
                            if(temp[0].trim().equals(apiName)){
                                String[] headStr = temp[1].split("\\+");
                                for(int j=0;j<headStr.length;j++){
                                    vetor.add(headStr[j]);
                                }
                            }
                        }
                    }
                }
            }
        }
        return vetor.elements();
    }
}

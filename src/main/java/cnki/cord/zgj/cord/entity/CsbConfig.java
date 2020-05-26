package cnki.cord.zgj.cord.entity;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cnkiconf.csb")
public class CsbConfig {
    private String url;
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    private String ak;
    public String getAk() {
        return ak;
    }
    public void setAk(String ak) {
        this.ak = ak;
    }

    private String sk;
    public String getSk() {
        return sk;
    }
    public void setSk(String sk) {
        this.sk = sk;
    }

    private String getQSYJS;
    public String getGetQSYJS() {
        return getQSYJS;
    }
    public void setGetQSYJS(String getQSYJS) {
        this.getQSYJS = getQSYJS;
    }

    private String getQSYJSVersion;
    public String getGetQSYJSVersion() {
        return getQSYJSVersion;
    }
    public void setGetQSYJSVersion(String getQSYJSVersion) {
        this.getQSYJSVersion = getQSYJSVersion;
    }

    private String getQSYJSMethon;
    public String getQSYJSMethon() {
        return getQSYJSMethon;
    }
    public void setGetQSYJSMethon(String getQSYJSMethon) {
        this.getQSYJSMethon = getQSYJSMethon;
    }

    private String getQSYJSPath;
    public String getGetQSYJSPath() {
        return getQSYJSPath;
    }
    public void setGetQSYJSPath(String getQSYJSPath) {
        this.getQSYJSPath = getQSYJSPath;
    }

    private String sendFilepathURL;
    public String getSendFilepathURL() {
        return sendFilepathURL;
    }
    public void setSendFilepathURL(String sendFilepathURL) {
        this.sendFilepathURL = sendFilepathURL;
    }

    private String qsyjsCalMQ;
    public String getQsyjsCalMQ() {
        return qsyjsCalMQ;
    }
    public void setQsyjsCalMQ(String qsyjsCalMQ) {
        this.qsyjsCalMQ = qsyjsCalMQ;
    }

    private String jzCalMQ;
    public String getJzCalMQ() {
        return jzCalMQ;
    }
    public void setJzCalMQ(String jzCalMQ) { this.jzCalMQ = jzCalMQ; }

}

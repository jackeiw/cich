package cnki.cord.zgj.cord.entity;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cnkiconf")
public class CnkiConf {
    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private CsbConfig csb;
    public CsbConfig getCsb(){ return csb; }
    public void setCsb(CsbConfig csb){ this.csb = csb; }
}

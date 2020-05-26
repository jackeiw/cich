package cnki.cord.zgj.cord.entity;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties(prefix = "cnkiconf.rabbitmq")
public class RabbitMQConfig {
    private String server;
    public String getServer() {
        return server;
    }
    public void setServer(String server) {
        this.server = server;
    }

    private int port;
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @see [说明这个方法]
     * @return
     * @since 2020年05月26日 上午10:38:49
     */
    @Override
    public String toString() {
        return "ConfigProperties [server=" + server + ", port=" + port + "]";
    }
}

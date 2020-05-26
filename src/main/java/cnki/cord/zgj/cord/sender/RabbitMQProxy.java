package cnki.cord.zgj.cord.sender;

import cnki.cord.zgj.cord.entity.CnkiConf;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component
public class RabbitMQProxy {
    private static final Logger log = LoggerFactory.getLogger(RabbitMQProxy.class);

    @Autowired
    private Environment env;

    @Autowired
    private CnkiConf cnkiConf;

    @Value("${cnkiconf.rabbitmq.server}")
    private String server;

    @Value("${cnkiconf.rabbitmq.port}")
    private int port;

    public void sendQueueMessage(String queueName,String message) {
        ConnectionFactory factory = new ConnectionFactory();
        if(server == null || server == ""){
            server = "localhost";
        }
        if(port == 0){
            port = 5672;
        }
        factory.setHost(server);
        factory.setPort(port);
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(queueName, false, false, false, null);
            channel.basicPublish("", queueName, null, message.getBytes());
            log.info(" [x] Sent Queue: [" + queueName + "],Message:'" + message + "'");
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }
}

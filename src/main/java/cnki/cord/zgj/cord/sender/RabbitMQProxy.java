package cnki.cord.zgj.cord.sender;

import cnki.cord.zgj.cord.common.QsyjsFileHandler;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
public class RabbitMQProxy {
    private static final Logger log = LoggerFactory.getLogger(RabbitMQProxy.class);
    @Value("${cnkiconf.rabbitMQ.server}")
    private String rabbitMQServer;

    @Value("${cnkiconf.rabbitMQ.port}")
    private int rabbitMQPort;

    public void sendQueueMessage(String queueName,String message) {
        ConnectionFactory factory = new ConnectionFactory();
        if(rabbitMQServer == null || rabbitMQServer == ""){
            rabbitMQServer = "localhost";
        }
        if(rabbitMQPort == 0){
            rabbitMQPort = 5672;
        }
        factory.setHost(rabbitMQServer);
        factory.setPort(rabbitMQPort);
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

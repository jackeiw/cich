package cnki.cord.zgj.cord.sender;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQProxy {
    @Value("${cnkiconf.rabbitMQ}")
    private String rabbitMQServer;

    private final static String QUEUE_NAME = "hello";
    public void sendAsc() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.107.98");
        //factory.setPort(2222);
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = "Hello World!2000";
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }
}

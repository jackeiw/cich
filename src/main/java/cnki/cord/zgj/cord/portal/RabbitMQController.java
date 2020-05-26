package cnki.cord.zgj.cord.portal;

import cnki.cord.zgj.cord.sender.RabbitMQProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/RMQ")
public class RabbitMQController {
    private final static String QUEUE_NAME = "hello";

    @GetMapping("test")
    public String sendAsc() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.107.98");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = "Hello World!2000";
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
            return "it's OK!";
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @Value("${cnkiconf.rabbitmq.server}")
    private String rabbitMQServer;

    @Value("${cnkiconf.rabbitmq.port}")
    private int rabbitMQPort;

    @Autowired
    RabbitMQProxy rabbitMQProxy;

    @GetMapping("test1")
    public String sendAsc1() {
        try {
            rabbitMQProxy.sendQueueMessage("hello1", "努力奋斗！");
            return "it's OK!";
        }
        catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

}

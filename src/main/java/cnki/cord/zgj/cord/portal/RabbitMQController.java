package cnki.cord.zgj.cord.portal;

import cnki.cord.zgj.cord.sender.RabbitMQProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.*;

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
        factory.setHost(rabbitMQServer);
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

    @RequestMapping(value ="test1/{mq}/{msg}", method = RequestMethod.GET)
    public String sendAsc1(@PathVariable("mq")  String mq, @PathVariable("msg")  String msg) {
        try {
            //rabbitMQProxy.sendQueueMessage("hello1", "努力奋斗！");
            rabbitMQProxy.sendQueueMessage(mq, msg);
            return "it's OK!";
        }
        catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }


    @Value("${cnkiconf.csb.qsyjsCalMQ}")
    private String qsyjsCalMQ;

    @GetMapping("more")
    public String sendMore() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitMQServer);
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(qsyjsCalMQ, false, false, false, null);
            for(int i=0; i<10000; i++)
            {
                String message = String.valueOf(i);
                //String message = "Hello World!2020,i am " + i;
                channel.basicPublish("", qsyjsCalMQ, null, message.getBytes());
                System.out.println(" [x] Sent '" + message + "'");
            }
            return "it's OK!";
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @Value("${cnkiconf.csb.jzCalMQ}")
    private String jzCalMQ;

    @GetMapping("jz")
    public String sendMoreJz() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitMQServer);
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(jzCalMQ, false, false, false, null);
            for(int i=0; i<10000; i++)
            {
                String message = "JZ_" + i;
                channel.basicPublish("", jzCalMQ, null, message.getBytes());
                System.out.println(" [x-JZ] Sent '" + message + "'");
            }
            return "it's OK!";
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}

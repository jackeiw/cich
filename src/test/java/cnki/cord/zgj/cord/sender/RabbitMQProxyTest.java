package cnki.cord.zgj.cord.sender;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class RabbitMQProxyTest {
    @Autowired
    RabbitMQProxy handler;

    @Test
    public void sendQueueMessage() {
        handler.sendQueueMessage("hello", "努力奋斗！");
    }
}
package cnki.cord.zgj.cord.sender;

import org.junit.Test;

import static org.junit.Assert.*;

public class RabbitMQProxyTest {

    @Test
    public void sendQueueMessage() {
        RabbitMQProxy handler = new RabbitMQProxy();
        handler.sendQueueMessage("hello", "努力奋斗！");
    }
}
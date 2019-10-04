package cnki.cord.zgj.cord.sender;

import cnki.cord.zgj.cord.receiver.WenshuMsgReceiver;
import com.tyyw.lcba.messagesystem.api.factory.MessageClientFactory;
import com.tyyw.lcba.messagesystem.api.model.MessageSendResult;
import com.tyyw.lcba.messagesystem.api.sender.common.CommonSenderClient;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/MqSender")
@Slf4j
@Service
public class MqSenderController {
    private static final Logger log = LoggerFactory.getLogger(WenshuMsgReceiver.class);

    @GetMapping("sendAsc")
    public String sendAsc() {
        return null;
    }

    @GetMapping("sendCommon")
    public String sendCommon() {
        commonSend();
        return null;
    }


    /**
     * 发送普通消息
     * @return
     */
    public MessageSendResult commonSend() {

        String firstTopic = "ZNBM_BMWC";
        String message = "测试根据一级主题发送普通消息";

        // 获取普通消息发送者对象 该例子默认为获取本单位下部署的MQ服务
        // 即该发送者对象发送消息的目的地为本单位部署的mq服务
        CommonSenderClient senderClient = MessageClientFactory.getDefaultMessageCommonSenderClient();

        log.info("创建成功-发送者：" + senderClient);

        // 发送普通消息
        // 第一个参数：一级主题名称
        // 第二个参数：消息内容
        // 消息内容可以是字符串或者字节数组
        MessageSendResult result = senderClient.send(firstTopic, message);

        // 接收普通消息请参考：com.tfswx.tyyw.demo.receiver.CommonMessageReceiver类

        log.info("发送状态码code：" + result.getCode());

        return result;
    }


    /**
     * 发送普通消息
     * @return
     */
    public MessageSendResult commonOtherSend() {

        String firstTopic = "COMMON_OTHER_TOPIC";
        String secondTopic = "COMMON_SECOND_TOPIC";
        String message = "测试根据一级主题和二级主题发送普通消息";

        // 获取普通消息发送者对象 该例子默认为获取本单位下部署的MQ服务
        // 即该发送者对象发送消息的目的地为本单位部署的mq服务
        CommonSenderClient senderClient = MessageClientFactory.getDefaultMessageCommonSenderClient();

        log.info("创建成功-发送者：" + senderClient);

        // 发送普通消息
        // 第一个参数：一级主题名称
        // 第二个参数：二级主题名称
        // 第三个参数：消息内容
        // 消息内容可以是字符串或者字节数组
        MessageSendResult result = senderClient.send(firstTopic, secondTopic, message);

        // 接收普通消息请参考：com.tfswx.tyyw.demo.receiver.CommonSecondMessageReceiver类

        log.info("发送状态码code：" + result.getCode());

        return result;
    }
}

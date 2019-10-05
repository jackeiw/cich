package cnki.cord.zgj.cord.receiver;

import cnki.cord.zgj.cord.common.QsyjsFileHandler;
import cnki.cord.zgj.cord.entity.BdMessageObject;
import com.tyyw.lcba.messagesystem.api.model.TYYWMessage;
import com.tyyw.lcba.messagesystem.api.receiver.annotation.MsgReceiveStation;
import com.tyyw.lcba.messagesystem.api.receiver.annotation.Receiver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@MsgReceiveStation(type = "COMMON")
@Component
@Slf4j
public class BDAJReceiverProxy {
    private static final Logger newLog = LoggerFactory.getLogger(BDAJReceiverProxy.class);

    @Autowired
    QsyjsFileHandler handler;

    // 发送普通消息请参考：com.tfswx.tyyw.demo.sender.proxy.CommonSenderProxy.commonSend()方法
    // 通过自定义注解类Receiver设置需要接收消息的一级主题名称topic，二级主题名称subtopic默认为"*"
    @Receiver(topic = "DZJZ_BDAJ")
    public void receive(BdMessageObject BdMsgObj, TYYWMessage tyywMessage) {
        newLog.info(Thread.currentThread().getName() + "，收到DZJZ_BDAJ消息：" + tyywMessage.getMessage()
                + "，消息ID：" + tyywMessage.getMsgID());

        if (StringUtils.isNotBlank(BdMsgObj.bmsah)){ //TODO:部门受案号传过来可能为乱码，需要处理一下。
            //QsyjsFileHandler handler = new QsyjsFileHandler();
            handler.requestQSYJS(BdMsgObj);
        }
    }
}

package cnki.cord.zgj.cord.receiver;

import cnki.cord.zgj.cord.entity.WsMessageObject;
import cnki.cord.zgj.cord.common.QsyjsFileHandler;
import com.tyyw.lcba.messagesystem.api.model.TYYWMessage;
import com.tyyw.lcba.messagesystem.api.receiver.annotation.MsgReceiveStation;
import com.tyyw.lcba.messagesystem.api.receiver.annotation.Receiver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// 通过自定义注解类MsgReceiveStation设置需要接收消息类型
// COMMON--普通消息、异步消息、事务消息
// ORDER--顺序消息
// BATCH--批量消息
@MsgReceiveStation(type = "COMMON")
@Component
@Slf4j
public class WenshuMsgReceiverProxy {
    private static final Logger newLog = LoggerFactory.getLogger(WenshuMsgReceiverProxy.class);

    @Autowired
    QsyjsFileHandler handler;

    // 发送普通消息请参考：com.tfswx.tyyw.demo.sender.proxy.CommonSenderProxy.commonSend()方法
    // 通过自定义注解类Receiver设置需要接收消息的一级主题名称topic，二级主题名称subtopic默认为"*"
    @Receiver(topic = "ZNBM_BMWC")
    public void receive(WsMessageObject WsMsgObj, TYYWMessage tyywMessage) {
        newLog.info(Thread.currentThread().getName() + "，收到ZNBM_BMWC消息：" + tyywMessage.getMessage()
                + "，消息ID：" + tyywMessage.getMsgID());

        if (StringUtils.isNotBlank(WsMsgObj.bmsah)){ //TODO:部门受案号传过来可能为乱码，需要处理一下。
            //QsyjsFileHandler handler = new QsyjsFileHandler();
            handler.requestQSYJS(WsMsgObj);
        }
    }

}

package cnki.cord.zgj.cord.receiver;


import cnki.cord.zgj.cord.entity.WsMessageObject;
import cnki.cord.zgj.cord.common.QsyjsFileHandler;
import com.alibaba.fastjson.JSONObject;
import com.tyyw.lcba.messagesystem.api.model.TYYWMessage;
import com.tyyw.lcba.messagesystem.api.receiver.AbstractMessageListener;
import com.tyyw.lcba.messagesystem.api.receiver.annotation.MsgReceiver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@MsgReceiver(topic = "CNKI_TEST")
@Component
@Slf4j
public class WenshuMsgReceiver  extends AbstractMessageListener {
    private static final Logger newLog = LoggerFactory.getLogger(WenshuMsgReceiver.class);

    @Autowired
    QsyjsFileHandler handler;

    @Override
    public void receive(TYYWMessage tyywMessage) throws Exception {
        System.out.println(Thread.currentThread().getName() + "，收到消息：" + tyywMessage.getMessage()
                + "，消息ID：" + tyywMessage.getMsgID());

        newLog.info(Thread.currentThread().getName() + "，收到消息：" + tyywMessage.getMessage()
                + "，消息ID：" + tyywMessage.getMsgID());

        String jsonData = tyywMessage.getMessage();
        JSONObject jsonObject = JSONObject.parseObject(jsonData);
        WsMessageObject wsMsgObject = new WsMessageObject();
        wsMsgObject.bsbh = jsonObject.getString("bsbh");//标识编号
        wsMsgObject.bmsah = jsonObject.getString("bmsah");//部门受案号
        wsMsgObject.dwbm = jsonObject.getString("dwbm");//单位编码

        if (StringUtils.isNotBlank(wsMsgObject.bmsah)){
            //QsyjsFileHandler handler = new QsyjsFileHandler();
            handler.requestQSYJS(wsMsgObject);
        }

    }


}

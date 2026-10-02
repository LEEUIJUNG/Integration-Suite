import com.sap.gateway.ip.core.customdev.util.Message
import java.util.HashMap;
import org.apache.camel.impl.DefaultAttachment
import javax.mail.util.ByteArrayDataSource

def Message processData(Message message) {
    def properties = message.getProperties();
    def body = message.getBody(String);

    def messageLog = messageLogFactory.getMessageLog(message)
    
    if (messageLog != null){
        messageLog.addAttachmentAsString("Where-Used_Report-QAS.CSV", body, 'text/plain')    
    }
    
    return message;
}
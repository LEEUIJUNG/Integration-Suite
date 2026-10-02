import com.sap.gateway.ip.core.customdev.util.Message
import java.util.HashMap;
import org.apache.camel.impl.DefaultAttachment
import javax.mail.util.ByteArrayDataSource

def Message processData(Message message) {
    def properties = message.getProperties();
    def KSTLogStart = properties.get("KSTLogStart");
    def KSTLogEnd = properties.get("KSTLogEnd");
    def body = message.getBody(String);

    def messageLog = messageLogFactory.getMessageLog(message)
    if (messageLog != null) {
        messageLog.addAttachmentAsString(KSTLogStart+"-"+KSTLogEnd+"_Message-Status-Overview_Report.CSV", body, 'text/plain')
    }
    return message;
}
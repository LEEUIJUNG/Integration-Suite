import com.sap.gateway.ip.core.customdev.util.Message
import java.util.HashMap;
import org.apache.camel.impl.DefaultAttachment
import javax.mail.util.ByteArrayDataSource

def Message processData(Message message) {
    def body = message.getBody(String);

    def messageLog = messageLogFactory.getMessageLog(message)
    if (messageLog != null) {
        messageLog.addAttachmentAsString("401_TEST_Report.CSV", body, 'text/plain')
    }
    return message;
}
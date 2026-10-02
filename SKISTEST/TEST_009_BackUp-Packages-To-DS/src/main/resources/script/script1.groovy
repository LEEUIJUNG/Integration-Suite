import com.sap.gateway.ip.core.customdev.util.Message
import java.util.HashMap;
import org.apache.camel.impl.DefaultAttachment
import groovy.json.JsonOutput

def Message processData(Message message) {
    def body = message.getBody(String);
    def headers = message.getHeaders();

    def messageLog = messageLogFactory.getMessageLog(message)
    println '{ "headers" : '
    println '"body": '

    if (messageLog != null){
        messageLog.addAttachmentAsString("Error_Log", '{ "headers" : ' + JsonOutput.toJson(headers) +', "body": '+ body + '}', 'text/plain')    
    }
    
    return message;
}
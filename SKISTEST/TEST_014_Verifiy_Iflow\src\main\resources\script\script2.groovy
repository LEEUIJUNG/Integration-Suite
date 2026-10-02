import com.sap.gateway.ip.core.customdev.util.Message

def Message processData(Message message) {
    def properties = message.getProperties();
    def body = message.getBody(String);

    def messageLog = messageLogFactory.getMessageLog(message)
    
    if (messageLog != null){
        messageLog.addAttachmentAsString("after", body, 'text/plain')    
    }
    
    return message;
}

import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;
import groovy.xml.*;
import java.util.*;
import java.text.SimpleDataFormat;

def Message ProcessData(Message message){

def body = message.getBody(String.class);
def SourceMessage                = new XmlParser().parseText(body);
    def QueuesExhausted = "";
    def QueuesCritical = "";

     SourceMessage.QueueState.each {
         value = it.text();
         state = it.State.text()
         name = it.Name.text()
         if ("1".equals(state)) {
             QueuesCritical = QueuesCritical +' '+ name
         }
     }
     
    SourceMessage.QueueState.each {
         value = it.text();
         state = it.State.text()
         name = it.Name.text()
         if ("2".equals(state)) {
             QueuesExhausted = QueuesExhausted +' '+ name
         }
     }

    message.setProperty("QueuesCriticalNames", QueuesCritical);
    message.setProperty("QueuesExhaustedNames", QueuesExhausted);
    return message;

}
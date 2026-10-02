/* Refer the link below to learn more about the use cases of script.
https://help.sap.com/viewer/368c481cd6954bdfa5d0435479fd4eaf/Cloud/ko/148851bf8192412cba1f9d2c17f4bd25.html

If you want to know more about the SCRIPT APIs, refer the link below
https://help.sap.com/doc/a56f52e1a58e4e2bac7f7adbf45b2e26/Cloud/ko/index.html */
import com.sap.gateway.ip.core.customdev.util.Message
import org.apache.camel.*
import org.osgi.framework.*
import java.util.zip.*
import java.io.*

def Message processData(Message message) {
    def logStep = message.getProperty('Step')
    def messageLog = messageLogFactory.getMessageLog(message)

    if (messageLog != null){
        def errorMap = [
                'B' : {return "B,C,D 로그 없음!"},
                'C' : {return "C,D 로그 없음!"},
                'D' : {return "D 로그 없음!"},
            ]
        def action = errorMap.get(logStep,{return "로그 파일 없음!"})
        messageLog.addAttachmentAsString("Error.txt", action(), 'text/plain')    
    }
    message.setBody(null)
    return message
}

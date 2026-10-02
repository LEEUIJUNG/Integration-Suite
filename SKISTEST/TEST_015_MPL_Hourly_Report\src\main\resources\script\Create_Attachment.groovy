import com.sap.gateway.ip.core.customdev.util.Message
import java.text.SimpleDateFormat;
import java.util.Date.*
import java.util.TimeZone
def Message processData(Message message) {
    def properties = message.getProperties();

    def LogStart = properties.get("LogStart");
    def LogEnd = properties.get("LogEnd");
    
    String dateFormat = "yyyy-MM-dd'T'HH:mm:ss"

    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat)
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"))
    
    Date dateStart = sdf.parse(LogStart)
    Date dateEnd = sdf.parse(LogEnd)

    def kstLogStart = dateStart.format(dateFormat,TimeZone.getTimeZone("Asia/Seoul"))
    def kstLogEnd = dateEnd.format(dateFormat,TimeZone.getTimeZone("Asia/Seoul"))

    def body = message.getBody(String);

    def messageLog = messageLogFactory.getMessageLog(message)
    if (messageLog != null) {
        messageLog.addAttachmentAsString(kstLogStart+"-"+kstLogEnd+"_MPL_Report.CSV", body, 'text/plain')
    }
    return message;
}

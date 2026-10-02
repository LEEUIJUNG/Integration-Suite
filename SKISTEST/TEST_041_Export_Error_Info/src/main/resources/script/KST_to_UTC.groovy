import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;
import groovy.xml.*;
import java.text.SimpleDateFormat;
import java.util.Date.*
import java.util.TimeZone

def Message processData(Message message) {
    def properties = message.getProperties();
    String LogStart = properties.get("LogStart");  
    String LogEnd = properties.get("LogEnd");

    message.setProperty("KSTLogStart",LogStart)
    message.setProperty("KSTLogEnd",LogEnd)


    String dateFormat = "yyyy-MM-dd'T'HH:mm:ss"

    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat)
    sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"))

    Date dateStart = sdf.parse(LogStart)
    Date dateEnd = sdf.parse(LogEnd)

    message.setProperty("LogStart",dateStart.format(dateFormat,TimeZone.getTimeZone("UTC")))
    message.setProperty("LogEnd",dateEnd.format(dateFormat,TimeZone.getTimeZone("UTC")))

    return message;
}
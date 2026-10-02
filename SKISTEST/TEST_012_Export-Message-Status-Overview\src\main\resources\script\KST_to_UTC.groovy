import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;
import groovy.xml.*;
import java.text.SimpleDateFormat;
import java.util.Date.*
import java.util.TimeZone

def Message processData(Message message) {
    def properties = message.getProperties();
    String logStart = properties.get("logStart");  
    String logEnd = properties.get("logEnd");

    message.setProperty("KSTLogStart",logStart)
    message.setProperty("KSTLogEnd",logEnd)


    String dateFormat = "yyyy-MM-dd'T'HH:mm:ss"

    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat)
    sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"))

    Date dateStart = sdf.parse(logStart)
    Date dateEnd = sdf.parse(logEnd)

    message.setProperty("logStart",dateStart.format(dateFormat,TimeZone.getTimeZone("UTC")))
    message.setProperty("logEnd",dateEnd.format(dateFormat,TimeZone.getTimeZone("UTC")))

    return message;
}
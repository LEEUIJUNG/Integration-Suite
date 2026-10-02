import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;
import groovy.xml.*;
import java.text.SimpleDateFormat;
import java.util.Date.*
import java.util.TimeZone
import org.w3c.dom.Node;

import java.io.StringWriter;
import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;
def Message processData(Message message) {
    
    def properties = message.getProperties();
    Node mplLogStart = properties.get("mplLogStart");
    Node mplLogEnd = properties.get("mplLogEnd");
    
    String mplLogStartStr = mplLogStart.getTextContent()
    String mplLogEndStr = mplLogEnd.getTextContent()
    
    String dateFormat = "yyyy-MM-dd'T'HH:mm:ss"

    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat)
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"))
    
    Date dateStart = sdf.parse(mplLogStartStr[0..-4])
    Date dateEnd = sdf.parse(mplLogEndStr[0..-4])

    message.setProperty("mplLogStart","<LogStart>"+dateStart.format(dateFormat,TimeZone.getTimeZone("Asia/Seoul"))+"</LogStart>")
    message.setProperty("mplLogEnd","<LogEnd>"+dateEnd.format(dateFormat,TimeZone.getTimeZone("Asia/Seoul"))+"</LogEnd>")
    
    return message;
}
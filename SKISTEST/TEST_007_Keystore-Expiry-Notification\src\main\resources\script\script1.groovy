import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;
import groovy.xml.*;
import java.text.SimpleDateFormat;
import java.util.Date.*;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
def Message processData(Message message) {
    
    def map = message.getHeaders();
    
    String getCertExpirydate = map.get("CertExpiryDate");
    
    Date CertExpirydate = new SimpleDateFormat("yyyy-MM-dd").parse(getCertExpirydate);

    Date dateNow = new Date();
    
    long dateDiff = CertExpirydate.getTime() - dateNow.getTime();
  
    def daysToExpire = TimeUnit.MILLISECONDS.toDays(dateDiff);

    message.setHeader("daysToExpire", daysToExpire);
    
    return message;
}
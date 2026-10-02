import com.sap.gateway.ip.core.customdev.util.Message;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

def Message processData(Message message) {
    def properties = message.getProperties();

    // 날짜 포맷 설정
    String dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
    SimpleDateFormat sdfKST = new SimpleDateFormat(dateFormat)
    sdfKST.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"))

    // 어제 날짜 기준 Calendar 생성
    Calendar calStart = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
    calStart.add(Calendar.DATE, -1)
    calStart.set(Calendar.HOUR_OF_DAY, 9)
    calStart.set(Calendar.MINUTE, 0)
    calStart.set(Calendar.SECOND, 0)

    Calendar calEnd = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
    calEnd.add(Calendar.DATE, -1)
    calEnd.set(Calendar.HOUR_OF_DAY, 9)
    calEnd.set(Calendar.MINUTE, 05)
    calEnd.set(Calendar.SECOND, 0)

    // KST 기준 날짜 문자열 생성
    String logStartKST = sdfKST.format(calStart.getTime())
    String logEndKST = sdfKST.format(calEnd.getTime())

    message.setProperty("KSTLogStart", logStartKST)
    message.setProperty("KSTLogEnd", logEndKST)

    // UTC 포맷으로 변환
    SimpleDateFormat sdfUTC = new SimpleDateFormat(dateFormat)
    sdfUTC.setTimeZone(TimeZone.getTimeZone("UTC"))

    String logStartUTC = sdfUTC.format(calStart.getTime())
    String logEndUTC = sdfUTC.format(calEnd.getTime())

    message.setProperty("LogStart", logStartUTC)
    message.setProperty("LogEnd", logEndUTC)

    return message;
}

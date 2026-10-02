/* Refer the link below to learn more about the use cases of script.
https://help.sap.com/viewer/368c481cd6954bdfa5d0435479fd4eaf/Cloud/ko/148851bf8192412cba1f9d2c17f4bd25.html

If you want to know more about the SCRIPT APIs, refer the link below
https://help.sap.com/doc/a56f52e1a58e4e2bac7f7adbf45b2e26/Cloud/ko/index.html */
import com.sap.gateway.ip.core.customdev.util.Message
import org.apache.camel.*
import org.osgi.framework.*
import java.util.zip.*
import java.util.Base64
import java.io.*

def Message processData(Message message) {
    // 1. Base64 문자열 가져오기
    def base64Zip = message.getBody(String)
    base64Zip = base64Zip.replaceAll("\\s+", "")  // 줄바꿈 및 공백 제거
    message.setBody(base64Zip)
    return message
}

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
    def logName = message.getProperty('entryId')
    byte[] zipBytes = message.getBody(byte[])

    // 2. ZIP 압축 해제 (메모리에서 처리)
    def zipContent = [:]  // Map<String, byte[]>
    def zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))
    ZipEntry entry
    
    def messageLog = messageLogFactory.getMessageLog(message)

    while ((entry = zis.nextEntry) != null) {
        if (!entry.isDirectory()) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream()
            byte[] buffer = new byte[1024]
            int len
            while ((len = zis.read(buffer)) != -1) {
                baos.write(buffer, 0, len)
            }
            zipContent[entry.name] = baos.toByteArray()
        }
    }
    zis.close()

    // 3. body 내용 추출
    String targetPath = "body"
    if (zipContent.containsKey(targetPath)) {
        String content = new String(zipContent[targetPath], "UTF-8")
        if (messageLog != null){
            messageLog.addAttachmentAsString(logName+ ".txt", content, 'text/plain')    
        }
        message.setBody(null)
    } else {
        throw new FileNotFoundException("파일 body 를 ZIP 안에서 찾을 수 없습니다.")
    }
    return message
}

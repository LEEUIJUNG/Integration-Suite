/* Refer the link below to learn more about the use cases of script.
https://help.sap.com/viewer/368c481cd6954bdfa5d0435479fd4eaf/Cloud/ko/148851bf8192412cba1f9d2c17f4bd25.html

If you want to know more about the SCRIPT APIs, refer the link below
https://help.sap.com/doc/a56f52e1a58e4e2bac7f7adbf45b2e26/Cloud/ko/index.html */
import com.sap.gateway.ip.core.customdev.util.Message
import groovy.json.JsonOutput
import org.apache.camel.*
import org.osgi.framework.*
import com.sap.gateway.ip.core.customdev.util.Message
import java.util.zip.*
import java.util.Base64
import java.io.*

def Message processData(Message message) {
    // 1. Base64 문자열 가져오기
    def base64Zip = message.getBody(String)
    base64Zip = base64Zip.replaceAll("\\s+", "")  // 줄바꿈 및 공백 제거

    // 2. Base64 디코딩 → byte[]
    byte[] zipBytes = Base64.decoder.decode(base64Zip)

    String sourceIflowID = message.getProperty("sourceIflowID");
    String targetIflowID = message.getProperty("targetIflowID");


    // 2. ZIP 압축 해제 (메모리에서 처리)
    def zipContent = [:]  // Map<String, byte[]>
    def zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))
    ZipEntry entry

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

    // 3. A1-SourceIFlow/META-INF/MANIFEST.MF 내용 수정
    String targetPath = "META-INF/MANIFEST.MF"
    if (zipContent.containsKey(targetPath)) {
        String content = new String(zipContent[targetPath], "UTF-8")
        content = content.replaceAll(sourceIflowID, targetIflowID)
        zipContent[targetPath] = content.getBytes("UTF-8")
    } else {
        throw new FileNotFoundException("파일 META-INF/MANIFEST.MF 를 ZIP 안에서 찾을 수 없습니다.")
    }

    // 5. 다시 ZIP으로 압축 (메모리 내)
    ByteArrayOutputStream newZipBaos = new ByteArrayOutputStream()
    ZipOutputStream zos = new ZipOutputStream(newZipBaos)

    zipContent.each { name, data ->
        zos.putNextEntry(new ZipEntry(name))
        zos.write(data)
        zos.closeEntry()
    }
    zos.close()

    // 6. Base64로 다시 인코딩하여 반환
    String finalBase64 = Base64.encoder.encodeToString(newZipBaos.toByteArray())
    message.setBody(finalBase64)
    return message
}
import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.xml.XmlUtil

def Message processData(Message message) {
    
    def body = message.getBody(String);
    
    if (body == ":"){
        message.setBody('')
        message.setProperty("isTableEmpty", "true");
    }else {
        try{
            def splitSrcNDst = body.trim().split(':')

            def src = splitSrcNDst[0].trim().split('\n')
            def dst = splitSrcNDst[1].trim().split('\n')

            if(src.length != dst.length){
                throw new Exception("REQ, RES 테이블 필드 수 다름")
            }

            def srcData = src[0..-1].collect { it.split('\t') }
            def dstData = dst[0..-1].collect { it.split('\t') }

            def xml = new StringBuilder()
            xml.append('<root>')
            xml.append('<table>')
            def isFirst = true
            srcData.eachWithIndex {
                row,
                index ->
                if(row[0] != null && row[0].length() > 0){
                    if(isFirst){
                        isFirst = false
                    }else{
                        xml.append('</table>')
                        xml.append('<table>')
                    }
                    xml.append('<tableName>')
                    xml.append('<src>')
                    xml.append("${row[0].replaceAll(/[\s\u00A0]+/,'')}")
                    xml.append('</src>')
                    xml.append('<dst>')
                    xml.append("${dstData[index][0].replaceAll(/[\s\u00A0]+/,'')}")
                    xml.append('</dst>')
                    xml.append('</tableName>')
                    xml.append('<field>')
                    xml.append('<src>')
                    xml.append("${row[1].replaceAll(/[\s\u00A0]+/,'')}")
                    xml.append('</src>')
                    xml.append('<dst>')
                    xml.append("${dstData[index][1].replaceAll(/[\s\u00A0]+/,'')}")
                    xml.append('</dst>')
                    xml.append('</field>')
                }else{
                    xml.append('<field>')
                    xml.append('<src>')
                    xml.append("${row[1].replaceAll(/[\s\u00A0]+/,'')}")
                    xml.append('</src>')
                    xml.append('<dst>')
                    xml.append("${dstData[index][1].replaceAll(/[\s\u00A0]+/,'')}")
                    xml.append('</dst>')
                    xml.append('</field>')
                }
            }
            xml.append('</table>')
            xml.append('</root>')
            def formattedXml = XmlUtil.serialize(xml.toString())    
            message.setBody(formattedXml)
            message.setProperty("isTableEmpty", "false");
        } catch (Exception ex) {
            def emsg = "REQ, RES 테이블 파싱중 에러 발생! : " + ex.message
            throw new Exception(emsg).initCause(ex)
        }
    }
    return message;
}

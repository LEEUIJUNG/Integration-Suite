import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.xml.XmlUtil

def Message processData(Message message) {
    def body = message.getBody(String);

    if(body == ":"){
        message.setBody('')
        message.setProperty("isFieldEmpty", "true");
    }else {
        try{
            def splitSrcNDst = body.trim().split(':')
            def src = splitSrcNDst[0].trim().split('\n')
            def dst = splitSrcNDst[1].trim().split('\n')

            if(src.length != dst.length){
                throw new Exception("REQ, RES 필드 수 다름")
            }

            def xml = new StringBuilder()
            xml.append('<root>')
            xml.append('<fields>')
            src.eachWithIndex {
                row,
                index ->
                    xml.append("<field><src>${row.replaceAll(/[\s\u00A0]+/,'')}</src><dst>${dst[index].replaceAll(/[\s\u00A0]+/,'')}</dst></field>")
            }
            xml.append('</fields>')
            xml.append('</root>')
            def formattedXml = XmlUtil.serialize(xml.toString())
  
            message.setBody(formattedXml)
            message.setProperty("isFieldEmpty", "false");
        } catch (Exception ex){
            def emsg = "REQ, RES 필드 파싱중 에러 발생! : " + ex.message
            throw new Exception(emsg).initCause(ex)
        }
    }
    return message;
}

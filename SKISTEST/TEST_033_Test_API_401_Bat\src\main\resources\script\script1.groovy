import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.xml.XmlUtil

def Message processData(Message message) {
    def body = message.getBody(String);
    def apiUrls = body.trim().split('\n')

    try {
        message.setBody(parseData(apiUrls))
    }catch(Exception e){
        message.setBody("");
        println e
    }

    return message;
}

def parseData(data){
    try{
        def xml = new StringBuilder()
        xml.append('<root>')
        xml.append('<urls>')
        data.eachWithIndex {
            row,
            index ->
                xml.append("<url>${row.replaceAll(/[\s\u00A0]+/,'')}</url>")
        }
        xml.append('</urls>')
        xml.append('</root>')
        return formattedXml = XmlUtil.serialize(xml.toString())
    } catch (Exception ex){
        def emsg = "Url 파싱중 에러 발생! : " + ex.message
        throw new Exception(emsg).initCause(ex)
        return ""
    }
}
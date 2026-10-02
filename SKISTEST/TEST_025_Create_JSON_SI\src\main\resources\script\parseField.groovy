import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.xml.XmlUtil

def Message processData(Message message) {
    def body = message.getBody(String);
    
    if (body == null) {
        message.setBody('')
        message.setProperty("fields", "")
        return message;
    } 
    
    try{
        fieldData = body.trim().split('\n')
        if(fieldData[0].length() != 0){
            message.setProperty("fields", parseData(fieldData));
        }else{
            message.setProperty("fields", "");
        }
    }catch(Exception e){
        println e
    }
    return message;
}

def parseData(data){
    try{
        def xml = new StringBuilder()
        xml.append('<root>')
        xml.append('<fields>')
        data.eachWithIndex {
            row,
            index ->
                xml.append("<field>${row.replaceAll(/[\s\u00A0]+/,'')}</field>")
        }
        xml.append('</fields>')
        xml.append('</root>')
        return formattedXml = XmlUtil.serialize(xml.toString())
    } catch (Exception ex){
        def emsg = "Field 파싱중 에러 발생! : " + ex.message
        throw new Exception(emsg).initCause(ex)
        return ""
    }
}
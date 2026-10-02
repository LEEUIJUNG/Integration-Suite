import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.xml.XmlUtil

def Message processData(Message message) {
    
    def body = message.getBody(String);

    if(body == null){
        message.setBody('')
    }else {
        def lines = body.trim().split('\n')
        def data = lines[0..-1].collect { it.split('\t') }
        def xml = new StringBuilder()
        xml.append('<root>')
        xml.append('<fields>')
        data.eachWithIndex {
            row,
            index ->
                xml.append("<field>${row[0].replaceAll(/[\s\u00A0]+/,'')}</field>")
        }
        xml.append('</fields>')
        xml.append('</root>')
        println xml.toString()
        def formattedXml = XmlUtil.serialize(xml.toString())
    
        message.setBody(formattedXml)   
    }
    return message;
}

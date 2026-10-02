import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.xml.XmlUtil

def Message processData(Message message) {
    
    def body = message.getBody(String);
    
    if (body == null){
        message.setBody('')
    }else {
            def lines = body.trim().split('\n')
        def data = lines[0..-1].collect { it.split('\t') }
        def xml = new StringBuilder()
        xml.append('<root>')
        def prevTableName = ''
        def currentTableName = ''
        def isFirst = true
        data.eachWithIndex {
            row,
            index ->
            if(row[0] != null && row[0].length() > 0){
                prevTableName = currentTableName
                currentTableName = row[0]
                if(isFirst){
                    xml.append('<table>')
                    isFirst = false
                }else{
                    xml.append("</${prevTableName}>")
                    xml.append('</table>')
                    xml.append('<table>')
                }
                xml.append("<tablename>${row[0].replaceAll(/[\s\u00A0]+/,'')}</tablename>")
                xml.append("<${currentTableName}>")
                xml.append("<field>${row[1].replaceAll(/[\s\u00A0]+/,'')}</field>")
            }else{
                    xml.append("<field>${row[1].replaceAll(/[\s\u00A0]+/,'')}</field>")    
            }
        }
        xml.append("</${currentTableName}>")
        xml.append('</table>')
        xml.append('</root>')
        println xml.toString()
        def formattedXml = XmlUtil.serialize(xml.toString())
    
        //def properties = message.getProperties();
        //message.setProperty("totalCounts", totalCounts);
    
        message.setBody(formattedXml)
    }
    return message;
}

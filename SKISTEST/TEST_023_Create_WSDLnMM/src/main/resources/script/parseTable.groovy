import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.xml.XmlUtil

def Message processData(Message message) {
    def body = message.getBody(String);

    if (body == ":"){
        message.setBody('')
    }else{
        def splitData = body.trim().split(':')
        def outbound = []
        def inbound = []
        
        outbound = splitData[0].trim().split('\n')

        if(outbound[0].length() != 0){
            message.setProperty("outboundTable", parseData(outbound));
        }else{
            message.setProperty("outboundTable", "");
        }

        try {
            inbound = splitData[1].trim().split('\n')
            message.setProperty("inboundTable", parseData(inbound));
        } catch (Exception e){
            message.setProperty("inboundTable", "");
            println e
        }

        try {
            if(outbound.length == inbound.length){
                message.setProperty("mmTable", createMmData(outbound,inbound));
            } else{
                message.setProperty("mmTable", "");
            }
        }catch(Exception e){
            message.setProperty("mmTable", "");
            println e
        }
    }
    message.setBody('')
    return message;
}
def createMmData(outbound,inbound){
    try{
        def outboundData = outbound[0..-1].collect { it.split('\t') }
        def inboundData = inbound[0..-1].collect { it.split('\t') }

        def xml = new StringBuilder()
        xml.append('<root>')
        xml.append('<table>')
        def isFirst = true
        outboundData.eachWithIndex {
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
                xml.append('<outbound>')
                xml.append("${row[0].replaceAll(/[\s\u00A0]+/,'')}")
                xml.append('</outbound>')
                xml.append('<inbound>')
                xml.append("${inboundData[index][0].replaceAll(/[\s\u00A0]+/,'')}")
                xml.append('</inbound>')
                xml.append('</tableName>')
                xml.append('<field>')
                xml.append('<outbound>')
                xml.append("${row[1].replaceAll(/[\s\u00A0]+/,'')}")
                xml.append('</outbound>')
                xml.append('<inbound>')
                xml.append("${inboundData[index][1].replaceAll(/[\s\u00A0]+/,'')}")
                xml.append('</inbound>')
                xml.append('</field>')
            }else{
                xml.append('<field>')
                xml.append('<outbound>')
                xml.append("${row[1].replaceAll(/[\s\u00A0]+/,'')}")
                xml.append('</outbound>')
                xml.append('<inbound>')
                xml.append("${inboundData[index][1].replaceAll(/[\s\u00A0]+/,'')}")
                xml.append('</inbound>')
                xml.append('</field>')
            }
        }
        xml.append('</table>')
        xml.append('</root>')
        return XmlUtil.serialize(xml.toString())
    } catch (Exception ex){
        def emsg = "Table 파싱중 에러 발생! : " + ex.message
        throw new Exception(emsg).initCause(ex)
    }
}

def parseData(data){
    try{
        def splitData = data[0..-1].collect { it.split('\t') }
        def xml = new StringBuilder()
        xml.append('<root>')
        xml.append('<table>')
        def isFirst = true
        splitData.eachWithIndex {
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
                xml.append("${row[0].replaceAll(/[\s\u00A0]+/,'')}")
                xml.append('</tableName>')
                xml.append('<field>')
                xml.append("${row[1].replaceAll(/[\s\u00A0]+/,'')}")
                xml.append('</field>')
            }else{
                xml.append('<field>')
                xml.append("${row[1].replaceAll(/[\s\u00A0]+/,'')}")
                xml.append('</field>')
            }
        }
        xml.append('</table>')
        xml.append('</root>')
        return XmlUtil.serialize(xml.toString())
    } catch (Exception ex){
        def emsg = "Table 파싱중 에러 발생! : " + ex.message
        throw new Exception(emsg).initCause(ex)
    }
}
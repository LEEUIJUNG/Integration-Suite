import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.xml.XmlUtil

def Message processData(Message message) {
    def body = message.getBody(String);

    if(body == ":"){
        message.setBody('')
    }else {
        def splitData = body.trim().split(':')
        def outbound = []
        def inbound = []

        outbound = splitData[0].trim().split('\n')

        if(outbound[0].length() != 0){
            message.setProperty("outboundField", parseData(outbound));
        }else{
            message.setProperty("outboundField", "");
        }

        try {
            inbound = splitData[1].trim().split('\n')
            message.setProperty("inboundField", parseData(inbound));
        } catch (Exception e){
            message.setProperty("inboundField", "");
            println e
        }
        try {
            if(outbound.length == inbound.length){
                message.setProperty("mmField", createMmData(outbound,inbound));
            } else{
                message.setProperty("mmField", "");
            }
        }catch(Exception e){
            message.setProperty("mmField", "");
            println e
        }
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

def createMmData (outboundData,inboundData){
    try{
        def xml = new StringBuilder()
        xml.append('<root>')
        xml.append('<fields>')
        outboundData.eachWithIndex {
            row,
            index ->
                xml.append("<field><outbound>${row.replaceAll(/[\s\u00A0]+/,'')}</outbound><inbound>${inboundData[index].replaceAll(/[\s\u00A0]+/,'')}</inbound></field>")
        }
        xml.append('</fields>')
        xml.append('</root>')
        return XmlUtil.serialize(xml.toString())
    } catch (Exception ex){
        def emsg = "Field 파싱중 에러 발생! : " + ex.message
        throw new Exception(emsg).initCause(ex)
        return ""
    }
}
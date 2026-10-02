import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.xml.XmlUtil

def Message processData(Message message) {
    def body = message.getBody(String);
    
    if (body == null) {
        message.setBody('')
        message.setProperty("structures", "")
        return message;
    } 
    
    try {
        structureData = body.trim().split('\n')

        if(structureData[0].length() != 0){
            message.setProperty("structures", parseData(structureData));
        }else{
            message.setProperty("structures", "");
        }
        message.setBody('')
        return message;
    }
    catch(Exception e){
        println e
    }
}

def parseData(data){
    try{
        def splitData = data[0..-1].collect { it.split('\t') }
        def xml = new StringBuilder()
        xml.append('<root>')
        xml.append('<structure>')
        def isFirst = true
        splitData.eachWithIndex {
            row,
            index ->
            if(row[0] != null && row[0].length() > 0){
                if(isFirst){
                    isFirst = false
                }else{
                    xml.append('</structure>')
                    xml.append('<structure>')
                }
                xml.append('<structureName>')
                xml.append("${row[0].replaceAll(/[\s\u00A0]+/,'')}")
                xml.append('</structureName>')
                xml.append('<field>')
                xml.append("${row[1].replaceAll(/[\s\u00A0]+/,'')}")
                xml.append('</field>')
            }else{
                xml.append('<field>')
                xml.append("${row[1].replaceAll(/[\s\u00A0]+/,'')}")
                xml.append('</field>')
            }
        }
        xml.append('</structure>')
        xml.append('</root>')
        return XmlUtil.serialize(xml.toString())
    } catch (Exception ex){
        def emsg = "Structure 파싱중 에러 발생! : " + ex.message
        throw new Exception(emsg).initCause(ex)
    }
}
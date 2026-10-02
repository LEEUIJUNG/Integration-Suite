import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.xml.XmlUtil

def Message processData(Message message) {
    def body = message.getBody(String);
    
    if (body == null) {
        message.setBody('')
        message.setProperty("tables", "")
        return message;
    } 

    try{
        tableData = body.trim().split('\n')

        if(tableData[0].length() != 0){
            message.setProperty("tables", parseData(tableData));
        }else{
            message.setProperty("tables", "");
        }
        message.setBody('')
        return message;
    }catch(Exception e){
        println e
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
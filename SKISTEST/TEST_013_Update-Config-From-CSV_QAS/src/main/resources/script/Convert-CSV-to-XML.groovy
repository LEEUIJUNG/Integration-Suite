import com.sap.gateway.ip.core.customdev.util.Message
import java.util.HashMap
import groovy.xml.XmlUtil

def Message processData(Message message) {
    def body = message.getBody(String)
    def rows = body.split('\n')
    while (rows && (rows[0] == null || rows[0].trim().isEmpty())) {
        rows = rows.drop(1)
    }

    def headers = rows[0].split(',')
    def builder = new StringBuilder()

    builder.append('<root>')
    rows[1..-1].each { row ->
        def values = row.split(',')
        builder.append('<configs>')
        headers.eachWithIndex { header, idx ->
            // values 배열의 길이를 확인하여 유효한 인덱스인지 체크
            if (idx < values.length && values[idx] != null && !(values[idx].trim().isEmpty())) {
                if (header == "IFLOW_ID") {
                    builder.append("<${header}>${values[idx]}</${header}>")
                } else {
                    builder.append("<param>")
                    builder.append("<key>${header}</key>")
                    builder.append("<val>${values[idx]}</val>")
                    builder.append("</param>")
                }
            }
        }
        builder.append('</configs>')
    }
    builder.append('</root>')
    message.setBody(builder.toString())
    return message
}
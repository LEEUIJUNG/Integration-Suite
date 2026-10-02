import com.sap.gateway.ip.core.customdev.util.Message
import java.nio.charset.Charset

def Message processData(Message message) {
       
        String httpQuery = message.getHeader('CamelHttpQuery', String)
        def queryParams = [:]

        if (httpQuery) {
            httpQuery.split('&').each { param ->
                def parts = param.split('=')
                if (parts.size() == 2) {
                    queryParams[parts[0]] = parts[1]
                } else if (parts.size() == 1) {
                    queryParams[parts[0]] = "" // Handle parameters without values
                }
            }
        }
        message.setProperties(queryParams)
    return message
}

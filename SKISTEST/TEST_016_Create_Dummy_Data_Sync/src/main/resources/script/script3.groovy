/* Refer the link below to learn more about the use cases of script.
https://help.sap.com/viewer/368c481cd6954bdfa5d0435479fd4eaf/Cloud/ko/148851bf8192412cba1f9d2c17f4bd25.html

If you want to know more about the SCRIPT APIs, refer the link below
https://help.sap.com/doc/a56f52e1a58e4e2bac7f7adbf45b2e26/Cloud/ko/index.html */

import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;
import groovy.xml.XmlUtil


// def convertCsvStringToJson(String csvString){
//     def lines = csvString.split('\n')
//     def headers = lines[0].split('\t')
//     def jsonData = []
//     lines[1..-1].each { line -> 
//         def values = line.split('\t')
//         def record = [:]
//         headers.eachWithIndex {
//             header, i -> record[header] = values[i]
//         }
//         jsonData << record
//     }
//         return JsonOutput.to.Json(jsonData)
// }

def Message processData(Message message) {
    
    def body = message.getBody(String);

    def lines = body.trim().split('\n')
    def headers = lines[0].split('\t')
    def data = lines[1..-1].collect { it.split('\t') }

    def xml = new StringBuilder()
    xml.append('<root>')
    data.each {
        row ->
            xml.append('<record>')
        headers.eachWithIndex {
            header,
            index ->
            xml.append("<${header}>${row[index]}</${header}>")
        }
        xml.append('</record>')
    }
    xml.append('</root>')
    
    def formattedXml = XmlUtil.serialize(xml.toString())

    message.setBody(formattedXml)
    //inspectObject(JsonOutput)    
    
    //def body = message.getBody(String.class);

    //message.setBody(convertCsvStringToJson(body));
    // message.setBody(inspectObject(JsonOutput));
    return message;
}
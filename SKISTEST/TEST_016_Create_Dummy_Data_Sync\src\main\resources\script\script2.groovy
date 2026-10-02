/* Refer the link below to learn more about the use cases of script.
https://help.sap.com/viewer/368c481cd6954bdfa5d0435479fd4eaf/Cloud/ko/148851bf8192412cba1f9d2c17f4bd25.html

If you want to know more about the SCRIPT APIs, refer the link below
https://help.sap.com/doc/a56f52e1a58e4e2bac7f7adbf45b2e26/Cloud/ko/index.html */
import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;

// def splitByTabs (String input){
//     if(input.contains("\t")){
//         def parts = input.split("\t",2)
//         def beforeTab = parts[0]
//         def afterTab = parts[1]
//     }
//     else {
//         return input
//     }
// }

// def parseTables(String input){
//     def lines = input.split("\n")
//     def tables = [:]
//     def currentTable = null
    
//     lines.each{
//         if(lines.contains("\t")){
//             def parts = line.split("\t",2)
//             currentTable = parts[0]
//             def data = parts[1]
//             tables[currentTable] = [data]
//         }else if (currentTable){
//             tables[currentTable] << line
//         } 
//     }
// }

def Message processData(Message message) {

    def body = message.getBody(String.class);
    
    // 앞,뒤 공백 제거
    message.setBody(body.trim());

    return message;
}
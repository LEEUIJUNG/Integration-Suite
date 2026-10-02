/* Refer the link below to learn more about the use cases of script.
https://help.sap.com/viewer/368c481cd6954bdfa5d0435479fd4eaf/Cloud/ko/148851bf8192412cba1f9d2c17f4bd25.html

If you want to know more about the SCRIPT APIs, refer the link below
https://help.sap.com/doc/a56f52e1a58e4e2bac7f7adbf45b2e26/Cloud/ko/index.html */
import com.sap.gateway.ip.core.customdev.util.Message
import java.util.HashMap;
import org.apache.camel.impl.DefaultAttachment
import javax.mail.util.ByteArrayDataSource

def Message processData(Message message) {

    def regexRmOpenBrace = "\\{"
    def regexRmCloseBrace = "}(?!.*})"
    def ifid = message.getProperty("IFID");
    def OSorIS = message.getProperty("OSorIS");
    def fqdd = message.getProperty("fieldsReqDummyData");
    def fsdd = message.getProperty("fieldsResDummyData");
    def sqdd = message.getProperty("structureReqDummyData");
    def ssdd = message.getProperty("structureResDummyData");
    def tqdd = message.getProperty("tablesReqDummyData");
    def tsdd = message.getProperty("tablesResDummyData");

    def reqList = [
        fqdd,
        sqdd,
        tqdd,
    ]
    def resList = [
        fsdd,
        ssdd,
        tsdd
    ]

    reqList = reqList.collect { 
        req -> req = req.replaceFirst(regexRmOpenBrace,"")
        req = req.replaceFirst(regexRmCloseBrace,"")
        return req
    }

    resList = resList.collect { 
        res -> res = res.replaceFirst(regexRmOpenBrace,"")
        res = res.replaceFirst(regexRmCloseBrace,"")
        return res
    }
    
    String newTable = reqList.get(2).replaceAll("\\{","[{").replaceAll("}","}]")
    reqList.set(2,newTable)

    newTable = resList.get(2).replaceAll("\\{","[{").replaceAll("}","}]")
    resList.set(2,newTable)

    def result = "-REQUSET\n{"

    reqList = reqList.collect {
        req -> 
        if(req != null && req != ""){
            result = result + req + ","
        }
    }
    result = result[0..-2] + "}"

    result = result + "\n-RESPONSE\n{"
    resList = resList.collect {
        res -> 
        if(res != null && res != ""){
            result = result + res + ","
        }
    }
    result = result[0..-2] + "}"
    
    def messageLog = messageLogFactory.getMessageLog(message)
    if (messageLog != null) {
        messageLog.addAttachmentAsString(ifid +"_"+OSorIS+'_Dummy_Data', result, 'text/plain')
    }
    return message;
}
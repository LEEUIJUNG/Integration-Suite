/* Refer the link below to learn more about the use cases of script.
 https://help.sap.com/viewer/368c481cd6954bdfa5d0435479fd4eaf/Cloud/ko/148851bf8192412cba1f9d2c17f4bd25.html
 If you want to know more about the SCRIPT APIs, refer the link below
 https://help.sap.com/doc/a56f52e1a58e4e2bac7f7adbf45b2e26/Cloud/ko/index.html */
import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;
import java.util.Calendar;

import com.sap.it.api.asdk.datastore.*
import com.sap.it.api.asdk.runtime.*

//Imports for the DataStore-class handling/access
import com.sap.esb.datastore.DataStore
import com.sap.esb.datastore.Data
import org.osgi.framework.*

import org.apache.camel.Exchange
import org.apache.camel.builder.SimpleBuilder

import java.text.SimpleDateFormat;
import java.util.HashMap;

def Message processData(Message message) {

	//금일 날자.
	def localTimeZone = TimeZone.getTimeZone('Asia/Seoul');
	Calendar cal = Calendar.getInstance(localTimeZone);
	cal.add(Calendar.DATE, -1);
	def dateFormat    = 'yyyy-MM-dd_HH_mm_SS.Ms';
	Calendar cal2 = Calendar.getInstance(localTimeZone)
	def toDate = cal2.time
	def todayGf = toDate.format(dateFormat, localTimeZone)

    //* 240627 cx0358 error logname 추가
	def logName = "Error_log_Attached";

	//Retry 의 경우
	def headers = message.getHeaders();
	def isretry = headers.get("isretry");


	//*DataStore 저장로직 시작(공통)
	//Get CamelContext and from that the DataStore instance
	def camelCtx = message.exchange.getContext()
	DataStore dataStore = (DataStore)camelCtx.getRegistry().lookupByName(DataStore.class.getName())


	//iflow error 표시.
	/*
	 def messageLog = messageLogFactory.getMessageLog(message);
	 pmap = message.getProperties();
	 iflow_error = pmap.get("iflow_error");
	 if(iflow_error!=null){
	 messageLog.addCustomHeaderProperty("iflow_error", "true" );  
	 }
	 */


	try{

		//DataStore 저장.
		if( isretry.equals('true') ){
			//Case1. error retry (재전송) 호출 - dataStore 에 저장 안함.
		}else{
			//Case2. 최초 Error 발생- dataStore 에 저장.

			//Properties 에서 페이로드 획득.
			map = message.getProperties();
			plm_payload = map.get("plm_payload");
			def payload;

			if( plm_payload){
				//plm_payload 가 있으면 plm_payload 저장.
				payload = plm_payload.getBytes("UTF-8")
			}else{
				//plm_payload 가 없으면 현 시점의 payload 저장.
				
				if(message.getBody() == null){
    				//2025-01-23 body null 처리 최윤서
				    payload = "Empty Payload!!"
				    payload = payload.getBytes("UTF-8")
				}else{
				    payload = message.getBody(java.lang.String) as String
				    payload = payload.getBytes("UTF-8")
				}
			}

			Exchange ex = message.exchange
			def evaluateSimple = { simpleExpression ->
				SimpleBuilder.simple(simpleExpression).evaluate(ex, String)
			}

		    String MessageProcessingLogID = map.get("SAP_MessageProcessingLogID");
			    
			//params => (DatastoreName, ContextName, EntryId, Body, Headers, MessageId, Version)
			//Note: Setting ContextName to null, will create a global Datastore
			//Data dsData = new Data( evaluateSimple('${camelId}') + "_ERROR" , null ,  todayGf , payload, headers, evaluateSimple('${property.SAP_MessageProcessingLogID}') , 0)
            Data dsData = new Data( evaluateSimple('${camelId}') + "_ERROR" , null ,  todayGf , payload, headers, MessageProcessingLogID , 0)

			//Write dsData element to the data store
			//params => (DataInstance, overwriteEntry, encrypt, alertPeriodInMs, expirePeriodInMs)
			// 14일 = 1209600000 ms ,   2일 = 172800000 ms  => 14일간 저장됨.
			dataStore.put(dsData, true, false, 172800000, 1209600000)


			//Custom 헤더에 ERROR DS 의 EntryId 값 표시.
			def messageLog = messageLogFactory.getMessageLog(message);
			if(todayGf!=null){
				messageLog.addCustomHeaderProperty("ERROR_DS_NAME", evaluateSimple('${camelId}') + "_ERROR");
				messageLog.addCustomHeaderProperty("ERROR_DS_ID", todayGf);
			}
		}
	}catch(Exception e){
		message.setProperty("SAP_MessageProcessingLogCustomStatus", "log_error")
		String str= e.getStackTrace().toString()
		String err_text = "Error occured in ErrorLog groovy : "  + logName  + " *Error Detail:: " + str
		
		def messageLog = messageLogFactory.getMessageLog(message);
		messageLog.addAttachmentAsString( "PayloadLog_Excption", err_text, "text/plain");
	}
	
	def ex = map.get("CamelExceptionCaught");
    def body = message.getBody(java.lang.String);

	if (ex!=null) {
	    def errmsg = '\n====================\n{"Error_Type" : "' + ex.getClass().getName() + '",\n"Error_Detail" : "' + ex.message
	    if(body !=null && body.trim() && body.contains('<SAP:Error')){
	        errmsg += '",\n"SAP_Error_Detail" : "'+ body
	    }
	    errmsg += '"}\n====================\n'
	    throw new RuntimeException(errmsg,ex)
    }

	return message;
}
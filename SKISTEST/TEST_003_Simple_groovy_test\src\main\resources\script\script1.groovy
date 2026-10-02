import com.sap.gateway.ip.core.customdev.util.Message;
import com.sap.esb.datastore.DataStore
import com.sap.esb.datastore.Data
import org.apache.camel.Exchange
import groovy.json.JsonSlurper
import groovy.util.XmlSlurper
/*
 1.사용 Property 이름
   - PAYLOAD_COUNT_TARGET_STEP : payload count 대상 logging step Number 지정.
   - PAYLOAD_COUNT_NAME :        payload count 대상 element 의 이름 지정.
   - PAYLOAD_DATASTORE_PERIOD :  Datastore 저장 기간(일) 지정.
   - payload_step : payload attached, datastore 저장 step 정보와 information(i), payload count 정보(p) 지정 ex)1234ABCDip
 2."Runtime Configuration" 에 허용 헤더 추가 : x-forwarded-for , user-agent
*/
def Message processData(Message message) {
    
//payload step 프로퍼티 생성.
    // LOG_STEP_NO 가져오기 (없으면 초기값 0)
    def currentNumber = message.getProperty("LOG_STEP_NO")?.toString() ?: "0"
    def number = 0
    try {
        number = Integer.parseInt(currentNumber)
    } catch (Exception e) {
        number = 0
    }
    // step 숫자 증가
    number += 1
    // 문자 결정 (A ~ Z 순환)
    def letterCharCode = ((number - 1) % 26) + ((int)'A')  // ASCII: A=65
    def letter = ((char) letterCharCode).toString()
    // Set updated properties
    message.setProperty("LOG_STEP_NO", number)
    message.setProperty("LOG_STEP_DSNO", letter)
    
    //file attached 의 file 이름.
	def logName = "(" + number + ")Payload_Attached";
	//Datastore 저장 문자 Position , file attached 순서 Position
	def dataStore_step = letter;
	def attached_step = number;
    
//0. 지정된 payload_step 값 확인. 
	def propertyMap = message.getProperties();
	String payload_step = propertyMap.get("payload_step");
	//Data row count 대상 element 이름.
	def payload_count_target_step = propertyMap.get("PAYLOAD_COUNT_TARGET_STEP") ?: "0"
	//Data store 메세지 저장 기간.
	def daysString = propertyMap.get("PAYLOAD_DATASTORE_PERIOD")?.toString() ?: "1"
	def messageLog = messageLogFactory.getMessageLog(message);
    
    try{
		//1. i 가 있으면 커스텀 헤더정보 표시. ( 최초 step 에서만 동작)
		if( payload_step.contains("i") && attached_step == 1 ){

            //Client IP , Agent 정보
			def clientIP = "";
			def value_agent = null;

			messageLog = messageLogFactory.getMessageLog(message);

			/*
			 !!주의!! Integration Flow 의 "Runtime Configuration" 에 하기 해더 허용 추가 필요.
			 */
			def map = message.getHeaders();
			//def IP_value = map.get("x-forwarded-for");
			def IP_value = map.get("x-forwarded-for") ?: "undefined"
			//value_agent = map.get("user-agent") as String;
			value_agent = map.get("user-agent") as String ?: "undefined"

        	//1-1)JMS QUEUE 재수행 count 표시.
        	def isretry = map.get("isretry");
    	    if( isretry){
    	    def retryCount = map.get("SAPJMSRetries") ?: 0
    		messageLog.addCustomHeaderProperty("Retry count", retryCount);
    	    }
        	
			//1-2)Client IP , Agent 정보 SET custom Header
			if(IP_value!=null){
				messageLog.addCustomHeaderProperty("clientIP", IP_value.replace("," , " -> "));
			}
			if(value_agent!=null){
				messageLog.addCustomHeaderProperty("user-agent", value_agent);
			}

		}
	}catch(Exception e){
		message.setProperty("SAP_MessageProcessingLogCustomStatus", "log_error")
		String str= e.getStackTrace().toString()
		String strl = str.replaceAll(",", System.lineSeparator())
		String err_text = "Error occured in PayloadLog groovy area1 'INFO' : "  + logName  + " *Error Detail:: " + strl
		messageLog.addAttachmentAsString( "PayloadLog_Excption", err_text, "text/plain");
	}
    
    //2. 페이로드 로깅 옵션 체크 - 페이로드 사용 case 에 body 값 공통 사용.
	if( payload_step.contains( attached_step as String ) |  payload_step.contains( dataStore_step) |  payload_step.contains("p") ){

		try{
			//body payload Get.
			def body = message.getBody(java.lang.String) as String;

			//2-1) 페이로드 file attached.
			if( payload_step.contains( attached_step as String ) ){
				//Body attached.
				messageLog.addAttachmentAsString( logName, body, "text/plain");
			}

			//2-2) 페이로드 Datastore 저장.
			if( payload_step.contains( dataStore_step) ){
				//Get CamelContext and from that the DataStore instance
				def camelCtx = message.exchange.getContext()
				DataStore dataStore = (DataStore)camelCtx.getRegistry().lookupByName(DataStore.class.getName())

				//Define headers and payload/body as byte[]
				//Map<String, Object> headers = ["headerName1":"me", "anotherHeader": false]
				def headers = message.getHeaders();

				//def payload = "This is sample data".getBytes("UTF-8")
				//def payload = message.getBody(java.lang.String) as String
				def payload = body
				payload = payload.getBytes("UTF-8")

				Exchange ex = message.exchange
                def camelId = ex.getContext().getName();

			    String MessageProcessingLogID = propertyMap.get("SAP_MessageProcessingLogID");
			    
				//Create datastore payload/data with the following parameters
				//params => (DatastoreName, ContextName, EntryId, Body, Headers, MessageId, Version)
				//Note: Setting ContextName to null, will create a global Datastore
				Data dsData = new Data( camelId , camelId , MessageProcessingLogID + "(" + dataStore_step + ")", payload, headers, MessageProcessingLogID , 0)
				
				//Write dsData element to the data store
				//params => (DataInstance, overwriteEntry, encrypt, alertPeriodInMs, expirePeriodInMs)
				// 14일 = 1209600000 ms ,   2일 = 172800000 ms
				//dataStore.put(dsData, true, false, 172800000, 1209600000)
				
				//Data store 보존기간(일 -> ms), 없으면 1일.
				def days = daysString.isInteger() ? daysString.toInteger() : 1
				def millis = days * 24 * 60 * 60 * 1000L  // convert to milliseconds
				dataStore.put(dsData, true, false, millis, millis)
			}
			
		    //2-3) 페이로드 Row count custom Header 표시.
			if( payload_step.contains("p") && payload_count_target_step.toInteger() == attached_step.toInteger() ){
    			def isXML = false
                def isJSON = false
                def dataType = "OTHERS"
                def rowSize = 0
                
                //row count 를 확인해야할 필드 이름
                def fieldName = propertyMap.get("PAYLOAD_COUNT_NAME");
                if( fieldName){
                    //data type 확인. Trim the body for clean checking
                    try {
                        if (body.startsWith("{") || body.startsWith("[")) {
                            isJSON = true
                            dataType = "JSON"
                        } else if (body.startsWith("<")) {
                            isXML = true
                            dataType = "XML"
                        } else {
                            dataType = "PLAINTEXT"
                        }
                    } catch (Exception e) {
                        dataType = "INVALID"
                    }
                    
                    //data type 에 따라 count 확인.
                    if(isXML){
                        def xmlparser = new XmlSlurper().parseText(body)
                        def xmltargetElement = xmlparser.fieldName
                        //rowSize = xmltargetElement.ROW.size()
                        rowSize = xmlparser.depthFirst().findAll{ it.name()==fieldName }.size()
                    
                    }else if(isJSON){
                        def json = new JsonSlurper().parseText(body)
                        
                        Closure countRows
                        countRows = { node ->
                            if (node instanceof Map) {
                                node.each { k, v ->
                                    if (k == fieldName && v instanceof List) {
                                        rowSize += v.size()
                                    } else {
                                        countRows(v) //재귀 호출
                                    }
                                }
                            } else if (node instanceof List) {
                                node.each { item -> countRows(item) }
                            }
                        }
                        // 4. 함수 호출
                        countRows.call(json)
                    }
                }
                
                messageLog.addCustomHeaderProperty("DATA TYPE(STEP: " + attached_step + ")", dataType);
                messageLog.addCustomHeaderProperty("DATA ROW COUNT(STEP: " + attached_step + ")", rowSize as String);
			    
			}

		}catch(Exception e){
			message.setProperty("SAP_MessageProcessingLogCustomStatus", "log_error")
			String str= e.getStackTrace().toString()
			String strl = str.replaceAll(",", System.lineSeparator())
			String err_text = "Error occured in PayloadLog groovy area 2 'PAYLOAD SAVE' : "  + logName  + " *Error Detail:: " + strl
			messageLog.addAttachmentAsString( "PayloadLog_Excption", err_text, "text/plain");
		}
	}
    
    return message;
}
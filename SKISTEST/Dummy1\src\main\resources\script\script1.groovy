/* Refer the link below to learn more about the use cases of script.
 https://help.sap.com/viewer/368c481cd6954bdfa5d0435479fd4eaf/Cloud/ko/148851bf8192412cba1f9d2c17f4bd25.html
 If you want to know more about the SCRIPT APIs, refer the link below
 https://help.sap.com/doc/a56f52e1a58e4e2bac7f7adbf45b2e26/Cloud/ko/index.html */
import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;
import java.util.Calendar;

import com.sap.it.api.asdk.datastore.*
import com.sap.it.api.asdk.runtime.*

import com.sap.esb.datastore.DataStore
import com.sap.esb.datastore.Data
import org.osgi.framework.*

import org.apache.camel.Exchange
import org.apache.camel.builder.SimpleBuilder

def Message processData(Message message) {
	def body = message.getBody(java.lang.String) as String;
	def payload = body				
	payload = payload.getBytes("UTF-8")
	def headers = message.getHeaders();
	def propertyMap = message.getProperties();
	String MessageProcessingLogID = propertyMap.get("SAP_MessageProcessingLogID");
	
	def camelCtx = message.exchange.getContext()

	DataStore dataStore = (DataStore)camelCtx.getRegistry().lookupByName(DataStore.class.getName())


	//Create datastore payload/data with the following parameters
	//params => (DatastoreName, ContextName, EntryId, Body, Headers, MessageId, Version)
	//Note: Setting ContextName to null, will create a global Datastore
	//Data dsData = new Data( evaluateSimple('${camelId}') , evaluateSimple('${camelId}') ,  evaluateSimple('${property.SAP_MessageProcessingLogID}') + "(" + dataStore_step + ")", payload, headers, evaluateSimple('${property.SAP_MessageProcessingLogID}') , 0)				
	Data dsData1 = new Data( "TestDSName_250718" , "qqqqAAAA" , MessageProcessingLogID, payload, headers, MessageProcessingLogID , 0)
	Data dsData2 = new Data( "TestDSName_250719" , "qqqqAAAA" , MessageProcessingLogID, payload, headers, MessageProcessingLogID , 0)
	


	//Write dsData element to the data store
	//params => (DataInstance, overwriteEntry, encrypt, alertPeriodInMs, expirePeriodInMs)
	// 14일 = 1209600000 ms ,   2일 = 172800000 ms
	dataStore.put(dsData1, true, false, 172800000, 1209600000)
	dataStore.put(dsData2, true, false, 172800000, 1209600000)
		
	return message;
}
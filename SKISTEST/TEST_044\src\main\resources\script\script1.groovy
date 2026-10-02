import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;
import java.util.Calendar;

//Imports for DataStoreService-class
import com.sap.it.api.asdk.datastore.*
import com.sap.it.api.asdk.runtime.*

import com.sap.esb.datastore.DataStore
import com.sap.esb.application.services.datastore.impl.DataStoreTablePostgreSQL;
import com.sap.esb.datastore.Data
import org.osgi.framework.*

import org.apache.camel.Exchange
import org.apache.camel.builder.SimpleBuilder

def Message processData(Message message) {
    
// 	def service = new Factory(DataStoreService.class).getService()
//     DataBean myDataBean = service.get("BackUp-Packages-To-DataStore","MMPR-2025-10-24")
//     message.setProperty("script-log",new String(myDataBean.getDataAsArray()));


    // 페이로드를 데이터스토어에 저장
    def camelCtx = message.exchange.getContext();

    DataStore dataStore = (DataStore)camelCtx.getRegistry().lookupByName(DataStore.class.getName());
    
    // def dsEntryIds = dataStore.selectIds("COCO_031_S_GOST-SAP_HTTP-PROXY","COCO_031_S_GOST-SAP_HTTP-PROXY")
    // def dsData = dataStore.get("COCO_031_S_GOST-SAP_HTTP-PROXY","COCO_031_S_GOST-SAP_HTTP-PROXY",dsEntryIds[0])

    // dataStore.countEntries("COCO_031_S_GOST-SAP_HTTP-PROXY")
    // dataStore.selectTids("COCO_031_S_GOST-SAP_HTTP-PROXY","COCO_031_S_GOST-SAP_HTTP-PROXY")
    // dataStore.get("BackUp-Packages-To-DataStore","MMPR-2025-10-24")
    // def entry = dataStore.get("BackUp-Packages-To-DataStore","BackUp-Packages-To-DataStore","MMPR-2025-10-24")
    
    // message.setProperty("script-log",new String(dsEntryArray[0].getDataAsArray()));
    // message.setProperty("script-log",dataStore.selectTids("COCO_031_S_GOST-SAP_HTTP-PROXY","COCO_031_S_GOST-SAP_HTTP-PROXY"));
    // DataStoreTablePostgreSQL dsTable = dataStore.getDataStoreTable();
    DataStoreTablePostgreSQL dsTable = new DataStoreTablePostgreSQL();
    
    message.setProperty("script-log",dsTable.toString());
    // message.setProperty("script-log",dataStore.deleteExpired(10));

    return message;
}
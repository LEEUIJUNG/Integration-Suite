import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;
import java.util.Calendar;

//Imports for DataStoreService-class
import com.sap.it.api.asdk.datastore.*
import com.sap.it.api.asdk.runtime.*

import com.sap.esb.datastore.DataStore
import com.sap.esb.datastore.Data
import org.osgi.framework.*

import org.apache.camel.Exchange
import org.apache.camel.builder.SimpleBuilder

def Message processData(Message message) {
    
    def camelCtx = message.exchange.getContext();
    String dsName = message.getProperty("dsName");
    def logId = message.getBody(String);
    
    //DataStore 가져옴
    DataStore dataStore = (DataStore)camelCtx.getRegistry().lookupByName(DataStore.class.getName());
    
    // 로그 삭제 
    // public int delete(String storeName, String qualifier, String id) throws DataStoreException
    int delCount = dataStore.delete(dsName,dsName,logId)

    message.setProperty("script-log",delCount);
    
    return message;
}
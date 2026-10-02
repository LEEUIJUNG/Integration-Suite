import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;

def Message processData(Message message) {
    // Get Properties
       map = message.getProperties();
       MaxCapacity = new BigDecimal(map.get("MaxCapacity"));
       Capacity = new BigDecimal(map.get("Capacity"));
       BigDecimal PercentCapacity = Capacity * 100 / MaxCapacity;
       BigDecimal CriticalLimit = MaxCapacity / 100 * 80;
       BigDecimal ExhaustedLimit = MaxCapacity / 100 * 95;       
       MaxQueueNumber = new BigDecimal(map.get("MaxQueues"));
       QueueNumber = new BigDecimal(map.get("Queues"));
       BigDecimal CriticalLimitQueues = MaxQueueNumber - 1;
       
       if (Capacity >= ExhaustedLimit) {
          CapacityStatus = "Exhausted";
       }
       else if (Capacity >= CriticalLimit) {
          CapacityStatus = "Critical";
       }
       else {
          CapacityStatus = "OK";
       }   
       
       if (QueueNumber >= CriticalLimitQueues) {
          QueueStatus = "Critical";
       }
       else {
          QueueStatus = "OK";
       }  
 
       int QueueCapacityWarning = new Integer(map.get("QueueCapacityWarning"));
       int QueueCapacityError = new Integer(map.get("QueueCapacityError"));
       if (QueueCapacityError >= 1) {
          QueueCapacityStatus = "Exhausted";
       }
       else if (QueueCapacityWarning >= 1) {
          QueueCapacityStatus = "Critical";
       }
       else {
          QueueCapacityStatus = "OK";
       }
   
       int Consumers = new Integer(map.get("Consumers"));
       if (Consumers == 1) {
         ConsumerStatus = "Critical";
       }
       else {
         ConsumerStatus = "OK";
       }
       
       int Providers = new Integer(map.get("Providers"));
       if (Providers == 1) {
         ProviderStatus = "Critical";
       }
       else {
         ProviderStatus = "OK";
       }
       
       int Transactions = new Integer(map.get("Transactions"));
       if (Transactions == 1) {
         TransactionStatus = "Critical";
       }
       else {
         TransactionStatus = "OK";
       }   
      
      message.setProperty("PercentCapacity", PercentCapacity);
      message.setProperty("CapacityStatus", CapacityStatus);
      message.setProperty("QueueCapacityStatus", QueueCapacityStatus);
      message.setProperty("QueueStatus", QueueStatus);
      message.setProperty("ConsumerStatus", ConsumerStatus);
      message.setProperty("ProviderStatus", ProviderStatus);
      message.setProperty("TransactionStatus", TransactionStatus);

       if ((CapacityStatus == "Exhausted") || (QueueCapacityStatus == "Exhausted"))  {
         message.setProperty("JMSStatus", "Exhausted");
       }  
       else if ((CapacityStatus == "Critical") || (QueueCapacityStatus == "Critical") || (QueueStatus == "Critical") || (ConsumerStatus == "Critical") || (ProviderStatus == "Critical") || (TransactionStatus == "Critical")) {
         message.setProperty("JMSStatus", "Critical");
        } 
       else {
         message.setProperty("JMSStatus", "OK");
       }
       return message;

}
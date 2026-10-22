import com.sap.gateway.ip.core.customdev.util.Message

def Message processData(Message message) {

    // 1. XI Receiver에 전달할 Header 설정 (이미 앞부분 CM에 작성 했음)
    /*
    message.setHeader("SapQualityOfService", "ExactlyOnceInOrder")
    message.setHeader("SapQueueId", "Z_TEST_EOIO")
    */

    // 2. MPL Custom Header 등록
    
    def qos = message.getHeader("SapQualityOfService", String)
    def queueId = message.getHeader("SapQueueId", String)
    
    def mpl = messageLogFactory.getMessageLog(message)

    if (mpl != null) {
        mpl.addCustomHeaderProperty(
            "SapQualityOfService", qos
        )

        mpl.addCustomHeaderProperty(
            "SapQueueId", queueId
        )
    }

    return message
}
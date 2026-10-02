//internal
import com.sap.gateway.ip.core.customdev.util.Message
import groovy.json.JsonOutput
import org.apache.camel.*
import org.osgi.framework.*
import org.apache.camel.impl.DefaultAttachment

Message processData(Message message) {
    def messageLog = messageLogFactory.getMessageLog(message)

    def result = [successful: true, text: ""]  // 최종 응답용

    try {
        // ── ① 중단할 메시지 정의
        def iflowID = message.getProperty("iflowID");
        def messageID = message.getProperty("messageID");
        def exceptionText = "Exchange force-stopped by admin"

        // ── ② OSGi 번들 컨텍스트에서 CamelContext 가져오기
        def bundleCtx = FrameworkUtil.getBundle(
            Class.forName("com.sap.gateway.ip.core.customdev.util.Message")
        ).getBundleContext()

        ServiceReference[] srs = bundleCtx.getServiceReferences(
            CamelContext.class.getName(),
            "(camel.context.name=${iflowID})"
        )

        if (!srs || srs.length == 0) {
            result.successful = false
            result.text = "No CamelContext found for iflowID=${iflowID}"
        } else {
            def camelContext = (CamelContext) bundleCtx.getService(srs[0])

            // messageLog.addAttachmentAsString('Log', body, 'text/plain')


            def inflight = camelContext.getInflightRepository().browse()

            if (inflight.isEmpty()) {
                result.successful = false
                result.text = "No messages in InflightRepository, thus nothing to stop."
            } else {
                // ── ③ Inflight Exchange 검색
                def repoEntry = inflight.find {
                    it.getExchange().getProperty("SAP_MessageProcessingLogID") == messageID }
                inflight.find {
                    message.setHeader (it.getExchange().getProperty("SAP_MessageProcessingLogID"),it.getExchange().getProperty("SAP_MessageProcessingLogID")) }
                if (repoEntry != null) {
                    // ── ④ 중단 처리
                    def exchange = repoEntry.getExchange()
                    exchange.setException(new InterruptedException(exceptionText))
                    exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE)
                } else {
                    result.successful = false
                    result.text = "No message found for given messageID=${messageID}"
                }
            }
        }
    }
    catch(Exception ex) {
        result.successful = false
        result.text = ex.getMessage()
    }

    // ── ⑤ 응답 생성
    def body = JsonOutput.toJson(result)
    message.setBody(body)
    message.setHeader('Content-Type', 'application/json')
    return message
}
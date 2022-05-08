package com.wutsi.application.web.endpoint

import com.amazonaws.util.IOUtils
import com.wutsi.application.web.service.WebTokenProvider
import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.net.HttpURLConnection
import java.net.URL
import javax.servlet.http.HttpServletResponse

@RestController
class FacebookFeedController(
    private val tokenProvider: WebTokenProvider,
    private val traceContext: TracingContext
) {
    @Autowired
    @Qualifier("CatalogEnvironment")
    private lateinit var env: com.wutsi.ecommerce.catalog.Environment

    @GetMapping("/feeds/{merchant-id}/facebook", produces = ["application/csv"])
    fun invoke(
        @PathVariable(name = "merchant-id") merchantId: Long,
        response: HttpServletResponse
    ) {
        val url = URL("${env.url}/v1/feeds/$merchantId/facebook")
        val cnn = url.openConnection() as HttpURLConnection
        try {
            cnn.setRequestProperty("Authorization", "Bearer ${tokenProvider.getToken()}")
            cnn.setRequestProperty(TracingContext.HEADER_TENANT_ID, traceContext.tenantId())
            cnn.setRequestProperty(TracingContext.HEADER_CLIENT_INFO, traceContext.clientInfo())
            cnn.setRequestProperty(TracingContext.HEADER_CLIENT_ID, traceContext.clientId())
            cnn.setRequestProperty(TracingContext.HEADER_TRACE_ID, traceContext.traceId())
            cnn.setRequestProperty(TracingContext.HEADER_DEVICE_ID, traceContext.deviceId())
            IOUtils.copy(cnn.inputStream, response.outputStream)
        } finally {
            cnn.disconnect()
        }
    }
}

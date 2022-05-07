package com.wutsi.application.web.endpoint

import com.amazonaws.util.IOUtils
import com.wutsi.application.web.service.WebTokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.net.HttpURLConnection
import java.net.URL
import javax.servlet.http.HttpServletResponse

@RestController
class FacebookFeedController(private val tokenProvider: WebTokenProvider) {
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
            IOUtils.copy(cnn.inputStream, response.outputStream)
        } finally {
            cnn.disconnect()
        }
    }
}

package com.wutsi.application.web.service

import com.wutsi.platform.core.security.TokenProvider
import com.wutsi.platform.security.WutsiSecurityApi
import com.wutsi.platform.security.dto.AuthenticationRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class WebTokenProvider(
    private val securityApi: WutsiSecurityApi,

    @Value("\${wutsi.platform.security.api-key}") private val apiKey: String
) : TokenProvider {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WebTokenProvider::class.java)
    }

    private var token: String? = null

    override fun getToken(): String? {
        if (token == null) {
            LOGGER.info("Authenticating...")
            token = securityApi.authenticate(
                AuthenticationRequest(
                    type = "application",
                    apiKey = apiKey
                )
            ).accessToken
        }

        return token
    }
}

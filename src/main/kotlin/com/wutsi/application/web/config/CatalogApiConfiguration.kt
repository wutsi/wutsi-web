package com.wutsi.application.web.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.application.web.service.FeignTenantIdRequestInterceptor
import com.wutsi.application.web.service.WebTokenProvider
import com.wutsi.platform.catalog.Environment.PRODUCTION
import com.wutsi.platform.catalog.Environment.SANDBOX
import com.wutsi.platform.catalog.WutsiCatalogApi
import com.wutsi.platform.catalog.WutsiCatalogApiBuilder
import com.wutsi.platform.core.security.feign.FeignAuthorizationRequestInterceptor
import com.wutsi.platform.core.tracing.feign.FeignTracingRequestInterceptor
import com.wutsi.platform.core.util.feign.Custom5XXErrorDecoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles

@Configuration
class CatalogApiConfiguration(
    private val tokenProvider: WebTokenProvider,
    private val tracingRequestInterceptor: FeignTracingRequestInterceptor,
    private val tenantIdRequestInterceptor: FeignTenantIdRequestInterceptor,
    private val mapper: ObjectMapper,
    private val env: Environment
) {
    @Bean
    fun catalogApi(): WutsiCatalogApi =
        WutsiCatalogApiBuilder().build(
            env = environment(),
            mapper = mapper,
            interceptors = listOf(
                tracingRequestInterceptor,
                tenantIdRequestInterceptor,
                FeignAuthorizationRequestInterceptor(tokenProvider)
            ),
            errorDecoder = Custom5XXErrorDecoder()
        )

    private fun environment(): com.wutsi.platform.catalog.Environment =
        if (env.acceptsProfiles(Profiles.of("prod")))
            PRODUCTION
        else
            SANDBOX
}

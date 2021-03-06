package com.wutsi.application.web.service

import com.wutsi.application.shared.service.TenantIdProvider
import com.wutsi.platform.core.tracing.TracingContext
import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.stereotype.Service

@Service
class FeignTenantIdRequestInterceptor(
    private val tenantIdProvider: TenantIdProvider
) : RequestInterceptor {
    override fun apply(template: RequestTemplate) {
        template.header(TracingContext.HEADER_TENANT_ID, tenantIdProvider.get().toString())
    }
}

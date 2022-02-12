package com.wutsi.application.web.service

import com.wutsi.platform.tenant.dto.Tenant
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import org.springframework.stereotype.Service
import org.springframework.web.context.WebApplicationContext
import javax.servlet.http.HttpServletRequest

@Service
@Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
class TenantProvider(
    private val env: Environment,
    private val service: TenantService,
    private val request: HttpServletRequest
) {
    fun get(): Tenant =
        if (isLocal())
            service.getTenant(1L)
        else
            service.getTenant(request.serverName)

    private fun isLocal(): Boolean =
        !isProd() && !isTest()

    private fun isProd(): Boolean =
        env.acceptsProfiles(Profiles.of("prod"))

    private fun isTest(): Boolean =
        env.acceptsProfiles(Profiles.of("test"))
}

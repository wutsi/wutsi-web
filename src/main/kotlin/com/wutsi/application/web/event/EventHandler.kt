package com.wutsi.application.web.event

import com.wutsi.application.web.service.TenantService
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.Event
import com.wutsi.platform.tenant.event.EventURN
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class EventHandler(
    private val tenantService: TenantService,
    private val logger: KVLogger,
) {
    @EventListener
    fun onEvent(event: Event) {
        if (EventURN.TENANT_LOADED.urn == event.type) {
            onTenantLoaded()
        }
    }

    private fun onTenantLoaded() {
        val count = tenantService.reload()
        logger.add("tenant_count", count)
    }
}

package com.wutsi.application.web.service

import com.wutsi.platform.tenant.WutsiTenantApi
import com.wutsi.platform.tenant.dto.Tenant
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class TenantService(private val tenantApi: WutsiTenantApi) {
    companion object {
        private var LOGGER = LoggerFactory.getLogger(TenantService::class.java)
    }

    private var tenants: List<Tenant>? = null

    fun getTenant(id: Long): Tenant =
        getTenants().find { it.id == id } ?: throw IllegalStateException("No tenant not found - $id")

    fun getTenant(host: String): Tenant =
        getTenants().find { it.webappUrl.contains(host) } ?: throw IllegalStateException("Not tenant found - $host")

    fun reload(): Int {
        // Load tenants
        val items = tenantApi.listTenants().tenants
        LOGGER.info("${items.size} tenant(s) loaded")

        // Cache
        tenants = items.map {
            tenantApi.getTenant(it.id).tenant
        }
        return tenants!!.size
    }

    private fun getTenants(): List<Tenant> {
        if (tenants == null)
            reload()

        return tenants!!
    }
}

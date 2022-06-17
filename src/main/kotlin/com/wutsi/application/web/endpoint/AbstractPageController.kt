package com.wutsi.application.web.endpoint

import com.wutsi.application.shared.service.TenantProvider
import com.wutsi.platform.qr.WutsiQrApi
import com.wutsi.platform.qr.dto.EncodeQRCodeRequest
import com.wutsi.platform.qr.entity.EntityType
import com.wutsi.platform.tenant.dto.Tenant
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import org.springframework.web.bind.annotation.ModelAttribute

abstract class AbstractPageController {
    @Value("\${wutsi.application.asset-url}")
    private lateinit var assetUrl: String

    @Autowired
    private lateinit var tenantProvider: TenantProvider

    @Autowired
    private lateinit var qrApi: WutsiQrApi

    @Autowired
    private lateinit var env: Environment

    @ModelAttribute(name = "assetUrl")
    fun getAssetUrl(): String = assetUrl

    @ModelAttribute(name = "pageId")
    fun getPageId(): String = pageId()

    @ModelAttribute(name = "tenant")
    fun getTenant(): Tenant = tenantProvider.get()

    @ModelAttribute(name = "tenantLogoUrl")
    fun getTenantLogoUrl(): String? =
        getTenant().logos.find { it.type == "WORDMARK" }?.url

    abstract fun pageId(): String

    protected fun getQrCodeUrl(id: String, type: EntityType): String? {
        try {
            val token = qrApi.encode(
                request = EncodeQRCodeRequest(
                    type = type.name,
                    id = id,
                )
            ).token

            val env = if (env.acceptsProfiles(Profiles.of("prod")))
                com.wutsi.platform.qr.Environment.PRODUCTION
            else
                com.wutsi.platform.qr.Environment.SANDBOX
            return "${env.url}/image/$token.png"
        } catch (ex: Exception) {
            LoggerFactory.getLogger(javaClass).warn("Unable to generate the QRCode for $type.$id", ex)
            return null
        }
    }
}

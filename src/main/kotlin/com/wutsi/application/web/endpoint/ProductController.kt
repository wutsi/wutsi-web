package com.wutsi.application.web.endpoint

import com.wutsi.analytics.tracking.WutsiTrackingApi
import com.wutsi.analytics.tracking.dto.PushTrackRequest
import com.wutsi.analytics.tracking.dto.Track
import com.wutsi.analytics.tracking.entity.EventType
import com.wutsi.application.shared.service.SharedUIMapper
import com.wutsi.application.shared.service.TenantProvider
import com.wutsi.ecommerce.catalog.WutsiCatalogApi
import com.wutsi.ecommerce.catalog.dto.Product
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.Account
import com.wutsi.platform.core.tracing.TracingContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.UUID
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/product")
class ProductController(
    private val catalogApi: WutsiCatalogApi,
    private val accountApi: WutsiAccountApi,
    private val sharedUIMapper: SharedUIMapper,
    private val tenantProvider: TenantProvider,
    private val trackingApi: WutsiTrackingApi,
    private val tracingContext: TracingContext,
    private val httpRequest: HttpServletRequest
) : AbstractPageController() {
    companion object {
        const val PAGE_ID = "page.Product"
        private val LOGGER = LoggerFactory.getLogger(ProfileController::class.java)
    }

    override fun pageId() = "page.product"

    @GetMapping
    fun index(@RequestParam id: Long, model: Model): String {
        val product: Product = findProduct(id)
        addOpenGraph(product, model)

        val tenant = tenantProvider.get()
        val productModel = sharedUIMapper.toProductModel(product, tenant)
        val accountModel = sharedUIMapper.toAccountModel(findAccount(product.accountId))
        model.addAttribute("product", productModel)
        model.addAttribute("account", accountModel)

        track(product)
        return "product"
    }

    private fun track(product: Product) {
        val correlationId = UUID.randomUUID().toString()
        track(correlationId, EventType.LOAD)
        track(correlationId, EventType.VIEW, product)
    }

    private fun track(correlationId: String, event: EventType, product: Product? = null) {
        try {
            val url = httpRequest.requestURL
            if (!httpRequest.queryString.isNullOrEmpty())
                url.append('?').append(httpRequest.queryString)

            trackingApi.push(
                request = PushTrackRequest(
                    track = Track(
                        time = System.currentTimeMillis(),
                        tenantId = tenantProvider.tenantId().toString(),
                        deviceId = tracingContext.deviceId(),
                        correlationId = correlationId,
                        accountId = null,
                        productId = product?.id?.toString(),
                        merchantId = product?.accountId?.toString(),
                        page = PAGE_ID,
                        event = event.name,
                        ua = httpRequest.getHeader("User-Agent"),
                        referer = httpRequest.getHeader("Referer"),
                        ip = httpRequest.getHeader("X-Forwarded-For") ?: httpRequest.remoteAddr,
                        url = url.toString()
                    )
                )
            )
        } catch (ex: Exception) {
            LOGGER.warn("Unable to track Event#$event - Product#${product?.id}", ex)
        }
    }

    private fun addOpenGraph(product: Product, model: Model) {
        model.addAttribute("title", product.title)
        model.addAttribute("description", product.summary)
        model.addAttribute("image", product.thumbnail?.url)
        model.addAttribute("type", "website")
    }

    private fun findAccount(id: Long): Account =
        accountApi.getAccount(id).account

    private fun findProduct(id: Long): Product =
        catalogApi.getProduct(id).product
}

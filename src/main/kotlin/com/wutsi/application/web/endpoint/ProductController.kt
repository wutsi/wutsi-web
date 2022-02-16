package com.wutsi.application.web.endpoint

import com.wutsi.application.shared.service.SharedUIMapper
import com.wutsi.application.shared.service.TenantProvider
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.Account
import com.wutsi.platform.catalog.WutsiCatalogApi
import com.wutsi.platform.catalog.dto.Product
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/product")
class ProductController(
    private val catalogApi: WutsiCatalogApi,
    private val accountApi: WutsiAccountApi,
    private val sharedUIMapper: SharedUIMapper,
    private val tenantProvider: TenantProvider
) : AbstractPageController() {
    override fun pageId() = "page.product"

    @GetMapping
    fun index(@RequestParam id: Long, model: Model): String {
        val product = findProduct(id)
        addOpenGraph(product, model)

        val tenant = tenantProvider.get()
        val productModel = sharedUIMapper.toProductModel(product, tenant)
        val accountModel = sharedUIMapper.toAccountModel(findAccount(product.accountId))
        model.addAttribute("product", productModel)
        model.addAttribute("account", accountModel)

        return "product"
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

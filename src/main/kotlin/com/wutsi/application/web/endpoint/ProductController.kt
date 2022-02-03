package com.wutsi.application.web.endpoint

import com.wutsi.platform.catalog.WutsiCatalogApi
import com.wutsi.platform.catalog.dto.Product
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/product")
class ProductController(
    private val catalogApi: WutsiCatalogApi,

    @Value("\${wutsi.application.asset-url}") private val assetUrl: String
) {
    @GetMapping
    fun index(@RequestParam id: Long, model: Model): String {
        val product = findProduct(id)

        model.addAttribute("assetUrl", assetUrl)
        model.addAttribute("title", product.title)
        model.addAttribute("description", product.summary)
        model.addAttribute("image", product.thumbnail?.url)
        model.addAttribute("type", "product")

        return "index"
    }

    private fun findProduct(id: Long): Product =
        catalogApi.getProduct(id).product
}

package com.wutsi.application.web.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.catalog.WutsiCatalogApi
import com.wutsi.platform.catalog.dto.GetProductResponse
import com.wutsi.platform.catalog.dto.PictureSummary
import com.wutsi.platform.catalog.dto.Product
import org.junit.jupiter.api.Test
import org.springframework.boot.test.mock.mockito.MockBean

internal class ProductControllerTest : SeleniumTestSupport() {
    @MockBean
    private lateinit var catalogApi: WutsiCatalogApi

    @Test
    fun `product profile`() {
        // GIVEN
        val product = Product(
            id = 1,
            title = "39 Carat neckless",
            summary = "This is a sample description of a user",
            thumbnail = PictureSummary(
                url = "https://st2.depositphotos.com/1001030/12469/i/950/depositphotos_124693804-stock-photo-afro-american-man-posing-in.jpg"
            )
        )
        doReturn(GetProductResponse(product)).whenever(catalogApi).getProduct(any())

        // WHEN
        navigate(url("product?id=${product.id}"))

        // THEN
        assertCurrentPageIs("page.product")

        assertElementAttribute("head title", "text", "${product.title} | Wutsi")
        assertElementAttribute("head meta[name='description']", "content", product.summary)

        assertElementAttribute("head title", "text", "${product.title} | Wutsi")
        assertElementAttribute("head meta[name='description']", "content", product.summary)
        assertElementAttribute("head meta[property='og:type']", "content", "website")

        assertElementAttribute(
            ".cta-android",
            "href",
            "https://play.google.com/store/apps/details?id=com.wutsi.wutsi_wallet"
        )
        assertElementNotPresent(".cta-ios")
    }
}

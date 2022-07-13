package com.wutsi.application.web.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.ecommerce.catalog.WutsiCatalogApi
import com.wutsi.ecommerce.catalog.dto.GetProductResponse
import com.wutsi.ecommerce.catalog.dto.PictureSummary
import com.wutsi.ecommerce.catalog.dto.Product
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.Account
import com.wutsi.platform.account.dto.Category
import com.wutsi.platform.account.dto.GetAccountResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.mock.mockito.MockBean

internal class ProductControllerTest : SeleniumTestSupport() {
    @MockBean
    private lateinit var catalogApi: WutsiCatalogApi

    @MockBean
    private lateinit var accountApi: WutsiAccountApi

    @Test
    fun product() {
        // GIVEN
        val product = createProduct()
        doReturn(GetProductResponse(product)).whenever(catalogApi).getProduct(any())

        val account = createAccount()
        doReturn(GetAccountResponse(account)).whenever(accountApi).getAccount(any())

        // WHEN
        val link = url("product?id=${product.id}")
        navigate(link)

        // THEN
        assertCurrentPageIs("page.product")

        // OpenGraph
        assertElementAttribute("head title", "text", "${product.title} | Wutsi")
        assertElementAttribute("head meta[name='description']", "content", product.summary)
        assertElementAttribute("head meta[property='og:title']", "content", product.title)
        assertElementAttribute("head meta[property='og:description']", "content", product.summary)
        assertElementAttribute("head meta[property='og:type']", "content", "website")
        assertElementAttribute(
            "head meta[property='og:image']",
            "content",
            product.thumbnail?.url
        )

        assertAppStoreLinksPresent()
    }

    private fun createAccount() = Account(
        id = 1,
        displayName = "Ray Sponsible",
        biography = "This is a sample biography of a user",
        country = "CM",
        language = "en",
        pictureUrl = "https://st2.depositphotos.com/1001030/12469/i/950/depositphotos_124693804-stock-photo-afro-american-man-posing-in.jpg",
        business = true,
        category = Category(
            id = 100,
            title = "Writer"
        )
    )

    private fun createProduct() = Product(
        id = 1,
        accountId = 555,
        title = "39 Carat Neckless",
        summary = "Steal her heart!",
        thumbnail = PictureSummary(
            url = "https://www.volusion.com/blog/content/images/2021/07/Product-Photos.jpg"
        ),
        price = 150000.0,
        comparablePrice = 170000.0,
        description = "This is a long description",
        currency = "XAF"
    )
}

package com.wutsi.application.web.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.analytics.tracking.WutsiTrackingApi
import com.wutsi.analytics.tracking.dto.PushTrackRequest
import com.wutsi.analytics.tracking.entity.EventType
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
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class ProductControllerTest : SeleniumTestSupport() {
    @MockBean
    private lateinit var catalogApi: WutsiCatalogApi

    @MockBean
    private lateinit var accountApi: WutsiAccountApi

    @MockBean
    private lateinit var trackingApi: WutsiTrackingApi

    @Test
    fun `product profile`() {
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
        assertElementAttribute("head title", "text", "${product.title} | Wutsi")
        assertElementAttribute("head meta[name='description']", "content", product.summary)
        assertElementAttribute("head meta[property='og:type']", "content", "website")

        // Content
        assertElementText(".slide--headline h1", product.title)
        assertElementText(".slide--bio", product.summary!!)
        assertElementText(".product-price", "150,000 FCFA")
        assertElementAttribute(".product-picture", "src", product.thumbnail!!.url)

        assertElementAttribute(
            ".cta-android",
            "href",
            "https://play.google.com/store/apps/details?id=com.wutsi.wutsi_wallet"
        )
        assertElementNotPresent(".cta-ios")

        val req = argumentCaptor<PushTrackRequest>()
        verify(trackingApi, times(2)).push(req.capture())

        val track1 = req.firstValue.track
        assertNotNull(track1.correlationId)
        assertEquals(EventType.LOAD.name, track1.event)
        assertNull(track1.productId)
        assertNull(track1.merchantId)
        assertNull(track1.accountId)
        assertEquals(ProductController.PAGE_ID, track1.page)
        assertEquals(link, track1.url)
        assertNotNull(track1.deviceId)
        assertNotNull(track1.ua)
        assertFalse(track1.bot)

        val track2 = req.secondValue.track
        assertEquals(track1.correlationId, track2.correlationId)
        assertEquals(EventType.VIEW.name, track2.event)
        assertEquals(product.id.toString(), track2.productId)
        assertEquals(product.accountId.toString(), track2.merchantId)
        assertNull(track1.accountId)
        assertEquals(ProductController.PAGE_ID, track2.page)
        assertEquals(link, track2.url)
        assertNotNull(track1.deviceId, track2.deviceId)
        assertEquals(track1.ua, track2.ua)
        assertEquals(track1.bot, track2.bot)
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

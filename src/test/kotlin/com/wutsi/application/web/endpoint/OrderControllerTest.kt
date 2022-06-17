package com.wutsi.application.web.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.qr.WutsiQrApi
import com.wutsi.platform.qr.dto.EncodeQRCodeResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.mock.mockito.MockBean

internal class OrderControllerTest : SeleniumTestSupport() {
    @MockBean
    private lateinit var qrApi: WutsiQrApi

    @Test
    fun order() {
        // GIVEN
        doReturn(EncodeQRCodeResponse("111-222-333")).whenever(qrApi).encode(any())

        // WHEN
        val link = url("order?id=repore-40945-5409540fa")
        navigate(link)

        // THEN
        assertCurrentPageIs("page.order")

        // OpenGraph
        assertElementAttribute("head title", "text", "Order #40FA | Wutsi")
        assertElementAttribute(
            "head meta[property='og:image']",
            "content",
            "https://wutsi-qr-server-test.herokuapp.com/image/111-222-333.png"
        )

        assertAppStoreLinksPresent()
    }
}

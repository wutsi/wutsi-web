package com.wutsi.application.web.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.Account
import com.wutsi.platform.account.dto.GetAccountResponse
import com.wutsi.platform.qr.WutsiQrApi
import com.wutsi.platform.qr.dto.EncodeQRCodeResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.mock.mockito.MockBean

internal class QRCodeControllerTest : SeleniumTestSupport() {
    @MockBean
    private lateinit var accountApi: WutsiAccountApi

    @MockBean
    private lateinit var qrApi: WutsiQrApi

    @Test
    fun account() {
        // GIVEN
        val account = Account(
            id = 1,
            displayName = "Ray Sponsible",
            biography = "This is a sample biography of a user",
            country = "CM",
            language = "en",
            pictureUrl = "https://st2.depositphotos.com/1001030/12469/i/950/depositphotos_124693804-stock-photo-afro-american-man-posing-in.jpg"
        )
        doReturn(GetAccountResponse(account)).whenever(accountApi).getAccount(any())

        doReturn(EncodeQRCodeResponse(token = "xxxxxx")).whenever(qrApi).encode(any())

        // WHEN
        navigate(url("qr-code/account/${account.id}.png"))
    }
}

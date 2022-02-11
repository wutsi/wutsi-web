package com.wutsi.application.web.endpoint

import org.junit.jupiter.api.Test

internal class HomeControllerTest : SeleniumTestSupport() {

    @Test
    fun index() {
        navigate(url(""))

        assertCurrentPageIs("page.home")
    }
}

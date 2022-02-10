package com.wutsi.application.web.endpoint

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.ModelAttribute

abstract class AbstractPageController {
    @Value("\${wutsi.application.asset-url}")
    private lateinit var assetUrl: String

    @ModelAttribute(name = "assetUrl")
    fun getAssetUrl(): String = assetUrl

    @ModelAttribute(name = "pageId")
    fun getPageId(): String = pageId()

    abstract fun pageId(): String
}

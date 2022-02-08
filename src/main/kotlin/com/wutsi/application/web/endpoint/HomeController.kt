package com.wutsi.application.web.endpoint

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/")
class HomeController(
    @Value("\${wutsi.application.asset-url}") private val assetUrl: String
) {
    @GetMapping
    fun index(model: Model): String {
        model.addAttribute("assetUrl", assetUrl)
        model.addAttribute("title", "Wutsi")
        model.addAttribute("type", "website")

        return "index"
    }
}

package com.wutsi.application.web.endpoint

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/")
class HomeController : AbstractPageController() {
    override fun pageId() = "page.home"

    @GetMapping
    fun index(model: Model): String {
        addOpenGraph(model)
        return "index"
    }

    private fun addOpenGraph(model: Model) {
        model.addAttribute("title", "Wutsi")
        model.addAttribute("type", "website")
    }
}

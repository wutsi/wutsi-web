package com.wutsi.application.web.endpoint

import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.Account
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/profile")
class ProfileController(
    private val accountApi: WutsiAccountApi,

    @Value("\${wutsi.application.asset-url}") private val assetUrl: String
) {
    @GetMapping
    fun index(@RequestParam id: Long, model: Model): String {
        val account = findAccount(id)

        model.addAttribute("assetUrl", assetUrl)
        model.addAttribute("title", account.displayName)
        model.addAttribute("description", account.biography)
        model.addAttribute("image", account.pictureUrl)
        model.addAttribute("site_name", account.website)
        model.addAttribute("type", "profile")

        return "index"
    }

    private fun findAccount(id: Long): Account =
        accountApi.getAccount(id).account
}

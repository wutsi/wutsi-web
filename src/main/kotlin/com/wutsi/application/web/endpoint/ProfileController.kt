package com.wutsi.application.web.endpoint

import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.Account
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/profile")
class ProfileController(
    private val accountApi: WutsiAccountApi,
) : AbstractPageController() {
    override fun pageId() = "page.profile"

    @GetMapping
    fun index(@RequestParam id: Long, model: Model): String {
        val account = findAccount(id)
        addOpenGraph(account, model)
        model.addAttribute("account", account)
        return "profile"
    }

    private fun addOpenGraph(account: Account, model: Model) {
        model.addAttribute("title", account.displayName)
        model.addAttribute("description", account.biography)
        model.addAttribute("image", account.pictureUrl)
        model.addAttribute("site_name", account.website)
        model.addAttribute("type", "profile")
    }

    private fun findAccount(id: Long): Account =
        accountApi.getAccount(id).account
}

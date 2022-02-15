package com.wutsi.application.web.endpoint

import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.Account
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/profile")
class ProfileController(
    private val accountApi: WutsiAccountApi,
    private val request: HttpServletRequest
) : AbstractPageController() {
    override fun pageId() = "page.profile"

    @GetMapping
    fun index(@RequestParam id: Long, model: Model): String {
        val account = findAccount(id)
        addOpenGraph(account, model)
        model.addAttribute("account", account)
        model.addAttribute("qrCodeUrl", getQrCodeUrl(id))
        return "profile"
    }

    private fun addOpenGraph(account: Account, model: Model) {
        model.addAttribute("title", account.displayName)
        model.addAttribute("description", account.biography)
        model.addAttribute("image", getQrCodeUrl(account.id))
        model.addAttribute("site_name", account.website)
        model.addAttribute("type", "profile")
    }

    private fun findAccount(id: Long): Account =
        accountApi.getAccount(id).account

    private fun getQrCodeUrl(id: Long): String {
        val port = if (request.serverPort == 80 || request.serverPort == 443)
            ""
        else
            ":${request.serverPort}"
        return "${request.scheme}://${request.serverName}$port/qr-code/account/$id.png"
    }
}

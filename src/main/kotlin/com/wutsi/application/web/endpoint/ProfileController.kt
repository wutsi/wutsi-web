package com.wutsi.application.web.endpoint

import com.wutsi.application.shared.service.SharedUIMapper
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.Account
import com.wutsi.platform.qr.WutsiQrApi
import com.wutsi.platform.qr.dto.EncodeQRCodeRequest
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/profile")
class ProfileController(
    private val accountApi: WutsiAccountApi,
    private val qrApi: WutsiQrApi,
    private val sharedUIMapper: SharedUIMapper,
    private val env: Environment,
) : AbstractPageController() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ProfileController::class.java)
    }

    override fun pageId() = "page.profile"

    @GetMapping
    fun index(@RequestParam id: Long, model: Model): String {
        val account = findAccount(id)
        val profile = sharedUIMapper.toAccountModel(account)
        addOpenGraph(account, model)
        model.addAttribute("account", profile)
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

    private fun getQrCodeUrl(id: Long): String? {
        try {
            val token = qrApi.encode(
                request = EncodeQRCodeRequest(
                    type = "account",
                    id = id.toString(),
                )
            ).token

            val env = if (env.acceptsProfiles(Profiles.of("prod")))
                com.wutsi.platform.qr.Environment.PRODUCTION
            else
                com.wutsi.platform.qr.Environment.SANDBOX
            return "${env.url}/image/$token.png"
        } catch (ex: Exception) {
            LOGGER.warn("Unable to generate the QRCode", ex)
            return null
        }
    }
}

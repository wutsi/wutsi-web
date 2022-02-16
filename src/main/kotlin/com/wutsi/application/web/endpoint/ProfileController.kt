package com.wutsi.application.web.endpoint

import com.wutsi.application.shared.service.SharedUIMapper
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.Account
import com.wutsi.platform.qr.WutsiQrApi
import com.wutsi.platform.qr.dto.EncodeQRCodeRequest
import io.github.g0dkar.qrcode.QRCode
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.io.ByteArrayOutputStream
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/profile")
class ProfileController(
    private val accountApi: WutsiAccountApi,
    private val qrApi: WutsiQrApi,
    private val request: HttpServletRequest,
    private val sharedUIMapper: SharedUIMapper,
) : AbstractPageController() {
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

    @GetMapping("/qr-code/{id}.png", produces = [MediaType.IMAGE_PNG_VALUE])
    fun qr(@PathVariable id: Long): ResponseEntity<ByteArray> {
        val account = findAccount(id)
        val ttl = 86400
        val data = qrApi.encode(
            request = EncodeQRCodeRequest(
                type = "account",
                id = account.id.toString(),
                timeToLive = ttl
            )
        ).token

        val image = ByteArrayOutputStream()
        QRCode(data).render(margin = 30, cellSize = 30).writeImage(image)
        val resource = ByteArrayResource(image.toByteArray(), MediaType.IMAGE_PNG_VALUE)

        return ResponseEntity.ok()
            .header(HttpHeaders.CACHE_CONTROL, "public, max-age=$ttl")
            .body(resource.byteArray)
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
        return "${request.scheme}://${request.serverName}$port/profile/qr-code/$id.png"
    }
}

package com.wutsi.application.web.endpoint

import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.Account
import com.wutsi.platform.qr.WutsiQrApi
import com.wutsi.platform.qr.dto.EncodeQRCodeRequest
import io.github.g0dkar.qrcode.QRCode
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders.CACHE_CONTROL
import org.springframework.http.HttpHeaders.CONTENT_DISPOSITION
import org.springframework.http.MediaType.IMAGE_PNG_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.io.ByteArrayOutputStream
import java.util.UUID

@Controller
@RequestMapping("/qr-code")
class QRCodeController(
    private val accountApi: WutsiAccountApi,
    private val qrApi: WutsiQrApi,
) {
    @GetMapping("/account/{id}")
    fun account(@PathVariable id: Long): ResponseEntity<ByteArrayResource> {
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
        val resource = ByteArrayResource(image.toByteArray(), IMAGE_PNG_VALUE)

        return ResponseEntity.ok()
            .header(CONTENT_DISPOSITION, "attachment; filename=\"qrcode-$id-${UUID.randomUUID()}.png\"")
            .header(CACHE_CONTROL, "public, max-age=$ttl")
            .body(resource)
    }

    private fun findAccount(id: Long): Account =
        accountApi.getAccount(id).account
}

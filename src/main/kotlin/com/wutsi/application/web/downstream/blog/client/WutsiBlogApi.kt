package com.wutsi.application.web.downstream.blog.client

import com.wutsi.application.news.downstream.blog.dto.GetStoryResponse
import feign.Headers
import feign.Param
import feign.RequestLine

interface WutsiBlogApi {
    @RequestLine("GET /v1/story/{id}")
    @Headers(value = ["Content-Type: application/json"])
    fun getStory(@Param("id") id: Long): GetStoryResponse
}

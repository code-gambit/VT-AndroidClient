package com.github.code.gambit.network.api

import com.github.code.gambit.data.entity.network.FileNetworkEntity
import com.github.code.gambit.data.entity.network.UrlNetworkEntity
import com.github.code.gambit.data.entity.network.UserNetworkEntity
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import javax.inject.Named

interface ApiService {

    @GET("user/{userId}")
    suspend fun getUser(@Path("userId") @Named("UID") userId: String): UserNetworkEntity

    @PUT("user/{userId}")
    suspend fun updateUser(@Path("userId") @Named("UID") userId: String, @Body userNetworkEntity: UserNetworkEntity): UserNetworkEntity

    @DELETE("user/{userId}")
    suspend fun deleteUser(@Path("userId") @Named("UID") userId: String): UserNetworkEntity

    @GET("user/{userId}/file")
    suspend fun getFiles(@Path("userId") @Named("UID") userId: String): List<FileNetworkEntity>

    @POST("user/{userId}/file")
    suspend fun uploadFiles(@Path("userId") @Named("UID") userId: String, @Body fileNetworkEntity: FileNetworkEntity): FileNetworkEntity

    @DELETE("file/{fileId}")
    suspend fun deleteFile(@Path("fileId") fileId: String): FileNetworkEntity

    @GET("file/{fileId}/url")
    suspend fun getUrls(@Path("fileId") fileId: String): List<UrlNetworkEntity>

    @POST("file/{fileId}/url")
    suspend fun generateUrl(@Path("fileId") fileId: String): UrlNetworkEntity

    @PUT("file/{fileId}/url/{urlId}")
    suspend fun updateUrl(
        @Path("fileId") fileId: String,
        @Path("urlId") urlId: String,
        @Body urlNetworkEntity: UrlNetworkEntity
    ): UrlNetworkEntity

    @DELETE("file/{fileId}/url/{urlId}")
    suspend fun deleteUrl(@Path("fileId") fileId: String, @Path("urlId") urlId: String): UrlNetworkEntity
}

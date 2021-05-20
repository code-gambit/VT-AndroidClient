package com.github.code.gambit.data.remote.services

import com.github.code.gambit.data.entity.network.FileNetworkEntity
import com.github.code.gambit.data.entity.network.UrlNetworkEntity
import com.github.code.gambit.data.entity.network.UserNetworkEntity
import com.github.code.gambit.data.remote.responses.ListResponse
import com.github.code.gambit.data.remote.responses.Response
import com.github.code.gambit.utility.AppConstant
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("user/{${AppConstant.API_PATH.USER_ID}}")
    suspend fun getUser(@Path("userId") userId: String): Response<UserNetworkEntity>

    @PUT("user/{${AppConstant.API_PATH.USER_ID}}")
    suspend fun updateUser(@Path("userId") userId: String, @Body userNetworkEntity: UserNetworkEntity): Response<UserNetworkEntity>

    @DELETE("user/{${AppConstant.API_PATH.USER_ID}}")
    suspend fun deleteUser(@Path("userId") userId: String): Response<UserNetworkEntity>

    @GET("user/{${AppConstant.API_PATH.USER_ID}}/file")
    suspend fun getFiles(@Path("userId") userId: String, @Query(AppConstant.API_QUERY.FILE_LEK) lastEvalKey: String?): ListResponse<FileNetworkEntity>

    @POST("user/{${AppConstant.API_PATH.USER_ID}}/file")
    suspend fun uploadFiles(@Path("userId") userId: String, @Body fileNetworkEntity: FileNetworkEntity): Response<FileNetworkEntity>

    @DELETE("user/{${AppConstant.API_PATH.USER_ID}}/file/{${AppConstant.API_PATH.FILE_ID}}")
    suspend fun deleteFile(@Path(AppConstant.API_PATH.USER_ID) userId: String, @Path(AppConstant.API_PATH.FILE_ID) fileId: String): Response<FileNetworkEntity>

    @GET("file/{${AppConstant.API_PATH.FILE_ID}}/url")
    suspend fun getUrls(@Path(AppConstant.API_PATH.FILE_ID) fileId: String): ListResponse<UrlNetworkEntity>

    @POST("file/{${AppConstant.API_PATH.FILE_ID}}/url")
    suspend fun generateUrl(@Path(AppConstant.API_PATH.FILE_ID) fileId: String): Response<UrlNetworkEntity>

    @PUT("file/{${AppConstant.API_PATH.FILE_ID}}/url/{${AppConstant.API_PATH.URL_ID}}")
    suspend fun updateUrl(
        @Path(AppConstant.API_PATH.FILE_ID) fileId: String,
        @Path(AppConstant.API_PATH.URL_ID) urlId: String,
        @Body urlNetworkEntity: UrlNetworkEntity
    ): Response<UrlNetworkEntity>

    @DELETE("file/{${AppConstant.API_PATH.FILE_ID}}/url/{${AppConstant.API_PATH.URL_ID}}")
    suspend fun deleteUrl(@Path(AppConstant.API_PATH.FILE_ID) fileId: String, @Path(AppConstant.API_PATH.URL_ID) urlId: String): Response<UrlNetworkEntity>
}

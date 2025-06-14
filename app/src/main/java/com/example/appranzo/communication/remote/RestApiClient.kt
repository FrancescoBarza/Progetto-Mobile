package com.example.appranzo.communication.remote

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.ContactsContract.CommonDataKinds.Email
import android.provider.Settings
import android.util.Base64
import com.example.appranzo.communication.remote.friendship.FriendshipRequestDto
import io.ktor.http.ContentType
import com.example.appranzo.communication.remote.loginDtos.AuthResults
import com.example.appranzo.communication.remote.loginDtos.LoginErrorReason
import com.example.appranzo.communication.remote.loginDtos.LoginRequestsDtos
import com.example.appranzo.communication.remote.loginDtos.PlaceDto
import com.example.appranzo.communication.remote.loginDtos.RegistrationErrorReason
import com.example.appranzo.communication.remote.loginDtos.RegistrationRequestsDtos
import com.example.appranzo.communication.remote.loginDtos.RequestId
import com.example.appranzo.communication.remote.loginDtos.UserDto
import com.example.appranzo.data.models.Place
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.RequestBody


class RestApiClient(val httpClient: HttpClient){
    companion object{
        const val REST_API_ADDRESS = "http://10.0.2.2:8080"

    fun isOnline(ctx:Context):Boolean{
        val connectivityManager = ctx
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager
            .getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true ||
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
    }

        fun openWirelessSettings(ctx:Context) {
            val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            if (intent.resolveActivity(ctx.packageManager) != null) {
                ctx.startActivity(intent)
            }
        }

    }

    public var accessToken :String = ""
    public var refreshToken :String = ""

    suspend fun login(username:String,password:String):AuthResults{
        try {
            val url = "$REST_API_ADDRESS/login"
            val loginDto = LoginRequestsDtos(username, password)
            val result = httpClient.post(url) {
                    contentType(ContentType.Application.Json)
                setBody(loginDto)
            }
            return when (result.status) {
                HttpStatusCode.BadRequest -> Json.decodeFromString<AuthResults.ErrorLoginResponseDto>(
                    result.bodyAsText()
                )

                HttpStatusCode.Accepted -> Json.decodeFromString<AuthResults.TokenDtos>(result.bodyAsText())
                else -> AuthResults.ErrorLoginResponseDto(
                    LoginErrorReason.INTERNAL_ERROR,
                    "Error Receiving Datas"
                )
            }
        }
        catch (e:Throwable){
            println(e.message)
            return AuthResults.ErrorLoginResponseDto(
                LoginErrorReason.INTERNAL_ERROR,
                "Unreachable Server"
            )
        }
    }

    suspend fun canILog():Boolean {
        try {
            val url = "$REST_API_ADDRESS/login/trylogin"
            val result = httpClient.get(url) {
                bearerAuth(accessToken)
            }
            return result.status==HttpStatusCode.Accepted
        }
        catch (e:Exception){
            return false
        }
    }

    fun getCurrentUserFromToken(): UserDto? {
        val token = accessToken.takeIf { it.isNotBlank() } ?: return null
        val parts = token.split(".")
        if (parts.size != 3) return null

        return try {
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
            val obj = Json.parseToJsonElement(payload).jsonObject
            val id       = obj["id"]?.jsonPrimitive?.int
            val username = obj["username"]?.jsonPrimitive?.content
            if (id != null && username != null) {
                UserDto(id = id, username = username, photoUrl = null  )
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun placeById(id:Int): Place? {
        try {
            val url = "$REST_API_ADDRESS/places/byId"
            val result = httpClient.post(url) {
                bearerAuth(accessToken)
                contentType(ContentType.Application.Json)
                setBody(RequestId(id))
            }
            if (result.status==HttpStatusCode.OK){
                return Json.decodeFromString<PlaceDto>(result.bodyAsText()).toDto()
            }
            else return null
        }
        catch (e:Exception){
            return null
        }
    }

    suspend fun getAllFriends():List<UserDto>{
        try {
            val url = "$REST_API_ADDRESS/friends"
            val result = httpClient.get(url) {
                bearerAuth(accessToken)
            }
            return if (result.status==HttpStatusCode.OK){
                Json.decodeFromString<List<UserDto>>(result.bodyAsText())
            } else emptyList()
        }
        catch (e:Exception){
            return emptyList()
        }
    }

    suspend fun removeAFriend(userDto: UserDto):Boolean{
        return try {
            val url = "$REST_API_ADDRESS/friends/removeAFriend/${userDto.id}"
            val result = httpClient.delete(url) {
                bearerAuth(accessToken)
            }
            result.status==HttpStatusCode.OK
        } catch (e:Exception){
            false
        }
    }

    suspend fun sendFriendshipRequest(userDto: UserDto):Boolean{
        return try {
            val url = "$REST_API_ADDRESS/friends/sendRequest"
            val result = httpClient.post(url) {
                bearerAuth(accessToken)
                contentType(ContentType.Application.Json)
                setBody(userDto)
            }
            result.status==HttpStatusCode.OK
        } catch (e:Exception){
            false
        }
    }

    suspend fun rejectFriendshipRequest(friendshipRequestDto: FriendshipRequestDto):Boolean{
        return try {
            val url = "$REST_API_ADDRESS/friends/rejectRequest"
            val result = httpClient.post(url) {
                bearerAuth(accessToken)
                contentType(ContentType.Application.Json)
                setBody(friendshipRequestDto)
            }
            result.status==HttpStatusCode.OK
        } catch (e:Exception){
            false
        }
    }

    suspend fun acceptFriendshipRequest(friendshipRequestDto: FriendshipRequestDto):Boolean{
        return try {
            val url = "$REST_API_ADDRESS/friends/acceptRequest"
            val result = httpClient.post(url) {
                bearerAuth(accessToken)
                contentType(ContentType.Application.Json)
                setBody(friendshipRequestDto)
            }
            result.status==HttpStatusCode.OK
        } catch (e:Exception){
            false
        }
    }



    suspend fun getPendingRequests():List<FriendshipRequestDto>{
        return try {
            val url = "$REST_API_ADDRESS/friends/pendingRequests"
            val result = httpClient.get(url) {
                bearerAuth(accessToken)
            }
            if (result.status==HttpStatusCode.OK){
                Json.decodeFromString<List<FriendshipRequestDto>>(result.bodyAsText())
            } else emptyList()
        } catch (e:Exception){
            emptyList()
        }
    }



    suspend fun refresh():AuthResults {
        try {
            val url = "$REST_API_ADDRESS/token/expiredAccess"
            val result = httpClient.post(url) {
                bearerAuth(refreshToken)
            }
            if (result.status==HttpStatusCode.OK){
                val tokens = Json.decodeFromString<AuthResults.TokenDtos>(result.bodyAsText())
                updateTokens(tokens.accessToken,tokens.refreshToken)
                return tokens
            }
            else{
                return AuthResults.ErrorLoginResponseDto(LoginErrorReason.CREDENTIALS_INVALID,"Error while using refresh token")
            }
        }
        catch (e:Exception){
            return AuthResults.ErrorLoginResponseDto(LoginErrorReason.INTERNAL_ERROR,"Error while using refresh token")
        }
    }

    suspend fun register(username: String,password: String,email: String):AuthResults {
        try {
            val url = "$REST_API_ADDRESS/register"
            val registerDto = RegistrationRequestsDtos(username, password, email, null)
            val result = httpClient.post(url) {
                contentType(ContentType.Application.Json)
                setBody(registerDto)
            }
            return when (result.status) {
                HttpStatusCode.BadRequest -> Json.decodeFromString<AuthResults.ErrorRegistrationResponseDto>(
                    result.bodyAsText()
                )

                HttpStatusCode.Accepted -> Json.decodeFromString<AuthResults.TokenDtos>(result.bodyAsText())

                else -> AuthResults.ErrorRegistrationResponseDto(RegistrationErrorReason.INTERNAL_ERROR,
                    "Error Receiving Datas")
            }
        }
        catch (e:Throwable){
            return  AuthResults.ErrorRegistrationResponseDto(RegistrationErrorReason.INTERNAL_ERROR,
                "Unreachable server")
        }
    }

    fun updateTokens(newAccessToken: String, newRefreshToken: String) {
        this.accessToken = newAccessToken
        this.refreshToken = newRefreshToken
    }
}
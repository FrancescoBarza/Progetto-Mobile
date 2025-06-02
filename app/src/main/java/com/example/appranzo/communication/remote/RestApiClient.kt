package com.example.appranzo.communication.remote

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.ContactsContract.CommonDataKinds.Email
import android.provider.Settings
import io.ktor.http.ContentType
import com.example.appranzo.communication.remote.loginDtos.AuthResults
import com.example.appranzo.communication.remote.loginDtos.LoginErrorReason
import com.example.appranzo.communication.remote.loginDtos.LoginRequestsDtos
import com.example.appranzo.communication.remote.loginDtos.RegistrationErrorReason
import com.example.appranzo.communication.remote.loginDtos.RegistrationRequestsDtos
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import io.ktor.http.*


class RestApiClient(val httpClient: HttpClient){
    companion object{
        private const val REST_API_ADDRESS = "http://10.0.2.2:8080"

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
}
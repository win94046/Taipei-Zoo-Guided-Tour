import com.example.crdemo.data.network.BaseUrlProvider
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.HttpUrl.Companion.toHttpUrl

class DynamicBaseUrlInterceptor(
    private val baseUrlProvider: BaseUrlProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalReq = chain.request()
        val originalUrl = originalReq.url

        val userBaseUrl = baseUrlProvider.getBaseUrl().toHttpUrl()

        val newUrl = originalUrl.newBuilder()
            .scheme(userBaseUrl.scheme)
            .host(userBaseUrl.host)
            .port(userBaseUrl.port)
            .build()

        val newReq = originalReq.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newReq)
    }
}

package br.com.lavicameta.data.remote;

import br.com.lavicameta.data.local.SessionStore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/** Configura OkHttp/Retrofit com o token de acesso e tempos limite. */
public final class ApiClient {

    private ApiClient() {
    }

    public static Gson gson() {
        return new GsonBuilder().create();
    }

    public static ApiService criar(String baseUrl, SessionStore sessao) {
        OkHttpClient http = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    String token = sessao.token();
                    if (token == null) {
                        return chain.proceed(original);
                    }
                    return chain.proceed(original.newBuilder().header("Authorization", "Bearer " + token).build());
                })
                .build();
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(http)
                .addConverterFactory(GsonConverterFactory.create(gson()))
                .build()
                .create(ApiService.class);
    }
}

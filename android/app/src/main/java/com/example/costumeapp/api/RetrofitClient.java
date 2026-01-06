package com.example.costumeapp.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import java.util.concurrent.TimeUnit;

public class RetrofitClient {
    private static final String BASE_URL = "http://10.0.2.2:8000/api/"; // 10.0.2.2 for Android Emulator
    private static Retrofit retrofit = null;
    private static OkHttpClient okHttpClient = null;

    public static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            android.util.Log.d("RETROFIT_CLIENT", "Initializing OkHttpClient with HTTP/1.1 and Connection:close");
            okHttpClient = new OkHttpClient.Builder()
                    // Timeouts longs pour éviter les coupures lors de l'upload d'images lourdes
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)

                    // Désactiver le retry car "php artisan serve" supporte mal les connexions
                    // multiples/répétées
                    .retryOnConnectionFailure(false)

                    // Force HTTP/1.1 car HTTP/2 peut poser problème avec le serveur de dev PHP
                    // local
                    .protocols(java.util.Collections.singletonList(okhttp3.Protocol.HTTP_1_1))

                    .addInterceptor(chain -> {
                        // 🔑 INTERCEPTOR CRITIQUE pour "php artisan serve"
                        // Problème : Le serveur de dev PHP coupe parfois la connexion brutalement
                        // Solution : On force la fermeture de la connexion après chaque requête (header
                        // "Connection: close")
                        // et on désactive la compression GZIP ("Accept-Encoding: identity").
                        okhttp3.Request request = chain.request().newBuilder()
                                .header("Connection", "close")
                                .header("Accept-Encoding", "identity")
                                .build();
                        return chain.proceed(request);
                    })
                    .build();
        }
        return okHttpClient;
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getOkHttpClient()) // Attache notre client OkHttp personnalisé
                    .addConverterFactory(GsonConverterFactory.create()) // Convertit le JSON en Java automatiquement
                    .build();
        }
        return retrofit;
    }
}

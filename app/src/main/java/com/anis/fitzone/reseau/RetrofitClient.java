package com.anis.fitzone.reseau;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Point d'accès unique au client Retrofit.
 * 10.0.2.2 est l'adresse spéciale de l'émulateur Android qui pointe vers
 * "localhost" de la machine hôte, où tourne json-server (port 3000).
 */
public final class RetrofitClient {

    private static final String BASE_URL = "http://10.0.2.2:3000/";

    private static ApiService apiService;

    private RetrofitClient() {
    }

    public static ApiService getApiService() {
        if (apiService == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .addInterceptor(logging)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }
}

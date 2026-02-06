package com.example.todoapp.api;
import android.content.Context;

import com.example.todoapp.utils.SessionManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "http://10.0.2.2:5000/";
    private static Retrofit retrofit;

    public static Retrofit getClient(Context context) {

        if (retrofit == null) {

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {

                        Request original = chain.request();

                        SessionManager session =
                                new SessionManager(
                                        context.getApplicationContext()
                                );

                        String token = session.getAccessToken();

                        Request.Builder builder = original.newBuilder();

                        if (token != null && !token.isEmpty()) {
                            builder.addHeader(
                                    "Authorization",
                                    "Bearer " + token
                            );
                        }

                        return chain.proceed(builder.build());
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}
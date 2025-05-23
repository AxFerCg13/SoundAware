package com.example.soundaware.api.connection;

import com.example.soundaware.api.models.audio.AudioResponse;

import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public class ApiClient {
    private static final String BASE_URL = "http://34.173.183.203:8000/";
    private static Retrofit retrofit = null;

    public interface ApiService {
        @Multipart
        @POST("audio")
        Call<AudioResponse> uploadAudio(
                @Part MultipartBody.Part file
        );
    }

    public static ApiService getApiService() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)  // Tiempo para conectarse al servidor
                    .readTimeout(60, TimeUnit.SECONDS)     // Tiempo para leer la respuesta
                    .writeTimeout(60, TimeUnit.SECONDS)    // Tiempo para subir el archivo
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }

    public static Call<AudioResponse> uploadAudioFile(MultipartBody.Part audioFile) {
        return getApiService().uploadAudio(audioFile);
    }
}
